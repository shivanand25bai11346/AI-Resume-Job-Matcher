package com.matcher.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.matcher.entity.JobEntity;
import com.matcher.entity.MatchResultEntity;
import com.matcher.entity.ResumeEntity;
import com.matcher.model.MatchRequest;
import com.matcher.model.MatchResponse;
import com.matcher.model.SkillInfo;
import com.matcher.repository.JobRepository;
import com.matcher.repository.MatchResultRepository;
import com.matcher.repository.ResumeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class MatchingEngineServiceTest {

    private MatchingEngineService matchingEngineService;
    private ResumeRepository resumeRepository;
    private JobRepository jobRepository;
    private MatchResultRepository matchResultRepository;

    @BeforeEach
    public void setup() {
        SkillTaxonomyService taxonomyService = new SkillTaxonomyService();
        SkillExtractionService extractionService = new SkillExtractionService(taxonomyService);
        SemanticSimilarityService semanticSimilarityService = new SemanticSimilarityService();
        SkillGapRoadmapService roadmapService = new SkillGapRoadmapService(taxonomyService);

        resumeRepository = Mockito.mock(ResumeRepository.class);
        jobRepository = Mockito.mock(JobRepository.class);
        matchResultRepository = Mockito.mock(MatchResultRepository.class);
        ObjectMapper objectMapper = new ObjectMapper();

        MatchResultEntity mockMatchResult = new MatchResultEntity();
        mockMatchResult.setId(1L);
        mockMatchResult.setCreatedAt(LocalDateTime.now());
        when(matchResultRepository.save(any(MatchResultEntity.class))).thenReturn(mockMatchResult);

        matchingEngineService = new MatchingEngineService(
                extractionService,
                taxonomyService,
                semanticSimilarityService,
                roadmapService,
                resumeRepository,
                jobRepository,
                matchResultRepository,
                objectMapper
        );
    }

    @Test
    public void testUserPromptScenarioMatching() {
        // From user request:
        // Resume: Vishnu, Java, Python, C++, React, MySQL, DSA, Spring Framework
        // Job: Java, Spring Boot, MySQL, REST API, Git, Docker, DSA
        MatchRequest request = new MatchRequest();
        request.setCandidateName("Vishnu");
        request.setJobTitle("Java Developer");
        request.setResumeText(
                "Vishnu\n" +
                "Skills: Java, Python, C++, React, Next.js, MongoDB, MySQL, DSA, Machine Learning, Spring Framework."
        );
        request.setJobDescription(
                "We are looking for a Java Developer.\n" +
                "Required Skills: Java, Spring Boot, MySQL, REST API, Git, Docker, DSA."
        );

        MatchResponse response = matchingEngineService.processMatch(request);

        assertNotNull(response);
        assertTrue(response.getOverallScore() > 0);

        // Check matched skills
        Set<String> matchedNames = response.getMatchedSkills().stream()
                .map(SkillInfo::getName)
                .collect(Collectors.toSet());

        assertTrue(matchedNames.contains("Java"), "Java should be matched");
        assertTrue(matchedNames.contains("MySQL"), "MySQL should be matched");
        assertTrue(matchedNames.contains("DSA"), "DSA should be matched");
        // Spring Boot is semantically matched because resume has Spring Framework
        assertTrue(matchedNames.contains("Spring Boot"), "Spring Boot should be matched semantically via Spring Framework");

        // Check missing skills (Gaps)
        Set<String> missingNames = response.getMissingSkills().stream()
                .map(SkillInfo::getName)
                .collect(Collectors.toSet());

        assertTrue(missingNames.contains("REST API"), "REST API should be a missing skill gap");
        assertTrue(missingNames.contains("Git"), "Git should be a missing skill gap");
        assertTrue(missingNames.contains("Docker"), "Docker should be a missing skill gap");

        // Check roadmap steps
        assertNotNull(response.getRoadmap());
        assertFalse(response.getRoadmap().isEmpty());

        List<String> roadmapSkills = response.getRoadmap().stream()
                .map(r -> r.getSkillName())
                .toList();

        assertTrue(roadmapSkills.contains("Docker"));
        assertTrue(roadmapSkills.contains("REST API"));
        assertTrue(roadmapSkills.contains("Git"));

        // Check extra applicant skills
        Set<String> extraNames = response.getExtraSkills().stream()
                .map(SkillInfo::getName)
                .collect(Collectors.toSet());

        assertTrue(extraNames.contains("Python") || extraNames.contains("C++") || extraNames.contains("React"));
    }
}
