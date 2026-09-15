package com.matcher.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.matcher.entity.MatchResultEntity;
import com.matcher.model.*;
import com.matcher.repository.MatchResultRepository;
import com.matcher.service.MatchingEngineService;
import com.matcher.service.PdfExtractionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api/match")
@CrossOrigin(origins = "*")
public class MatchController {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final MatchingEngineService matchingEngineService;
    private final PdfExtractionService pdfExtractionService;
    private final MatchResultRepository matchResultRepository;
    private final ObjectMapper objectMapper;

    public MatchController(
            MatchingEngineService matchingEngineService,
            PdfExtractionService pdfExtractionService,
            MatchResultRepository matchResultRepository,
            ObjectMapper objectMapper) {
        this.matchingEngineService = matchingEngineService;
        this.pdfExtractionService = pdfExtractionService;
        this.matchResultRepository = matchResultRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * Matches raw resume text with a job description.
     */
    @PostMapping
    public ResponseEntity<?> matchText(@RequestBody MatchRequest request) {
        try {
            MatchResponse response = matchingEngineService.processMatch(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error during matching: " + e.getMessage()));
        }
    }

    /**
     * Matches an uploaded PDF resume directly with a job description.
     */
    @PostMapping("/upload")
    public ResponseEntity<?> matchPdfUpload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("jobDescription") String jobDescription,
            @RequestParam(value = "jobTitle", required = false) String jobTitle,
            @RequestParam(value = "company", required = false) String company,
            @RequestParam(value = "candidateName", required = false) String candidateName) {
        try {
            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Please upload a valid PDF resume file."));
            }

            String resumeText = pdfExtractionService.extractText(file);
            MatchRequest request = new MatchRequest();
            request.setResumeText(resumeText);
            request.setJobDescription(jobDescription);
            request.setFileName(file.getOriginalFilename());
            request.setJobTitle(jobTitle);
            request.setCompany(company);
            request.setCandidateName(candidateName);

            MatchResponse response = matchingEngineService.processMatch(request);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Failed to extract text from PDF: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Processing error: " + e.getMessage()));
        }
    }

    /**
     * Retrieves a stored match result by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getMatchById(@PathVariable Long id) {
        Optional<MatchResultEntity> entityOpt = matchResultRepository.findById(id);
        if (entityOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        MatchResultEntity entity = entityOpt.get();
        MatchResponse response = mapEntityToResponse(entity);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves recent matches.
     */
    @GetMapping("/history")
    public ResponseEntity<List<Map<String, Object>>> getRecentMatches() {
        List<MatchResultEntity> matches = matchResultRepository.findTop10ByOrderByCreatedAtDesc();
        List<Map<String, Object>> result = new ArrayList<>();

        for (MatchResultEntity m : matches) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", m.getId());
            item.put("matchScore", m.getMatchScore());
            item.put("skillMatchScore", m.getSkillMatchScore());
            item.put("candidateName", m.getResume() != null ? m.getResume().getCandidateName() : "Unknown");
            item.put("jobTitle", m.getJob() != null ? m.getJob().getJobTitle() : "Job Match");
            item.put("company", m.getJob() != null ? m.getJob().getCompany() : "");
            item.put("createdAt", m.getCreatedAt() != null ? m.getCreatedAt().format(DATE_FORMATTER) : "");
            result.add(item);
        }

        return ResponseEntity.ok(result);
    }

    private MatchResponse mapEntityToResponse(MatchResultEntity entity) {
        MatchResponse res = new MatchResponse();
        res.setMatchId(entity.getId());
        res.setOverallScore(entity.getMatchScore() != null ? entity.getMatchScore() : 0.0);
        res.setSkillMatchScore(entity.getSkillMatchScore() != null ? entity.getSkillMatchScore() : 0.0);
        res.setTfidfScore(entity.getTfidfScore() != null ? entity.getTfidfScore() : 0.0);
        res.setSemanticScore(entity.getSemanticScore() != null ? entity.getSemanticScore() : 0.0);

        if (entity.getResume() != null) {
            res.setCandidateName(entity.getResume().getCandidateName());
        }
        if (entity.getJob() != null) {
            res.setJobTitle(entity.getJob().getJobTitle());
            res.setCompany(entity.getJob().getCompany());
        }
        if (entity.getCreatedAt() != null) {
            res.setCreatedAt(entity.getCreatedAt().format(DATE_FORMATTER));
        }

        try {
            if (entity.getMatchedSkillsJson() != null) {
                res.setMatchedSkills(objectMapper.readValue(entity.getMatchedSkillsJson(), new TypeReference<List<SkillInfo>>() {}));
            }
            if (entity.getMissingSkillsJson() != null) {
                res.setMissingSkills(objectMapper.readValue(entity.getMissingSkillsJson(), new TypeReference<List<SkillInfo>>() {}));
            }
            if (entity.getExtraSkillsJson() != null) {
                res.setExtraSkills(objectMapper.readValue(entity.getExtraSkillsJson(), new TypeReference<List<SkillInfo>>() {}));
            }
            if (entity.getRoadmapJson() != null) {
                res.setRoadmap(objectMapper.readValue(entity.getRoadmapJson(), new TypeReference<List<RoadmapStep>>() {}));
            }
        } catch (Exception e) {
            // fallback
        }

        res.setTotalMatchedSkills(res.getMatchedSkills().size());
        res.setTotalMissingSkills(res.getMissingSkills().size());
        res.setTotalRequiredSkills(res.getTotalMatchedSkills() + res.getTotalMissingSkills());

        return res;
    }
}
