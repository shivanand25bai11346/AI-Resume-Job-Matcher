package com.matcher.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "resumes")
public class ResumeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "candidate_name")
    private String candidateName;

    @Column(name = "candidate_email")
    private String candidateEmail;

    @Lob
    @Column(name = "resume_text", columnDefinition = "LONGTEXT")
    private String resumeText;

    @Lob
    @Column(name = "extracted_skills_json", columnDefinition = "TEXT")
    private String extractedSkillsJson;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public ResumeEntity() {
        this.createdAt = LocalDateTime.now();
    }

    public ResumeEntity(String fileName, String candidateName, String resumeText, String extractedSkillsJson) {
        this.fileName = fileName;
        this.candidateName = candidateName;
        this.resumeText = resumeText;
        this.extractedSkillsJson = extractedSkillsJson;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getCandidateName() {
        return candidateName;
    }

    public void setCandidateName(String candidateName) {
        this.candidateName = candidateName;
    }

    public String getCandidateEmail() {
        return candidateEmail;
    }

    public void setCandidateEmail(String candidateEmail) {
        this.candidateEmail = candidateEmail;
    }

    public String getResumeText() {
        return resumeText;
    }

    public void setResumeText(String resumeText) {
        this.resumeText = resumeText;
    }

    public String getExtractedSkillsJson() {
        return extractedSkillsJson;
    }

    public void setExtractedSkillsJson(String extractedSkillsJson) {
        this.extractedSkillsJson = extractedSkillsJson;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
