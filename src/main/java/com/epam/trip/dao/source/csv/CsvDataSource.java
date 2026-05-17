package com.epam.trip.dao.source.csv;

import com.epam.trip.dao.source.DataSource;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public abstract class CsvDataSource implements DataSource {
    protected String filePath;

    public CsvDataSource(String filePath) {
        this.filePath = filePath;
    }

    public abstract List<String[]> readAll();

    public abstract void writeAll(List<String[]> data);

    protected List<String[]> readCsvFile() {
        List<String[]> data = new ArrayList<>();
        File file = findFile();

        if (file == null || !file.exists()) {
            System.err.println("CSV file not found: " + filePath);
            System.err.println("Tried locations:");
            System.err.println("  - " + filePath);
            System.err.println("  - src/main/resources/" + filePath);
            System.err.println("  - target/classes/" + filePath);
            return data;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("#")) {
                    continue;
                }
                String[] values = line.split(",", -1);
                data.add(values);
            }
        } catch (IOException e) {
            System.err.println("Error reading CSV file: " + filePath);
            e.printStackTrace();
        }
        return data;
    }

    private File findFile() {
        // Try the direct path first
        File file = new File(filePath);
        if (file.exists()) {
            return file;
        }

        // Try src/main/resources/ prefix (for development)
        file = new File("src/main/resources/" + filePath);
        if (file.exists()) {
            return file;
        }

        // Try target/classes/ prefix (for Maven)
        file = new File("target/classes/" + filePath);
        if (file.exists()) {
            return file;
        }

        // Return null if not found
        return null;
    }

    protected void writeCsvFile(List<String[]> data, String header) {
        File file = new File(filePath);
        File parentDir = file.getParentFile();

        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            if (header != null && !header.isEmpty()) {
                bw.write("# " + header);
                bw.newLine();
            }

            for (String[] row : data) {
                bw.write(String.join(",", row));
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing CSV file: " + filePath);
            e.printStackTrace();
        }
    }
}