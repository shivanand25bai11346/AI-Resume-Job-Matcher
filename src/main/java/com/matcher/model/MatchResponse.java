package com.matcher.model;

import java.util.ArrayList;
import java.util.List;

public class MatchResponse {
    private Long matchId;
    private double overallScore;
    private double skillMatchScore;
    private double tfidfScore;
    private double semanticScore;
    private String verdict;
    private String verdictDescription;

    private List<SkillInfo> matchedSkills = new ArrayList<>();
    private List<SkillInfo> missingSkills = new ArrayList<>();
    private List<SkillInfo> extraSkills = new ArrayList<>();
    private List<RoadmapStep> roadmap = new ArrayList<>();

    private int totalRequiredSkills;
    private int totalMatchedSkills;
    private int totalMissingSkills;
    private int totalResumeSkills;

    private String candidateName;
    private String jobTitle;
    private String company;
    private String createdAt;

    public MatchResponse() {}

    public Long getMatchId() {
        return matchId;
    }

    public void setMatchId(Long matchId) {
        this.matchId = matchId;
    }

    public double getOverallScore() {
        return overallScore;
    }

    public void setOverallScore(double overallScore) {
        this.overallScore = overallScore;
    }

    public double getSkillMatchScore() {
        return skillMatchScore;
    }

    public void setSkillMatchScore(double skillMatchScore) {
        this.skillMatchScore = skillMatchScore;
    }

    public double getTfidfScore() {
        return tfidfScore;
    }

    public void setTfidfScore(double tfidfScore) {
        this.tfidfScore = tfidfScore;
    }

    public double getSemanticScore() {
        return semanticScore;
    }

    public void setSemanticScore(double semanticScore) {
        this.semanticScore = semanticScore;
    }

    public String getVerdict() {
        return verdict;
    }

    public void setVerdict(String verdict) {
        this.verdict = verdict;
    }

    public String getVerdictDescription() {
        return verdictDescription;
    }

    public void setVerdictDescription(String verdictDescription) {
        this.verdictDescription = verdictDescription;
    }

    public List<SkillInfo> getMatchedSkills() {
        return matchedSkills;
    }

    public void setMatchedSkills(List<SkillInfo> matchedSkills) {
        this.matchedSkills = matchedSkills;
    }

    public List<SkillInfo> getMissingSkills() {
        return missingSkills;
    }

    public void setMissingSkills(List<SkillInfo> missingSkills) {
        this.missingSkills = missingSkills;
    }

    public List<SkillInfo> getExtraSkills() {
        return extraSkills;
    }

    public void setExtraSkills(List<SkillInfo> extraSkills) {
        this.extraSkills = extraSkills;
    }

    public List<RoadmapStep> getRoadmap() {
        return roadmap;
    }

    public void setRoadmap(List<RoadmapStep> roadmap) {
        this.roadmap = roadmap;
    }

    public int getTotalRequiredSkills() {
        return totalRequiredSkills;
    }

    public void setTotalRequiredSkills(int totalRequiredSkills) {
        this.totalRequiredSkills = totalRequiredSkills;
    }

    public int getTotalMatchedSkills() {
        return totalMatchedSkills;
    }

    public void setTotalMatchedSkills(int totalMatchedSkills) {
        this.totalMatchedSkills = totalMatchedSkills;
    }

    public int getTotalMissingSkills() {
        return totalMissingSkills;
    }

    public void setTotalMissingSkills(int totalMissingSkills) {
        this.totalMissingSkills = totalMissingSkills;
    }

    public int getTotalResumeSkills() {
        return totalResumeSkills;
    }

    public void setTotalResumeSkills(int totalResumeSkills) {
        this.totalResumeSkills = totalResumeSkills;
    }

    public String getCandidateName() {
        return candidateName;
    }

    public void setCandidateName(String candidateName) {
        this.candidateName = candidateName;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
