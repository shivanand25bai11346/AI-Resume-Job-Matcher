package com.matcher.model;

import java.util.List;

public class RoadmapStep {
    private int stepNumber;
    private String skillName;
    private SkillCategory category;
    private String priority; // High, Medium, Foundational
    private String rationale;
    private String estimatedTime;
    private List<String> recommendedTopics;

    public RoadmapStep() {}

    public RoadmapStep(int stepNumber, String skillName, SkillCategory category, String priority, String rationale, String estimatedTime, List<String> recommendedTopics) {
        this.stepNumber = stepNumber;
        this.skillName = skillName;
        this.category = category;
        this.priority = priority;
        this.rationale = rationale;
        this.estimatedTime = estimatedTime;
        this.recommendedTopics = recommendedTopics;
    }

    public int getStepNumber() {
        return stepNumber;
    }

    public void setStepNumber(int stepNumber) {
        this.stepNumber = stepNumber;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public SkillCategory getCategory() {
        return category;
    }

    public void setCategory(SkillCategory category) {
        this.category = category;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getRationale() {
        return rationale;
    }

    public void setRationale(String rationale) {
        this.rationale = rationale;
    }

    public String getEstimatedTime() {
        return estimatedTime;
    }

    public void setEstimatedTime(String estimatedTime) {
        this.estimatedTime = estimatedTime;
    }

    public List<String> getRecommendedTopics() {
        return recommendedTopics;
    }

    public void setRecommendedTopics(List<String> recommendedTopics) {
        this.recommendedTopics = recommendedTopics;
    }
}
