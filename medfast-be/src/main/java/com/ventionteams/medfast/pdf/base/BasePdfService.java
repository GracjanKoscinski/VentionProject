package com.ventionteams.medfast.pdf.base;

import com.ventionteams.medfast.exception.medicaltestappointment.PdfGenerationException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Random;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

/**
 * Base class for pdf generation.
 */
public class BasePdfService implements PdfService {

  /**
   * Generates basic pdf with information and logo.
   */
  public byte[] generatePdf(String[] pdfInfo, String title, PDRectangle pageSize) {

    try (PDDocument document = new PDDocument()) {
      PDPage page = new PDPage(pageSize);
      document.addPage(page);
      try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
        String logoImagePath = Objects.requireNonNull(
            getClass().getResource("/templates/logos/logo.png")).getPath();
        PDImageXObject logoImage = PDImageXObject.createFromFile(logoImagePath, document);
        float logoWidth = 150;
        float logoHeight = 50;
        float marginTop = 50;
        float marginRight = 100;
        float logoX = page.getMediaBox().getWidth() - marginRight - logoWidth;
        float logoY = page.getMediaBox().getHeight() - marginTop - logoHeight;
        contentStream.drawImage(logoImage, logoX, logoY, logoWidth, logoHeight);

        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 20);
        float titleX = 100;
        float titleY = logoY - logoHeight / 2;
        contentStream.beginText();
        contentStream.newLineAtOffset(titleX, titleY);
        contentStream.showText(title);
        contentStream.endText();

        int yaxisOffset = 700;
        contentStream.setFont(PDType1Font.HELVETICA, 12);
        for (String info : pdfInfo) {
          drawText(contentStream, info, 100, yaxisOffset);
          yaxisOffset -= 20;
        }
        contentStream.close();

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        document.save(byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
      }

    } catch (IOException e) {
      throw new PdfGenerationException(String.format(
          "PDF generation failed for title '%s': %s", title, e.getMessage()));
    }
  }

  /**
   * Adds table with given content to the pdf.
   */
  public byte[] addTableToPdf(String[] headers, String[][] content, int yaxisStart, byte[] pdf) {
    try (PDDocument document = PDDocument.load(pdf)) {
      PDPage page = document.getPage(0);
      try (PDPageContentStream contentStream =
          new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true)) {
        float margin = 100;
        float tableWidth = 110 * headers.length;
        float yaxisPosition = yaxisStart;
        float rowHeight = 20f;
        float cellMargin = 10f;
        float yaxisMargin = 5f;

        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
        for (int i = 0; i < headers.length; i++) {
          drawText(contentStream, headers[i],
              margin + i * (tableWidth / headers.length) + cellMargin,
              yaxisPosition + yaxisMargin);
        }
        yaxisPosition -= rowHeight;

        contentStream.setFont(PDType1Font.HELVETICA, 12);
        for (String[] row : content) {
          for (int i = 0; i < row.length; i++) {
            drawText(contentStream, row[i],
                margin + i * (tableWidth / headers.length) + cellMargin,
                yaxisPosition + yaxisMargin);
          }
          yaxisPosition -= rowHeight;
        }

        contentStream.setLineWidth(1f);
        yaxisPosition = yaxisStart;
        for (int i = 0; i <= content.length; i++) {
          contentStream.moveTo(margin, yaxisPosition);
          contentStream.lineTo(margin + tableWidth, yaxisPosition);
          contentStream.stroke();
          yaxisPosition -= rowHeight;
        }
        float nextX = margin;
        for (int i = 0; i <= headers.length; i++) {
          contentStream.moveTo(nextX, yaxisStart);
          contentStream.lineTo(nextX, yaxisPosition + rowHeight);
          contentStream.stroke();
          nextX += tableWidth / headers.length;
        }
        contentStream.close();

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        document.save(byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
      }

    } catch (IOException e) {
      throw new PdfGenerationException(String.format("Error adding table to PDF: %s.",
          e.getMessage()));
    }
  }

  /**
   * Adds mock doctor's signature with given content to the pdf.
   */
  public byte[] addDoctorSignatureToPdf(byte[] pdf, int yaxisStart) {
    try (PDDocument document = PDDocument.load(pdf)) {
      PDPage page = document.getPage(0);
      try (PDPageContentStream contentStream = new PDPageContentStream(document, page,
          PDPageContentStream.AppendMode.APPEND, true)) {
        String signatureImagePath = Objects.requireNonNull(
            getClass().getResource("/templates/logos/signature.png")).getPath();
        contentStream.setFont(PDType1Font.HELVETICA, 12);
        drawText(contentStream, "Doctor's Signature:", 100, yaxisStart - 40);
        PDImageXObject pdImage = PDImageXObject.createFromFile(signatureImagePath, document);
        contentStream.drawImage(pdImage, 50, yaxisStart - 120, 200, 50);
        contentStream.close();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        document.save(byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
      }
    } catch (IOException e) {
      throw new PdfGenerationException(String.format(
          "Error adding doctor's signature to PDF at y-axis position %s: %s.",
          yaxisStart, e.getMessage()));
    }
  }

  public String getRandomValue(double min, double max) {
    Random random = new Random();
    return String.format("%.1f", min + (max - min) * random.nextDouble());
  }

  public String getRandomInt(int min, int max) {
    Random random = new Random();
    return String.format("%,d", min + random.nextInt(max - min + 1));
  }

  /**
   * Draws text on the pdf.
   */
  public void drawText(PDPageContentStream contentStream,
      String text, float x, float y) throws IOException {
    contentStream.beginText();
    contentStream.newLineAtOffset(x, y);
    contentStream.showText(text);
    contentStream.endText();
  }
}
