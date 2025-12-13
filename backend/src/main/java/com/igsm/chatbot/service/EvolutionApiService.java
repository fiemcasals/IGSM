package com.igsm.chatbot.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.util.Map;
import java.util.HashMap;

@Service
public class EvolutionApiService {

    @Value("${evolution.api.url}")
    private String apiUrl;

    @Value("${evolution.api.instance}")
    private String instance;

    @Value("${evolution.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public void sendTextMessage(String remoteJid, String text) {
        try {
            String url = apiUrl + "/message/sendText/" + instance;

            // Extract number from remoteJid (remove @s.whatsapp.net or @lid)
            String number = remoteJid.split("@")[0];

            Map<String, Object> body = new HashMap<>();
            body.put("number", number);
            body.put("text", text);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("apikey", apiKey);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            restTemplate.postForEntity(url, request, String.class);
            System.out.println("📤 Reply sent to " + number);

        } catch (Exception e) {
            System.err.println("❌ Error sending message: " + e.getMessage());
        }
    }
}
