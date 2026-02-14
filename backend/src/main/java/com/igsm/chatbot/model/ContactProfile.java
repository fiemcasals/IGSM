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
    private String email;

    @ManyToOne
    @JoinColumn(name = "team_member_id")
    private TeamMember assignedMember;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "profile_tags", joinColumns = @JoinColumn(name = "profile_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private java.util.List<Tag> tags = new java.util.ArrayList<>();

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
