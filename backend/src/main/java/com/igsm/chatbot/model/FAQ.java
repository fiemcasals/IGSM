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

    // --- MÉTODOS MANUALES PARA ASEGURAR COMPILACIÓN ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }

    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }
}
