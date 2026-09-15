package com.matcher.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class PdfExtractionServiceTest {

    private final PdfExtractionService pdfExtractionService = new PdfExtractionService();

    @Test
    public void testExtractTextFromPdfBytes() throws IOException {
        // Create an in-memory PDF using PDFBox 3.x
        byte[] pdfBytes;
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage();
            doc.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(doc, page)) {
                contentStream.beginText();
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 12);
                contentStream.newLineAtOffset(50, 700);
                contentStream.showText("Vishnu Sharma - Java Software Engineer");
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Skills: Java, Spring Boot, MySQL, REST API, Git, Docker");
                contentStream.endText();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            doc.save(baos);
            pdfBytes = baos.toByteArray();
        }

        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);

        // Extract text
        String extractedText = pdfExtractionService.extractTextFromBytes(pdfBytes);
        assertNotNull(extractedText);
        assertTrue(extractedText.contains("Vishnu Sharma"));
        assertTrue(extractedText.contains("Java"));
        assertTrue(extractedText.contains("Spring Boot"));
        assertTrue(extractedText.contains("MySQL"));
    }
}
