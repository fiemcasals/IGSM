package com.igsm.chatbot.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "faqs")
public class FAQ {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 1000)
    private String question;

    @Column(length = 5000)
    private String answer;
}
