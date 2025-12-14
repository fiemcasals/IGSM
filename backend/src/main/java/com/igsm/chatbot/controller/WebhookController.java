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
        try {
            String eventType = (String) payload.get("event");
            System.out.println("🔔 Webhook received. Event: " + eventType);
            if ("messages.upsert".equals(eventType)) {
                Map<String, Object> data = (Map<String, Object>) payload.get("data");
                Map<String, Object> key = (Map<String, Object>) data.get("key");
                String remoteJid = (String) key.get("remoteJid");

                Object fromMeObj = key.get("fromMe");
                boolean fromMe = fromMeObj instanceof Boolean && (Boolean) fromMeObj;
                if (fromMe)
                    return;

                System.out.println("📩 New Message from: " + remoteJid);
                System.out.println("   FromMe: " + fromMe);

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
                    System.out.println("⚠️ Text is null, ignoring.");
                    return;
                }
                text = text.trim();
                System.out.println("   Text: " + text);

                // Global Exit
                if (text.equalsIgnoreCase("GRACIAS") || text.equals("9")) {
                    userSessionService.clearUserState(remoteJid);
                    evolutionApiService.sendTextMessage(remoteJid,
                            "👋 ¡Hasta luego! Gracias por contactarte con el IGSM.");
                    return;
                }

                String currentState = userSessionService.getUserState(remoteJid);
                System.out.println("   Current State: " + currentState);

                // Global Start / Reset
                // Global Start / Reset
                if (text.equalsIgnoreCase("INFO") || text.equals("0")) {
                    System.out.println("   Matched INFO or 0. Showing Main Menu.");
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
                        userSessionService.setUserState(remoteJid, "WAITING_PRE_REG_NAME");
                        evolutionApiService.sendTextMessage(remoteJid,
                                "📝 *Pre-inscripción*\n\nPor favor, ingrese su *Nombre*:");
                    } else if (text.equals("2")) {
                        // Volver al menu
                        showMainMenu(remoteJid);
                    } else if (text.equals("3")) {
                        // Finalizar
                        userSessionService.clearUserState(remoteJid);
                        evolutionApiService.sendTextMessage(remoteJid,
                                "👋 ¡Hasta luego! Gracias por contactarte con el IGSM.");
                    } else {
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

                    // Finalize Pre-registration
                    String diplo = userSessionService.getSessionData(remoteJid, "current_diplo");
                    String name = userSessionService.getSessionData(remoteJid, "name");
                    String surname = userSessionService.getSessionData(remoteJid, "surname");
                    String dni = userSessionService.getSessionData(remoteJid, "dni");
                    String mail = userSessionService.getSessionData(remoteJid, "mail");
                    String edu = userSessionService.getSessionData(remoteJid, "education");
                    String phone = userSessionService.getSessionData(remoteJid, "phone");

                    System.out.println("✅ NEW PRE-REGISTRATION:");
                    System.out.println("Diplo: " + diplo);
                    System.out.println("Name: " + name);
                    System.out.println("Surname: " + surname);
                    System.out.println("DNI: " + dni);
                    System.out.println("Mail: " + mail);
                    System.out.println("Edu: " + edu);
                    System.out.println("Phone: " + phone);

                    userSessionService.setUserState(remoteJid, "WAITING_FINAL_DECISION");
                    evolutionApiService.sendTextMessage(remoteJid,
                            "✅ *¡Datos registrados correctamente!*\n\n" +
                                    "Hemos recibido su pre-inscripción para la *" + diplo + "*.\n" +
                                    "Nos pondremos en contacto con usted a la brevedad.\n\n" +
                                    "1. Volver al Menú Principal\n" +
                                    "2. Finalizar");
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

                // Default / Fallback (if no state or unknown input, treat as new session
                // request or show help)
                // For now, if no state, assume they want to start
                if (currentState == null || currentState.isEmpty() || "NONE".equals(currentState)) {
                    System.out.println("   No active state. Defaulting to Main Menu.");
                    showMainMenu(remoteJid);
                }

            }
        } catch (Exception e) {
            System.err.println("⚠️ Error parsing webhook: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showMainMenu(String remoteJid) {
        userSessionService.setUserState(remoteJid, "WAITING_DIPLO_SELECTION");
        String menu = "🏛️ *Bienvenido al Asistente Virtual del IGSM* 🏛️\n\n" +
                "Por favor, seleccione el número de la diplomatura de su interés:\n\n" +
                "1. DESARROLLO WEB\n" +
                "2. ENERGÍAS RENOVABLES\n" +
                "3. MOLDES Y MATRICES\n" +
                "4. HIDROCARBUROS\n" +
                "5. DISEÑO E IMPRESIÓN 3D\n" +
                "6. BROMATOLOGÍA\n" +
                "7. AGRICULTURA DE PRECISIÓN\n" +
                "8. TECNOLOGÍA AGROPECUARIA\n" +
                "9. DESARROLLO DE SOFTWARE\n" +
                "10. ROBÓTICA\n" +
                "11. MEDIO AMBIENTE";
        evolutionApiService.sendTextMessage(remoteJid, menu);
    }

    private void handleDiploSelection(String remoteJid, String text) {
        String response = "";
        String diploName = "";

        switch (text) {
            case "1":
                diploName = "Diplomatura en Desarrollo Web";
                response = "🌐 *DESARROLLO WEB*\n\n" +
                        "🎯 *Objetivo:* Formar especialistas en soluciones web con tecnologías de vanguardia.\n" +
                        "📋 *Requisitos:* Título/certificación nivel secundario. Manejo básico de Windows e Internet. Conexión Wi-Fi, PC/tablet/smartphone (no > 5 años).\n"
                        +
                        "⏱️ *Duración:* 280 horas reloj (aprox. 7 meses).\n" +
                        "📚 *Contenidos Clave:* HTML, CSS, JavaScript, React, Base de Datos (SQL/NoSQL), Node.js/MongoDB, Diseño UX/UI, Proyecto Final.";
                break;
            case "2":
                diploName = "Diplomatura en Energías Renovables";
                response = "☀️ *ENERGÍAS RENOVABLES*\n\n" +
                        "🎯 *Objetivo:* Formación científico-tecnológica para la inserción laboral en el sector de energías renovables.\n"
                        +
                        "📋 *Requisitos:* Título/certificación nivel secundario.\n" +
                        "⏱️ *Duración:* 128 horas (Módulos principales).\n" +
                        "📚 *Contenidos Clave:* Fundamentos (Sistemas Eléctricos), Biomasa y Biocombustibles, Tecnología Solar Fotovoltaica y Térmica, Energía Eólica, Gestión de la Energía.";
                break;
            case "3":
                diploName = "Diplomatura en Moldes y Matrices";
                response = "⚙️ *MOLDES Y MATRICES*\n\n" +
                        "🎯 *Objetivo:* Construir, desarrollar y evaluar moldes, matrices y dispositivos para la industria metalmecánica.\n"
                        +
                        "📋 *Requisitos:* Título/certificación nivel secundario.\n" +
                        "⏱️ *Duración:* 6 módulos (48 horas c/u).\n" +
                        "📚 *Contenidos Clave:* Dibujo Técnico (CAD), Moldes de Inyección y Soplado, Diseño de Matrices, Tratamientos Térmicos, Metrología.";
                break;
            case "4":
                diploName = "Diplomatura en Hidrocarburos";
                response = "🛢️ *HIDROCARBUROS*\n\n" +
                        "🎯 *Objetivo:* Introducción integral al sector, desde exploración hasta producción, aspectos técnicos y ambientales.\n"
                        +
                        "📋 *Requisitos:* Título/certificación nivel secundario.\n" +
                        "⏱️ *Duración:* 5 módulos (48 horas c/u).\n" +
                        "📚 *Contenidos Clave:* Geología del Petróleo, Perforación, Producción y Transporte, Refinación y Petroquímica, Seguridad y Gestión Ambiental.";
                break;
            case "5":
                diploName = "Diplomatura en Diseño e Impresión 3D";
                response = "🖨️ *DISEÑO E IMPRESIÓN 3D*\n\n" +
                        "🎯 *Objetivo:* Modelado 3D de componentes y conjuntos utilizando software profesional.\n" +
                        "📋 *Requisitos:* Título/certificación nivel secundario. Conocimiento básico de dibujo técnico.\n"
                        +
                        "⏱️ *Duración:* 6 módulos (48 horas c/u).\n" +
                        "📚 *Contenidos Clave:* Modelado con Solid Edge v20, Diseño de piezas, Dibujo 2D, Tecnologías de Impresión 3D (FDM, SLA), Slicing.";
                break;
            case "6":
                diploName = "Diplomatura en Bromatología";
                response = "🍎 *BROMATOLOGÍA*\n\n" +
                        "🎯 *Objetivo:* Garantizar seguridad, calidad e inocuidad en la industria alimentaria.\n" +
                        "📋 *Requisitos:* Título/certificación nivel secundario.\n" +
                        "⏱️ *Duración:* 6 módulos (48 horas c/u).\n" +
                        "📚 *Contenidos Clave:* Química de Alimentos, Microbiología, Bromatología y Nutrición, Controles de Calidad, Normativas (HACCP, ISO 22000).";
                break;
            case "7":
                diploName = "Diplomatura en Agricultura de Precisión";
                response = "🛰️ *AGRICULTURA DE PRECISIÓN*\n\n" +
                        "🎯 *Objetivo:* Aplicar tecnologías avanzadas (TIC) para optimizar recursos y productividad agropecuaria.\n"
                        +
                        "📋 *Requisitos:* Título/certificación nivel secundario.\n" +
                        "⏱️ *Duración:* 290 horas reloj (aprox. 7 meses).\n" +
                        "📚 *Contenidos Clave:* SIG, Sensores Remotos (drones), Big Data, Trazabilidad, Monitoreo de cultivos, Maquinaria de Dosis Variable.";
                break;
            case "8":
                diploName = "Diplomatura en Tecnología Agropecuaria";
                response = "🚜 *TECNOLOGÍA AGROPECUARIA*\n\n" +
                        "🎯 *Objetivo:* Aplicar robótica y automatización para mejorar productividad y sostenibilidad agrícola.\n"
                        +
                        "📋 *Requisitos:* Título/certificación nivel secundario.\n" +
                        "⏱️ *Duración:* 5 módulos (48 horas c/u).\n" +
                        "📚 *Contenidos Clave:* Robótica Agrícola, Sistemas de Control, Agricultura de Precisión, Electrónica e Instrumentación, IoT en el Agro.";
                break;
            case "9":
                diploName = "Diplomatura en Desarrollo de Software";
                response = "💻 *DESARROLLO DE SOFTWARE*\n\n" +
                        "🎯 *Objetivo:* Formación práctica en desarrollo de software, estructuras de datos y algoritmos.\n"
                        +
                        "📋 *Requisitos:* Título/certificación nivel secundario.\n" +
                        "⏱️ *Duración:* 384 horas (8 módulos de 48hs).\n" +
                        "📚 *Contenidos Clave:* POO, Estructuras de Datos, Lenguajes (Java, Python), SQL, Sistemas Operativos y Redes.";
                break;
            case "10":
                diploName = "Diplomatura en Robótica";
                response = "🤖 *ROBÓTICA*\n\n" +
                        "🎯 *Objetivo:* Diseño, programación e implementación de sistemas robóticos y automatización.\n"
                        +
                        "📋 *Requisitos:* Título/certificación nivel secundario.\n" +
                        "⏱️ *Duración:* 6 módulos (48 horas c/u).\n" +
                        "📚 *Contenidos Clave:* Robótica y Mecánica, Programación (C++, Python, ROS), Electrónica y Sensores, Diseño y Simulación (CAD), Automatización.";
                break;
            case "11":
                diploName = "Diplomatura en Medio Ambiente";
                response = "🌍 *MEDIO AMBIENTE*\n\n" +
                        "🎯 *Objetivo:* Detectar y diagnosticar problemas ambientales, preservación sustentable de recursos.\n"
                        +
                        "📋 *Requisitos:* Secundario completo.\n" +
                        "⏱️ *Duración:* 10 meses / 304 horas reloj.\n" +
                        "📚 *Contenidos Clave:* Ciencias de la Tierra, EIA, Normativa Ambiental, Sistemas de Gestión Ambiental, Energía y Medio Ambiente.";
                break;
            default:
                evolutionApiService.sendTextMessage(remoteJid,
                        "⚠️ Opción no válida. Por favor, ingrese un número del 1 al 11.");
                return;
        }

        userSessionService.putSessionData(remoteJid, "current_diplo", diploName);
        userSessionService.setUserState(remoteJid, "WAITING_POST_DIPLO_ACTION");
        evolutionApiService.sendTextMessage(remoteJid,
                response + "\n\n1. Pre-inscribirse\n2. Volver al Menú Principal\n3. Finalizar conversación");
    }
}
