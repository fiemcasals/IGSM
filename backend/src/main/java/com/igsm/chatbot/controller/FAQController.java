package com.igsm.chatbot.controller;

import com.igsm.chatbot.model.FAQ;
import com.igsm.chatbot.repository.FAQRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/faqs")
public class FAQController {

    @Autowired
    private FAQRepository faqRepository;

    @GetMapping
    public List<FAQ> getAllFAQs() {
        return faqRepository.findAll();
    }

    @PostMapping
    public FAQ createFAQ(@RequestBody FAQ faq) {
        return faqRepository.save(faq);
    }

    @PutMapping("/{id}")
    public FAQ updateFAQ(@PathVariable Long id, @RequestBody FAQ faqDetails) {
        FAQ faq = faqRepository.findById(id).orElseThrow();
        faq.setQuestion(faqDetails.getQuestion());
        faq.setAnswer(faqDetails.getAnswer());
        return faqRepository.save(faq);
    }

    @DeleteMapping("/{id}")
    public void deleteFAQ(@PathVariable Long id) {
        faqRepository.deleteById(id);
    }
}
