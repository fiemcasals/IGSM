package com.igsm.chatbot.controller;

import com.igsm.chatbot.service.EvolutionApiService;
import com.igsm.chatbot.service.UserSessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/webhook")
public class WebhookController {

    private static final Logger logger = LoggerFactory.getLogger(WebhookController.class);

    @Autowired
    private EvolutionApiService evolutionApiService;

    @Autowired
    private UserSessionService userSessionService;

    @Autowired
    private com.igsm.chatbot.repository.DiplomaturaRepository diplomaturaRepository;

    @Autowired
    private com.igsm.chatbot.repository.InquiryRepository inquiryRepository;

    @Autowired
    private com.igsm.chatbot.repository.SubscriptionRepository subscriptionRepository;

    @PostMapping("/evolution")
    public void receiveMessage(@RequestBody Map<String, Object> payload) {
        try {
            logger.debug("📥 Full Webhook Payload: {}", payload);
            String eventType = (String) payload.get("event");
            logger.info("🔔 Webhook received. Event: {}", eventType);

            if ("messages.upsert".equals(eventType)) {
                Map<String, Object> data = (Map<String, Object>) payload.get("data");
                Map<String, Object> key = (Map<String, Object>) data.get("key");
                String remoteJid = (String) key.get("remoteJid");

                Object fromMeObj = key.get("fromMe");
                boolean fromMe = fromMeObj instanceof Boolean && (Boolean) fromMeObj;

                logger.info("📩 New Message from: {}, fromMe: {}", remoteJid, fromMe);

                Map<String, Object> message = (Map<String, Object>) data.get("message");
                String text = "";
                if (message != null) {
                    if (message.containsKey("conversation")) {
                        text = (String) message.get("conversation");
                    } else if (message.containsKey("extendedTextMessage")) {
                        Map<String, Object> extended = (Map<String, Object>) message.get("extendedTextMessage");
                        text = (String) extended.get("text");
                    }
                }

                if (text == null) {
                    logger.warn("⚠️ Text is null, ignoring message from {}", remoteJid);
                    return;
                }
                text = text.trim();
                logger.info("   Text: {}", text);

                if (fromMe) {
                    if (text.equalsIgnoreCase("INFO")) {
                        logger.info("🔔 Admin Trigger: 'INFO' sent by me. Triggering menu for {}", remoteJid);
                    } else {
                        logger.debug("   Ignoring message sent by me: {}", text);
                        return; // Ignore other messages from me
                    }
                }

                // Ignore Group Messages
                if (remoteJid.endsWith("@g.us")) {
                    logger.info("🔇 Ignoring Group Message from: {}", remoteJid);
                    return;
                }

                // Global Exit
                if (text.equalsIgnoreCase("GRACIAS") || text.equals("99")) {
                    logger.info("   Global Exit triggered by {}", remoteJid);
                    userSessionService.clearUserState(remoteJid);
                    evolutionApiService.sendTextMessage(remoteJid,
                            "👋 ¡Hasta luego! Gracias por contactarte con el IGSM.");
                    return;
                }

                String currentState = userSessionService.getUserState(remoteJid);
                logger.info("   Current State for {}: {}", remoteJid, currentState);

                // Global Start / Reset
                if (text.equalsIgnoreCase("INFO") || text.equals("0")) {
                    logger.info("   Matched INFO or 0. Showing Main Menu to {}", remoteJid);
                    showMainMenu(remoteJid);
                    return;
                }

                // Handle State: WAITING_DIPLO_SELECTION
                if ("WAITING_DIPLO_SELECTION".equals(currentState)) {
                    handleDiploSelection(remoteJid, text);
                    return;
                }

                // Handle State: WAITING_POST_DIPLO_ACTION
                if ("WAITING_POST_DIPLO_ACTION".equals(currentState)) {
                    if (text.equals("1")) {
                        // Pre-inscribirse
                        logger.info("   User {} chose Pre-registration", remoteJid);
                        userSessionService.setUserState(remoteJid, "WAITING_PRE_REG_NAME");
                        evolutionApiService.sendTextMessage(remoteJid,
                                "📝 *Pre-inscripción*\n\nPor favor, ingrese su *Nombre*:");
                    } else if (text.equals("2")) {
                        // Volver al menu
                        logger.info("   User {} chose Back to Menu", remoteJid);
                        showMainMenu(remoteJid);
                    } else if (text.equals("3")) {
                        // Finalizar
                        logger.info("   User {} chose Finish", remoteJid);
                        userSessionService.clearUserState(remoteJid);
                        evolutionApiService.sendTextMessage(remoteJid,
                                "👋 ¡Hasta luego! Gracias por contactarte con el IGSM.");
                    } else {
                        logger.warn("   User {} sent invalid option in WAITING_POST_DIPLO_ACTION: {}", remoteJid, text);
                        evolutionApiService.sendTextMessage(remoteJid,
                                "⚠️ Opción no válida.\n\n1. Pre-inscribirse\n2. Volver al Menú Principal\n3. Finalizar conversación");
                    }
                    return;
                }

                // --- Pre-registration Flow ---

                if ("WAITING_PRE_REG_NAME".equals(currentState)) {
                    userSessionService.putSessionData(remoteJid, "name", text);
                    userSessionService.setUserState(remoteJid, "WAITING_PRE_REG_SURNAME");
                    evolutionApiService.sendTextMessage(remoteJid, "Por favor, ingrese su *Apellido*:");
                    return;
                }

                if ("WAITING_PRE_REG_SURNAME".equals(currentState)) {
                    userSessionService.putSessionData(remoteJid, "surname", text);
                    userSessionService.setUserState(remoteJid, "WAITING_PRE_REG_DNI");
                    evolutionApiService.sendTextMessage(remoteJid, "🔢 Ingrese su *DNI* (sin puntos):");
                    return;
                }

                if ("WAITING_PRE_REG_DNI".equals(currentState)) {
                    userSessionService.putSessionData(remoteJid, "dni", text);
                    userSessionService.setUserState(remoteJid, "WAITING_PRE_REG_MAIL");
                    evolutionApiService.sendTextMessage(remoteJid, "📧 Ingrese su *Correo Electrónico*:");
                    return;
                }

                if ("WAITING_PRE_REG_MAIL".equals(currentState)) {
                    userSessionService.putSessionData(remoteJid, "mail", text);
                    userSessionService.setUserState(remoteJid, "WAITING_PRE_REG_EDUCATION");
                    evolutionApiService.sendTextMessage(remoteJid,
                            "🎓 Ingrese su *Nivel de Estudio alcanzado* (ej. Secundario, Terciario, Universitario):");
                    return;
                }

                if ("WAITING_PRE_REG_EDUCATION".equals(currentState)) {
                    userSessionService.putSessionData(remoteJid, "education", text);
                    userSessionService.setUserState(remoteJid, "WAITING_PRE_REG_PHONE");
                    evolutionApiService.sendTextMessage(remoteJid, "📱 Ingrese su *Número de Celular*:");
                    return;
                }

                if ("WAITING_PRE_REG_PHONE".equals(currentState)) {
                    userSessionService.putSessionData(remoteJid, "phone", text);

                    String diploIdStr = userSessionService.getSessionData(remoteJid, "current_diplo_id");
                    Long diploId = Long.parseLong(diploIdStr);
                    com.igsm.chatbot.model.Diplomatura d = diplomaturaRepository.findById(diploId).orElse(null);

                    if (d != null) {
                        logger.info("Checking Diplo Type for Upload: ID={}, Name={}, Type={}", d.getId(), d.getName(),
                                d.getType());
                    }

                    if (d != null && "LICENCIATURA".equalsIgnoreCase(d.getType())) {
                        userSessionService.setUserState(remoteJid, "WAITING_FILE_UPLOAD");
                        evolutionApiService.sendTextMessage(remoteJid,
                                "📂 Para finalizar la inscripción a la Licenciatura, por favor *envíe una foto o PDF* de su título previo o documentación requerida.");
                        return;
                    }

                    // If not Licenciatura, save immediately
                    saveSubscription(remoteJid, d, null, null);
                    return;
                }

                if ("WAITING_FILE_UPLOAD".equals(currentState)) {
                    String mimeType = null;
                    String fileUrl = null;

                    // Check for media
                    Map<String, Object> msg = (Map<String, Object>) ((Map<String, Object>) payload.get("data"))
                            .get("message");
                    if (msg != null) {
                        if (msg.containsKey("imageMessage")) {
                            Map<String, Object> img = (Map<String, Object>) msg.get("imageMessage");
                            fileUrl = (String) img.get("url");
                            mimeType = (String) img.get("mimetype");
                        } else if (msg.containsKey("documentMessage")) {
                            Map<String, Object> doc = (Map<String, Object>) msg.get("documentMessage");
                            fileUrl = (String) doc.get("url");
                            mimeType = (String) doc.get("mimetype");
                        }
                    }

                    if (fileUrl == null && (text == null || text.isEmpty())) {
                        evolutionApiService.sendTextMessage(remoteJid,
                                "⚠️ Por favor, envíe un archivo (Imagen o PDF).");
                        return;
                    }

                    // If text is sent instead of file, we might want to warn, or accept text as a
                    // link?
                    // Let's assume strict file upload. If text is present but no fileUrl, it might
                    // be a user comment.
                    // If fileUrl is null, let's try to see if it's a text message with a link? No,
                    // let's enforce media message.
                    if (fileUrl == null) {
                        evolutionApiService.sendTextMessage(remoteJid,
                                "⚠️ No detectamos un archivo. Por favor envíe la imagen o PDF.");
                        return;
                    }

                    String diploIdStr = userSessionService.getSessionData(remoteJid, "current_diplo_id");
                    Long diploId = Long.parseLong(diploIdStr);
                    com.igsm.chatbot.model.Diplomatura d = diplomaturaRepository.findById(diploId).orElse(null);

                    saveSubscription(remoteJid, d, fileUrl, mimeType);
                    return;
                }

                if ("WAITING_FINAL_DECISION".equals(currentState)) {
                    if (text.equals("1")) {
                        showMainMenu(remoteJid);
                    } else {
                        userSessionService.clearUserState(remoteJid);
                        evolutionApiService.sendTextMessage(remoteJid,
                                "👋 ¡Hasta luego! Gracias por contactarte con el IGSM.");
                    }
                    return;
                }

                // Default / Fallback
                if (currentState == null || currentState.isEmpty() || "NONE".equals(currentState)) {
                    logger.info("   No active state. Ignoring message to allow free chat.");
                    return;
                }

            }
        } catch (Exception e) {
            logger.error("⚠️ Error parsing webhook: {}", e.getMessage(), e);
        }
    }

