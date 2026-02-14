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
            List<Recipient> allRecipients;

            if (campaign.getTag() != null) {
                allRecipients = contactProfileRepository.findAll().stream()
                        .filter(p -> p.getTags().contains(campaign.getTag()) && p.getEmail() != null
                                && !p.getEmail().isEmpty())
                        .map(p -> new Recipient(p.getEmail(), p.getNickname()))
                        .collect(Collectors.toList());
            } else if (campaign.getManualRecipients() != null) {
                allRecipients = java.util.Arrays.stream(campaign.getManualRecipients().split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .map(email -> new Recipient(email, "Suscriptor"))
                        .collect(Collectors.toList());
            } else {
                // No target
                campaign.setStatus(CampaignStatus.COMPLETED);
                campaignRepository.save(campaign);
                return;
            }

            int start = campaign.getLastProcessedIndex();
            int end = Math.min(start + campaign.getBatchSize(), allRecipients.size());

            if (start >= allRecipients.size()) {
                campaign.setStatus(CampaignStatus.COMPLETED);
                campaign.setCompletedAt(LocalDateTime.now());
                campaignRepository.save(campaign);
                return;
            }

            List<Recipient> batch = allRecipients.subList(start, end);

            for (Recipient recipient : batch) {
                try {
                    String body = campaign.getTemplate().getBody()
                            .replace("{{NAME}}", recipient.name != null ? recipient.name : "Usuario");

                    emailService.sendEmail(recipient.email, campaign.getTemplate().getSubject(), body);
                    campaign.setSentCount(campaign.getSentCount() + 1);
                } catch (Exception e) {
                    campaign.setErrorCount(campaign.getErrorCount() + 1);
                }
            }

            campaign.setLastProcessedIndex(end);
            campaign.setNextRunTime(LocalDateTime.now().plusSeconds(campaign.getIntervalSeconds()));

            if (end >= allRecipients.size()) {
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

    private static class Recipient {
        String email;
        String name;

        Recipient(String email, String name) {
            this.email = email;
            this.name = name;
        }
    }
}
