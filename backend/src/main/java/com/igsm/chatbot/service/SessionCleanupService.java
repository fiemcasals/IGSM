package com.igsm.chatbot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
@EnableScheduling
public class SessionCleanupService {

    @Autowired
    private UserSessionService userSessionService;

    @Autowired
    private EvolutionApiService evolutionApiService;

    // Se ejecuta cada 2 minutos para revisar sesiones inactivas
    @Scheduled(fixedRate = 120000) 
    public void checkInactivity() {
        // Obtenemos todos los JIDs que tienen una sesión activa
        // Nota: Necesitarás un método en tu SessionService que devuelva los JIDs activos
        Map<String, String> activeSessions = userSessionService.getAllActiveStates(); 

        long now = System.currentTimeMillis();
        long timeout = 5 * 60 * 1000; // 5 minutos de gracia

        activeSessions.forEach((jid, state) -> {
            if ("WAITING_MESSAGE_BODY".equals(state)) {
                String lastActivityStr = userSessionService.getSessionData(jid, "last_activity");
                if (lastActivityStr != null) {
                    long lastActivity = Long.parseLong(lastActivityStr);
                    if ((now - lastActivity) > timeout) {
                        // Enviar saludo de despedida atento
                        evolutionApiService.sendTextMessage(jid, 
                            "Hacemos una pausa por aquí para no interrumpirte. 😊\n\n" +
                            "Cualquier otra consulta que tengas, solo volvé a escribir y estaré listo para ayudarte. ¡Quedamos atentos!");
                        
                        // Limpiar sesión para que el próximo mensaje active el menú principal
                        userSessionService.clearUserState(jid);
                    }
                }
            }
        });
    }
}
