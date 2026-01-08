package com.igsm.chatbot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(EvolutionApiService.class);

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

            // Extract number from remoteJid (remove @s.whatsapp.net or @lid, and device ID
            // like :1)
            String number = remoteJid.split("@")[0].split(":")[0];

            logger.info("📤 Sending message to {}: {}", number, text);

            Map<String, Object> body = new HashMap<>();
            body.put("number", number);
            body.put("text", text);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("apikey", apiKey);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            restTemplate.postForEntity(url, request, String.class);
            logger.info("✅ Message sent successfully to {}", number);

        } catch (Exception e) {
            logger.error("❌ Error sending message to {}: {}", remoteJid, e.getMessage(), e);
        }
    }

    public void sendTextWithQuote(String remoteJid, String text, String quotedMessageId) {
        try {
            String url = apiUrl + "/message/sendText/" + instance;

            // Extract number from remoteJid
            String number = remoteJid.split("@")[0].split(":")[0];

            logger.info("📤 Sending quoted message to {}: {}", number, text);

            Map<String, Object> body = new HashMap<>();
            body.put("number", number);
            body.put("text", text);

            if (quotedMessageId != null && !quotedMessageId.isEmpty()) {
                Map<String, Object> quoted = new HashMap<>();
                quoted.put("key", Map.of("id", quotedMessageId));
                body.put("quoted", quoted);
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("apikey", apiKey);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            restTemplate.postForEntity(url, request, String.class);
            logger.info("✅ Quoted message sent successfully to {}", number);

        } catch (Exception e) {
            logger.error("❌ Error sending quoted message to {}: {}", remoteJid, e.getMessage(), e);
        }
    }
}
