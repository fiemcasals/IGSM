package com.igsm.chatbot.controller;

import com.igsm.chatbot.model.EmailCampaign;
import com.igsm.chatbot.repository.EmailCampaignRepository;
import com.igsm.chatbot.service.EmailCampaignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/campaigns")
@CrossOrigin(origins = "*")
public class EmailCampaignController {

    @Autowired
    private EmailCampaignRepository campaignRepository;

    @Autowired
    private EmailCampaignService campaignService;

    @GetMapping
    public List<EmailCampaign> getAll() {
        return campaignRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> createAndStart(@RequestBody Map<String, Object> payload) {
        try {
            String name = (String) payload.get("name");
            Long tagId = Long.parseLong(payload.get("tagId").toString());
            Long templateId = Long.parseLong(payload.get("templateId").toString());
            int batchSize = Integer.parseInt(payload.getOrDefault("batchSize", 10).toString());
            int interval = Integer.parseInt(payload.getOrDefault("intervalSeconds", 60).toString());

            EmailCampaign campaign = campaignService.createCampaign(name, tagId, templateId, batchSize, interval);
            campaignService.startCampaign(campaign.getId());
            return ResponseEntity.ok(campaign);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/pause")
    public ResponseEntity<?> pause(@PathVariable Long id) {
        return ResponseEntity.ok(campaignService.pauseCampaign(id));
    }

    @PostMapping("/{id}/resume")
    public ResponseEntity<?> resume(@PathVariable Long id) {
        return ResponseEntity.ok(campaignService.resumeCampaign(id));
    }
}
