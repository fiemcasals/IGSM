package com.igsm.chatbot.controller;

import com.igsm.chatbot.model.EmailTemplate;
import com.igsm.chatbot.repository.EmailTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/email-templates")
@CrossOrigin(origins = "*")
public class EmailTemplateController {

    @Autowired
    private EmailTemplateRepository templateRepository;

    @GetMapping
    public List<EmailTemplate> getAll() {
        return templateRepository.findAll();
    }

    @PostMapping
    public EmailTemplate create(@RequestBody EmailTemplate template) {
        return templateRepository.save(template);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmailTemplate> update(@PathVariable Long id, @RequestBody EmailTemplate details) {
        return templateRepository.findById(id).map(t -> {
            t.setName(details.getName());
            t.setSubject(details.getSubject());
            t.setBody(details.getBody());
            return ResponseEntity.ok(templateRepository.save(t));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (!templateRepository.existsById(id))
            return ResponseEntity.notFound().build();
        templateRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
