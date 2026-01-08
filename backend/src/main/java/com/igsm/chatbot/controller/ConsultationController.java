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

        if (message != null && !message.isEmpty()) {
            // 1. Send message via Evolution API with quote
            String fullMessage = message
                    + "\n\n📝 *Para continuar esta conversación, simplemente responda a este mensaje.*";
            evolutionApiService.sendTextWithQuote(originalConsultation.getUserId(), fullMessage,
                    originalConsultation.getMessageId());

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
            consultationRepository.save(originalConsultation);
        }
    }
}
