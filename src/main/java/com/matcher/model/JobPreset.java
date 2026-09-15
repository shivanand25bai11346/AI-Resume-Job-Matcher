package com.matcher.model;

public class JobPreset {
    private String id;
    private String title;
    private String company;
    private String description;
    private String sampleResumeText;
    private String sampleCandidateName;

    public JobPreset() {}

    public JobPreset(String id, String title, String company, String description, String sampleResumeText, String sampleCandidateName) {
        this.id = id;
        this.title = title;
        this.company = company;
        this.description = description;
        this.sampleResumeText = sampleResumeText;
        this.sampleCandidateName = sampleCandidateName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSampleResumeText() {
        return sampleResumeText;
    }

    public void setSampleResumeText(String sampleResumeText) {
        this.sampleResumeText = sampleResumeText;
    }

    public String getSampleCandidateName() {
        return sampleCandidateName;
    }

    public void setSampleCandidateName(String sampleCandidateName) {
        this.sampleCandidateName = sampleCandidateName;
    }
}
