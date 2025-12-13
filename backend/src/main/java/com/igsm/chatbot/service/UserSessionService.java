package com.igsm.chatbot.service;

import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserSessionService {

    // Map<RemoteJid, CurrentState>
    private final Map<String, String> userStates = new ConcurrentHashMap<>();

    public String getUserState(String remoteJid) {
        return userStates.getOrDefault(remoteJid, "NONE");
    }

    public void setUserState(String remoteJid, String state) {
        userStates.put(remoteJid, state);
    }

    public void clearUserState(String remoteJid) {
        userStates.remove(remoteJid);
    }
}
