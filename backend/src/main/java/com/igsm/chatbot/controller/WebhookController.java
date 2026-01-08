package com.igsm.chatbot.controller;

import com.igsm.chatbot.service.EvolutionApiService;
import com.igsm.chatbot.service.UserSessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.Comparator;
import java.util.HashMap;

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

    @Autowired
    private com.igsm.chatbot.repository.ConsultationRepository consultationRepository;

    @Autowired
    private com.igsm.chatbot.repository.FAQRepository faqRepository;

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
                }

                // Extract messageId
                String messageId = (String) key.get("id");
                logger.info("   Message ID: {}", messageId);

                // Check if it's a reply to a previous message
                boolean isReply = false;
                if (message != null && message.containsKey("extendedTextMessage")) {
                    Map<String, Object> extended = (Map<String, Object>) message.get("extendedTextMessage");
                    if (extended.containsKey("contextInfo")) {
                        Map<String, Object> contextInfo = (Map<String, Object>) extended.get("contextInfo");
                        if (contextInfo.containsKey("stanzaId")) {
                            isReply = true;
                            logger.info("↩️ Detected reply to message ID: {}", contextInfo.get("stanzaId"));
                        }
                    }
                }

                if (text == null) {
                    logger.warn("⚠️ Text is null, ignoring message from {}", remoteJid);
                    return;
                }
                text = text.trim();
                logger.info("   Text: {}", text);

                // Handle Replies as Consultations
                if (isReply) {
                    try {
                        com.igsm.chatbot.model.Consultation consultation = new com.igsm.chatbot.model.Consultation();
                        consultation.setUserId(remoteJid);
                        // Try to find existing contact phone from previous consultations or session
                        String contactPhone = userSessionService.getSessionData(remoteJid, "contact_phone");
                        if (contactPhone == null) {
                             // Fallback: use the sender's number if not found in session
                             contactPhone = remoteJid.split("@")[0];
                        }
                        consultation.setContactPhone(contactPhone);
                        consultation.setMessage(text);
                        consultation.setMessageId(messageId);
                        
                        consultationRepository.save(consultation);
                        logger.info("✅ Reply saved as consultation for {}", remoteJid);
                        
                        // Optional: Confirm receipt or stay silent to simulate chat
                        // evolutionApiService.sendTextMessage(remoteJid, "✅"); 
                        return; 
                    } catch (Exception e) {
                        logger.error("❌ Error saving reply consultation: {}", e.getMessage());
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
                            "👋🏻 ¡Hasta Luego! Gracias por contactarte con nosotros.\n\n" +
                                    "☎️ Ante consultas particulares o necesidad de asesoramiento personalizado, podés indicarnos días y horarios de contacto.");
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

                // Handle State: WAITING_MAIN_MENU_SELECTION
                if ("WAITING_MAIN_MENU_SELECTION".equals(currentState)) {
                    handleMainMenuSelection(remoteJid, text);
                    return;
                }

                // Handle State: WAITING_SUBMENU_SELECTION
                if ("WAITING_SUBMENU_SELECTION".equals(currentState)) {
                    handleSubmenuSelection(remoteJid, text);
                    return;
                }

                // Handle State: WAITING_POST_DIPLO_ACTION
                if ("WAITING_POST_DIPLO_ACTION".equals(currentState)) {
                    String categoryType = userSessionService.getSessionData(remoteJid, "current_category_type");

                    if (categoryType != null) {
                        if (text.equals("1")) {
                            // Volver a Categoria
                            logger.info("   User {} chose Back to Category: {}", remoteJid, categoryType);
                            showSubmenu(remoteJid, categoryType);
                        } else if (text.equals("2")) {
                            // Volver al menu
                            logger.info("   User {} chose Back to Menu", remoteJid);
                            userSessionService.removeSessionData(remoteJid, "current_category_type");
                            showMainMenu(remoteJid);
                        } else if (text.equals("3")) {
                            // Finalizar
                            logger.info("   User {} chose Finish", remoteJid);
                            exitConversation(remoteJid);
                        } else {
                            String catDisplay = toTitleCase(categoryType + (categoryType.endsWith("A") ? "s" : ""));
                            if (categoryType.equals("PROFESORADO"))
                                catDisplay = "Profesorados";
                            evolutionApiService.sendTextMessage(remoteJid,
                                    "⚠️ Opción no válida.\n\n1. Volver a " + catDisplay
                                            + "\n2. Volver al Menú Principal\n3. Finalizar conversación");
                        }
                    } else {
                        // No category context
                        if (text.equals("1")) {
                            // Volver al menu
                            logger.info("   User {} chose Back to Menu", remoteJid);
                            showMainMenu(remoteJid);
                        } else if (text.equals("2")) {
                            // Finalizar
                            logger.info("   User {} chose Finish", remoteJid);
                            exitConversation(remoteJid);
                        } else {
                            evolutionApiService.sendTextMessage(remoteJid,
                                    "⚠️ Opción no válida.\n\n1. Volver al Menú Principal\n2. Finalizar conversación");
                        }
                    }
                    return;
                }

                // --- Consultation Flow ---

                if ("WAITING_CONTACT_CHOICE".equals(currentState)) {
                    if (text.equals("1")) {
                        String number = remoteJid.split("@")[0];
                        userSessionService.putSessionData(remoteJid, "contact_phone", number);
                        userSessionService.setUserState(remoteJid, "WAITING_MESSAGE_BODY");
                        evolutionApiService.sendTextMessage(remoteJid, "📝 Por favor, escriba su mensaje o consulta:");
                    } else if (text.equals("2")) {
                        userSessionService.setUserState(remoteJid, "WAITING_CONTACT_NUMBER");
                        evolutionApiService.sendTextMessage(remoteJid,
                                "📱 Por favor, ingrese el número de contacto (con código de área):");
                    } else {
                        evolutionApiService.sendTextMessage(remoteJid,
                                "⚠️ Opción no válida.\n1. Usar el número actual\n2. Ingresar otro número");
                    }
                    return;
                }

                if ("WAITING_CONTACT_NUMBER".equals(currentState)) {
                    userSessionService.putSessionData(remoteJid, "contact_phone", text);
                    userSessionService.setUserState(remoteJid, "WAITING_MESSAGE_BODY");
                    evolutionApiService.sendTextMessage(remoteJid, "📝 Por favor, escriba su mensaje o consulta:");
                    return;
                }

                if ("WAITING_MESSAGE_BODY".equals(currentState)) {
                    String contactPhone = userSessionService.getSessionData(remoteJid, "contact_phone");

                    // Extract messageId safely
                    String msgId = null;
                    if (key != null && key.containsKey("id")) {
                        msgId = (String) key.get("id");
                    }

                    com.igsm.chatbot.model.Consultation consultation = new com.igsm.chatbot.model.Consultation();
                    consultation.setUserId(remoteJid);
                    consultation.setContactPhone(contactPhone);
                    consultation.setMessage(text);
                    consultation.setMessageId(msgId);

                    consultationRepository.save(consultation);

                    userSessionService.clearUserState(remoteJid);
                    evolutionApiService.sendTextMessage(remoteJid,
                            "✅ *¡Mensaje recibido!*\n\n" +
                                    "Un representante se pondrá en contacto con usted a la brevedad al número: "
                                    + contactPhone + ".\n\n" +
                                    "¡Muchas gracias!");
                    return;
                }

                // --- FAQ Flow ---

                if ("WAITING_FAQ_SELECTION".equals(currentState)) {
                    List<com.igsm.chatbot.model.FAQ> faqs = faqRepository.findAll();
                    faqs.sort((f1, f2) -> f1.getId().compareTo(f2.getId()));

                    try {
                        int selection = Integer.parseInt(text);
                        if (selection >= 1 && selection <= faqs.size()) {
                            com.igsm.chatbot.model.FAQ selectedFAQ = faqs.get(selection - 1);
                            evolutionApiService.sendTextMessage(remoteJid,
                                    "❓ *" + selectedFAQ.getQuestion() + "*\n\n" +
                                            "💡 " + selectedFAQ.getAnswer() + "\n\n" +
                                            "0. Volver al Menú Principal");
                        } else if (selection == 0) {
                            showMainMenu(remoteJid);
                        } else {
                            evolutionApiService.sendTextMessage(remoteJid, "⚠️ Opción no válida.");
                        }
                    } catch (NumberFormatException e) {
                        evolutionApiService.sendTextMessage(remoteJid, "⚠️ Por favor, ingrese un número válido.");
                    }
                    return;
                }

                // Default / Fallback
                if (currentState == null || currentState.isEmpty() || "NONE".equals(currentState)) {
                    logger.info("   No active active state. Ignoring message to allow free chat.");
                    return;
                }

            }
        } catch (Exception e) {
            logger.error("⚠️ Error parsing webhook: {}", e.getMessage(), e);
        }
    }

    private String toTitleCase(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        StringBuilder titleCase = new StringBuilder();
        boolean nextTitleCase = true;

        for (char c : input.toLowerCase().toCharArray()) {
            if (Character.isSpaceChar(c)) {
                nextTitleCase = true;
            } else if (nextTitleCase) {
                c = Character.toTitleCase(c);
                nextTitleCase = false;
            }
            titleCase.append(c);
        }
        return titleCase.toString();
    }

    private void showMainMenu(String remoteJid) {
        userSessionService.setUserState(remoteJid, "WAITING_MAIN_MENU_SELECTION");
        userSessionService.removeSessionData(remoteJid, "current_category_type"); // Ensure clean state
        StringBuilder menu = new StringBuilder(
                "Bienvenido a nuestro asistente virtual 👋🏻\n" +
                        "Para conocer nuestra oferta académica, obtener información y realizar la preinscripción, selecciona el número correspondiente de tu interés:\n\n");

        List<com.igsm.chatbot.model.Diplomatura> allDiplos = diplomaturaRepository.findAll();
        Map<String, List<com.igsm.chatbot.model.Diplomatura>> grouped = allDiplos.stream()
                .collect(Collectors.groupingBy(d -> d.getType() != null ? d.getType().toUpperCase() : "OTROS"));

        // Define order of categories
        List<String> orderedTypes = List.of("DIPLOMATURA", "TECNICATURA", "LICENCIATURA", "PROFESORADO", "OTROS");
        List<String> availableOptions = new ArrayList<>();
        Map<Integer, String> optionMap = new HashMap<>(); // Index -> Type or ID

        int index = 1;

        for (String type : orderedTypes) {
            if (grouped.containsKey(type)) {
                List<com.igsm.chatbot.model.Diplomatura> items = grouped.get(type);
                if (items.size() > 1) {
                    // Show Category Name (Pluralized roughly)
                    String displayName = toTitleCase(type + (type.endsWith("A") ? "s" : ""));
                    if (type.equals("PROFESORADO"))
                        displayName = "Profesorados"; // Manual fix if needed, but 'Profesorado' -> 'Profesorados' logic
                                                      // is simple

                    menu.append(index).append(". ").append(displayName).append("\n");
                    userSessionService.putSessionData(remoteJid, "menu_option_" + index, "TYPE:" + type);
                    index++;
                } else if (items.size() == 1) {
                    // Show Item Name directly
                    com.igsm.chatbot.model.Diplomatura item = items.get(0);
                    menu.append(index).append(". ").append(toTitleCase(item.getName())).append("\n");
                    userSessionService.putSessionData(remoteJid, "menu_option_" + index, "ID:" + item.getId());
                    index++;
                }
            }
        }

        // Add static options
        menu.append(index).append(". Preguntas Frecuentes\n");
        userSessionService.putSessionData(remoteJid, "menu_option_" + index, "STATIC:FAQ");
        index++;

        menu.append(index).append(". Deje su mensaje a un representante\n");
        userSessionService.putSessionData(remoteJid, "menu_option_" + index, "STATIC:CONTACT");
        index++;

        menu.append(index).append(". FINALIZAR CONVERSACIÓN");
        userSessionService.putSessionData(remoteJid, "menu_option_" + index, "STATIC:EXIT");

        userSessionService.putSessionData(remoteJid, "max_menu_options", String.valueOf(index));
        evolutionApiService.sendTextMessage(remoteJid, menu.toString());
    }

    private void handleMainMenuSelection(String remoteJid, String text) {
        try {
            int selection = Integer.parseInt(text);
            String maxOptionsStr = userSessionService.getSessionData(remoteJid, "max_menu_options");
            int maxOptions = maxOptionsStr != null ? Integer.parseInt(maxOptionsStr) : 0;

            if (selection >= 1 && selection <= maxOptions) {
                String action = userSessionService.getSessionData(remoteJid, "menu_option_" + selection);
                if (action == null) {
                    evolutionApiService.sendTextMessage(remoteJid, "⚠️ Opción no válida.");
                    return;
                }

                if (action.startsWith("TYPE:")) {
                    String type = action.substring(5);
                    showSubmenu(remoteJid, type);
                } else if (action.startsWith("ID:")) {
                    Long id = Long.parseLong(action.substring(3));
                    // Direct access, no category context
                    userSessionService.removeSessionData(remoteJid, "current_category_type");
                    showDiploDetails(remoteJid, id);
                } else if (action.equals("STATIC:FAQ")) {
                    showFAQMenu(remoteJid);
                } else if (action.equals("STATIC:CONTACT")) {
                    startContactFlow(remoteJid);
                } else if (action.equals("STATIC:EXIT")) {
                    exitConversation(remoteJid);
                }
            } else {
                evolutionApiService.sendTextMessage(remoteJid,
                        "⚠️ Opción no válida. Por favor, ingrese un número del 1 al " + maxOptions + ".");
            }
        } catch (NumberFormatException e) {
            evolutionApiService.sendTextMessage(remoteJid, "⚠️ Por favor, ingrese un número válido.");
        }
    }

    private void showSubmenu(String remoteJid, String type) {
        List<com.igsm.chatbot.model.Diplomatura> allDiplos = diplomaturaRepository.findAll();
        List<com.igsm.chatbot.model.Diplomatura> filtered = allDiplos.stream()
                .filter(d -> d.getType() != null && d.getType().equalsIgnoreCase(type))
                .sorted(Comparator.comparing(com.igsm.chatbot.model.Diplomatura::getName))
                .collect(Collectors.toList());

        if (filtered.isEmpty()) {
            evolutionApiService.sendTextMessage(remoteJid, "⚠️ No hay opciones disponibles en esta categoría.");
            showMainMenu(remoteJid);
            return;
        }

        userSessionService.setUserState(remoteJid, "WAITING_SUBMENU_SELECTION");
        userSessionService.putSessionData(remoteJid, "current_category_type", type); // Store context

        // Store IDs for submenu selection mapping
        for (int i = 0; i < filtered.size(); i++) {
            userSessionService.putSessionData(remoteJid, "submenu_option_" + (i + 1),
                    String.valueOf(filtered.get(i).getId()));
        }
        userSessionService.putSessionData(remoteJid, "submenu_max_options", String.valueOf(filtered.size()));

        StringBuilder menu = new StringBuilder("🎓 *" + toTitleCase(type) + "*\n\nSeleccione una opción:\n\n");
        for (int i = 0; i < filtered.size(); i++) {
            menu.append((i + 1)).append(". ").append(toTitleCase(filtered.get(i).getName())).append("\n");
        }
        menu.append("\n0. Volver al Menú Principal");

        evolutionApiService.sendTextMessage(remoteJid, menu.toString());
    }

    private void handleSubmenuSelection(String remoteJid, String text) {
        try {
            int selection = Integer.parseInt(text);
            if (selection == 0) {
                showMainMenu(remoteJid);
                return;
            }

            String maxOptionsStr = userSessionService.getSessionData(remoteJid, "submenu_max_options");
            int maxOptions = maxOptionsStr != null ? Integer.parseInt(maxOptionsStr) : 0;

            if (selection >= 1 && selection <= maxOptions) {
                String idStr = userSessionService.getSessionData(remoteJid, "submenu_option_" + selection);
                if (idStr != null) {
                    showDiploDetails(remoteJid, Long.parseLong(idStr));
                } else {
                    evolutionApiService.sendTextMessage(remoteJid, "⚠️ Error al recuperar la opción.");
                }
            } else {
                evolutionApiService.sendTextMessage(remoteJid, "⚠️ Opción no válida.");
            }
        } catch (NumberFormatException e) {
            evolutionApiService.sendTextMessage(remoteJid, "⚠️ Por favor, ingrese un número válido.");
        }
    }

    private void showDiploDetails(String remoteJid, Long diploId) {
        com.igsm.chatbot.model.Diplomatura selectedDiplo = diplomaturaRepository.findById(diploId).orElse(null);
        if (selectedDiplo == null) {
            evolutionApiService.sendTextMessage(remoteJid, "⚠️ La opción seleccionada no está disponible.");
            showMainMenu(remoteJid);
            return;
        }

        // Log Inquiry
        com.igsm.chatbot.model.Inquiry inquiry = new com.igsm.chatbot.model.Inquiry();
        inquiry.setDiplomatura(selectedDiplo);
        inquiryRepository.save(inquiry);

        userSessionService.putSessionData(remoteJid, "current_diplo_id", String.valueOf(selectedDiplo.getId()));
        userSessionService.putSessionData(remoteJid, "current_diplo_name", selectedDiplo.getName());
        userSessionService.setUserState(remoteJid, "WAITING_POST_DIPLO_ACTION");

        String categoryType = userSessionService.getSessionData(remoteJid, "current_category_type");
        StringBuilder response = new StringBuilder(selectedDiplo.getContent());
        response.append("\n");

        if (categoryType != null) {
            String catDisplay = toTitleCase(categoryType + (categoryType.endsWith("A") ? "s" : ""));
            if (categoryType.equals("PROFESORADO"))
                catDisplay = "Profesorados";
            response.append("\n1. Volver a ").append(catDisplay);
            response.append("\n2. Volver al Menú Principal");
            response.append("\n3. Finalizar conversación");
        } else {
            response.append("\n1. Volver al Menú Principal");
            response.append("\n2. Finalizar conversación");
        }

        evolutionApiService.sendTextMessage(remoteJid, response.toString());
    }

    private void showFAQMenu(String remoteJid) {
        List<com.igsm.chatbot.model.FAQ> faqs = faqRepository.findAll();
        if (faqs.isEmpty()) {
            evolutionApiService.sendTextMessage(remoteJid,
                    "⚠️ No hay preguntas frecuentes cargadas por el momento.\n\n0. Volver al Menú Principal");
            return;
        }

        faqs.sort((f1, f2) -> f1.getId().compareTo(f2.getId()));
        StringBuilder faqMenu = new StringBuilder(
                "❓ *Preguntas Frecuentes*\n\nSeleccione una pregunta para ver la respuesta:\n\n");
        for (int i = 0; i < faqs.size(); i++) {
            faqMenu.append((i + 1)).append(". ").append(faqs.get(i).getQuestion()).append("\n");
        }
        faqMenu.append("\n0. Volver al Menú Principal");

        userSessionService.setUserState(remoteJid, "WAITING_FAQ_SELECTION");
        evolutionApiService.sendTextMessage(remoteJid, faqMenu.toString());
    }

    private void startContactFlow(String remoteJid) {
        userSessionService.setUserState(remoteJid, "WAITING_CONTACT_CHOICE");
        evolutionApiService.sendTextMessage(remoteJid,
                "📞 *Contacto*\n\n" +
                        "¿A qué número desea que lo contactemos?\n\n" +
                        "1. Usar el número desde el cual se comunica (" + remoteJid.split("@")[0] + ")\n" +
                        "2. Ingresar otro número");
    }

    private void exitConversation(String remoteJid) {
        userSessionService.clearUserState(remoteJid);
        evolutionApiService.sendTextMessage(remoteJid,
                "👋🏻 ¡Hasta Luego! Gracias por contactarte con nosotros.\n\n" +
                        "☎️ Ante consultas particulares o necesidad de asesoramiento personalizado, podés indicarnos días y horarios de contacto.");
    }

}
