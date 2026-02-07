package com.igsm.chatbot.repository;

import com.igsm.chatbot.model.Consultation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConsultationRepository extends JpaRepository<Consultation, Long> {
    List<Consultation> findAllByOrderByTimestampDesc();

    List<Consultation> findByUserId(String userId);

    List<Consultation> findBySeenFalse();

    @org.springframework.data.jpa.repository.Query("SELECT c FROM Consultation c WHERE c.timestamp = (SELECT MAX(c2.timestamp) FROM Consultation c2 WHERE c2.userId = c.userId)")
    List<Consultation> findLatestConsultations();
}
