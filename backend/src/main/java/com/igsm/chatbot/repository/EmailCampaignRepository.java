package com.igsm.chatbot.repository;

import com.igsm.chatbot.model.EmailCampaign;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmailCampaignRepository extends JpaRepository<EmailCampaign, Long> {
    List<EmailCampaign> findByStatus(EmailCampaign.CampaignStatus status);
}
