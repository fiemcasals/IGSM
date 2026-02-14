package com.igsm.chatbot.service;

import com.igsm.chatbot.model.ContactProfile;
import com.igsm.chatbot.model.EmailCampaign;
import com.igsm.chatbot.model.EmailCampaign.CampaignStatus;
import com.igsm.chatbot.model.EmailTemplate;
import com.igsm.chatbot.model.Tag;
import com.igsm.chatbot.repository.ContactProfileRepository;
import com.igsm.chatbot.repository.EmailCampaignRepository;
import com.igsm.chatbot.repository.EmailTemplateRepository;
import com.igsm.chatbot.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EmailCampaignService {

    @Autowired
    private EmailCampaignRepository campaignRepository;

    @Autowired
    private EmailTemplateRepository templateRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private ContactProfileRepository contactProfileRepository;

    public EmailCampaign createCampaign(String name, Long tagId, Long templateId, int batchSize, int intervalSeconds) {
        Tag tag = tagRepository.findById(tagId).orElseThrow(() -> new RuntimeException("Tag not found"));
        EmailTemplate template = templateRepository.findById(templateId)
                .orElseThrow(() -> new RuntimeException("Template not found"));

        EmailCampaign campaign = new EmailCampaign();
        campaign.setName(name);
        campaign.setTag(tag);
        campaign.setTemplate(template);
        campaign.setBatchSize(batchSize);
        campaign.setIntervalSeconds(intervalSeconds);
        campaign.setStatus(CampaignStatus.PENDING);

        // Calculate total recipients initially
        long count = contactProfileRepository.findAll().stream()
                .filter(p -> p.getTags().contains(tag) && p.getEmail() != null && !p.getEmail().isEmpty())
                .count();
        campaign.setTotalRecipients((int) count);

        return campaignRepository.save(campaign);
    }

    public EmailCampaign startCampaign(Long id) {
        return updateStatus(id, CampaignStatus.RUNNING);
    }

    public EmailCampaign pauseCampaign(Long id) {
        return updateStatus(id, CampaignStatus.PAUSED);
    }

    public EmailCampaign resumeCampaign(Long id) {
        return updateStatus(id, CampaignStatus.RUNNING);
    }

    private EmailCampaign updateStatus(Long id, CampaignStatus status) {
        EmailCampaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Campaign not found"));
        campaign.setStatus(status);
        if (status == CampaignStatus.RUNNING) {
            campaign.setNextRunTime(LocalDateTime.now());
        }
        return campaignRepository.save(campaign);
    }
}
