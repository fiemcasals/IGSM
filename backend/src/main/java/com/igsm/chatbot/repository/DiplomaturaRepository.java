package com.igsm.chatbot.repository;

import com.igsm.chatbot.model.Diplomatura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiplomaturaRepository extends JpaRepository<Diplomatura, Long> {
}
