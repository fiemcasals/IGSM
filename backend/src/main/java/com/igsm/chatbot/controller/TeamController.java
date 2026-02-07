package com.igsm.chatbot.controller;

import com.igsm.chatbot.model.ContactProfile;
import com.igsm.chatbot.model.TeamMember;
import com.igsm.chatbot.model.Consultation;
import com.igsm.chatbot.repository.ContactProfileRepository;
import com.igsm.chatbot.repository.TeamMemberRepository;
import com.igsm.chatbot.repository.ConsultationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/team")
@CrossOrigin(origins = "*")
public class TeamController {

    @Autowired
    private TeamMemberRepository teamMemberRepository;

    @Autowired
    private ContactProfileRepository contactProfileRepository;

    @Autowired
    private ConsultationRepository consultationRepository;

    // --- Team Member Management ---

    @GetMapping("/members")
    public List<TeamMember> getAllMembers() {
        return teamMemberRepository.findAll();
    }

    @PostMapping("/members")
    public TeamMember createMember(@RequestBody Map<String, String> payload) {
        String name = payload.get("name");
        return teamMemberRepository.save(new TeamMember(name));
    }

    @DeleteMapping("/members/{id}")
    public void deleteMember(@PathVariable Long id) {
        teamMemberRepository.deleteById(id);
    }

    // --- Contact Profile Management ---

    @GetMapping("/profiles")
    public List<ContactProfile> getAllProfiles() {
        return contactProfileRepository.findAll();
    }

    @PostMapping("/profiles/{remoteJid}/assign")
    public ContactProfile assignProfile(@PathVariable String remoteJid, @RequestBody Map<String, Long> payload) {
        Long memberId = payload.get("memberId");

        ContactProfile profile = contactProfileRepository.findById(remoteJid)
                .orElse(new ContactProfile(remoteJid));

        if (memberId != null) {
            TeamMember member = teamMemberRepository.findById(memberId).orElse(null);
            profile.setAssignedMember(member);
        } else {
            profile.setAssignedMember(null); // Unassign
        }

        return contactProfileRepository.save(profile);
    }

    @PostMapping("/profiles/{remoteJid}/nickname")
    public ContactProfile setNickname(@PathVariable String remoteJid, @RequestBody Map<String, String> payload) {
        String nickname = payload.get("nickname");

        ContactProfile profile = contactProfileRepository.findById(remoteJid)
                .orElse(new ContactProfile(remoteJid));

        profile.setNickname(nickname);
        return contactProfileRepository.save(profile);
    }

    @GetMapping("/stats")
    public Map<Long, Long> getTeamStats() {
        Map<Long, Long> stats = new java.util.HashMap<>();

        // Get the LATEST message for each conversation
        List<Consultation> latestMessages = consultationRepository.findLatestConsultations();

        for (Consultation c : latestMessages) {
            // Count ONLY if the last message is NOT from admin (i.e. waiting for reply)
            // AND it hasn't been manually marked as seen
            if (!c.isAdminReply() && !c.isSeen()) {
                ContactProfile profile = contactProfileRepository.findById(c.getUserId()).orElse(null);
                if (profile != null && profile.getAssignedMember() != null) {
                    stats.merge(profile.getAssignedMember().getId(), 1L, Long::sum);
                } else {
                    // Unassigned
                    stats.merge(0L, 1L, Long::sum);
                }
            }
        }

        return stats;
    }
}
