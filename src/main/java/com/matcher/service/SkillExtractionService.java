package com.matcher.service;

import com.matcher.model.SkillCategory;
import com.matcher.model.SkillInfo;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SkillExtractionService {

    private final SkillTaxonomyService taxonomyService;

    // Email regex
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}",
            Pattern.CASE_INSENSITIVE
    );

    public SkillExtractionService(SkillTaxonomyService taxonomyService) {
        this.taxonomyService = taxonomyService;
    }

    /**
     * Extracts skills from the given text with occurrence counts and categorized info.
     */
    public List<SkillInfo> extractSkills(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }

        Map<String, SkillInfo> extractedMap = new LinkedHashMap<>();
        String normalizedText = " " + text.toLowerCase().replaceAll("[\\t\\r\\n]+", " ") + " ";

        for (SkillTaxonomyService.SkillDefinition skillDef : taxonomyService.getAllSkills()) {
            int count = 0;
            for (String alias : skillDef.getAliases()) {
                count += countOccurrences(normalizedText, alias);
            }

            if (count > 0) {
                extractedMap.put(
                        skillDef.getCanonicalName().toLowerCase(),
                        new SkillInfo(skillDef.getCanonicalName(), skillDef.getCategory(), count)
                );
            }
        }

        List<SkillInfo> result = new ArrayList<>(extractedMap.values());
        // Sort by category ordinal, then name
        result.sort(Comparator.comparing((SkillInfo s) -> s.getCategory().ordinal())
                              .thenComparing(SkillInfo::getName));
        return result;
    }

    /**
     * Extracts only skill canonical names.
     */
    public Set<String> extractSkillNames(String text) {
        List<SkillInfo> skills = extractSkills(text);
        Set<String> names = new LinkedHashSet<>();
        for (SkillInfo s : skills) {
            names.add(s.getName());
        }
        return names;
    }

    /**
     * Extracts candidate email address if present in resume text.
     */
    public String extractEmail(String text) {
        if (text == null) return null;
        Matcher matcher = EMAIL_PATTERN.matcher(text);
        if (matcher.find()) {
            return matcher.group(0);
        }
        return null;
    }

    /**
     * Heuristically extracts candidate name from the first few non-empty lines of resume text.
     */
    public String extractCandidateName(String text) {
        if (text == null || text.isBlank()) return "Candidate";
        String[] lines = text.split("\\r?\\n");
        for (String line : lines) {
            String trimmed = line.trim();
            // Look for a line with 2-4 capitalized words that isn't a header or email
            if (trimmed.length() >= 3 && trimmed.length() <= 40
                    && !trimmed.contains("@")
                    && !trimmed.toLowerCase().contains("resume")
                    && !trimmed.toLowerCase().contains("curriculum")
                    && !trimmed.toLowerCase().contains("profile")
                    && !trimmed.toLowerCase().contains("experience")
                    && !trimmed.toLowerCase().contains("education")
                    && !trimmed.matches(".*\\d+.*")) {
                return trimmed;
            }
        }
        return "Candidate";
    }

    private final Map<String, Pattern> patternCache = new HashMap<>();

    private Pattern getCompiledPattern(String term) {
        return patternCache.computeIfAbsent(term.toLowerCase(), t -> {
            String escaped = Pattern.quote(t);

            String before;
            if (t.startsWith(".")) {
                before = "(?<=^|[\\s,;:!?'\"()\\[\\]{}<>|~*])";
            } else {
                before = "(?<=^|[^a-zA-Z0-9])";
            }

            String after;
            if (t.equals("c")) {
                after = "(?=$|[^a-zA-Z0-9+#])";
            } else if (t.equals("r")) {
                after = "(?=$|[^a-zA-Z0-9])";
            } else {
                after = "(?=$|[^a-zA-Z0-9])";
            }

            return Pattern.compile(before + escaped + after, Pattern.CASE_INSENSITIVE);
        });
    }

    /**
     * Counts occurrences of a skill alias safely avoiding substring collisions.
     * e.g., "c" should not match in "react" or "c++", "java" should not match in "javascript".
     */
    private int countOccurrences(String text, String term) {
        try {
            Pattern pattern = getCompiledPattern(term);
            Matcher matcher = pattern.matcher(text);
            int count = 0;
            while (matcher.find()) {
                count++;
            }
            return count;
        } catch (Exception e) {
            return 0;
        }
    }
}

