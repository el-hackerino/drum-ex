package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    static String inputFilePath = "template.ly";
    static String combosFilePath = "combos.txt";
    static String PH_TITLE = "<TITLE>";
    static String PH_NOTES = "<NOTES>";
    static String PH_BARS = "<BARS>";
    static String TITLE = "Generated exercise 1";
    static final int numberOfBars = 30;
    static String outputFilePath = "output.ly";

    static void main() throws IOException {
        List<String> combos = readCombos();
        StringBuilder notes = buildNotePart(combos);
        String content = readTemplate();

        content = content.replace(PH_TITLE, TITLE);
        content = content.replace(PH_NOTES, notes);
        content = content.replace(PH_BARS, numberOfBars + "");

        Files.writeString(Paths.get(outputFilePath), content);
        IO.println(content);
    }

    private static List<String> readCombos() {
        InputStream inputStream = Main.class.getClassLoader().getResourceAsStream(combosFilePath);
        assert inputStream != null;
        return new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines().filter(l -> !l.isBlank()).toList();
    }

    private static String readTemplate() {
        InputStream inputStream = Main.class.getClassLoader().getResourceAsStream(inputFilePath);
        assert inputStream != null;
        return new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
    }

    private static StringBuilder buildNotePart(List<String> combos) {
        StringBuilder notes = new StringBuilder();
        for (int i = 0; i < numberOfBars / 3; i++) {
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 4; k++) {
                    int sampleNr = (int) (Math.random() * combos.size());
                    notes.append(combos.get(sampleNr)).append(" ");
                }
                notes.append("| ");
            }
            notes.append("\n          \\break");
            if (i < numberOfBars / 3 - 1) {
                notes.append("\n          ");
            }
        }
        return notes;
    }
}
