package com.matcher.controller;

import com.matcher.model.JobPreset;
import com.matcher.model.SkillInfo;
import com.matcher.service.SkillExtractionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/jobs")
@CrossOrigin(origins = "*")
public class JobController {

    private final SkillExtractionService skillExtractionService;

    public JobController(SkillExtractionService skillExtractionService) {
        this.skillExtractionService = skillExtractionService;
    }

    /**
     * Parses raw job description text to extract required skills.
     */
    @PostMapping("/parse")
    public ResponseEntity<?> parseJobDescription(@RequestBody Map<String, String> payload) {
        String description = payload.get("description");
        if (description == null || description.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Job description cannot be empty."));
        }

        List<SkillInfo> requiredSkills = skillExtractionService.extractSkills(description);
        Map<String, Object> response = new HashMap<>();
        response.put("requiredSkills", requiredSkills);
        response.put("skillCount", requiredSkills.size());

        return ResponseEntity.ok(response);
    }

    /**
     * Returns curated realistic presets for immediate 1-click testing.
     */
    @GetMapping("/presets")
    public ResponseEntity<List<JobPreset>> getPresets() {
        List<JobPreset> presets = new ArrayList<>();

        // Preset 1: Senior Java Developer (from user's example with skill gaps)
        presets.add(new JobPreset(
                "java-backend",
                "Senior Java Backend Developer",
                "Acme Fintech Solutions",
                "We are seeking an experienced Senior Java Developer to build high-throughput transaction processing systems.\n\n" +
                "Key Responsibilities:\n" +
                "- Architect and maintain resilient microservices in Java and Spring Boot.\n" +
                "- Design high-performance relational schemas using MySQL and PostgreSQL.\n" +
                "- Build clean RESTful APIs and integrate event-driven messaging with Kafka.\n" +
                "- Containerize and deploy services using Docker and Kubernetes on AWS cloud.\n" +
                "- Apply rigorous Unit Testing using JUnit and Mockito.\n\n" +
                "Required Skills:\n" +
                "- Java (Core Java, Collections, Multithreading)\n" +
                "- Spring Boot & Spring Framework\n" +
                "- MySQL & REST API\n" +
                "- Git & CI/CD\n" +
                "- Docker\n" +
                "- AWS\n" +
                "- DSA (Data Structures & Algorithms)",
                "Vishnu Sharma\n" +
                "vishnu.dev@example.com | +1 (555) 234-5678 | San Francisco, CA\n\n" +
                "PROFESSIONAL SUMMARY:\n" +
                "Dedicated Software Engineer with 3+ years of hands-on experience in Java, Python, and modern web application development. Passionate about algorithms, scalable systems, and database engineering.\n\n" +
                "TECHNICAL SKILLS:\n" +
                "- Languages: Java, Python, C++, SQL, JavaScript\n" +
                "- Frameworks: Spring Framework, React, Next.js, Express.js\n" +
                "- Databases: MySQL, MongoDB, SQLite\n" +
                "- Core Concepts: DSA, OOP, System Design, REST API\n" +
                "- Version Control: Git, GitHub Actions\n\n" +
                "WORK EXPERIENCE:\n" +
                "Software Engineer - CloudTech Labs (2023 - Present)\n" +
                "- Designed database schemas in MySQL improving query latency by 35%.\n" +
                "- Developed backend modules in Java and Spring Framework handling 50k daily active users.\n" +
                "- Implemented RESTful APIs and automated test suites with JUnit.\n" +
                "- Utilized Git for version control and Agile Scrum sprints.",
                "Vishnu Sharma"
        ));

        // Preset 2: Full-Stack React & Node Engineer
        presets.add(new JobPreset(
                "fullstack-web",
                "Full-Stack Web Engineer",
                "Vanguard Digital",
                "We are looking for a versatile Full-Stack Engineer skilled in React, TypeScript, Node.js, and cloud deployments.\n\n" +
                "Requirements:\n" +
                "- 2+ years of experience with React, Next.js, and TypeScript.\n" +
                "- Strong server-side capabilities with Node.js, Express.js, and REST API design.\n" +
                "- Experience with MongoDB or PostgreSQL.\n" +
                "- Familiarity with Docker, TailwindCSS, Git, and CI/CD workflows.\n" +
                "- Understanding of Microservices and System Design.",
                "Alex Rivera\n" +
                "alex.rivera@example.com | Seattle, WA\n\n" +
                "Full-Stack Developer with 2+ years developing responsive web apps.\n" +
                "Skills: JavaScript, TypeScript, React, Next.js, Node.js, Express.js, MongoDB, TailwindCSS, Git, REST API, DSA.\n" +
                "Built multiple production web platforms using React, Node.js, and MongoDB.",
                "Alex Rivera"
        ));

        // Preset 3: AI / ML Engineer
        presets.add(new JobPreset(
                "ai-engineer",
                "Machine Learning & AI Engineer",
                "Cognitive AI Labs",
                "Looking for a Machine Learning Engineer to train models and develop intelligent applications.\n\n" +
                "Required Skills:\n" +
                "- Python, Scikit-Learn, TensorFlow, PyTorch\n" +
                "- Machine Learning, Deep Learning, NLP, Large Language Models\n" +
                "- Pandas, NumPy, FastAPI\n" +
                "- Docker & Git\n" +
                "- REST API integration",
                "Priya Patel\n" +
                "priya.patel@example.com\n\n" +
                "AI Researcher and Python Developer.\n" +
                "Skills: Python, Machine Learning, Deep Learning, NLP, PyTorch, TensorFlow, Pandas, NumPy, Scikit-Learn, Git, REST API.\n" +
                "Developed NLP classification pipelines and deployed machine learning APIs using FastAPI.",
                "Priya Patel"
        ));

        return ResponseEntity.ok(presets);
    }
}
