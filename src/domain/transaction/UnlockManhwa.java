package domain.transaction;
import domain.content.access.*;
import domain.user.User;

public class UnlockManhwa extends Transaction {
    private int cost;
    private Access access;

    public UnlockManhwa(User user, int cost, Access access) {
        super(user);
        this.cost = cost;
        this.access = access;
    }

    @Override
    public boolean execute() {

        // prevent buying twice for a user 
        if (user.getLibrary().hasValidAccess(access.getManhwa())) return false;


        // if user has insuffecient coins then
        // user.getWallet().deductCoins(cost) =  true -> !true
        if (!user.getWallet().deductCoins(cost)) return false;
        user.getLibrary().addAccess(access);
        return true;
    }
}