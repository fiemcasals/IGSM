package com.igsm.chatbot.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "consultations")
public class Consultation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId; // remoteJid
    private String contactPhone;

    @Column(length = 5000)
    private String message;

    private boolean seen = false;
    private boolean replied = false;

    private String messageId;
    private boolean adminReply = false;

    private LocalDateTime timestamp;

    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now();
    }

    // --- MÉTODOS MANUALES PARA ASEGURAR COMPILACIÓN ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getMessageId() { return messageId; }
    public void setMessageId(String messageId) { this.messageId = messageId; }

    public boolean isSeen() { return seen; }
    public void setSeen(boolean seen) { this.seen = seen; }

    public boolean isReplied() { return replied; }
    public void setReplied(boolean replied) { this.replied = replied; }

    public boolean isAdminReply() { return adminReply; }
    public void setAdminReply(boolean adminReply) { this.adminReply = adminReply; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
