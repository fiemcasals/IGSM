package com.igsm.chatbot.repository;

import com.igsm.chatbot.model.ContactProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactProfileRepository extends JpaRepository<ContactProfile, String> {
}
