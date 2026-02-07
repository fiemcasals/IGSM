package com.igsm.chatbot.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "contact_profiles")
public class ContactProfile {
    @Id
    private String remoteJid; // PK matches userId in Consultations

    private String nickname;

    @ManyToOne
    @JoinColumn(name = "team_member_id")
    private TeamMember assignedMember;

    public ContactProfile() {
    }

    public ContactProfile(String remoteJid) {
        this.remoteJid = remoteJid;
    }

    public String getRemoteJid() {
        return remoteJid;
    }

    public void setRemoteJid(String remoteJid) {
        this.remoteJid = remoteJid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public TeamMember getAssignedMember() {
        return assignedMember;
    }

    public void setAssignedMember(TeamMember assignedMember) {
        this.assignedMember = assignedMember;
    }
}
