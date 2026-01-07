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
        Consultation consultation = consultationRepository.findById(id).orElseThrow();
        String message = payload.get("message");

        if (message != null && !message.isEmpty()) {
            evolutionApiService.sendTextMessage(consultation.getUserId(), message);
            consultation.setReplied(true);
            consultationRepository.save(consultation);
        }
    }
}
