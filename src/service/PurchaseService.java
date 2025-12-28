package service;

import domain.content.Manhwa;
import domain.content.access.Access;
import domain.content.access.PermanentAccess;
import domain.content.access.RentalAccess;
import domain.transaction.Transaction;
import domain.transaction.UnlockManhwa;
import domain.user.User;

public class PurchaseService {

    public double calculateCoinValue(Manhwa m) {
        int totalManhwaCount = 200;

        int chapter_count = Integer.parseInt(m.getChapters());
        // 1. Popularity Score (Inverted Rank)
        double rankScore = (double) (totalManhwaCount - m.getRank()) / totalManhwaCount * 20;

        // 2. Volume Score (Handling unknown chapters)
        double chapterScore;
        if (chapter_count > 0) {
            chapterScore = chapter_count * 0.5; // 0.5 coins per chapter
        } else {
            // If unknown/ongoing, give it a flat "Ongoing Premium"
            chapterScore = 25.0; 
        }

        // 3. Quality Score
        double ratingScore = m.getRating() * 2;

        return Math.round(rankScore + chapterScore + ratingScore);
    }

    public boolean rentManhwa(User user, Manhwa manhwa) {
        double RENTAL_COST = calculateCoinValue(manhwa);
        Access access = new RentalAccess(manhwa);
        
        Transaction tx = new UnlockManhwa(user, RENTAL_COST, access);
        return tx.execute();
    }

        public boolean buyManhwa(User user, Manhwa manhwa) {
        double PERMANENT_COST = calculateCoinValue(manhwa);

        Access access = new PermanentAccess(manhwa);
        Transaction tx = new UnlockManhwa(user, PERMANENT_COST, access);
        return tx.execute();
    }

    
}
