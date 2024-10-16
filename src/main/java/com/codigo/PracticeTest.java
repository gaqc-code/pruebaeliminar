package com.codigo;

import org.apache.poi.xwpf.usermodel.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

class Question {
    String question;
    List<String> options;
    List<String> correctAnswers;

    public Question(String question, List<String> options, List<String> correctAnswers) {
        this.question = question;
        this.options = options;
        this.correctAnswers = correctAnswers;
    }
}

public class PracticeTest {

    public static void main(String[] args) {
        List<Question> questions = readQuestionsFromWord("src/main/resources/Preguntas_examen.docx");
        takeTest(questions);
    }

    public static List<Question> readQuestionsFromWord(String filePath) {
        List<Question> questions = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(filePath);
             XWPFDocument document = new XWPFDocument(fis)) {

            List<XWPFParagraph> paragraphs = document.getParagraphs();
            String question = "";
            List<String> options = new ArrayList<>();
            List<String> correctAnswers = new ArrayList<>();
            for (XWPFParagraph para : paragraphs) {
                String text = para.getText().trim();
                if (text.startsWith("Q")) {
                    // Nueva pregunta detectada
                    if (!question.isEmpty()) {
                        questions.add(new Question(question, new ArrayList<>(options), new ArrayList<>(correctAnswers)));
                    }
                    question = text.trim();  // Asume que empieza con algo como "Q1: "
                    options.clear();
                    correctAnswers.clear();
                } else if (isOption(para)) {
                    // Opción de respuesta
                    String optionText = getFullText(para).trim();
                    boolean isCorrect = false;

                    // Recorre los 'runs' para ver si está resaltado
                    for (XWPFRun run : para.getRuns()) {
                        if (run.isHighlighted()) {
                            isCorrect = true;
                            break;
                        }
                    }

                    options.add(optionText);
                    if (isCorrect) {
                        correctAnswers.add(optionText.substring(0,1));
                        }
                }
            }
            // Agregar la última pregunta
            if (!question.isEmpty()) {
                questions.add(new Question(question, options, correctAnswers));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return questions;
    }

    // Método para detectar si el párrafo es una opción (empieza con a), b), c), etc.)
    private static boolean isOption(XWPFParagraph para) {
        String text = getFullText(para).trim();
        return !text.startsWith("Q");
    }

    // Método para obtener el texto completo del párrafo, incluyendo corridas (runs) con numeración/tabulación
    private static String getFullText(XWPFParagraph para) {
        StringBuilder fullText = new StringBuilder();
        for (XWPFRun run : para.getRuns()) {
            fullText.append(run.text());
        }
        return fullText.toString();
    }

    public static void takeTest(List<Question> questions) {

        Scanner scanner = new Scanner(System.in);
        int score = 0;
        int cantidad_preguntas = 5;
        int count_q = cantidad_preguntas;
        int new_random = 0;
        Random aleatorio = new Random(System.currentTimeMillis());
        List <Integer> num_q = new ArrayList<Integer>();;
        for (int i = 1 ; i <= count_q; i++){
            new_random = aleatorio.nextInt(questions.size());
            if (!num_q.contains(new_random)) {
                num_q.add(new_random);
            } else {
                i--;
            }
        }

        while (count_q != 0) {
            System.out.println(questions.get(num_q.get(count_q-1)).question);
            int contador = 0;
            for (String option : questions.get(num_q.get(count_q-1)).options) {
                System.out.println(option);
            }
            System.out.print("Elige una o más opciones (separadas por comas): ");
            String[] userAnswers = scanner.nextLine().split(",");

            boolean isCorrect = true;
            for (String answer : userAnswers) {
                if (!questions.get(num_q.get(count_q-1)).correctAnswers.contains(answer.trim())) {
                    isCorrect = false;
                    break;
                }
            }

            if (isCorrect && userAnswers.length == questions.get(num_q.get(count_q-1)).correctAnswers.size()) {
                System.out.println("Correcto!");
                score++;
            } else {
                System.out.println("Incorrecto. Las respuestas correctas eran: " + String.join(", ", questions.get(num_q.get(count_q-1)).correctAnswers));
            }
            System.out.println();
            count_q--;
        }

        System.out.println("Tu puntaje final es: " + score + " de " + cantidad_preguntas);
    }
}
