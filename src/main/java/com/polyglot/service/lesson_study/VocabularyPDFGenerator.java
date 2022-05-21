package com.polyglot.service.lesson_study;

import com.itextpdf.text.*;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.DottedLineSeparator;
import com.polyglot.model.WordToLearn;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service responsible for generating a pdf for the vocabulary of a lesson.
 */
@Service
public class VocabularyPDFGenerator {

    private static final Font TITLE_FONT = new Font(Font.FontFamily.TIMES_ROMAN, 36,
            Font.BOLD);
    private static final Font SUBTITLE_FONT = new Font(Font.FontFamily.TIMES_ROMAN, 22,
            Font.BOLD);
    private static final Font SUBTITLE_FONT2 = new Font(Font.FontFamily.TIMES_ROMAN, 20,
            Font.BOLD);
    private static final Font SMALL_BOLD_FONT = new Font(Font.FontFamily.TIMES_ROMAN, 16,
            Font.BOLD);
    private static final Font SMALL_FONT = new Font(Font.FontFamily.TIMES_ROMAN, 14);

    private static final String LOGO_PATH = "src/main/resources/img/lesson.png";


    /**
     * Generates a PDF for a lesson's vocabulary.
     * @param lessonTitle is the title of the lesson.
     * @param courseTitle is the title of the course to which the lesson belongs.
     * @param indexOfLessonInsideCourse is the ordered index of the lesson inside the course.
     * @param wordsToLearn is the list of words that the user has ever labelled as unknown.
     * @return the genarated pdf.
     */
    public ByteArrayInputStream createLessonVocabularyPDF(String lessonTitle, String courseTitle,
                                              int indexOfLessonInsideCourse,
                                              List<WordToLearn> wordsToLearn)  {
        try {
            Document document = new Document();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, out);

            document.open();
            addMetaData(document, lessonTitle);
            addContent(document, lessonTitle, courseTitle, indexOfLessonInsideCourse, wordsToLearn);
            document.close();

            return new ByteArrayInputStream(out.toByteArray());
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void addMetaData(Document document, String lessonTitle) {
        document.addTitle(String.format("%s's Vocabulary", lessonTitle));
        document.addSubject("vocabulary");
    }

    private void addContent(Document document,
                            String lessonTitle,
                            String courseTitle,
                            int indexOfLessonInsideCourse,
                            List<WordToLearn> wordsToLearn)
            throws DocumentException, IOException {
        addPrefaceParagraph(document, lessonTitle, courseTitle, indexOfLessonInsideCourse);

        List<WordToLearn> completedWords =
                wordsToLearn.stream()
                        .filter(wordToLearn -> wordToLearn.getCollectedPoints() >= wordToLearn.getLesson().getCourse().getMinPointsPerWord())
                        .collect(Collectors.toList());

        List<WordToLearn> inProgressWords =
                wordsToLearn.stream()
                        .filter(wordToLearn -> wordToLearn.getCollectedPoints() < wordToLearn.getLesson().getCourse().getMinPointsPerWord())
                        .collect(Collectors.toList());


        Paragraph tablePara1 = new Paragraph("Unknown Words",
                SUBTITLE_FONT2);
        tablePara1.setAlignment(Element.ALIGN_CENTER);
        addEmptyLine(tablePara1, 1);
        document.add(tablePara1);
        addVocabularyTable(document,inProgressWords);

        Paragraph paragraph = new Paragraph();
        addEmptyLine(paragraph, 2);
        document.add(paragraph);

        Paragraph tablePara2 = new Paragraph("Studied Words",
                SUBTITLE_FONT2);
        tablePara2.setAlignment(Element.ALIGN_CENTER);
        addEmptyLine(tablePara2, 1);
        document.add(tablePara2);
        addVocabularyTable(document,completedWords);
    }

    private void addPrefaceParagraph(Document document, String lessonTitle, String courseTitle,
                                     int indexOfLessonInsideCourse) throws DocumentException,
            IOException {
        Paragraph preface = new Paragraph();
        Image image = Image.getInstance(LOGO_PATH);
        image.setAlignment(Element.ALIGN_CENTER);
        image.scaleAbsolute(100, 100);
        preface.add(image);
        addEmptyLine(preface, 1);

        Paragraph subtitlePara = new Paragraph("Vocabulary of", SMALL_BOLD_FONT);
        subtitlePara.setAlignment(Element.ALIGN_CENTER);
        preface.add(subtitlePara);

        Paragraph titlePara = new Paragraph(lessonTitle, TITLE_FONT);
        titlePara.setAlignment(Element.ALIGN_CENTER);
        preface.add(titlePara);
        addEmptyLine(preface, 1);

        Paragraph subtitlePara2 = new Paragraph(String.format("Lesson nr. %d of course %s",
                indexOfLessonInsideCourse, courseTitle),
                SMALL_BOLD_FONT);
        subtitlePara2.setAlignment(Element.ALIGN_CENTER);
        preface.add(subtitlePara2);
        addEmptyLine(preface, 3);

        document.add(preface);
    }

    private void addVocabularyTable(Document document,
                                    List<WordToLearn> wordToLearnList) throws DocumentException {
        PdfPTable pdfPTable = new PdfPTable(2);


        for (WordToLearn wordToLearn : wordToLearnList) {
            PdfPCell pdfPCell1 = new PdfPCell(new Paragraph(wordToLearn.getOriginalWord()));
            PdfPCell pdfPCell2 = new PdfPCell(new Paragraph(wordToLearn.getTranslation()));
            pdfPTable.addCell(pdfPCell1);
            pdfPTable.addCell(pdfPCell2);
        }

        document.add(pdfPTable);
    }

    private void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }

}
