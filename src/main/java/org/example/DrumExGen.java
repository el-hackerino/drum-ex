package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Map.entry;

public class DrumExGen {
    static String templateFilePath = "template.ly";
    static String samplesFilePath = "samples.txt";
    static String PH_TITLE = "<TITLE>";
    static String PH_SAMPLES = "<SAMPLES>";
    static String SAMPLE_SUFFIX_TOP = "_top";
    static String SAMPLE_SUFFIX_BOTTOM = "_btm";
    static String PH_NOTES_TOP = "<NOTES_TOP>";
    static String PH_NOTES_BOTTOM = "<NOTES_BOTTOM>";
    static String PH_BARS = "<NR_OF_BARS>";
    static String TITLE = "Random exercise 1";
    static final int numberOfBars = 30;
    static String outputFilePath = "output.ly";
    public static final Map<Character, String> NOTE_ABBREVIATIONS = Map.ofEntries(
            entry('b', "bd"),
            entry('s', "sn"),
            entry('-', "r")
    );
    static String[] durationMarkers = {"16", "8", "8.", "4"};

    static void main() throws IOException {
        List<Sample> samples = readSamples();
        String sampleString = printSamples(samples);
        Sample notes = printMelody(samples);
        String content = readTemplate();

        content = content.replace(PH_TITLE, TITLE);
        content = content.replace(PH_SAMPLES, sampleString);
        content = content.replace(PH_NOTES_TOP, notes.top());
        content = content.replace(PH_NOTES_BOTTOM, notes.bottom());
        content = content.replace(PH_BARS, numberOfBars + "");

        Files.writeString(Paths.get(outputFilePath), content);
        IO.println(content);
    }

    private static String printSamples(List<Sample> samples) {
        StringBuilder result = new StringBuilder();
        for (Sample sample : samples) {
            result.append(sample.name()).append(SAMPLE_SUFFIX_TOP).append(" = \\drummode { ").append(sample.top()).append(" }\n");
            result.append(sample.name()).append(SAMPLE_SUFFIX_BOTTOM).append(" = \\drummode { ").append(sample.bottom()).append(" }\n");
        }
        return result.toString();
    }

    private static List<Sample> readSamples() {
        InputStream inputStream = DrumExGen.class.getClassLoader().getResourceAsStream(samplesFilePath);
        assert inputStream != null;
        List<String> sampleCodes = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines().filter(l -> !l.isBlank() && !l.startsWith("%")).toList();
        List<Sample> samples = new ArrayList<>();
        int counter = 0;
        for (String sampleCode : sampleCodes) {
            StringBuilder topBuilder = new StringBuilder();
            StringBuilder bottomBuilder = new StringBuilder();
            int noteDuration = 0;
            char lastNote = '-';
            for (int pos = 0; pos < 4; pos++) {
                char c = sampleCode.charAt(pos);
                topBuilder.append(c == 's' ? "sn16 " : "hh16 ");
                if (c == '-') {
                    if (pos > 0) {
                        noteDuration++;
                    }
                } else {
                    if (pos > 0) {
                        bottomBuilder.append(NOTE_ABBREVIATIONS.get(lastNote));
                        bottomBuilder.append(durationMarkers[noteDuration]).append(" ");
                    }
                    lastNote = c;
                    noteDuration = 0;
                }
                if (pos == 3) {
                    bottomBuilder.append(NOTE_ABBREVIATIONS.get(lastNote));
                    bottomBuilder.append(durationMarkers[noteDuration]).append(" ");
                }
            }
            char character = (char) ('A' + counter);
            String sampleName = "c" + character;
            Sample sample = new Sample(topBuilder.toString(), bottomBuilder.toString(), sampleName);
            samples.add(sample);
            counter++;
        }
        return samples;
    }

    private static String readTemplate() {
        InputStream inputStream = DrumExGen.class.getClassLoader().getResourceAsStream(templateFilePath);
        assert inputStream != null;
        return new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
    }

    private static Sample printMelody(List<Sample> samples) {
        StringBuilder notesTop = new StringBuilder();
        StringBuilder notesBottom = new StringBuilder();
        for (int lineNr = 0; lineNr < numberOfBars / 3; lineNr++) {
            for (int barNr = 0; barNr < 3; barNr++) {
                for (int quarterNr = 0; quarterNr < 4; quarterNr++) {
                    Sample sample = selectSample(samples, quarterNr);
                    notesTop.append("\\").append(sample.name()).append(SAMPLE_SUFFIX_TOP).append(" ");
                    notesBottom.append("\\").append(sample.name()).append(SAMPLE_SUFFIX_BOTTOM).append(" ");
                }
                notesTop.append("| ");
                notesBottom.append("| ");
            }
            notesTop.append("\n        \\break");
            notesBottom.append("\n        \\break");
            if (lineNr < numberOfBars / 3 - 1) {
                notesTop.append("\n        ");
                notesBottom.append("\n        ");
            }
        }
        return new Sample(notesTop.toString(), notesBottom.toString(), "(Melody)");
    }

    private static Sample selectSample(List<Sample> samples, int k) {
        int sampleNr;
        Sample sample;
        boolean startsWithSnare;
        boolean startsWithBass;
        boolean startsWithPause;
        do {
            sampleNr = (int) (Math.random() * samples.size());
            sample = samples.get(sampleNr);
            startsWithSnare = sample.bottom().startsWith("sn");
            startsWithBass = sample.bottom().startsWith("bd");
            startsWithPause = sample.bottom().startsWith("r");
        } while (!((k == 0 && startsWithBass)
                || (k % 2 == 1 && startsWithSnare)
                || (k == 2 && (startsWithBass || startsWithPause))));
        return sample;
    }
}
