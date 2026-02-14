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

    @Autowired
    private com.igsm.chatbot.repository.ContactProfileRepository contactProfileRepository;

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
        // Updated Header: Detailed Lead Info
        csv.append("Fecha,Teléfono,Email,Interés (Carrera),Tipo\n");

        // 1. Get all Inquiries (Interests)
        List<com.igsm.chatbot.model.Inquiry> inquiries = inquiryRepository.findAll();

        // Pre-fetch profiles for performance (or query individually if list is small)
        Map<String, com.igsm.chatbot.model.ContactProfile> profiles = contactProfileRepository.findAll().stream()
                .collect(Collectors.toMap(com.igsm.chatbot.model.ContactProfile::getRemoteJid, p -> p));

        // Format Date
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        for (com.igsm.chatbot.model.Inquiry inq : inquiries) {
            String phone = inq.getContactPhone();
            String email = "";
            String course = inq.getDiplomatura() != null ? inq.getDiplomatura().getName() : "Desconocido";
            String type = inq.getDiplomatura() != null ? inq.getDiplomatura().getType() : "";

            // Try to find email in Profile
            if (profiles.containsKey(inq.getUserId())) {
                String e = profiles.get(inq.getUserId()).getEmail();
                if (e != null)
                    email = e;
            }

            csv.append(inq.getTimestamp().format(formatter)).append(",")
                    .append(phone).append(",")
                    .append(email).append(",")
                    .append("\"").append(course.replace("\"", "\"\"")).append("\",")
                    .append(type).append("\n");
        }

        // Optional: Append Subscriptions as well?
        // User asked "a que oferta academica se anotaron", which could mean Inquiries
        // OR Subscriptions.
        // Let's add Subscriptions too, marked as "INSCRIPCION".

        List<com.igsm.chatbot.model.Subscription> subs = subscriptionRepository.findAll();
        for (com.igsm.chatbot.model.Subscription s : subs) {
            String phone = s.getPhone();
            // Subscription has its own mail field, but fallback to profile if empty
            String email = s.getMail();
            if (email == null || email.isEmpty()) {
                if (profiles.containsKey(s.getUserId())) {
                    String e = profiles.get(s.getUserId()).getEmail();
                    if (e != null)
                        email = e;
                }
            }
            String course = s.getDiplomatura() != null ? s.getDiplomatura().getName() : "Desconocido";

            csv.append(s.getTimestamp().format(formatter)).append(",")
                    .append(phone).append(",")
                    .append(email != null ? email : "").append(",")
                    .append("\"").append(course.replace("\"", "\"\"")).append("\",")
                    .append("INSCRIPCION_CONFIRMADA").append("\n");
        }

        return org.springframework.http.ResponseEntity.ok()
                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"reporte_interesados.csv\"")
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
