package com.matcher.service;

import com.matcher.model.RoadmapStep;
import com.matcher.model.SkillCategory;
import com.matcher.model.SkillInfo;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SkillGapRoadmapService {

    private final SkillTaxonomyService taxonomyService;

    // Curated learning topics for prominent skills
    private static final Map<String, List<String>> SKILL_TOPICS = new HashMap<>();

    static {
        SKILL_TOPICS.put("spring boot", Arrays.asList(
                "Dependency Injection & Spring Beans",
                "Spring Boot Starters & Auto-Configuration",
                "Spring Data JPA & Repositories",
                "Spring Web MVC REST Controllers",
                "Exception Handling & Validation"
        ));
        SKILL_TOPICS.put("rest api", Arrays.asList(
                "HTTP Methods (GET, POST, PUT, DELETE) & Status Codes",
                "RESTful Resource Modeling & URI Design",
                "Request/Response Serialization (JSON)",
                "API Authentication (JWT, OAuth2)",
                "OpenAPI / Swagger Documentation"
        ));
        SKILL_TOPICS.put("docker", Arrays.asList(
                "Docker Architecture & Container Fundamentals",
                "Writing optimized Dockerfiles for Java/Node apps",
                "Docker Compose for Multi-Container Services",
                "Image Optimization & Multi-Stage Builds",
                "Container Networking & Volumes"
        ));
        SKILL_TOPICS.put("kubernetes", Arrays.asList(
                "Pods, Deployments, and ReplicaSets",
                "Services (ClusterIP, NodePort, LoadBalancer)",
                "ConfigMaps and Secrets Management",
                "Ingress Controllers and Routing",
                "Scaling & Self-Healing Mechanisms"
        ));
        SKILL_TOPICS.put("aws", Arrays.asList(
                "AWS IAM (Users, Roles, Policies)",
                "EC2 Compute Instances & VPC Networking",
                "S3 Object Storage & Lifecycle Rules",
                "RDS Managed Relational Databases",
                "AWS Lambda & Serverless Basics"
        ));
        SKILL_TOPICS.put("microservices", Arrays.asList(
                "Decomposing Monoliths into Microservices",
                "Inter-Service Communication (REST vs gRPC vs Messaging)",
                "API Gateway Pattern & Service Discovery",
                "Distributed Tracing & Centralized Logging",
                "Resilience Patterns (Circuit Breaker, Retry)"
        ));
        SKILL_TOPICS.put("mysql", Arrays.asList(
                "Relational Schema Design & Normalization",
                "Complex Joins, Grouping & Aggregations",
                "Indexes & Query Optimization (EXPLAIN)",
                "ACID Transactions & Isolation Levels"
        ));
        SKILL_TOPICS.put("postgresql", Arrays.asList(
                "Advanced SQL & JSONB Data Types",
                "Indexes (B-Tree, GIN) & Query Performance",
                "Stored Procedures & Triggers",
                "Connection Pooling with PgBouncer"
        ));
        SKILL_TOPICS.put("react", Arrays.asList(
                "JSX & Component Lifecycle",
                "State & Props Management with Hooks (useState, useEffect)",
                "Context API & Global State",
                "Routing with React Router",
                "Consuming REST APIs & Async Data Fetching"
        ));
        SKILL_TOPICS.put("ci/cd", Arrays.asList(
                "Continuous Integration Workflows",
                "Automated Testing & Linting in Pipelines",
                "Artifact Building & Container Publishing",
                "Deployment Strategies (Blue-Green, Canary)",
                "GitHub Actions or Jenkins Pipeline Setup"
        ));
        SKILL_TOPICS.put("git", Arrays.asList(
                "Branching Strategies (GitFlow, Trunk-Based)",
                "Merge vs Rebase Workflows",
                "Resolving Merge Conflicts",
                "Pull Requests & Code Review Best Practices"
        ));
        SKILL_TOPICS.put("dsa", Arrays.asList(
                "Arrays, Hash Maps, and Linked Lists",
                "Trees, Graphs, and BFS/DFS Traversals",
                "Sorting, Searching & Binary Search",
                "Dynamic Programming & Greedy Algorithms",
                "Time & Space Complexity Analysis (Big-O)"
        ));
        SKILL_TOPICS.put("system design", Arrays.asList(
                "Scalability, Availability & Reliability",
                "Load Balancing & Horizontal Scaling",
                "Caching Strategies (Redis, Memcached)",
                "Database Sharding & Replication",
                "Message Queues & Async Processing"
        ));
    }

    public SkillGapRoadmapService(SkillTaxonomyService taxonomyService) {
        this.taxonomyService = taxonomyService;
    }

    /**
     * Generates an ordered, actionable learning roadmap for missing skills.
     */
    public List<RoadmapStep> generateRoadmap(List<SkillInfo> missingSkills) {
        if (missingSkills == null || missingSkills.isEmpty()) {
            return Collections.emptyList();
        }

        // Sort missing skills by prerequisite level:
        // Level 1: Foundational (Languages, DSA, Git)
        // Level 2: Core Frameworks & APIs (Spring Boot, React, REST API, SQL)
        // Level 3: Architecture, Microservices, Caching, Containers (Docker, Redis, Kafka)
        // Level 4: Cloud & Orchestration (Kubernetes, AWS, Terraform)
        List<SkillInfo> sortedSkills = new ArrayList<>(missingSkills);
        sortedSkills.sort((a, b) -> {
            int levelA = getPrerequisiteLevel(a.getName());
            int levelB = getPrerequisiteLevel(b.getName());
            if (levelA != levelB) {
                return Integer.compare(levelA, levelB);
            }
            return a.getName().compareToIgnoreCase(b.getName());
        });

        List<RoadmapStep> roadmap = new ArrayList<>();
        int stepNum = 1;

        for (SkillInfo skill : sortedSkills) {
            String skillKey = skill.getName().toLowerCase();
            int level = getPrerequisiteLevel(skill.getName());

            String priority;
            String estimatedTime;
            if (level <= 2) {
                priority = "Critical Requirement";
                estimatedTime = "1 - 2 weeks";
            } else if (level == 3) {
                priority = "High Impact";
                estimatedTime = "2 - 3 weeks";
            } else {
                priority = "Advanced Specialization";
                estimatedTime = "3 - 4 weeks";
            }

            String rationale = buildRationale(skill.getName(), skill.getCategory(), level);
            List<String> topics = SKILL_TOPICS.getOrDefault(skillKey, buildDefaultTopics(skill.getName()));

            RoadmapStep step = new RoadmapStep(
                    stepNum++,
                    skill.getName(),
                    skill.getCategory(),
                    priority,
                    rationale,
                    estimatedTime,
                    topics
            );
            roadmap.add(step);
        }

        return roadmap;
    }

    private int getPrerequisiteLevel(String skillName) {
        SkillTaxonomyService.SkillDefinition def = taxonomyService.findSkill(skillName);
        if (def != null) {
            return def.getLearningPrerequisiteLevel();
        }
        return 2;
    }

    private String buildRationale(String skillName, SkillCategory category, int level) {
        if (category == SkillCategory.PROGRAMMING_LANGUAGE) {
            return "Mastering core language syntax and idioms is the bedrock for all associated libraries and technical interviews.";
        } else if (category == SkillCategory.FRAMEWORK) {
            return "Central to the employer's day-to-day stack. Building hands-on projects with this framework directly boosts hireability.";
        } else if (category == SkillCategory.DATABASE) {
            return "Essential for data modeling, transaction integrity, and query performance in production environments.";
        } else if (category == SkillCategory.CLOUD_DEVOPS) {
            return "Critical for modern cloud deployment, microservice containerization, and automated delivery pipelines.";
        } else if (category == SkillCategory.ARCHITECTURE_CONCEPT) {
            return "Required for designing scalable, maintainable distributed systems and passing technical architectural rounds.";
        } else if (category == SkillCategory.AI_DATA) {
            return "Enables data-driven intelligent features, model integration, and modern analytics.";
        } else {
            return "Key developer tooling required for quality assurance, automated testing, and team collaboration.";
        }
    }

    private List<String> buildDefaultTopics(String skillName) {
        return Arrays.asList(
                "Core Concepts & Architecture of " + skillName,
                "Hands-on Project Setup & Configuration",
                "Common Patterns, Best Practices & Anti-patterns",
                "Integration with Backend Services & Databases",
                "Unit Testing & Troubleshooting"
        );
    }
}
