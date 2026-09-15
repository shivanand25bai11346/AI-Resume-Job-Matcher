package com.matcher.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "jobs")
public class JobEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "company")
    private String company;

    @Column(name = "job_title", nullable = false)
    private String jobTitle;

    @Column(name = "location")
    private String location;

    @Lob
    @Column(name = "description", columnDefinition = "LONGTEXT", nullable = false)
    private String description;

    @Lob
    @Column(name = "required_skills_json", columnDefinition = "TEXT")
    private String requiredSkillsJson;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public JobEntity() {
        this.createdAt = LocalDateTime.now();
    }

    public JobEntity(String company, String jobTitle, String description, String requiredSkillsJson) {
        this.company = company;
        this.jobTitle = jobTitle;
        this.description = description;
        this.requiredSkillsJson = requiredSkillsJson;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRequiredSkillsJson() {
        return requiredSkillsJson;
    }

    public void setRequiredSkillsJson(String requiredSkillsJson) {
        this.requiredSkillsJson = requiredSkillsJson;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
