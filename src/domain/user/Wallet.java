package domain.user;

public class Wallet {

    private float coins;

    // constructor 
    public Wallet(int coins){
        this.coins = coins;
    }

    // rents / buys a manhwa
    public boolean deductCoins(int amount) {
        if (coins < amount) return false;
        coins -= amount;
        return true;
    }

    // user purchase a coin
    public void addCoins(int amount) {
        coins += amount;
    }

}
