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

@Service
public class PdfExportService {

    public ByteArrayInputStream generateExpensePdf(
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

            // 1. Header Title
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, new Color(30, 41, 59));
            Paragraph title = new Paragraph("SmartVault - Expense Statement", titleFont);
            title.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(title);

            // 2. Metadata Subtitle
            Font metaFont = FontFactory.getFont(FontFactory.HELVETICA, 10, new Color(100, 116, 139));
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd MMM yyyy");
            String period = "Period: " + startDate.format(dtf) + " to " + endDate.format(dtf);

            Paragraph sub = new Paragraph("Account: " + userEmail + "\n" + period + "\n\n", metaFont);
            sub.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(sub);

            // 3. Table Setup (4 Columns: Date, Category, Description, Amount)
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100f);
            table.setWidths(new float[]{2.5f, 3.0f, 4.5f, 2.5f});
            table.setSpacingBefore(10f);

            // 4. Table Headers
            Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Color.WHITE);
            String[] headers = {"Date", "Category", "Description", "Amount (INR)"};

            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headFont));
                cell.setBackgroundColor(new Color(37, 99, 235)); // Tailwind Blue-600
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(8f);
                table.addCell(cell);
            }

            // 5. Populate Data Rows
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
                        PdfPCell cell = new PdfPCell(new Phrase(row[i] != null ? row[i] : "-", dataFont));
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

        return new ByteArrayInputStream(out.toByteArray());
    }
}