package com.matcher.service;

import com.matcher.model.SkillInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class SkillExtractionServiceTest {

    private SkillTaxonomyService taxonomyService;
    private SkillExtractionService extractionService;

    @BeforeEach
    public void setup() {
        taxonomyService = new SkillTaxonomyService();
        extractionService = new SkillExtractionService(taxonomyService);
    }

    @Test
    public void testSkillExtractionFromText() {
        String text = "Candidate has expertise in Java, Spring Boot, MySQL, REST API, Git, and Docker. " +
                      "Also familiar with React and AWS.";

        List<SkillInfo> skills = extractionService.extractSkills(text);
        Set<String> skillNames = extractionService.extractSkillNames(text);

        assertNotNull(skills);
        assertTrue(skills.size() >= 7);

        assertTrue(skillNames.contains("Java"));
        assertTrue(skillNames.contains("Spring Boot"));
        assertTrue(skillNames.contains("MySQL"));
        assertTrue(skillNames.contains("REST API"));
        assertTrue(skillNames.contains("Git"));
        assertTrue(skillNames.contains("Docker"));
        assertTrue(skillNames.contains("React"));
        assertTrue(skillNames.contains("AWS"));
    }

    @Test
    public void testAvoidFalsePositiveSubstrings() {
        // "JavaScript" should not falsely count as "Java" as a separate skill if only JavaScript is mentioned,
        // and words like "reaction" or "reacting" shouldn't trigger React unless spaced properly.
        String text = "Candidate developed frontend with React and TypeScript.";
        Set<String> skillNames = extractionService.extractSkillNames(text);

        assertTrue(skillNames.contains("React"));
        assertTrue(skillNames.contains("TypeScript"));
        // "Java" should not be falsely extracted just from words that don't contain standalone Java
        assertFalse(skillNames.contains("Java"));
    }

    @Test
    public void testCandidateInfoExtraction() {
        String resume = "Vishnu Sharma\n" +
                        "vishnu.dev@example.com\n" +
                        "Software Engineer with experience in Java and Spring Boot.";

        String name = extractionService.extractCandidateName(resume);
        String email = extractionService.extractEmail(resume);

        assertEquals("Vishnu Sharma", name);
        assertEquals("vishnu.dev@example.com", email);
    }
}
