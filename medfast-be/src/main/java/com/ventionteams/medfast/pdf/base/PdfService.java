package com.ventionteams.medfast.pdf.base;

import java.io.IOException;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

/**
 * Interface for pdf generation.
 */
public interface PdfService {

  /**
   * Generates a PDF document with the provided information and title.
   *
   * @param pdfInfo The information to be included in the PDF.
   * @param title The title of the PDF.
   * @param pageSize The size of the PDF page.
   * @return A byte array representing the generated PDF.
   */
  byte[] generatePdf(String[] pdfInfo, String title, PDRectangle pageSize);

  /**
   * Adds a table to the provided PDF document.
   *
   * @param headers The headers of the table.
   * @param content The content of the table.
   * @param yaxisStart The Y-axis starting position for the table.
   * @param pdf The PDF document to which the table will be added.
   * @return A byte array representing the modified PDF.
   * @throws IOException If an I/O error occurs.
   */
  byte[] addTableToPdf(String[] headers,
                      String[][] content,
                      int yaxisStart,
                      byte[] pdf) throws IOException;

  /**
   * Adds a doctor's signature to the provided PDF document.
   *
   * @param pdf The PDF document to which the signature will be added.
   * @param yaxisStart The Y-axis starting position for the signature.
   * @return A byte array representing the modified PDF.
   * @throws IOException If an I/O error occurs.
   */
  byte[] addDoctorSignatureToPdf(byte[] pdf, int yaxisStart) throws IOException;

  String getRandomValue(double min, double max);

  String getRandomInt(int min, int max);

  /**
   * Draws text at the specified position on the PDF page.
   *
   * @param contentStream The content stream used to draw text on the PDF page.
   * @param text The text to be drawn.
   * @param x The X-axis position.
   * @param y The Y-axis position.
   * @throws IOException If an I/O error occurs.
   */
  void drawText(PDPageContentStream contentStream,
                String text,
                float x,
                float y) throws IOException;

}
