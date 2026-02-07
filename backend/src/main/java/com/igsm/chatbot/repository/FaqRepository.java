package com.igsm.chatbot.repository;

import com.igsm.chatbot.model.Faq;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FaqRepository extends JpaRepository<Faq, Long> {
    // Custom queries if needed, for now standard CRUD is enough
}
