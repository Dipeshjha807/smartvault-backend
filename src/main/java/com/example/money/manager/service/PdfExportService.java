package com.example.money.manager.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PdfExportService {

    // Helper: CategoryEntity(...) ke lambe string se sirf clean name extract karega
    private String cleanCategoryText(String raw) {
        if (raw == null || raw.trim().isEmpty() || raw.equalsIgnoreCase("null")) {
            return "-";
        }
        if (raw.startsWith("CategoryEntity(") || raw.contains("name=")) {
            Matcher matcher = Pattern.compile("name=([^,)]+)").matcher(raw);
            if (matcher.find()) {
                return matcher.group(1).trim();
            }
        }
        return raw;
    }

    // 1. Email Attachment ke liye byte array generate karne ka method
    public byte[] generateExpensePdfBytes(
            String userEmail,
            LocalDate startDate,
            LocalDate endDate,
            List<String[]> rows,
            double totalAmount
    ) {
        Document document = new Document(PageSize.A4, 36, 36, 40, 40);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Header Title
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, new Color(30, 41, 59));
            Paragraph title = new Paragraph("SmartVault - Expense Statement", titleFont);
            title.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(title);

            // Metadata Subtitle
            Font metaFont = FontFactory.getFont(FontFactory.HELVETICA, 10, new Color(100, 116, 139));
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd MMM yyyy");
            String period = "Period: " + startDate.format(dtf) + " to " + endDate.format(dtf);

            Paragraph sub = new Paragraph("Account: " + userEmail + "\n" + period + "\n\n", metaFont);
            sub.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(sub);

            // Table Setup (4 Columns: Date, Category, Description, Amount)
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100f);
            table.setWidths(new float[]{2.5f, 3.0f, 4.5f, 2.5f});
            table.setSpacingBefore(10f);

            // Table Headers
            Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Color.WHITE);
            String[] headers = {"Date", "Category", "Description", "Amount (INR)"};

            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headFont));
                cell.setBackgroundColor(new Color(37, 99, 235)); // Tailwind Blue-600
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(8f);
                table.addCell(cell);
            }

            // Populate Data Rows
            Font dataFont = FontFactory.getFont(FontFactory.HELVETICA, 10, new Color(51, 65, 85));
            if (rows.isEmpty()) {
                PdfPCell emptyCell = new PdfPCell(new Phrase("No expense records found for this period.", dataFont));
                emptyCell.setColspan(4);
                emptyCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                emptyCell.setPadding(12f);
                table.addCell(emptyCell);
            } else {
                for (String[] row : rows) {
                    for (int i = 0; i < row.length; i++) {
                        String cellValue = row[i] != null ? row[i] : "-";

                        // Column 1 Category hai, isko clean karega
                        if (i == 1) {
                            cellValue = cleanCategoryText(cellValue);
                        }

                        PdfPCell cell = new PdfPCell(new Phrase(cellValue, dataFont));
                        cell.setPadding(6f);
                        cell.setHorizontalAlignment(i == 3 ? Element.ALIGN_RIGHT : Element.ALIGN_LEFT);
                        table.addCell(cell);
                    }
                }

                // Total Summary Row
                Font totalFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, new Color(15, 23, 42));
                PdfPCell labelCell = new PdfPCell(new Phrase("Total Spending:", totalFont));
                labelCell.setColspan(3);
                labelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                labelCell.setPadding(6f);
                table.addCell(labelCell);

                PdfPCell totalValCell = new PdfPCell(new Phrase(String.format("₹ %.2f", totalAmount), totalFont));
                totalValCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                totalValCell.setPadding(6f);
                table.addCell(totalValCell);
            }

            document.add(table);
            document.close();

        } catch (DocumentException e) {
            throw new RuntimeException("Failed to create PDF statement: " + e.getMessage());
        }

        return out.toByteArray();
    }

    // 2. Direct Browser Download ke liye Stream method
    public ByteArrayInputStream generateExpensePdf(
            String userEmail,
            LocalDate startDate,
            LocalDate endDate,
            List<String[]> rows,
            double totalAmount
    ) {
        byte[] pdfBytes = generateExpensePdfBytes(userEmail, startDate, endDate, rows, totalAmount);
        return new ByteArrayInputStream(pdfBytes);
    }
}