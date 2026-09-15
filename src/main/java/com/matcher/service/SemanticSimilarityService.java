package com.matcher.service;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;

@Service
public class SemanticSimilarityService {

    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
            "a", "about", "above", "after", "again", "against", "all", "am", "an", "and", "any", "are", "aren't",
            "as", "at", "be", "because", "been", "before", "being", "below", "between", "both", "but", "by",
            "can't", "cannot", "could", "couldn't", "did", "didn't", "do", "does", "doesn't", "doing", "don't",
            "down", "during", "each", "few", "for", "from", "further", "had", "hadn't", "has", "hasn't", "have",
            "haven't", "having", "he", "he'd", "he'll", "he's", "her", "here", "here's", "hers", "herself",
            "him", "himself", "his", "how", "how's", "i", "i'd", "i'll", "i'm", "i've", "if", "in", "into",
            "is", "isn't", "it", "it's", "its", "itself", "let's", "me", "more", "most", "mustn't", "my",
            "myself", "no", "nor", "not", "of", "off", "on", "once", "only", "or", "other", "ought", "our",
            "ours", "ourselves", "out", "over", "own", "same", "shan't", "she", "she'd", "she'll", "she's",
            "should", "shouldn't", "so", "some", "such", "than", "that", "that's", "the", "their", "theirs",
            "them", "themselves", "then", "there", "there's", "these", "they", "they'd", "they'll", "they're",
            "they've", "this", "those", "through", "to", "too", "under", "until", "up", "very", "was", "wasn't",
            "we", "we'd", "we'll", "we're", "we've", "were", "weren't", "what", "what's", "when", "when's",
            "where", "where's", "which", "while", "who", "who's", "whom", "why", "why's", "with", "won't",
            "would", "wouldn't", "you", "you'd", "you'll", "you're", "you've", "your", "yours", "yourself",
            "yourselves", "will", "looking", "role", "requirements", "responsibilities", "experience", "candidate",
            "team", "work", "skills", "ability", "working", "knowledge", "years", "qualification"
    ));

    private static final Pattern TOKEN_SPLIT = Pattern.compile("[^a-zA-Z0-9+#.-]+");

    /**
     * Calculates the Cosine Similarity percentage (0 to 100) between two text documents using TF-IDF.
     */
    public double calculateTfidfCosineSimilarity(String textA, String textB) {
        if (textA == null || textB == null || textA.isBlank() || textB.isBlank()) {
            return 0.0;
        }

        List<String> tokensA = tokenize(textA);
        List<String> tokensB = tokenize(textB);

        if (tokensA.isEmpty() || tokensB.isEmpty()) {
            return 0.0;
        }

        // Build combined vocabulary
        Set<String> vocabulary = new HashSet<>();
        vocabulary.addAll(tokensA);
        vocabulary.addAll(tokensB);

        // Term frequencies
        Map<String, Double> tfA = computeTf(tokensA);
        Map<String, Double> tfB = computeTf(tokensB);

        // Compute IDF across 2 documents
        Map<String, Double> idf = new HashMap<>();
        int totalDocs = 2;
        for (String term : vocabulary) {
            int docCount = 0;
            if (tfA.containsKey(term)) docCount++;
            if (tfB.containsKey(term)) docCount++;
            // Smooth IDF
            double idfVal = Math.log(1.0 + ((double) totalDocs / (double) docCount)) + 1.0;
            idf.put(term, idfVal);
        }

        // Compute TF-IDF vectors
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (String term : vocabulary) {
            double vA = tfA.getOrDefault(term, 0.0) * idf.get(term);
            double vB = tfB.getOrDefault(term, 0.0) * idf.get(term);

            dotProduct += vA * vB;
            normA += vA * vA;
            normB += vB * vB;
        }

        if (normA == 0.0 || normB == 0.0) {
            return 0.0;
        }

        double cosine = dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
        // Clamp to [0, 1] and scale to 100
        double result = Math.min(1.0, Math.max(0.0, cosine)) * 100.0;
        return Math.round(result * 10.0) / 10.0;
    }

    private List<String> tokenize(String text) {
        String[] rawTokens = TOKEN_SPLIT.split(text.toLowerCase());
        List<String> tokens = new ArrayList<>();
        for (String token : rawTokens) {
            String trimmed = token.trim();
            if (trimmed.length() > 1 && !STOP_WORDS.contains(trimmed)) {
                tokens.add(trimmed);
            }
        }
        return tokens;
    }

    private Map<String, Double> computeTf(List<String> tokens) {
        Map<String, Double> tf = new HashMap<>();
        double total = tokens.size();
        for (String t : tokens) {
            tf.put(t, tf.getOrDefault(t, 0.0) + 1.0);
        }
        for (Map.Entry<String, Double> entry : tf.entrySet()) {
            entry.setValue(entry.getValue() / total);
        }
        return tf;
    }
}
