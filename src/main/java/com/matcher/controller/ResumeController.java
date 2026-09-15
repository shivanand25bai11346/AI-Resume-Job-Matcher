package com.matcher.controller;

import com.matcher.model.SkillInfo;
import com.matcher.service.PdfExtractionService;
import com.matcher.service.SkillExtractionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/resume")
@CrossOrigin(origins = "*")
public class ResumeController {

    private final PdfExtractionService pdfExtractionService;
    private final SkillExtractionService skillExtractionService;

    public ResumeController(PdfExtractionService pdfExtractionService, SkillExtractionService skillExtractionService) {
        this.pdfExtractionService = pdfExtractionService;
        this.skillExtractionService = skillExtractionService;
    }

    /**
     * Uploads a PDF resume, extracts raw text with PDFBox and parses skills.
     */
    @PostMapping("/upload")
    public ResponseEntity<?> uploadResumePdf(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Please select a non-empty PDF file."));
            }

            String fileName = file.getOriginalFilename();
            if (fileName != null && !fileName.toLowerCase().endsWith(".pdf")) {
                return ResponseEntity.badRequest().body(Map.of("error", "Only PDF files are supported."));
            }

            String text = pdfExtractionService.extractText(file);
            List<SkillInfo> skills = skillExtractionService.extractSkills(text);
            String candidateName = skillExtractionService.extractCandidateName(text);
            String email = skillExtractionService.extractEmail(text);

            Map<String, Object> response = new HashMap<>();
            response.put("fileName", fileName);
            response.put("candidateName", candidateName);
            response.put("candidateEmail", email);
            response.put("extractedText", text);
            response.put("extractedSkills", skills);
            response.put("skillCount", skills.size());

            return ResponseEntity.ok(response);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to extract text from PDF: " + e.getMessage()));
        }
    }

    /**
     * Parses raw resume text directly.
     */
    @PostMapping("/parse-text")
    public ResponseEntity<?> parseResumeText(@RequestBody Map<String, String> payload) {
        String text = payload.get("text");
        if (text == null || text.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Text content cannot be empty."));
        }

        List<SkillInfo> skills = skillExtractionService.extractSkills(text);
        String candidateName = skillExtractionService.extractCandidateName(text);
        String email = skillExtractionService.extractEmail(text);

        Map<String, Object> response = new HashMap<>();
        response.put("candidateName", candidateName);
        response.put("candidateEmail", email);
        response.put("extractedSkills", skills);
        response.put("skillCount", skills.size());

        return ResponseEntity.ok(response);
    }
}
