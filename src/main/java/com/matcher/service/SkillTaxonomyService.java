package com.matcher.service;

import com.matcher.model.SkillCategory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SkillTaxonomyService {

    public static class SkillDefinition {
        private final String canonicalName;
        private final SkillCategory category;
        private final List<String> aliases;
        private final int learningPrerequisiteLevel; // 1 = Foundational, 2 = Core, 3 = Advanced, 4 = Cloud/DevOps

        public SkillDefinition(String canonicalName, SkillCategory category, int level, String... aliases) {
            this.canonicalName = canonicalName;
            this.category = category;
            this.learningPrerequisiteLevel = level;
            List<String> list = new ArrayList<>();
            list.add(canonicalName.toLowerCase());
            for (String alias : aliases) {
                list.add(alias.toLowerCase());
            }
            this.aliases = Collections.unmodifiableList(list);
        }

        public String getCanonicalName() {
            return canonicalName;
        }

        public SkillCategory getCategory() {
            return category;
        }

        public List<String> getAliases() {
            return aliases;
        }

        public int getLearningPrerequisiteLevel() {
            return learningPrerequisiteLevel;
        }
    }

    private final Map<String, SkillDefinition> skillMap = new LinkedHashMap<>();
    private final Map<String, Map<String, Double>> semanticRelationships = new HashMap<>();

    public SkillTaxonomyService() {
        initializeTaxonomy();
        initializeSemanticRelationships();
    }

    private void addSkill(String name, SkillCategory category, int level, String... aliases) {
        SkillDefinition def = new SkillDefinition(name, category, level, aliases);
        skillMap.put(name.toLowerCase(), def);
    }

    private void addRelation(String skillA, String skillB, double similarity) {
        semanticRelationships.computeIfAbsent(skillA.toLowerCase(), k -> new HashMap<>())
                .put(skillB.toLowerCase(), similarity);
        semanticRelationships.computeIfAbsent(skillB.toLowerCase(), k -> new HashMap<>())
                .put(skillA.toLowerCase(), similarity);
    }

    private void initializeTaxonomy() {
        // --- Programming Languages ---
        addSkill("Java", SkillCategory.PROGRAMMING_LANGUAGE, 1, "java", "core java", "java se", "java ee", "java 8", "java 11", "java 17", "java 21");
        addSkill("Python", SkillCategory.PROGRAMMING_LANGUAGE, 1, "python", "python3", "py");
        addSkill("C++", SkillCategory.PROGRAMMING_LANGUAGE, 1, "c++", "cpp");
        addSkill("C#", SkillCategory.PROGRAMMING_LANGUAGE, 1, "c#", "csharp", ".net c#");
        addSkill("C", SkillCategory.PROGRAMMING_LANGUAGE, 1, "c language");
        addSkill("JavaScript", SkillCategory.PROGRAMMING_LANGUAGE, 1, "javascript", "js", "ecmascript", "es6");
        addSkill("TypeScript", SkillCategory.PROGRAMMING_LANGUAGE, 2, "typescript", "ts");
        addSkill("Go", SkillCategory.PROGRAMMING_LANGUAGE, 2, "golang", "go language");
        addSkill("Rust", SkillCategory.PROGRAMMING_LANGUAGE, 2, "rustlang");
        addSkill("Kotlin", SkillCategory.PROGRAMMING_LANGUAGE, 2, "kotlin");
        addSkill("Swift", SkillCategory.PROGRAMMING_LANGUAGE, 2, "swift");
        addSkill("SQL", SkillCategory.PROGRAMMING_LANGUAGE, 1, "sql", "structured query language");
        addSkill("PHP", SkillCategory.PROGRAMMING_LANGUAGE, 1, "php", "php8");
        addSkill("Ruby", SkillCategory.PROGRAMMING_LANGUAGE, 1, "ruby");
        addSkill("Scala", SkillCategory.PROGRAMMING_LANGUAGE, 2, "scala");
        addSkill("R", SkillCategory.PROGRAMMING_LANGUAGE, 2, "r programming", "r-lang");
        addSkill("Dart", SkillCategory.PROGRAMMING_LANGUAGE, 2, "dart");
        addSkill("Shell Scripting", SkillCategory.PROGRAMMING_LANGUAGE, 2, "bash", "shell", "powershell", "zsh");

        // --- Frameworks & Libraries ---
        addSkill("Spring Boot", SkillCategory.FRAMEWORK, 2, "spring boot", "springboot", "spring-boot");
        addSkill("Spring Framework", SkillCategory.FRAMEWORK, 2, "spring framework", "spring mvc", "spring core", "spring");
        addSkill("Spring Cloud", SkillCategory.FRAMEWORK, 3, "spring cloud", "spring-cloud");
        addSkill("Hibernate", SkillCategory.FRAMEWORK, 2, "hibernate", "jpa", "spring data jpa");
        addSkill("React", SkillCategory.FRAMEWORK, 2, "react", "react.js", "reactjs");
        addSkill("Next.js", SkillCategory.FRAMEWORK, 3, "next.js", "nextjs", "next");
        addSkill("Angular", SkillCategory.FRAMEWORK, 2, "angular", "angular.js", "angularjs", "angular 2+");
        addSkill("Vue.js", SkillCategory.FRAMEWORK, 2, "vue", "vue.js", "vuejs");
        addSkill("Node.js", SkillCategory.FRAMEWORK, 2, "node.js", "nodejs", "node");
        addSkill("Express.js", SkillCategory.FRAMEWORK, 2, "express", "express.js", "expressjs");
        addSkill("NestJS", SkillCategory.FRAMEWORK, 3, "nestjs", "nest.js");
        addSkill("Django", SkillCategory.FRAMEWORK, 2, "django");
        addSkill("FastAPI", SkillCategory.FRAMEWORK, 2, "fastapi", "fast api");
        addSkill("Flask", SkillCategory.FRAMEWORK, 2, "flask");
        addSkill("ASP.NET", SkillCategory.FRAMEWORK, 2, "asp.net", "asp.net core", ".net core", "dotnet");
        addSkill("Ruby on Rails", SkillCategory.FRAMEWORK, 2, "rails", "ruby on rails");
        addSkill("TailwindCSS", SkillCategory.FRAMEWORK, 2, "tailwindcss", "tailwind");
        addSkill("Bootstrap", SkillCategory.FRAMEWORK, 1, "bootstrap", "bootstrap 5");
        addSkill("GraphQL", SkillCategory.FRAMEWORK, 3, "graphql", "apollo graphql");

        // --- Databases & Storage ---
        addSkill("MySQL", SkillCategory.DATABASE, 2, "mysql");
        addSkill("PostgreSQL", SkillCategory.DATABASE, 2, "postgresql", "postgres", "psql");
        addSkill("MongoDB", SkillCategory.DATABASE, 2, "mongodb", "mongo");
        addSkill("Redis", SkillCategory.DATABASE, 3, "redis");
        addSkill("Cassandra", SkillCategory.DATABASE, 3, "cassandra", "apache cassandra");
        addSkill("SQLite", SkillCategory.DATABASE, 1, "sqlite");
        addSkill("Oracle", SkillCategory.DATABASE, 2, "oracle db", "oracle database", "pl/sql");
        addSkill("Elasticsearch", SkillCategory.DATABASE, 3, "elasticsearch", "elastic search", "elk");
        addSkill("DynamoDB", SkillCategory.DATABASE, 3, "dynamodb", "aws dynamodb");
        addSkill("MariaDB", SkillCategory.DATABASE, 2, "mariadb");
        addSkill("Neo4j", SkillCategory.DATABASE, 3, "neo4j", "graph database");

        // --- Cloud & DevOps ---
        addSkill("Docker", SkillCategory.CLOUD_DEVOPS, 3, "docker", "docker container", "containerization");
        addSkill("Kubernetes", SkillCategory.CLOUD_DEVOPS, 4, "kubernetes", "k8s");
        addSkill("AWS", SkillCategory.CLOUD_DEVOPS, 3, "aws", "amazon web services", "ec2", "s3", "lambda");
        addSkill("Azure", SkillCategory.CLOUD_DEVOPS, 3, "azure", "microsoft azure");
        addSkill("Google Cloud", SkillCategory.CLOUD_DEVOPS, 3, "gcp", "google cloud", "google cloud platform");
        addSkill("Jenkins", SkillCategory.CLOUD_DEVOPS, 3, "jenkins");
        addSkill("CI/CD", SkillCategory.CLOUD_DEVOPS, 3, "ci/cd", "cicd", "continuous integration", "continuous deployment");
        addSkill("GitHub Actions", SkillCategory.CLOUD_DEVOPS, 3, "github actions", "gh actions");
        addSkill("Terraform", SkillCategory.CLOUD_DEVOPS, 4, "terraform", "iac", "infrastructure as code");
        addSkill("Linux", SkillCategory.CLOUD_DEVOPS, 2, "linux", "ubuntu", "debian", "centos", "redhat");
        addSkill("Git", SkillCategory.CLOUD_DEVOPS, 1, "git", "github", "gitlab", "version control");
        addSkill("Nginx", SkillCategory.CLOUD_DEVOPS, 3, "nginx", "reverse proxy");
        addSkill("Ansible", SkillCategory.CLOUD_DEVOPS, 4, "ansible");

        // --- Architecture & Concepts ---
        addSkill("REST API", SkillCategory.ARCHITECTURE_CONCEPT, 2, "rest api", "rest apis", "restful api", "restful apis", "restful", "rest");
        addSkill("Microservices", SkillCategory.ARCHITECTURE_CONCEPT, 3, "microservices", "microservice architecture", "micro-services");
        addSkill("DSA", SkillCategory.ARCHITECTURE_CONCEPT, 1, "dsa", "data structures and algorithms", "data structures & algorithms", "data structures", "algorithms");
        addSkill("System Design", SkillCategory.ARCHITECTURE_CONCEPT, 3, "system design", "distributed systems", "high level design", "low level design", "hld", "lld");
        addSkill("OOP", SkillCategory.ARCHITECTURE_CONCEPT, 1, "oop", "object oriented programming", "oops");
        addSkill("Kafka", SkillCategory.ARCHITECTURE_CONCEPT, 3, "kafka", "apache kafka", "event streaming", "message broker");
        addSkill("RabbitMQ", SkillCategory.ARCHITECTURE_CONCEPT, 3, "rabbitmq");
        addSkill("Agile", SkillCategory.ARCHITECTURE_CONCEPT, 1, "agile", "scrum", "kanban", "sprint");
        addSkill("Design Patterns", SkillCategory.ARCHITECTURE_CONCEPT, 2, "design patterns", "solid principles", "mvc");
        addSkill("WebSockets", SkillCategory.ARCHITECTURE_CONCEPT, 3, "websockets", "websocket");

        // --- AI, ML & Data Science ---
        addSkill("Machine Learning", SkillCategory.AI_DATA, 3, "machine learning", "ml");
        addSkill("Deep Learning", SkillCategory.AI_DATA, 4, "deep learning", "dl", "neural networks");
        addSkill("NLP", SkillCategory.AI_DATA, 3, "nlp", "natural language processing");
        addSkill("TensorFlow", SkillCategory.AI_DATA, 3, "tensorflow");
        addSkill("PyTorch", SkillCategory.AI_DATA, 3, "pytorch");
        addSkill("Pandas", SkillCategory.AI_DATA, 2, "pandas");
        addSkill("NumPy", SkillCategory.AI_DATA, 2, "numpy");
        addSkill("Scikit-Learn", SkillCategory.AI_DATA, 3, "scikit-learn", "sklearn");
        addSkill("Large Language Models", SkillCategory.AI_DATA, 4, "llm", "llms", "large language models", "generative ai", "genai");
        addSkill("Computer Vision", SkillCategory.AI_DATA, 4, "computer vision", "opencv");

        // --- Testing & Developer Tools ---
        addSkill("JUnit", SkillCategory.TESTING_TOOL, 2, "junit", "junit5", "junit 5");
        addSkill("Mockito", SkillCategory.TESTING_TOOL, 2, "mockito");
        addSkill("Postman", SkillCategory.TESTING_TOOL, 1, "postman");
        addSkill("Maven", SkillCategory.TESTING_TOOL, 2, "maven", "mvn");
        addSkill("Gradle", SkillCategory.TESTING_TOOL, 2, "gradle");
        addSkill("Swagger", SkillCategory.TESTING_TOOL, 2, "swagger", "openapi");
        addSkill("JIRA", SkillCategory.TESTING_TOOL, 1, "jira");
    }

    private void initializeSemanticRelationships() {
        // Spring ecosystem
        addRelation("Spring Boot", "Spring Framework", 0.90);
        addRelation("Spring Boot", "Spring Cloud", 0.85);
        addRelation("Spring Boot", "Hibernate", 0.80);
        addRelation("Spring Framework", "Hibernate", 0.80);
        addRelation("Spring Boot", "Java", 0.85);

        // React ecosystem
        addRelation("React", "Next.js", 0.88);
        addRelation("React", "JavaScript", 0.85);
        addRelation("React", "TypeScript", 0.85);

        // JavaScript / TypeScript / Node
        addRelation("JavaScript", "TypeScript", 0.88);
        addRelation("Node.js", "Express.js", 0.90);
        addRelation("Node.js", "NestJS", 0.85);
        addRelation("Node.js", "JavaScript", 0.85);

        // Databases & SQL
        addRelation("SQL", "MySQL", 0.90);
        addRelation("SQL", "PostgreSQL", 0.90);
        addRelation("SQL", "Oracle", 0.85);
        addRelation("SQL", "SQLite", 0.80);
        addRelation("MySQL", "PostgreSQL", 0.85);
        addRelation("MySQL", "MariaDB", 0.92);
        addRelation("MongoDB", "Redis", 0.75);

        // Containers & Cloud
        addRelation("Docker", "Kubernetes", 0.85);
        addRelation("AWS", "Azure", 0.78);
        addRelation("AWS", "Google Cloud", 0.78);
        addRelation("Azure", "Google Cloud", 0.78);
        addRelation("CI/CD", "Jenkins", 0.85);
        addRelation("CI/CD", "GitHub Actions", 0.88);
        addRelation("Docker", "CI/CD", 0.80);

        // Architecture & APIs
        addRelation("REST API", "GraphQL", 0.75);
        addRelation("REST API", "Microservices", 0.80);
        addRelation("REST API", "Swagger", 0.80);
        addRelation("Kafka", "RabbitMQ", 0.85);
        addRelation("Microservices", "Docker", 0.80);
        addRelation("Microservices", "Kubernetes", 0.80);

        // AI & Machine Learning
        addRelation("Machine Learning", "Deep Learning", 0.88);
        addRelation("Machine Learning", "NLP", 0.85);
        addRelation("Machine Learning", "Scikit-Learn", 0.90);
        addRelation("Deep Learning", "TensorFlow", 0.90);
        addRelation("Deep Learning", "PyTorch", 0.90);
        addRelation("TensorFlow", "PyTorch", 0.88);
        addRelation("Large Language Models", "NLP", 0.90);
        addRelation("Python", "Machine Learning", 0.80);
        addRelation("Python", "FastAPI", 0.85);
        addRelation("Python", "Django", 0.85);
    }

    public Collection<SkillDefinition> getAllSkills() {
        return skillMap.values();
    }

    public SkillDefinition findSkill(String name) {
        if (name == null) return null;
        return skillMap.get(name.toLowerCase());
    }

    /**
     * Finds semantic similarity score (0.0 to 1.0) between two skill names.
     */
    public double getSemanticSimilarity(String skillA, String skillB) {
        if (skillA == null || skillB == null) return 0.0;
        if (skillA.equalsIgnoreCase(skillB)) return 1.0;

        Map<String, Double> relsA = semanticRelationships.get(skillA.toLowerCase());
        if (relsA != null && relsA.containsKey(skillB.toLowerCase())) {
            return relsA.get(skillB.toLowerCase());
        }
        return 0.0;
    }

    /**
     * Checks if a skill in a list has a strong semantic relation to the target skill.
     * Returns the best matching related skill and its similarity weight, or null if none above threshold.
     */
    public Map.Entry<String, Double> findBestSemanticMatch(String targetSkill, Collection<String> candidateSkills) {
        if (targetSkill == null || candidateSkills == null || candidateSkills.isEmpty()) {
            return null;
        }

        String targetLower = targetSkill.toLowerCase();
        Map<String, Double> rels = semanticRelationships.get(targetLower);
        if (rels == null) return null;

        String bestMatch = null;
        double bestScore = 0.0;

        for (String candidate : candidateSkills) {
            String candLower = candidate.toLowerCase();
            if (rels.containsKey(candLower)) {
                double score = rels.get(candLower);
                if (score > bestScore && score >= 0.70) {
                    bestScore = score;
                    bestMatch = candidate;
                }
            }
        }

        if (bestMatch != null) {
            return new AbstractMap.SimpleEntry<>(bestMatch, bestScore);
        }
        return null;
    }
}
