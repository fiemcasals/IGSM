package com.igsm.chatbot.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "tags")
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    // Color/Visuals for frontend?
    private String color = "blue"; // Default color

    public Tag() {
    }

    public Tag(String name) {
        this.name = name;
    }
}
