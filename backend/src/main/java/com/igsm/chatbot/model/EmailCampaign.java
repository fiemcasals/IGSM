package com.igsm.chatbot.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "email_campaigns")
public class EmailCampaign {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // Helpers user identify campaign

    @Enumerated(EnumType.STRING)
    private CampaignStatus status = CampaignStatus.PENDING;

    // Target Audience
    @ManyToOne
    @JoinColumn(name = "tag_id")
    private Tag tag;

    // Content
    @ManyToOne(cascade = CascadeType.ALL) // Can be a snapshot or reference
    @JoinColumn(name = "template_id")
    private EmailTemplate template;

    // Progress Tracking
    private int totalRecipients = 0;
    private int sentCount = 0;
    private int lastProcessedIndex = 0; // Index in the list of recipients (if sorted)

    // Errors
    private int errorCount = 0;
    @Column(columnDefinition = "TEXT")
    private String lastError;

    // Throttling / Scheduling
    private int batchSize = 10; // Emails per run
    private int intervalSeconds = 60; // Seconds between runs
    private LocalDateTime nextRunTime = LocalDateTime.now();

    @Column(columnDefinition = "TEXT")
    private String manualRecipients; // Comma separated list of emails

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime completedAt;

    public enum CampaignStatus {
        PENDING, RUNNING, PAUSED, COMPLETED, FAILED
    }
}
