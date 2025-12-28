package app;

import persistence.Repository;
import domain.content.Manhwa;
import java.util.List;

public class CsvOpener {

    public static void main(String[] args) {
        Repository repo = new Repository();
        List<Manhwa> manhwaList = repo.loadManhwaFromCSV();
        
        System.out.println("Loaded " + manhwaList.size() + " manhwa:");
        for (Manhwa m : manhwaList) {
            System.out.println("Rank: " + m.getRank() + ", Title: " + m.getTitle());
        }
    }
}