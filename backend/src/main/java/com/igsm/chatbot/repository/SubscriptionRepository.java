package com.igsm.chatbot.repository;

import com.igsm.chatbot.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    long countByDiplomaturaId(Long diplomaturaId);
}
