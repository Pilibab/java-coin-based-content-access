package domain.content.access;
import java.time.LocalDateTime;

import domain.content.Manhwa;

public abstract class Access {
    protected Manhwa manhwa;

    public Access(Manhwa manhwa) {
        this.manhwa = manhwa;
    }

    public Manhwa getManhwa() {
        return manhwa;
    }
    // empty "promise" doesnt have a value but every class 
    // that inherits acess has the ability to read a manhwa 
    public abstract boolean isValid();

        // New method - returns null for permanent access, expiry date for rental
    public abstract LocalDateTime getExpiryDate();
}

