package com.igsm.chatbot.controller;

import com.igsm.chatbot.repository.DiplomaturaRepository;
import com.igsm.chatbot.repository.InquiryRepository;
import com.igsm.chatbot.repository.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/stats")
@CrossOrigin(origins = "*")
public class StatsController {

    @Autowired
    private DiplomaturaRepository diplomaturaRepository;

    @Autowired
    private InquiryRepository inquiryRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @GetMapping
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();

        List<Map<String, Object>> inquiries = diplomaturaRepository.findAll().stream().map(d -> {
            Map<String, Object> map = new HashMap<>();
            map.put("name", d.getName());
            map.put("count", inquiryRepository.countByDiplomaturaId(d.getId()));
            return map;
        }).collect(Collectors.toList());

        List<Map<String, Object>> subscriptions = diplomaturaRepository.findAll().stream().map(d -> {
            Map<String, Object> map = new HashMap<>();
            map.put("name", d.getName());
            map.put("count", subscriptionRepository.countByDiplomaturaId(d.getId()));
            return map;
        }).collect(Collectors.toList());

        stats.put("inquiries", inquiries);
        stats.put("subscriptions", subscriptions);

        return stats;
    }

    @GetMapping("/export")
    public org.springframework.http.ResponseEntity<String> exportStats() {
        StringBuilder csv = new StringBuilder();
        csv.append("ID,Nombre,Descripcion,Consultas,Inscripciones\n");

        List<com.igsm.chatbot.model.Diplomatura> diplos = diplomaturaRepository.findAll();
        // Sort by ID
        diplos.sort((d1, d2) -> d1.getId().compareTo(d2.getId()));

        for (com.igsm.chatbot.model.Diplomatura d : diplos) {
            long inquiries = inquiryRepository.countByDiplomaturaId(d.getId());
            long subscriptions = subscriptionRepository.countByDiplomaturaId(d.getId());

            csv.append(d.getId()).append(",")
                    .append("\"").append(d.getName().replace("\"", "\"\"")).append("\",")
                    .append("\"").append(d.getDescription().replace("\"", "\"\"")).append("\",")
                    .append(inquiries).append(",")
                    .append(subscriptions).append("\n");
        }

        return org.springframework.http.ResponseEntity.ok()
                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"diplomaturas_stats.csv\"")
                .header(org.springframework.http.HttpHeaders.CONTENT_TYPE, "text/csv; charset=UTF-8")
                .body(csv.toString());
    }

    @GetMapping("/subscriptions")
    public List<Map<String, Object>> getSubscriptions() {
        return subscriptionRepository.findAll().stream().map(sub -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", sub.getId());
            if (sub.getDiplomatura() != null) {
                map.put("diplomaturaName", sub.getDiplomatura().getName());
                map.put("diplomaturaType", sub.getDiplomatura().getType());
            }
            map.put("userId", sub.getUserId());
            map.put("name", sub.getName());
            map.put("surname", sub.getSurname());
            map.put("dni", sub.getDni());
            map.put("mail", sub.getMail());
            map.put("education", sub.getEducation());
            map.put("phone", sub.getPhone());
            map.put("fileUrl", sub.getFileUrl());
            map.put("mimeType", sub.getMimeType());
            map.put("timestamp", sub.getTimestamp());
            return map;
        }).collect(Collectors.toList());
    }

    @GetMapping("/inquiries")
    public List<Map<String, Object>> getInquiries() {
        return inquiryRepository.findAll().stream()
                .sorted((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()))
                .map(inq -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", inq.getId());
                    map.put("userId", inq.getUserId());
                    map.put("contactPhone", inq.getContactPhone());
                    map.put("timestamp", inq.getTimestamp());
                    if (inq.getDiplomatura() != null) {
                        map.put("diplomaturaName", inq.getDiplomatura().getName());
                        map.put("diplomaturaType", inq.getDiplomatura().getType());
                    }
                    return map;
                }).collect(Collectors.toList());
    }
}
