//Diplomatura.java fue creado para representar una diplomatura en la base de datos

package com.igsm.chatbot.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity // estas lineas indica que esta clase es una entidad, es decir, que representa
        // una tabla en la base de datos
@Data
@Table(name = "diplomaturas") // esta linea indica que esta
public class Diplomatura {

    @Id // esta linea indica que esta variable es la primary key de la tabla
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String type; // DIPLOMATURA, TECNICATURA, LICENCIATURA

    @Column(length = 2000)
    private String description;

    @Column(length = 5000)
    private String content; // The full response text for the bot

    @OneToMany(mappedBy = "diplomatura", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<Subscription> subscriptions;

    @OneToMany(mappedBy = "diplomatura", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<Inquiry> inquiries;
}
