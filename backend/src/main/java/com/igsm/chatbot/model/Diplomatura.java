package com.igsm.chatbot.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Data
@Table(name = "diplomaturas")
public class Diplomatura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String type; // DIPLOMATURA, TECNICATURA, LICENCIATURA

    @Column(length = 2000)
    private String description;

    @Column(length = 5000)
    private String content; 

    // Campos faltantes que el controlador está pidiendo:
    private String duration;
    private String price;

    @OneToMany(mappedBy = "diplomatura", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Subscription> subscriptions;

    @OneToMany(mappedBy = "diplomatura", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Inquiry> inquiries;

    // --- MÉTODOS MANUALES PARA ASEGURAR COMPILACIÓN ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }

    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }
}
