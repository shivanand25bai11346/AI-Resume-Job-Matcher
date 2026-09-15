package com.matcher.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.matcher.entity.JobEntity;
import com.matcher.entity.MatchResultEntity;
import com.matcher.entity.ResumeEntity;
import com.matcher.model.*;
import com.matcher.repository.JobRepository;
import com.matcher.repository.MatchResultRepository;
import com.matcher.repository.ResumeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class MatchingEngineService {

    private static final Logger log = LoggerFactory.getLogger(MatchingEngineService.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final SkillExtractionService extractionService;
    private final SkillTaxonomyService taxonomyService;
    private final SemanticSimilarityService semanticSimilarityService;
    private final SkillGapRoadmapService roadmapService;
    private final ResumeRepository resumeRepository;
    private final JobRepository jobRepository;
    private final MatchResultRepository matchResultRepository;
    private final ObjectMapper objectMapper;

    public MatchingEngineService(
            SkillExtractionService extractionService,
            SkillTaxonomyService taxonomyService,
            SemanticSimilarityService semanticSimilarityService,
            SkillGapRoadmapService roadmapService,
            ResumeRepository resumeRepository,
            JobRepository jobRepository,
            MatchResultRepository matchResultRepository,
            ObjectMapper objectMapper) {
        this.extractionService = extractionService;
        this.taxonomyService = taxonomyService;
        this.semanticSimilarityService = semanticSimilarityService;
        this.roadmapService = roadmapService;
        this.resumeRepository = resumeRepository;
        this.jobRepository = jobRepository;
        this.matchResultRepository = matchResultRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public MatchResponse processMatch(MatchRequest request) {
        String resumeText = request.getResumeText() != null ? request.getResumeText().trim() : "";
        String jobText = request.getJobDescription() != null ? request.getJobDescription().trim() : "";

        if (resumeText.isBlank() || jobText.isBlank()) {
            throw new IllegalArgumentException("Both Resume text and Job Description are required.");
        }

        // 1. Skill Extraction
        List<SkillInfo> resumeSkills = extractionService.extractSkills(resumeText);
        List<SkillInfo> jobSkills = extractionService.extractSkills(jobText);

        // Map for quick lookup
        Map<String, SkillInfo> resumeSkillMap = new HashMap<>();
        for (SkillInfo s : resumeSkills) {
            resumeSkillMap.put(s.getName().toLowerCase(), s);
        }

        List<SkillInfo> matchedSkills = new ArrayList<>();
        List<SkillInfo> missingSkills = new ArrayList<>();
        double semanticCreditSum = 0.0;
        int exactMatchCount = 0;

        // 2. Compare Job Requirements with Resume Skills
        for (SkillInfo jobSkill : jobSkills) {
            String jobSkillKey = jobSkill.getName().toLowerCase();
            if (resumeSkillMap.containsKey(jobSkillKey)) {
                // Exact Match
                jobSkill.setMatched(true);
                jobSkill.setRelevanceWeight(1.0);
                matchedSkills.add(jobSkill);
                exactMatchCount++;
            } else {
                // Check Semantic Relation
                Map.Entry<String, Double> semanticMatch = taxonomyService.findBestSemanticMatch(
                        jobSkill.getName(), resumeSkillMap.keySet()
                );

                if (semanticMatch != null && semanticMatch.getValue() >= 0.70) {
                    // Award partial credit for related concept
                    jobSkill.setMatched(true);
                    jobSkill.setRelevanceWeight(semanticMatch.getValue());
                    jobSkill.setRelatedTo(resumeSkillMap.get(semanticMatch.getKey()).getName());
                    matchedSkills.add(jobSkill);
                    semanticCreditSum += semanticMatch.getValue();
                } else {
                    // Skill Gap
                    jobSkill.setMatched(false);
                    missingSkills.add(jobSkill);
                }
            }
        }

        // 3. Extra Skills (Candidate has, but job didn't explicitly mandate)
        Set<String> jobSkillKeySet = new HashSet<>();
        for (SkillInfo j : jobSkills) {
            jobSkillKeySet.add(j.getName().toLowerCase());
        }
        List<SkillInfo> extraSkills = new ArrayList<>();
        for (SkillInfo r : resumeSkills) {
            if (!jobSkillKeySet.contains(r.getName().toLowerCase())) {
                extraSkills.add(r);
            }
        }

        // 4. Calculate Scores
        int totalJobSkills = Math.max(1, jobSkills.size());
        double totalMatchesWithCredit = exactMatchCount + semanticCreditSum;
        double skillMatchScore = Math.min(100.0, (totalMatchesWithCredit / totalJobSkills) * 100.0);
        skillMatchScore = Math.round(skillMatchScore * 10.0) / 10.0;

        // TF-IDF Cosine Similarity
        double tfidfScore = semanticSimilarityService.calculateTfidfCosineSimilarity(resumeText, jobText);

        // Semantic Contextual Score
        double semanticScore = calculateAverageSemanticScore(jobSkills, resumeSkillMap.keySet());

        // Composite Overall Score: 60% Skill Match + 25% TF-IDF Cosine + 15% Semantic Context
        double compositeScore = (0.60 * skillMatchScore) + (0.25 * tfidfScore) + (0.15 * semanticScore);
        compositeScore = Math.round(Math.min(100.0, Math.max(0.0, compositeScore)) * 10.0) / 10.0;

        // 5. Generate Roadmap for Gaps
        List<RoadmapStep> roadmap = roadmapService.generateRoadmap(missingSkills);

        // 6. Verdict and Description
        String[] verdictInfo = determineVerdict(compositeScore, matchedSkills.size(), missingSkills.size());

        // Candidate & Job Info
        String candidateName = request.getCandidateName() != null && !request.getCandidateName().isBlank()
                ? request.getCandidateName()
                : extractionService.extractCandidateName(resumeText);
        String candidateEmail = request.getCandidateEmail() != null
                ? request.getCandidateEmail()
                : extractionService.extractEmail(resumeText);
        String jobTitle = request.getJobTitle() != null && !request.getJobTitle().isBlank()
                ? request.getJobTitle()
                : "Software Engineer";
        String company = request.getCompany() != null && !request.getCompany().isBlank()
                ? request.getCompany()
                : "Technology Company";

        // 7. Persist to Database
        MatchResultEntity savedEntity = null;
        try {
            ResumeEntity resumeEntity = new ResumeEntity(
                    request.getFileName() != null ? request.getFileName() : "resume.txt",
                    candidateName,
                    resumeText,
                    objectMapper.writeValueAsString(resumeSkills)
            );
            resumeEntity.setCandidateEmail(candidateEmail);
            resumeRepository.save(resumeEntity);

            JobEntity jobEntity = new JobEntity(
                    company,
                    jobTitle,
                    jobText,
                    objectMapper.writeValueAsString(jobSkills)
            );
            jobRepository.save(jobEntity);

            MatchResultEntity matchEntity = new MatchResultEntity();
            matchEntity.setResume(resumeEntity);
            matchEntity.setJob(jobEntity);
            matchEntity.setMatchScore(compositeScore);
            matchEntity.setSkillMatchScore(skillMatchScore);
            matchEntity.setTfidfScore(tfidfScore);
            matchEntity.setSemanticScore(semanticScore);
            matchEntity.setMatchedSkillsJson(objectMapper.writeValueAsString(matchedSkills));
            matchEntity.setMissingSkillsJson(objectMapper.writeValueAsString(missingSkills));
            matchEntity.setExtraSkillsJson(objectMapper.writeValueAsString(extraSkills));
            matchEntity.setRoadmapJson(objectMapper.writeValueAsString(roadmap));

            savedEntity = matchResultRepository.save(matchEntity);
        } catch (Exception e) {
            log.warn("Database persistence note: {}", e.getMessage());
        }

        // 8. Build Response
        MatchResponse response = new MatchResponse();
        if (savedEntity != null) {
            response.setMatchId(savedEntity.getId());
            response.setCreatedAt(savedEntity.getCreatedAt().format(DATE_FORMATTER));
        }
        response.setOverallScore(compositeScore);
        response.setSkillMatchScore(skillMatchScore);
        response.setTfidfScore(tfidfScore);
        response.setSemanticScore(semanticScore);
        response.setVerdict(verdictInfo[0]);
        response.setVerdictDescription(verdictInfo[1]);
        response.setMatchedSkills(matchedSkills);
        response.setMissingSkills(missingSkills);
        response.setExtraSkills(extraSkills);
        response.setRoadmap(roadmap);
        response.setTotalRequiredSkills(jobSkills.size());
        response.setTotalMatchedSkills(matchedSkills.size());
        response.setTotalMissingSkills(missingSkills.size());
        response.setTotalResumeSkills(resumeSkills.size());
        response.setCandidateName(candidateName);
        response.setJobTitle(jobTitle);
        response.setCompany(company);

        return response;
    }

    private double calculateAverageSemanticScore(List<SkillInfo> jobSkills, Set<String> resumeSkills) {
        if (jobSkills.isEmpty()) return 50.0;
        double total = 0.0;
        for (SkillInfo j : jobSkills) {
            if (resumeSkills.contains(j.getName().toLowerCase())) {
                total += 100.0;
            } else {
                Map.Entry<String, Double> match = taxonomyService.findBestSemanticMatch(j.getName(), resumeSkills);
                if (match != null) {
                    total += match.getValue() * 100.0;
                } else {
                    total += 10.0; // Baseline concept awareness
                }
            }
        }
        double avg = total / jobSkills.size();
        return Math.round(avg * 10.0) / 10.0;
    }

    private String[] determineVerdict(double score, int matched, int missing) {
        if (score >= 82.0) {
            return new String[]{
                    "Outstanding Match",
                    "Strong alignment with job requirements! Candidate possesses the primary tech stack and demonstrated skills needed for immediate productivity."
            };
        } else if (score >= 70.0) {
            return new String[]{
                    "Strong Match",
                    "Great fit for the position. Candidate possesses most critical skills, with easily bridgeable minor skill gaps."
            };
        } else if (score >= 55.0) {
            return new String[]{
                    "Moderate Match",
                    "Solid foundational match. Key core competencies are present, but dedicated upskilling on specific required tools is recommended."
            };
        } else if (score >= 40.0) {
            return new String[]{
                    "Potential Match",
                    "Candidate shows partial skill overlap. Significant learning curve expected for production readiness."
            };
        } else {
            return new String[]{
                    "Skill Gap Alert",
                    "Substantial divergence between resume profile and job specifications. Follow the recommended learning roadmap to qualify."
            };
        }
    }
}
