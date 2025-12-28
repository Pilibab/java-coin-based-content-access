package service;

public class PurchaseService {

    private int coins;

    public PurchaseService() {
        this.coins = 100; // starting coins
    }

    /**
     * Attempts to buy a chapter.
     *
     * @param chapterId identifier of the chapter
     * @return true if purchase succeeded, false otherwise
     */
    public boolean buyChapter(String chapterId) {
        int price = 10;

        if (coins < price) {
            return false;
        }

        coins -= price;
        return true;
    }

    /**
     * Returns current coin balance.
     */
    public int getCoins() {
        return coins;
    }
}
