package com.igsm.chatbot.service;

import com.igsm.chatbot.model.Faq;
import com.igsm.chatbot.repository.FaqRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FaqService {

    @Autowired
    private FaqRepository faqRepository;

    public List<Faq> getAllFaqs() {
        return faqRepository.findAll();
    }

    public Faq createFaq(Faq faq) {
        return faqRepository.save(faq);
    }

    public Faq updateFaq(Long id, Faq faqDetails) {
        Optional<Faq> faq = faqRepository.findById(id);
        if (faq.isPresent()) {
            Faq existingFaq = faq.get();
            existingFaq.setQuestion(faqDetails.getQuestion());
            existingFaq.setAnswer(faqDetails.getAnswer());
            return faqRepository.save(existingFaq);
        }
        return null;
    }

    public void deleteFaq(Long id) {
        faqRepository.deleteById(id);
    }

    public String generateFaqMenu() {
        List<Faq> faqs = faqRepository.findAll();
        if (faqs.isEmpty()) {
            return "❌ No hay preguntas frecuentes registradas por el momento.";
        }

        StringBuilder sb = new StringBuilder("❓ *Preguntas Frecuentes*\n\n");
        int index = 1;
        for (Faq faq : faqs) {
            sb.append(index).append(". ").append(faq.getQuestion()).append("\n");
            index++;
        }
        sb.append("\nResponda con el número de la pregunta para ver la respuesta.");
        return sb.toString();
    }

    public String getFaqAnswer(int selectionIndex) {
        List<Faq> faqs = faqRepository.findAll();
        if (selectionIndex < 1 || selectionIndex > faqs.size()) {
            return "❌ Selección inválida. Por favor, intente nuevamente.";
        }
        Faq selected = faqs.get(selectionIndex - 1);
        selected.incrementQueryCount();
        faqRepository.save(selected);
        return "*" + selected.getQuestion() + "*\n\n" + selected.getAnswer();
    }
}
