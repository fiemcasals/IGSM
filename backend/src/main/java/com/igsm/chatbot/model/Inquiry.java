package com.igsm.chatbot.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "inquiries")
public class Inquiry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "diplomatura_id")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Diplomatura diplomatura; // esta linea indica que esta variable es una foreign key de la tabla

    private String userId;
    private String contactPhone;

    private LocalDateTime timestamp; // esta linea indica que esta variable es una foreign key de la tabla

    @PrePersist // este comentario indica que el metodo onCreate() se ejecuta antes de persistir
                // el objeto
    protected void onCreate() { // ests funcion es para asignarle un valor a la variable timestamp
        timestamp = LocalDateTime.now();
    }
}
