package com.igsm.chatbot.controller;

import com.igsm.chatbot.service.EvolutionApiService;
import com.igsm.chatbot.service.UserSessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Comparator;

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
    private com.igsm.chatbot.repository.ConsultationRepository consultationRepository;

    @Autowired
    private com.igsm.chatbot.repository.InquiryRepository inquiryRepository;

    @Autowired
    private com.igsm.chatbot.service.FaqService faqService;

    @Autowired
    private com.igsm.chatbot.repository.ContactProfileRepository contactProfileRepository;

    @PostMapping("/evolution")
    public void receiveMessage(@RequestBody Map<String, Object> payload) {
        try {
            String eventType = (String) payload.get("event");
            if (!"messages.upsert".equals(eventType))
                return;

            Map<String, Object> data = (Map<String, Object>) payload.get("data");
            Map<String, Object> key = (Map<String, Object>) data.get("key");
            String remoteJid = (String) key.get("remoteJid");
            String messageId = (String) key.get("id");
            boolean fromMe = Boolean.TRUE.equals(key.get("fromMe"));

            Map<String, Object> message = (Map<String, Object>) data.get("message");
            String text = extraerTexto(message);

            if (text == null || remoteJid.endsWith("@g.us"))
                return;
            text = text.trim();

            userSessionService.putSessionData(remoteJid, "last_activity", String.valueOf(System.currentTimeMillis()));

            if (esRespuesta(message)) {
                saveReplyAsConsultation(remoteJid, text, messageId);
                return;
            }

            // 1. DISPARADOR MANUAL (Solo cuando vos escribís desde el celular del bot)
            // Si el mensaje es MÍO y digo "Hola!", mando el menú.
            if (fromMe && text.equalsIgnoreCase("Hola!")) {
                showMainMenu(remoteJid);
                return;
            }
            // 2. FILTRO DE SEGURIDAD (La pieza que falta)
            if (fromMe) {
                return;
            }

            if (text.equalsIgnoreCase("GRACIAS")) {
                exitConversation(remoteJid);
                return;
            }

            if (text.equalsIgnoreCase("INFO") || text.equals("Hola!")) {
                showMainMenu(remoteJid);
                return;
            }

            String currentState = userSessionService.getUserState(remoteJid);
            procesarEstado(remoteJid, text, currentState, messageId);

        } catch (Exception e) {
            logger.error("⚠️ Error en webhook: {}", e.getMessage());
        }
    }

    private String extraerTexto(Map<String, Object> message) {
        if (message == null)
            return null;
        if (message.containsKey("conversation"))
            return (String) message.get("conversation");
        if (message.containsKey("extendedTextMessage")) {
            Map<String, Object> ext = (Map<String, Object>) message.get("extendedTextMessage");
            return (String) ext.get("text");
        }
        return null;
    }

    private boolean esRespuesta(Map<String, Object> message) {
        if (message == null || !message.containsKey("extendedTextMessage"))
            return false;
        Map<String, Object> ext = (Map<String, Object>) message.get("extendedTextMessage");
        return ext.containsKey("contextInfo") && ((Map<String, Object>) ext.get("contextInfo")).containsKey("stanzaId");
    }

    private void procesarEstado(String remoteJid, String text, String state, String msgId) {
        if ("WAITING_MAIN_MENU_SELECTION".equals(state))
            handleMainMenuSelection(remoteJid, text);
        else if ("WAITING_SUBMENU_SELECTION".equals(state))
            handleSubmenuSelection(remoteJid, text);
        else if ("WAITING_MESSAGE_BODY".equals(state))
            handleReply(remoteJid, text, msgId);
        else if ("WAITING_FAQ_SELECTION".equals(state))
            handleFaqSelection(remoteJid, text);
        else if ("WAITING_FAQ_READING".equals(state))
            handleFaqReading(remoteJid, text);
        else if ("WAITING_EMAIL".equals(state))
            handleEmailInput(remoteJid, text);
        else {
            showMainMenu(remoteJid);
        }
    }

    private void handleMainMenuSelection(String remoteJid, String text) {
        String optionData = userSessionService.getSessionData(remoteJid, "menu_option_" + text);
        if (optionData == null) {
            simulateTypingDelay();
            evolutionApiService.sendTextMessage(remoteJid, "⚠️ Opción no válida. Selecciona un número del menú.");
            return;
        }
        if (optionData.startsWith("TYPE:")) {
            showSubmenu(remoteJid, optionData.substring(5));
        } else if (optionData.equals("STATIC:FAQ")) {
            showFaqMenu(remoteJid);
        } else if (optionData.equals("STATIC:CONTACT")) {
            startContactFlow(remoteJid);
        } else if (optionData.equals("STATIC:CONTACT")) {
            startContactFlow(remoteJid);
        } else if (optionData.equals("STATIC:EMAIL")) {
            startEmailFlow(remoteJid);
        } else if (optionData.equals("STATIC:EXIT")) {
            exitConversation(remoteJid);
        }
    }

    private void handleSubmenuSelection(String remoteJid, String text) {
        if ("0".equals(text)) {
            showMainMenu(remoteJid);
            return; // Don't save
        }

        String isViewingDetail = userSessionService.getSessionData(remoteJid, "is_viewing_detail");
        if ("1".equals(text) && "true".equals(isViewingDetail)) {
            String lastType = userSessionService.getSessionData(remoteJid, "current_category_type");
            userSessionService.removeSessionData(remoteJid, "is_viewing_detail");
            showSubmenu(remoteJid, lastType != null ? lastType : "DIPLOMATURA");
            return;
        }

        String idStr = userSessionService.getSessionData(remoteJid, "submenu_option_" + text);
        if (idStr == null) {
            simulateTypingDelay();
            evolutionApiService.sendTextMessage(remoteJid, "⚠️ Opción no válida.");
            return; // Don't save invalid options
        }

        try {
            Long id = Long.parseLong(idStr);
            diplomaturaRepository.findById(id).ifPresentOrElse(diplo -> {
                saveInquiry(remoteJid, diplo);
                String contenidoBot = diplo.getContent();
                if (contenidoBot != null && !contenidoBot.isEmpty()) {
                    userSessionService.putSessionData(remoteJid, "is_viewing_detail", "true");
                    String nav = "\n\n---\n0️⃣ *Menú Principal*\n1️⃣ *Volver al listado anterior*";
                    simulateTypingDelay();
                    evolutionApiService.sendTextMessage(remoteJid, contenidoBot + nav);
                }
            }, () -> showMainMenu(remoteJid));
        } catch (Exception e) {
            logger.error("Error: {}", e.getMessage());
        }
    }

    private void showMainMenu(String remoteJid) {
        userSessionService.setUserState(remoteJid, "WAITING_MAIN_MENU_SELECTION");
        userSessionService.removeSessionData(remoteJid, "is_viewing_detail");

        StringBuilder menu = new StringBuilder(
                "Bienvenido a nuestro asistente virtual 👋🏻\n\n📢 *No olvides registrar tu correo (Opción 6) para mantenerte actualizado.*\n\nSelecciona una opción:\n\n");
        List<com.igsm.chatbot.model.Diplomatura> allDiplos = diplomaturaRepository.findAll();

        // Agrupamos para saber qué categorías mostrar
        java.util.Map<String, List<com.igsm.chatbot.model.Diplomatura>> grouped = allDiplos.stream()
                .collect(java.util.stream.Collectors
                        .groupingBy(d -> d.getType() != null ? d.getType().toUpperCase() : "OTROS"));

        int index = 1;
        if (grouped.containsKey("DIPLOMATURA")) {
            menu.append(index).append(". Diplomaturas\n");
            userSessionService.putSessionData(remoteJid, "menu_option_" + index, "TYPE:DIPLOMATURA");
            index++;
        }
        if (grouped.containsKey("TECNICATURA")) {
            menu.append(index).append(". Tecnicaturas\n");
            userSessionService.putSessionData(remoteJid, "menu_option_" + index, "TYPE:TECNICATURA");
            index++;
        }
        if (grouped.containsKey("LICENCIATURA") || grouped.containsKey("PROFESORADO")) {
            menu.append(index).append(". Licenciatura y Tramo docente\n");
            userSessionService.putSessionData(remoteJid, "menu_option_" + index, "TYPE:LIC_PROF");
            index++;
        }

        menu.append(index).append(". Preguntas Frecuentes\n");
        userSessionService.putSessionData(remoteJid, "menu_option_" + index, "STATIC:FAQ");
        index++;

        menu.append(index).append(". Contacto con el equipo IGSM - UTN\n");
        userSessionService.putSessionData(remoteJid, "menu_option_" + index, "STATIC:CONTACT");
        index++;

        menu.append(index).append(". Registrar e-mail para recibir información reciente\n");
        userSessionService.putSessionData(remoteJid, "menu_option_" + index, "STATIC:EMAIL");
        index++;

        menu.append(index).append(". Finalizar conversación");
        userSessionService.putSessionData(remoteJid, "menu_option_" + index, "STATIC:EXIT");

        simulateTypingDelay();
        evolutionApiService.sendTextMessage(remoteJid, menu.toString());
    }

    private void showSubmenu(String remoteJid, String type) {
        List<com.igsm.chatbot.model.Diplomatura> filtered;
        userSessionService.putSessionData(remoteJid, "current_category_type", type);
        userSessionService.removeSessionData(remoteJid, "is_viewing_detail");

        String displayTitle;
        if ("LIC_PROF".equals(type)) {
            filtered = diplomaturaRepository.findAll().stream()
                    .filter(d -> d.getType() != null && (d.getType().equalsIgnoreCase("LICENCIATURA")
                            || d.getType().equalsIgnoreCase("PROFESORADO")))
                    .sorted(java.util.Comparator.comparing(com.igsm.chatbot.model.Diplomatura::getName))
                    .collect(java.util.stream.Collectors.toList());
            displayTitle = "Licenciatura y Tramo docente";
        } else {
            displayTitle = switch (type.toUpperCase()) {
                case "DIPLOMATURA" -> "Diplomaturas";
                case "TECNICATURA" -> "Tecnicaturas";
                default -> toTitleCase(type) + "s";
            };

            String finalType = type;
            filtered = diplomaturaRepository.findAll().stream()
                    .filter(d -> d.getType() != null && d.getType().equalsIgnoreCase(finalType))
                    .sorted(java.util.Comparator.comparing(com.igsm.chatbot.model.Diplomatura::getName))
                    .collect(java.util.stream.Collectors.toList());
        }

        userSessionService.setUserState(remoteJid, "WAITING_SUBMENU_SELECTION");

        StringBuilder menu = new StringBuilder("🎓 *" + displayTitle + "*\n\nSeleccione una opción:\n\n");
        for (int i = 0; i < filtered.size(); i++) {
            menu.append((i + 1)).append(". ").append(toTitleCase(filtered.get(i).getName())).append("\n");
            userSessionService.putSessionData(remoteJid, "submenu_option_" + (i + 1),
                    String.valueOf(filtered.get(i).getId()));
        }
        menu.append("\n0️⃣ *Volver al Menú Principal*");
        simulateTypingDelay();
        evolutionApiService.sendTextMessage(remoteJid, menu.toString());
    }

    private void showFaqMenu(String remoteJid) {
        userSessionService.setUserState(remoteJid, "WAITING_FAQ_SELECTION");
        String faqMenu = faqService.generateFaqMenu() + "\n0️⃣ *Volver al Menú Principal*";
        simulateTypingDelay();
        evolutionApiService.sendTextMessage(remoteJid, faqMenu);
    }

    private void handleFaqSelection(String remoteJid, String text) {
        if ("0".equals(text)) {
            showMainMenu(remoteJid);
            return;
        }

        try {
            int selection = Integer.parseInt(text);
            String answer = faqService.getFaqAnswer(selection);
            userSessionService.setUserState(remoteJid, "WAITING_FAQ_READING");
            simulateTypingDelay();
            evolutionApiService.sendTextMessage(remoteJid,
                    answer + "\n\n1️⃣ *Volver a Preguntas Frecuentes*\n0️⃣ *Volver al Menú Principal*");
        } catch (NumberFormatException e) {
            simulateTypingDelay();
            evolutionApiService.sendTextMessage(remoteJid, "⚠️ Por favor, ingresa un número válido.");
        }
    }

    private void handleFaqReading(String remoteJid, String text) {
        if ("0".equals(text)) {
            showMainMenu(remoteJid);
        } else if ("1".equals(text)) {
            showFaqMenu(remoteJid);
        } else {
            simulateTypingDelay();
            evolutionApiService.sendTextMessage(remoteJid,
                    "⚠️ Opción no válida.\n\n1️⃣ *Volver a Preguntas Frecuentes*\n0️⃣ *Volver al Menú Principal*");
        }
    }

    private void handleReply(String remoteJid, String text, String msgId) {
        if ("0".equals(text.trim())) {
            showMainMenu(remoteJid);
            return;
        }
        saveReplyAsConsultation(remoteJid, text, msgId);
    }

    // --- MÉTODOS DE APOYO MANTENIDOS ---
    private void startContactFlow(String remoteJid) {
        userSessionService.setUserState(remoteJid, "WAITING_MESSAGE_BODY");
        simulateTypingDelay();
        evolutionApiService.sendTextMessage(remoteJid,
                "📞 *Contacto*\n\nPor favor, escribí tu consulta y un miembro del equipo te responderá a la brevedad.");
    }

    private void saveInquiry(String remoteJid, com.igsm.chatbot.model.Diplomatura diplo) {
        try {
            com.igsm.chatbot.model.Inquiry inquiry = new com.igsm.chatbot.model.Inquiry();
            inquiry.setDiplomatura(diplo);
            inquiry.setUserId(remoteJid);
            inquiry.setContactPhone(remoteJid.split("@")[0]);
            inquiryRepository.save(inquiry);
        } catch (Exception e) {
            logger.error("Error saving inquiry", e);
        }
    }

    private void saveReplyAsConsultation(String remoteJid, String text, String messageId) {
        try {
            String cp = userSessionService.getSessionData(remoteJid, "contact_phone");
            if (cp == null)
                cp = remoteJid.split("@")[0];
            com.igsm.chatbot.model.Consultation c = new com.igsm.chatbot.model.Consultation();
            c.setUserId(remoteJid);
            c.setContactPhone(cp);
            c.setMessage(text);
            c.setMessageId(messageId);
            consultationRepository.save(c);
            simulateTypingDelay();
            evolutionApiService.sendTextMessage(remoteJid,
                    "¡✅ *¡Recibido!* Ya lo anoté📝, un miembro del equipo revisará tu mensaje y te responderá lo antes posible.\n0️⃣ Volver al Menú Principal");
            userSessionService.setUserState(remoteJid, "WAITING_MESSAGE_BODY");
        } catch (Exception e) {
            logger.error("Error saving consultation", e);
        }
    }

    private void startEmailFlow(String remoteJid) {
        userSessionService.setUserState(remoteJid, "WAITING_EMAIL");
        simulateTypingDelay();
        evolutionApiService.sendTextMessage(remoteJid,
                "📧 *Registro de Email*\n\nPor favor, ingresá tu dirección de correo electrónico a continuación:");
    }

    private void handleEmailInput(String remoteJid, String text) {
        if ("0".equals(text.trim())) {
            showMainMenu(remoteJid);
            return;
        }

        // Basic validation
        if (!text.contains("@") || !text.contains(".")) {
            simulateTypingDelay();
            evolutionApiService.sendTextMessage(remoteJid,
                    "⚠️ El formato del correo no parece válido. Intentalo de nuevo o escribí 0 para salir.");
            return;
        }

        try {
            com.igsm.chatbot.model.ContactProfile profile = contactProfileRepository.findById(remoteJid)
                    .orElse(new com.igsm.chatbot.model.ContactProfile(remoteJid));

            profile.setEmail(text.trim());
            contactProfileRepository.save(profile);

            simulateTypingDelay();
            evolutionApiService.sendTextMessage(remoteJid,
                    "✅ *¡Correo registrado con éxito!*\n\nTe mantendremos al tanto de las novedades.\n\n");

            showMainMenu(remoteJid);

        } catch (Exception e) {
            logger.error("Error saving email", e);
            evolutionApiService.sendTextMessage(remoteJid, "❌ Error al guardar. Intenta de nuevo más tarde.");
            showMainMenu(remoteJid);
        }
    }

    private void exitConversation(String jid) {
        userSessionService.clearUserState(jid);
        simulateTypingDelay();
        evolutionApiService.sendTextMessage(jid, "👋🏻¡Hasta Luego! Gracias por contactarte con nosotros. IGSM - UTN");
    }

    private void simulateTypingDelay() {
        try {
            Thread.sleep(1500); // 1.5 seconds delay
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private String getDisplayName(String type) {
        return switch (type) {
            case "DIPLOMATURA" -> "Diplomaturas";
            case "TECNICATURA" -> "Tecnicaturas";
            case "LICENCIATURA", "PROFESORADO" -> "Licenciatura/Profesorado";
            default -> toTitleCase(type);
        };
    }

    private String toTitleCase(String input) {
        if (input == null || input.isEmpty())
            return input;
        StringBuilder sb = new StringBuilder();
        boolean next = true;
        for (char c : input.toLowerCase().toCharArray()) {
            if (Character.isSpaceChar(c))
                next = true;
            else if (next) {
                c = Character.toTitleCase(c);
                next = false;
            }
            sb.append(c);
        }
        return sb.toString();
    }
}
