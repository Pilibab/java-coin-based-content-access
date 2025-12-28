package persistence;

import domain.content.Manhwa;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Repository {
    
    public List<Manhwa> loadManhwaFromCSV() {
        List<Manhwa> manhwaList = new ArrayList<>();
        // String csvFile = "csv/merged_manhwa_complete.csv";
        String csvFile = "src/persistence/csv/merged_manhwa_complete.csv";
        String line;
        boolean isFirstLine = true;
        
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false; // Skip header
                    continue;
                }
                
                // Simple CSV parsing, assuming no commas in fields except in quoted synopsis
                String[] fields = parseCSVLine(line);
                if (fields.length >= 9) {
                    int rank = Integer.parseInt(fields[0].trim());
                    String title = fields[1].trim();
                    String synopsis = fields[2].trim();
                    String coverImageUrl = fields[3].trim();
                    double rating = Double.parseDouble(fields[4].trim());
                    String chapters = fields[5].trim();
                    String publishedDate = fields[6].trim();
                    String tags = fields[7].trim();
                    String link = fields[8].trim();
                    
                    Manhwa manhwa = new Manhwa(rank, title, synopsis, coverImageUrl, rating, chapters, publishedDate, tags, link);
                    manhwaList.add(manhwa);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return manhwaList;
    }
    
    private String[] parseCSVLine(String line) {
        List<String> fields = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder field = new StringBuilder();
        
        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                fields.add(field.toString());
                field.setLength(0);
            } else {
                field.append(c);
            }
        }
        fields.add(field.toString()); // Add the last field
        
        return fields.toArray(new String[0]);
    }
}
