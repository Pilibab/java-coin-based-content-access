package app;

import persistence.Repository;
import domain.content.Manhwa;
import java.util.List;

public class CsvOpener {
    public static List<Manhwa> getDb() {
        Repository repo = new Repository();
        return repo.loadManhwaFromCSV();
    }
}