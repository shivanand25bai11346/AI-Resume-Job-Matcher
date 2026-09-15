package com.matcher.model;

import java.util.Objects;

public class SkillInfo {
    private String name;
    private SkillCategory category;
    private int occurrences;
    private boolean matched;
    private double relevanceWeight; // 1.0 for exact, 0.7-0.9 for semantic relation
    private String relatedTo; // if matched semantically, the counterpart skill

    public SkillInfo() {}

    public SkillInfo(String name, SkillCategory category) {
        this.name = name;
        this.category = category;
        this.occurrences = 1;
        this.matched = false;
        this.relevanceWeight = 1.0;
    }

    public SkillInfo(String name, SkillCategory category, int occurrences) {
        this.name = name;
        this.category = category;
        this.occurrences = occurrences;
        this.matched = false;
        this.relevanceWeight = 1.0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SkillCategory getCategory() {
        return category;
    }

    public void setCategory(SkillCategory category) {
        this.category = category;
    }

    public int getOccurrences() {
        return occurrences;
    }

    public void setOccurrences(int occurrences) {
        this.occurrences = occurrences;
    }

    public boolean isMatched() {
        return matched;
    }

    public void setMatched(boolean matched) {
        this.matched = matched;
    }

    public double getRelevanceWeight() {
        return relevanceWeight;
    }

    public void setRelevanceWeight(double relevanceWeight) {
        this.relevanceWeight = relevanceWeight;
    }

    public String getRelatedTo() {
        return relatedTo;
    }

    public void setRelatedTo(String relatedTo) {
        this.relatedTo = relatedTo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SkillInfo skillInfo = (SkillInfo) o;
        return Objects.equals(name.toLowerCase(), skillInfo.name.toLowerCase());
    }

    @Override
    public int hashCode() {
        return Objects.hash(name.toLowerCase());
    }
}
