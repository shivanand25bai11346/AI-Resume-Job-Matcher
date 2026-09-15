package com.matcher.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "matches")
public class MatchResultEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "resume_id")
    private ResumeEntity resume;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "job_id")
    private JobEntity job;

    @Column(name = "match_score", nullable = false)
    private Double matchScore;

    @Column(name = "skill_match_score")
    private Double skillMatchScore;

    @Column(name = "tfidf_score")
    private Double tfidfScore;

    @Column(name = "semantic_score")
    private Double semanticScore;

    @Lob
    @Column(name = "matched_skills_json", columnDefinition = "TEXT")
    private String matchedSkillsJson;

    @Lob
    @Column(name = "missing_skills_json", columnDefinition = "TEXT")
    private String missingSkillsJson;

    @Lob
    @Column(name = "extra_skills_json", columnDefinition = "TEXT")
    private String extraSkillsJson;

    @Lob
    @Column(name = "roadmap_json", columnDefinition = "TEXT")
    private String roadmapJson;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public MatchResultEntity() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ResumeEntity getResume() {
        return resume;
    }

    public void setResume(ResumeEntity resume) {
        this.resume = resume;
    }

    public JobEntity getJob() {
        return job;
    }

    public void setJob(JobEntity job) {
        this.job = job;
    }

    public Double getMatchScore() {
        return matchScore;
    }

    public void setMatchScore(Double matchScore) {
        this.matchScore = matchScore;
    }

    public Double getSkillMatchScore() {
        return skillMatchScore;
    }

    public void setSkillMatchScore(Double skillMatchScore) {
        this.skillMatchScore = skillMatchScore;
    }

    public Double getTfidfScore() {
        return tfidfScore;
    }

    public void setTfidfScore(Double tfidfScore) {
        this.tfidfScore = tfidfScore;
    }

    public Double getSemanticScore() {
        return semanticScore;
    }

    public void setSemanticScore(Double semanticScore) {
        this.semanticScore = semanticScore;
    }

    public String getMatchedSkillsJson() {
        return matchedSkillsJson;
    }

    public void setMatchedSkillsJson(String matchedSkillsJson) {
        this.matchedSkillsJson = matchedSkillsJson;
    }

    public String getMissingSkillsJson() {
        return missingSkillsJson;
    }

    public void setMissingSkillsJson(String missingSkillsJson) {
        this.missingSkillsJson = missingSkillsJson;
    }

    public String getExtraSkillsJson() {
        return extraSkillsJson;
    }

    public void setExtraSkillsJson(String extraSkillsJson) {
        this.extraSkillsJson = extraSkillsJson;
    }

    public String getRoadmapJson() {
        return roadmapJson;
    }

    public void setRoadmapJson(String roadmapJson) {
        this.roadmapJson = roadmapJson;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
