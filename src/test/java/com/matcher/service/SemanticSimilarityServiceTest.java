package com.matcher.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SemanticSimilarityServiceTest {

    private final SemanticSimilarityService similarityService = new SemanticSimilarityService();

    @Test
    public void testTfidfCosineSimilarityIdenticalDocs() {
        String doc = "We are seeking an experienced Java Software Engineer skilled in Spring Boot, MySQL, and Docker.";
        double score = similarityService.calculateTfidfCosineSimilarity(doc, doc);

        assertTrue(score >= 95.0, "Identical documents should yield near 100% similarity");
    }

    @Test
    public void testTfidfCosineSimilarityRelatedDocs() {
        String docA = "Java Software Developer with Spring Boot microservices, MySQL database, and Docker containerization.";
        String docB = "Looking for a backend developer in Java and Spring Boot to build REST APIs and deploy with Docker.";

        double score = similarityService.calculateTfidfCosineSimilarity(docA, docB);
        assertTrue(score > 30.0, "Related technical documents should have significant similarity");
    }

    @Test
    public void testTfidfCosineSimilarityUnrelatedDocs() {
        String docA = "Executive chef specializing in French pastry baking and culinary restaurant kitchen management.";
        String docB = "Senior Java developer building distributed cloud systems with Kubernetes and Apache Kafka.";

        double score = similarityService.calculateTfidfCosineSimilarity(docA, docB);
        assertTrue(score < 15.0, "Completely disjoint documents should have very low similarity");
    }
}
