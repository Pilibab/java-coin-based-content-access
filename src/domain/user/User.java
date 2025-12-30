package domain.user;
// import domain.user.*;

public class User {
    private Wallet wallet;
    private Library library;
    private String name;

    public User(String name,int coins) {
        this.wallet = new Wallet(coins);
        this.library = new Library();
        this.name = name;
    }

    public String getName() {
        return name;
    }
    public Wallet getWallet() {
        return wallet;
    }

    public Library getLibrary() {
        return library;
    }
}

