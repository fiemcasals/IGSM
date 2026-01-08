package com.igsm.chatbot.service;

import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserSessionService {

    private final Map<String, String> userStates = new ConcurrentHashMap<>();
    private final Map<String, Map<String, String>> userSessionData = new ConcurrentHashMap<>();

    public String getUserState(String remoteJid) {
        return userStates.getOrDefault(remoteJid, "NONE");
    }

    public void setUserState(String remoteJid, String state) {
        userStates.put(remoteJid, state);
        // Cada vez que cambia el estado, registramos la hora de actividad
        putSessionData(remoteJid, "last_activity", String.valueOf(System.currentTimeMillis()));
    }

    // --- NUEVO MÉTODO PARA EL CLEANUP SERVICE ---
    public Map<String, String> getAllActiveStates() {
        return new HashMap<>(userStates);
    }

    public void clearUserState(String remoteJid) {
        userStates.remove(remoteJid);
        userSessionData.remove(remoteJid);
    }

    public void putSessionData(String remoteJid, String key, String value) {
        userSessionData.computeIfAbsent(remoteJid, k -> new ConcurrentHashMap<>()).put(key, value);
    }

    public String getSessionData(String remoteJid, String key) {
        return userSessionData.getOrDefault(remoteJid, new ConcurrentHashMap<>()).get(key);
    }

    public void removeSessionData(String remoteJid, String key) {
        if (userSessionData.containsKey(remoteJid)) {
            userSessionData.get(remoteJid).remove(key);
        }
    }
}
