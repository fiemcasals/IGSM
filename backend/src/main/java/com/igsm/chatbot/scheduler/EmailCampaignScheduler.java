package com.igsm.chatbot.scheduler;

import com.igsm.chatbot.model.ContactProfile;
import com.igsm.chatbot.model.EmailCampaign;
import com.igsm.chatbot.model.EmailCampaign.CampaignStatus;
import com.igsm.chatbot.repository.ContactProfileRepository;
import com.igsm.chatbot.repository.EmailCampaignRepository;
import com.igsm.chatbot.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class EmailCampaignScheduler {

    @Autowired
    private EmailCampaignRepository campaignRepository;

    @Autowired
    private ContactProfileRepository contactProfileRepository;

    @Autowired
    private EmailService emailService;

    // Run every minute
    @Scheduled(cron = "0 * * * * *")
    public void processCampaigns() {
        List<EmailCampaign> runningCampaigns = campaignRepository.findByStatus(CampaignStatus.RUNNING);

        for (EmailCampaign campaign : runningCampaigns) {
            if (campaign.getNextRunTime() != null && campaign.getNextRunTime().isBefore(LocalDateTime.now())) {
                processBatch(campaign);
            }
        }
    }

    private void processBatch(EmailCampaign campaign) {
        try {
            // Get all recipients for this tag
            // Note: In a real large-scale app, we would paginate DB query.
            // Here we verify list consistency by re-fetching.
            List<ContactProfile> allProfiles = contactProfileRepository.findAll().stream()
                    .filter(p -> p.getTags().contains(campaign.getTag()) && p.getEmail() != null
                            && !p.getEmail().isEmpty())
                    .collect(Collectors.toList());

            int start = campaign.getLastProcessedIndex();
            int end = Math.min(start + campaign.getBatchSize(), allProfiles.size());

            if (start >= allProfiles.size()) {
                campaign.setStatus(CampaignStatus.COMPLETED);
                campaign.setCompletedAt(LocalDateTime.now());
                campaignRepository.save(campaign);
                return;
            }

            List<ContactProfile> batch = allProfiles.subList(start, end);

            for (ContactProfile profile : batch) {
                try {
                    String body = campaign.getTemplate().getBody()
                            .replace("{{NAME}}", profile.getNickname() != null ? profile.getNickname() : "Usuario");

                    emailService.sendEmail(profile.getEmail(), campaign.getTemplate().getSubject(), body);
                    campaign.setSentCount(campaign.getSentCount() + 1);
                } catch (Exception e) {
                    campaign.setErrorCount(campaign.getErrorCount() + 1);
                    campaign.setLastError(e.getMessage());
                }
            }

            campaign.setLastProcessedIndex(end);
            campaign.setNextRunTime(LocalDateTime.now().plusSeconds(campaign.getIntervalSeconds()));

            if (end >= allProfiles.size()) {
                campaign.setStatus(CampaignStatus.COMPLETED);
                campaign.setCompletedAt(LocalDateTime.now());
            }

            campaignRepository.save(campaign);

        } catch (Exception e) {
            campaign.setStatus(CampaignStatus.FAILED);
            campaign.setLastError(e.getMessage());
            campaignRepository.save(campaign);
        }
    }
}