    private void showMainMenu(String remoteJid) {
        userSessionService.setUserState(remoteJid, "WAITING_DIPLO_SELECTION");
        StringBuilder menu = new StringBuilder(
                "Bienvenido a nuestro asistente virtual 👋🏻\n" +
                        "Para conocer nuestra oferta académica, obtener información y realizar la preinscripción, selecciona el número correspondiente de tu interés:\n\n");

        List<com.igsm.chatbot.model.Diplomatura> diplos = diplomaturaRepository.findAll();
        // Sort by ID to maintain consistent order
        diplos.sort((d1, d2) -> d1.getId().compareTo(d2.getId()));

        for (int i = 0; i < diplos.size(); i++) {
            menu.append((i + 1)).append(". ").append(diplos.get(i).getName()).append("\n");
        }
        menu.append((diplos.size() + 1)).append(". FINALIZAR CONVERSACIÓN");

        evolutionApiService.sendTextMessage(remoteJid, menu.toString());
    }

    private void handleDiploSelection(String remoteJid, String text) {
        List<com.igsm.chatbot.model.Diplomatura> diplos = diplomaturaRepository.findAll();
        diplos.sort((d1, d2) -> d1.getId().compareTo(d2.getId()));

        try {
            int selection = Integer.parseInt(text);
            if (selection >= 1 && selection <= diplos.size()) {
                com.igsm.chatbot.model.Diplomatura selectedDiplo = diplos.get(selection - 1);

                // Log Inquiry
                com.igsm.chatbot.model.Inquiry inquiry = new com.igsm.chatbot.model.Inquiry();
                inquiry.setDiplomatura(selectedDiplo);
                inquiryRepository.save(inquiry);

                userSessionService.putSessionData(remoteJid, "current_diplo_id", String.valueOf(selectedDiplo.getId()));
                userSessionService.putSessionData(remoteJid, "current_diplo_name", selectedDiplo.getName());
                userSessionService.setUserState(remoteJid, "WAITING_POST_DIPLO_ACTION");

                evolutionApiService.sendTextMessage(remoteJid,
                        selectedDiplo.getContent()
                                + "\n\n1. Pre-inscribirse\n2. Volver al Menú Principal\n3. Finalizar conversación");
            } else if (selection == diplos.size() + 1) {
                userSessionService.clearUserState(remoteJid);
                evolutionApiService.sendTextMessage(remoteJid,
                        "👋 ¡Hasta luego! Gracias por contactarte con el IGSM.");
            } else {
                evolutionApiService.sendTextMessage(remoteJid,
                        "⚠️ Opción no válida. Por favor, ingrese un número del 1 al " + (diplos.size() + 1) + ".");
            }
        } catch (NumberFormatException e) {
            evolutionApiService.sendTextMessage(remoteJid,
                    "⚠️ Opción no válida. Por favor, ingrese un número válido.");
        }
    }

