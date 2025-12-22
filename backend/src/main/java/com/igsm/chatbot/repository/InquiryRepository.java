package com.igsm.chatbot.repository;

import com.igsm.chatbot.model.Inquiry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
    long countByDiplomaturaId(Long diplomaturaId);
}
