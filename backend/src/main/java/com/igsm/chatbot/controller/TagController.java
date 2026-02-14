package com.igsm.chatbot.controller;

import com.igsm.chatbot.model.ContactProfile;
import com.igsm.chatbot.model.Tag;
import com.igsm.chatbot.repository.ContactProfileRepository;
import com.igsm.chatbot.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tags")
@CrossOrigin(origins = "*")
public class TagController {

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private ContactProfileRepository contactProfileRepository;

    @Autowired
    private com.igsm.chatbot.service.EmailService emailService;

    // List all tags
    @GetMapping
    public List<Tag> getAllTags() {
        return tagRepository.findAll();
    }

    // Create a new tag
    @PostMapping
    public ResponseEntity<Tag> createTag(@RequestBody Map<String, String> payload) {
        String name = payload.get("name");
        String color = payload.getOrDefault("color", "blue");

        if (name == null || name.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        // Check duplicate
        if (tagRepository.existsByName(name)) {
            return ResponseEntity.badRequest().body(tagRepository.findByName(name).get());
        }

        Tag tag = new Tag(name.trim());
        tag.setColor(color);
        return ResponseEntity.ok(tagRepository.save(tag));
    }

    // Delete tag (be careful with relationships)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTag(@PathVariable Long id) {
        if (!tagRepository.existsById(id))
            return ResponseEntity.notFound().build();
        // JPA should handle join table clean up automatically if configured,
        // but explicit check might be safer.
        // For simple ManyToMany, deleting the Tag usually removes rows in join table.
        tagRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // Assign tag to profile
    @PostMapping("/assign")
    public ResponseEntity<?> assignTagToProfile(@RequestBody Map<String, Object> payload) {
        String remoteJid = (String) payload.get("remoteJid");
        Long tagId = ((Number) payload.get("tagId")).longValue();

        return contactProfileRepository.findById(remoteJid).map(profile -> {
            return tagRepository.findById(tagId).map(tag -> {
                if (!profile.getTags().contains(tag)) {
                    profile.getTags().add(tag);
                    contactProfileRepository.save(profile);
                }
                return ResponseEntity.ok(profile);
            }).orElse(ResponseEntity.notFound().build());
        }).orElse(ResponseEntity.notFound().build());
    }

    // Remove tag from profile
    @PostMapping("/remove")
    public ResponseEntity<?> removeTagFromProfile(@RequestBody Map<String, Object> payload) {
        String remoteJid = (String) payload.get("remoteJid");
        Long tagId = ((Number) payload.get("tagId")).longValue();

        return contactProfileRepository.findById(remoteJid).map(profile -> {
            boolean removed = profile.getTags().removeIf(t -> t.getId().equals(tagId));
            if (removed) {
                contactProfileRepository.save(profile);
            }
            return ResponseEntity.ok(profile);
        }).orElse(ResponseEntity.notFound().build());
    }

    // Bulk Email to Tag
    @PostMapping("/{tagId}/email")
    public ResponseEntity<?> sendEmailToTag(@PathVariable Long tagId, @RequestBody Map<String, String> payload) {
        String subject = payload.get("subject");
        String body = payload.get("body");

        if (subject == null || body == null) {
            return ResponseEntity.badRequest().body("Subject and Body are required");
        }

        return tagRepository.findById(tagId).map(tag -> {
            // Find all profiles with this tag
            List<ContactProfile> profiles = contactProfileRepository.findAll(); // Optimization: Use custom query
                                                                                // findByTagsId
            List<String> emails = profiles.stream()
                    .filter(p -> p.getTags().contains(tag)) // Filter in memory for now or update Repo
                    .map(ContactProfile::getEmail)
                    .filter(email -> email != null && !email.isEmpty())
                    .collect(java.util.stream.Collectors.toList());

            if (emails.isEmpty()) {
                return ResponseEntity.ok("No recipients found for this tag.");
            }

            emailService.sendBulkEmail(emails, subject, body);

            return ResponseEntity.ok("Sending emails to " + emails.size() + " recipients.");
        }).orElse(ResponseEntity.notFound().build());
    }
}
