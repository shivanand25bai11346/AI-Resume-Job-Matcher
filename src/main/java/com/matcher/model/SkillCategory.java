package com.matcher.model;

public enum SkillCategory {
    PROGRAMMING_LANGUAGE("Programming Languages", "badge-lang", "#3b82f6"),
    FRAMEWORK("Frameworks & Libraries", "badge-framework", "#8b5cf6"),
    DATABASE("Databases & Storage", "badge-db", "#10b981"),
    CLOUD_DEVOPS("Cloud & DevOps", "badge-cloud", "#06b6d4"),
    ARCHITECTURE_CONCEPT("Architecture & Concepts", "badge-arch", "#f59e0b"),
    AI_DATA("AI, ML & Data Science", "badge-ai", "#ec4899"),
    TESTING_TOOL("Testing & Developer Tools", "badge-tool", "#64748b");

    private final String displayName;
    private final String cssClass;
    private final String color;

    SkillCategory(String displayName, String cssClass, String color) {
        this.displayName = displayName;
        this.cssClass = cssClass;
        this.color = color;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getCssClass() {
        return cssClass;
    }

    public String getColor() {
        return color;
    }
}
