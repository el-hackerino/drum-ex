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

public class DrumExGen {
    static String templateFilePath = "template.ly";
    static String samplesFilePath = "samples.txt";
    static String PH_TITLE = "<TITLE>";
    static String PH_NOTES = "<NOTES>";
    static String PH_BARS = "<BARS>";
    static String TITLE = "Random exercise 1";
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
        InputStream inputStream = DrumExGen.class.getClassLoader().getResourceAsStream(samplesFilePath);
        assert inputStream != null;
        return new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines().filter(l -> !l.isBlank()).toList();
    }

    private static String readTemplate() {
        InputStream inputStream = DrumExGen.class.getClassLoader().getResourceAsStream(templateFilePath);
        assert inputStream != null;
        return new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
    }

    private static StringBuilder buildNotePart(List<String> samples) {
        StringBuilder notes = new StringBuilder();
        for (int lineNr = 0; lineNr < numberOfBars / 3; lineNr++) {
            for (int barNr = 0; barNr < 3; barNr++) {
                for (int quarterNr = 0; quarterNr < 4; quarterNr++) {
                    int sampleNr = findSample(samples, quarterNr);
                    notes.append(samples.get(sampleNr)).append(" ");
                }
                notes.append("| ");
            }
            notes.append("\n          \\break");
            if (lineNr < numberOfBars / 3 - 1) {
                notes.append("\n          ");
            }
        }
        return notes;
    }

    private static int findSample(List<String> combos, int k) {
        int sampleNr;
        String sample;
        boolean startsWithSnare;
        boolean startsWithBass;
        boolean startsWithPause;
        do {
            sampleNr = (int) (Math.random() * combos.size());
            sample = combos.get(sampleNr);
            startsWithSnare = sample.startsWith("sn");
            startsWithBass = sample.startsWith("bd");
            startsWithPause = sample.startsWith("r");
        } while (!((k == 0 && startsWithBass)
                || (k % 2 == 1 && startsWithSnare)
                || (k == 2 && (startsWithBass || startsWithPause))));
        IO.println(k + ":" + sample);
        return sampleNr;
    }
}
