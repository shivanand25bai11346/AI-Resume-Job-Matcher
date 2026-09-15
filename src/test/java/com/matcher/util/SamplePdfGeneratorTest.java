package com.matcher.util;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class SamplePdfGeneratorTest {

    @Test
    public void generateSamplePdf() throws IOException {
        File file = new File("sample-resume-vishnu.pdf");

        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage();
            doc.addPage(page);

            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                cs.beginText();
                cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 16);
                cs.newLineAtOffset(50, 750);
                cs.showText("Vishnu Sharma");
                cs.endText();

                cs.beginText();
                cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 10);
                cs.newLineAtOffset(50, 730);
                cs.showText("vishnu.sharma@example.com | +1 (555) 019-2834 | San Francisco, CA | github.com/vishnu-dev");
                cs.endText();

                cs.beginText();
                cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 12);
                cs.newLineAtOffset(50, 690);
                cs.showText("PROFESSIONAL SUMMARY");
                cs.endText();

                cs.beginText();
                cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 10);
                cs.newLineAtOffset(50, 670);
                cs.showText("Software Engineer with 3+ years experience building web systems and backend services.");
                cs.endText();

                cs.beginText();
                cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 12);
                cs.newLineAtOffset(50, 630);
                cs.showText("TECHNICAL SKILLS");
                cs.endText();

                cs.beginText();
                cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 10);
                cs.newLineAtOffset(50, 610);
                cs.showText("Programming Languages: Java, Python, C++, SQL, JavaScript");
                cs.newLineAtOffset(0, -18);
                cs.showText("Frameworks: Spring Framework, React, Next.js, Express.js");
                cs.newLineAtOffset(0, -18);
                cs.showText("Databases: MySQL, MongoDB, SQLite");
                cs.newLineAtOffset(0, -18);
                cs.showText("Concepts & Tools: DSA, OOP, REST API, Git, System Design");
                cs.endText();

                cs.beginText();
                cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 12);
                cs.newLineAtOffset(50, 510);
                cs.showText("WORK EXPERIENCE");
                cs.endText();

                cs.beginText();
                cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 10);
                cs.newLineAtOffset(50, 490);
                cs.showText("Software Engineer - Apex Cloud Solutions (2023 - Present)");
                cs.newLineAtOffset(0, -18);
                cs.showText("- Developed high-performance backend modules in Java and Spring Framework.");
                cs.newLineAtOffset(0, -18);
                cs.showText("- Optimized MySQL relational queries, reducing latency by 32%.");
                cs.newLineAtOffset(0, -18);
                cs.showText("- Implemented RESTful web endpoints and collaborated across agile sprints.");
                cs.endText();
            }

            doc.save(file);
        }

        assertTrue(file.exists());
        assertTrue(file.length() > 0);
    }
}
