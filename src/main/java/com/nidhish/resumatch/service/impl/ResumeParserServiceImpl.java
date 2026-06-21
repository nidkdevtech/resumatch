package com.nidhish.resumatch.service.impl;

import com.nidhish.resumatch.service.ResumeParserService;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class ResumeParserServiceImpl implements ResumeParserService {

    @Override
    public String extractText(byte[] pdfBytes) {
        if (pdfBytes == null || pdfBytes.length == 0) {
            throw new IllegalArgumentException("PDF byte array cannot be null or empty.");
        }

        try (PDDocument document = Loader.loadPDF(pdfBytes)) {

            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);

        } catch (IOException e) {
            // Rethrow as a safe runtime exception with context
            throw new RuntimeException("Failed to read PDF from byte array: " + e.getMessage(), e);
        }
    }
}