    private void saveSubscription(String remoteJid, com.igsm.chatbot.model.Diplomatura d, String fileUrl,
            String mimeType) {
        String name = userSessionService.getSessionData(remoteJid, "name");
        String surname = userSessionService.getSessionData(remoteJid, "surname");
        String dni = userSessionService.getSessionData(remoteJid, "dni");
        String mail = userSessionService.getSessionData(remoteJid, "mail");
        String edu = userSessionService.getSessionData(remoteJid, "education");
        String phone = userSessionService.getSessionData(remoteJid, "phone");

        try {
            if (d != null) {
                com.igsm.chatbot.model.Subscription sub = new com.igsm.chatbot.model.Subscription();
                sub.setDiplomatura(d);
                sub.setUserId(remoteJid);
                sub.setName(name);
                sub.setSurname(surname);
                sub.setDni(dni);
                sub.setMail(mail);
                sub.setEducation(edu);
                sub.setPhone(phone);
                sub.setFileUrl(fileUrl);
                sub.setMimeType(mimeType);
                subscriptionRepository.save(sub);
            }
        } catch (Exception e) {
            logger.error("Error saving subscription: {}", e.getMessage(), e);
        }

        logger.info("✅ NEW PRE-REGISTRATION SAVED:");
        logger.info("Diplo: {}", d.getName());
        logger.info("User: {} {}", name, surname);
        if (fileUrl != null)
            logger.info("File: {}", fileUrl);

        userSessionService.setUserState(remoteJid, "WAITING_FINAL_DECISION");
        evolutionApiService.sendTextMessage(remoteJid,
                "✅ *¡Datos registrados correctamente!*\n\n" +
                        "Hemos recibido su pre-inscripción para la *" + d.getName() + "*.\n" +
                        "Nos pondremos en contacto con usted a la brevedad.\n\n" +
                        "1. Volver al Menú Principal\n" +
                        "2. Finalizar");
    }
}
