package domain.user;
// import domain.user.*;

public class User {
    private Wallet wallet;
    private Library library;

    public User(int coins) {
        this.wallet = new Wallet(coins);
        this.library = new Library();
    }

    public User() { 
        // default coin... would this work?
        this(10);
    }

    public Wallet getWallet() {
        return wallet;
    }

    public Library getLibrary() {
        return library;
    }
}

