package com.igsm.chatbot.controller;

import com.igsm.chatbot.service.EvolutionApiService;
import com.igsm.chatbot.service.UserSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/webhook")
public class WebhookController {

    @Autowired
    private EvolutionApiService evolutionApiService;

    @Autowired
    private UserSessionService userSessionService;

    @PostMapping("/evolution")
    public void receiveMessage(@RequestBody Map<String, Object> payload) {
        // Basic parsing to identify message content
        try {
            String eventType = (String) payload.get("event");
            if ("messages.upsert".equals(eventType)) {
                Map<String, Object> data = (Map<String, Object>) payload.get("data");
                Map<String, Object> key = (Map<String, Object>) data.get("key");
                String remoteJid = (String) key.get("remoteJid");

                // Check for "fromMe" to avoid infinite loops
                Object fromMeObj = key.get("fromMe");
                boolean fromMe = fromMeObj instanceof Boolean && (Boolean) fromMeObj;
                if (fromMe)
                    return;

                System.out.println("📩 New Message from: " + remoteJid);

                // Extract Text
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

                if (text == null)
                    return;
                text = text.trim();

                String currentState = userSessionService.getUserState(remoteJid);

                // Global: Back to Main Menu
                if (text.equals("0")) {
                    userSessionService.setUserState(remoteJid, "WAITING_ROLE");
                    evolutionApiService.sendTextMessage(remoteJid,
                            "🏛️ *Bienvenido al Asistente Virtual del IGSM* 🏛️\n\n" +
                                    "Somos una institución comprometida con la excelencia académica y la formación tecnológica de vanguardia.\n\n"
                                    +
                                    "¿En qué podemos ayudarle hoy?\n\n" +
                                    "1️⃣ *Soy Alumno*: Consultas administrativas, cuotas y notas.\n" +
                                    "2️⃣ *Soy Interesado*: Información sobre nuestras Diplomaturas y cursos.\n\n" +
                                    "0. Volver al inicio");
                    return;
                }

                // 1. Start Conversation
                if (text.equalsIgnoreCase("INFO")) {
                    userSessionService.setUserState(remoteJid, "WAITING_ROLE");
                    evolutionApiService.sendTextMessage(remoteJid,
                            "🏛️ *Bienvenido al Asistente Virtual del IGSM* 🏛️\n\n" +
                                    "Somos una institución comprometida con la excelencia académica y la formación tecnológica de vanguardia.\n\n"
                                    +
                                    "¿En qué podemos ayudarle hoy?\n\n" +
                                    "1️⃣ *Soy Alumno*: Consultas administrativas, cuotas y notas.\n" +
                                    "2️⃣ *Soy Interesado*: Información sobre nuestras Diplomaturas y cursos.\n\n" +
                                    "0. Volver al inicio");
                    return;
                }

                // 2. Handle Role Selection
                if ("WAITING_ROLE".equals(currentState)) {
                    if (text.equals("1")) {
                        userSessionService.setUserState(remoteJid, "WAITING_STUDENT_OPTION");
                        evolutionApiService.sendTextMessage(remoteJid,
                                "🎓 *Menú Alumnos*\n\n" +
                                        "Seleccione una opción:\n" +
                                        "1. Estado Académico\n" +
                                        "2. Valor de Cuotas\n" +
                                        "3. Consultar Deuda\n\n" +
                                        "0. Volver al inicio");
                    } else if (text.equals("2")) {
                        userSessionService.setUserState(remoteJid, "WAITING_GUEST_OPTION");
                        evolutionApiService.sendTextMessage(remoteJid,
                                "🏫 *Oferta Académica IGSM*\n\n" +
                                        "Conozca nuestras diplomaturas:\n" +
                                        "1. Diplomatura en Desarrollo de Software\n" +
                                        "2. Diplomatura en Robótica e IoT\n" +
                                        "3. Diplomatura en Inteligencia Artificial\n\n" +
                                        "Ingrese el número de la opción deseada.\n\n" +
                                        "0. Volver al inicio");
                    } else {
                        evolutionApiService.sendTextMessage(remoteJid,
                                "⚠️ Opción no válida.\nIngrese 1 (Alumno) o 2 (Invitado).\n\n0. Volver al inicio");
                    }
                    return;
                }

                // 3. Handle Student Options
                if ("WAITING_STUDENT_OPTION".equals(currentState)) {
                    String response = "";
                    switch (text) {
                        case "1":
                            response = "📄 *Estado Académico*\n\nUsted es alumno regular.\nÚltima materia aprobada: Programación II (Nota: 9).\nPromedio general: 8.50";
                            break;
                        case "2":
                            response = "💰 *Valor de Cuotas*\n\nLa cuota actual es de $50.000 ARS.\nVencimiento: día 10 de cada mes.\nMedios de pago: Transferencia, MercadoPago, Tarjeta.";
                            break;
                        case "3":
                            response = "✅ *Estado de Cuenta*\n\nUsted no registra deuda actualmente.\n¡Gracias por mantener su cuenta al día!";
                            break;
                        default:
                            evolutionApiService.sendTextMessage(remoteJid,
                                    "⚠️ Opción no válida. Ingrese 1, 2 o 3.\n\n0. Volver al inicio");
                            return;
                    }
                    // Append footer and send
                    evolutionApiService.sendTextMessage(remoteJid, response + "\n\n0. Volver al inicio");
                    // Keep state to allow asking other questions
                    return;
                }

                // 4. Handle Guest Options
                if ("WAITING_GUEST_OPTION".equals(currentState)) {
                    String response = "";
                    switch (text) {
                        case "1":
                            response = "💻 *Diplomatura en Desarrollo de Software*\n\nAprenda Java, Spring Boot, React y bases de datos.\nDuración: 8 meses.\nModalidad: 100% Online (Clases en vivo + Campus).\nSalida laboral: Full Stack Developer.";
                            break;
                        case "2":
                            response = "🤖 *Diplomatura en Robótica e IoT*\n\nDiseñe y construya robots con Arduino y ESP32.\nDuración: 6 meses.\nIncluye: Kit de materiales enviado a domicilio.\nProyectos prácticos desde el primer mes.";
                            break;
                        case "3":
                            response = "🧠 *Diplomatura en Inteligencia Artificial*\n\nDomine Python, Machine Learning y Redes Neuronales.\nDuración: 10 meses.\nRequisito: Conocimientos básicos de programación.\nCertificación avanzada.";
                            break;
                        default:
                            evolutionApiService.sendTextMessage(remoteJid,
                                    "⚠️ Opción no válida. Ingrese 1, 2 o 3.\n\n0. Volver al inicio");
                            return;
                    }
                    // Append footer and send
                    evolutionApiService.sendTextMessage(remoteJid, response + "\n\n0. Volver al inicio");
                    // Keep state
                    return;
                }

            }
        } catch (Exception e) {
            System.err.println("⚠️ Error parsing webhook: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
