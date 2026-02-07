package com.igsm.chatbot.controller;

import com.igsm.chatbot.model.Consultation;
import com.igsm.chatbot.repository.ConsultationRepository;
import com.igsm.chatbot.service.EvolutionApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/consultations")
public class ConsultationController {

    @Autowired
    private ConsultationRepository consultationRepository;

    @Autowired
    private EvolutionApiService evolutionApiService;

    @GetMapping
    public List<Consultation> getAllConsultations() {
        return consultationRepository.findAllByOrderByTimestampDesc();
    }

    @PutMapping("/{id}/seen")
    public Consultation toggleSeen(@PathVariable Long id) {
        Consultation consultation = consultationRepository.findById(id).orElseThrow();
        consultation.setSeen(!consultation.isSeen());
        return consultationRepository.save(consultation);
    }

    @PostMapping("/{id}/reply")
    public void replyToConsultation(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        Consultation originalConsultation = consultationRepository.findById(id).orElseThrow();
        String message = payload.get("message");
        String quoteMessageId = payload.get("quoteMessageId");

        if (message != null && !message.isEmpty()) {
            // 1. Send message via Evolution API
            if (quoteMessageId != null && !quoteMessageId.isEmpty()) {
                // Specific reply with quote
                // Use a cleaner footer or none at all if requested, user asked to remove footer
                // for general replies
                // For specific replies, we might still want to just send the text.
                evolutionApiService.sendTextWithQuote(originalConsultation.getUserId(), message, quoteMessageId);
            } else {
                // General reply (no quote, no footer)
                evolutionApiService.sendTextMessage(originalConsultation.getUserId(), message);
            }

            // 2. Save Admin Reply as a new Consultation record
            Consultation adminReply = new Consultation();
            adminReply.setUserId(originalConsultation.getUserId());
            adminReply.setContactPhone(originalConsultation.getContactPhone()); // Keep same contact info
            adminReply.setMessage(message); // Store just the reply text
            adminReply.setAdminReply(true);
            adminReply.setSeen(true); // Admin messages are seen by definition
            adminReply.setReplied(true);
            consultationRepository.save(adminReply);

            // 3. Mark original as replied
            originalConsultation.setReplied(true);
            originalConsultation.setSeen(true); // Also mark as seen if replying
            consultationRepository.save(originalConsultation);
        }
    }

    @PutMapping("/user/{userId}/seen")
    public void markAllAsSeen(@PathVariable String userId) {
        List<Consultation> userConsultations = consultationRepository.findByUserId(userId);
        for (Consultation c : userConsultations) {
            if (!c.isSeen()) {
                c.setSeen(true);
                consultationRepository.save(c);
            }
        }
    }

    @DeleteMapping("/user/{userId}")
    public void deleteConsultationByUser(@PathVariable String userId) {
        List<Consultation> userConsultations = consultationRepository.findByUserId(userId);
        consultationRepository.deleteAll(userConsultations);
    }

    @PutMapping("/user/{userId}/unseen")
    public void markLastAsUnseen(@PathVariable String userId) {
        // Logica: Buscar el ultimo mensaje del usuario (no admin) y marcarlo como
        // seen=false
        List<Consultation> userConsultations = consultationRepository.findByUserId(userId);
        // Sort descending
        userConsultations.sort((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()));

        for (Consultation c : userConsultations) {
            if (!c.isAdminReply()) {
                c.setSeen(false);
                consultationRepository.save(c);
                break; // Only mark the latest one
            }
        }
    }
}
