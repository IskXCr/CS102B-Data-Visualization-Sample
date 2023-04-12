package com.example.demo;

import com.example.demo.model.CountryInfoEntry;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Stream;

public class DataManager {
    /**
     * WHO Data path
     */
    private static final Path dataSrcPath = Path.of("data");

    /**
     * Url that points to the link of the latest data file from W.H.O.
     */
    private static final URL dataUrl0;
    private static final String dataPrefix0 = "WHO-COVID-19-global-table-data-";
    private static final String dataSuffix0 = ".csv";

    /**
     * Storing data entries that correspond to different date.
     */
    private static final List<Path> dataEntry = new ArrayList<>();

    private static final Map<LocalDate, List<CountryInfoEntry>> entryMap = new TreeMap<>();


    // Perform simple initializations.
    static {
        try {
            dataUrl0 = new URL("https://covid19.who.int/WHO-COVID-19-global-table-data.csv");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        reloadAllDataFromFile();
        fetchLatestData();
    }

    public static void reloadAllDataFromFile() {
        clearAllData();

        try (Stream<Path> dataPaths = Files.list(dataSrcPath)) {
            dataPaths.filter(p -> p.toString().startsWith(dataSrcPath + File.separator + dataPrefix0)).peek(e -> System.out.println("Found data file: " + e)).forEach(dataEntry::add);
        } catch (IOException e) {
            System.err.println("Cannot load any data.");
        }
        dataEntry.forEach(e -> {
            try {
                LocalDate date = LocalDate.parse(e.getFileName().toString().replace(dataPrefix0, "").replace(dataSuffix0, ""), DateTimeFormatter.ISO_LOCAL_DATE);
                entryMap.put(date, Files.readAllLines(e).stream().skip(1).map(DataManager::processToken).toList());
                System.out.println("Loaded data from date: " + date);
            } catch (IOException ex) {
                System.err.println("Path: \"" + e + "\" cannot be read.");
            }
        });
    }

    public static List<CountryInfoEntry> fetchLatestData() {
        List<CountryInfoEntry> result;
        Path destPath = Path.of("data\\WHO-COVID-19-global-table-data-" + LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE) + ".csv");

        try (ReadableByteChannel readableByteChannel = Channels.newChannel(dataUrl0.openStream());
             FileOutputStream fos = new FileOutputStream(destPath.toFile());
             FileChannel fc = fos.getChannel()) {
            fc.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
            entryMap.put(LocalDate.now(), result = Files.readAllLines(destPath).stream().skip(1).map(DataManager::processToken).toList());
        } catch (Exception e) {
            System.err.println("Error occurred on fetching latest data.");
            e.printStackTrace();
            return Collections.emptyList();
        }

        return result;
    }

    public static List<CountryInfoEntry> getLatestEntry() {
        return entryMap.getOrDefault(LocalDate.now(), fetchLatestData());
    }


    // Following are utility routines.

    private static void clearAllData() {
        entryMap.clear();
        dataEntry.clear();
    }

    public static Map<LocalDate, List<CountryInfoEntry>> getEntryMap() {
        return entryMap;
    }

    public static List<CountryInfoEntry> readEntryFromFile(Path path) throws IOException {
        return Files.readAllLines(path).stream().skip(1).map(DataManager::processToken).toList();
    }

    public static void writeEntryToFile(Path path, List<CountryInfoEntry> entryList, String note) {
        try (FileWriter fw = new FileWriter(path.toFile());
             BufferedWriter bw = new BufferedWriter((fw))) {
            bw.write("DataEntry Created: " + LocalDateTime.now() + ". Note=\"" + note + "\"\n");
            entryList.forEach(e -> {
                try {
                    bw.write(e.name() + ","
                            + e.region() + ","
                            + e.caseCmltTotal() + ","
                            + e.caseCmltNorm() + ","
                            + e.caseReported7D() + ","
                            + e.caseReported7DNorm() + ","
                            + e.caseReported24H() + ","
                            + e.deathCmltTotal() + ","
                            + e.deathCmltNorm() + ","
                            + e.deathReported7D() + ","
                            + e.deathReported7DNorm() + ","
                            + e.deathReported24H() + ",\n");
                } catch (IOException ex) {
                    System.err.println("Error occurred while writing entry" + e + "to data.");
                    throw new RuntimeException(ex);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static CountryInfoEntry processToken(String s) {
        // Ref: https://stackoverflow.com/questions/18893390/splitting-on-comma-outside-quotes
//        String[] tokens = Pattern.compile("\"[^\"]*\"|[^,]+").matcher(s).results().map(MatchResult::group).toArray(String[]::new);
        String[] tokens = readCSVLine(s);
        try {
            return new CountryInfoEntry(
                    normalizeName(tokens[0]),
                    normalizeName(tokens[1]),
                    tryParseDouble(tokens[2]),
                    tryParseDouble(tokens[3]),
                    tryParseDouble(tokens[4]),
                    tryParseDouble(tokens[5]),
                    tryParseDouble(tokens[6]),
                    tryParseDouble(tokens[7]),
                    tryParseDouble(tokens[8]),
                    tryParseDouble(tokens[9]),
                    tryParseDouble(tokens[10]),
                    tryParseDouble(tokens[11]));
        } catch (IndexOutOfBoundsException e) {
            System.out.println(s);
            throw e;
        }
    }

    /**
     * Handles only one layer of quotes
     *
     * @param s
     * @return
     */
    private static String[] readCSVLine(String s) {
        List<String> result = new ArrayList<>();
        StringBuilder curr = new StringBuilder();
        boolean inQuote = false;
        for (char c : s.toCharArray()) {
            // If in quote, switch state
            if (c == '"') inQuote = !inQuote;
            else if (!inQuote) { // Else, do character appending
                if (c == ',') {
                    result.add(curr.toString());
                    curr = new StringBuilder();
                } else
                    curr.append(c);
            }
        }
        if (s.charAt(s.length() - 1) != ',')
            result.add(curr.toString());
        return result.toArray(String[]::new);
    }

    private static Double tryParseDouble(String s) {
        if (s == null || s.isBlank())
            return 0.0;
        else
            return Double.parseDouble(s);
    }

    private static String normalizeName(String s) {
        if (s.length() > 1 && s.startsWith("\"") && s.endsWith("\""))
            s = s.substring(1, s.length() - 1);
        return s;
    }
}
