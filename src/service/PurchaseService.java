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

        String chapters = m.getChapters(); // Assuming this returns a String
        int chapter_count = 0;
        
        // checks if chapter count is unknown 
        if ("unknown".equalsIgnoreCase(chapters)) {
            // statusText = "Status: Ongoing";
            chapter_count = 0;
        } else {
            chapter_count = Integer.parseInt(m.getChapters());
        }
        // Popularity Score (Inverted Rank)
        double rankScore = (double) (totalManhwaCount - m.getRank()) / totalManhwaCount * 20;

        // Volume Score (Handling unknown chapters)
        double chapterScore;
        if (chapter_count > 0) {
            chapterScore = chapter_count * 0.5; // 0.5 coins per chapter
        } else {
            // If unknown/ongoing, give it a flat "Ongoing Premium"
            chapterScore = 25.0; 
        }

        // Quality Score
        double ratingScore = m.getRating() * 2;

        return (rankScore + chapterScore + ratingScore);
    }

    /**
     * Calculates the cost to rent the Manhwa for a limited time (e.g., 72 hours).
     * Usually set at 50% of the total calculated value.
     * * @param manhwa_cost The total dynamic value calculated by calculateCoinValue.
     * @return The rental cost as a float.
     */
    double getActualCOst(double manhwa_cost, String purchase_type) {
        if (purchase_type == "rent") {
            // 0.5f represents 50% of the total value
            return (manhwa_cost * 0.5);
        } else {
            return manhwa_cost * 0.5;
        }

    }

    public boolean rentManhwa(User user, Manhwa manhwa) {
        double RENTAL_COST = getActualCOst(calculateCoinValue(manhwa), "rent");
        Access access = new RentalAccess(manhwa);
        
        Transaction tx = new UnlockManhwa(user, RENTAL_COST, access);
        return tx.execute();
    }

    public boolean buyManhwa(User user, Manhwa manhwa) {
        double PERMANENT_COST = getActualCOst(calculateCoinValue(manhwa), "buy");

        Access access = new PermanentAccess(manhwa);
        Transaction tx = new UnlockManhwa(user, PERMANENT_COST, access);
        return tx.execute();
    }

    
    
}