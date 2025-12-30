package domain.transaction;
import domain.user.User;

public abstract class Transaction {
    protected User user;

    public Transaction(User user) {
        this.user = user;
    }

    public abstract boolean execute();
}

