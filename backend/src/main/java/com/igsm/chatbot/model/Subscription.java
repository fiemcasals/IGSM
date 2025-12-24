package com.igsm.chatbot.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "subscriptions")
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "diplomatura_id")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Diplomatura diplomatura;

    private String userId; // remoteJid

    private String name;
    private String surname;
    private String dni;
    private String mail;
    private String education;
    private String phone;

    @Column(length = 2000)
    private String fileUrl;

    private String mimeType;

    private LocalDateTime timestamp;

    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now();
    }
}
