package com.matcher.service;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Service
public class PdfExtractionService {

    private static final Logger log = LoggerFactory.getLogger(PdfExtractionService.class);

    /**
     * Extracts text from an uploaded MultipartFile PDF using Apache PDFBox.
     */
    public String extractText(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Uploaded file is empty or null.");
        }
        byte[] bytes = file.getBytes();
        return extractTextFromBytes(bytes);
    }

    /**
     * Extracts text from PDF bytes using Apache PDFBox 3.x Loader.
     */
    public String extractTextFromBytes(byte[] pdfBytes) throws IOException {
        if (pdfBytes == null || pdfBytes.length == 0) {
            throw new IllegalArgumentException("PDF byte array is empty.");
        }

        try (PDDocument document = Loader.loadPDF(pdfBytes)) {
            if (document.isEncrypted()) {
                throw new IllegalStateException("The uploaded PDF is encrypted and cannot be parsed.");
            }

            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            String text = stripper.getText(document);

            // Clean up and normalize whitespace
            return normalizeExtractedText(text);
        } catch (IOException e) {
            log.error("Failed to parse PDF document: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Extracts text from an InputStream.
     */
    public String extractTextFromStream(InputStream inputStream) throws IOException {
        byte[] bytes = inputStream.readAllBytes();
        return extractTextFromBytes(bytes);
    }

    private String normalizeExtractedText(String text) {
        if (text == null) {
            return "";
        }
        // Normalize line breaks and multiple spaces
        return text.replaceAll("\r\n", "\n")
                   .replaceAll("\r", "\n")
                   .replaceAll("[ \t]+", " ")
                   .replaceAll("\n{3,}", "\n\n")
                   .trim();
    }
}
