package domain.content.access;
import java.time.LocalDateTime;

import domain.content.Manhwa;

public class PermanentAccess extends Access {

    public PermanentAccess(Manhwa manhwa) {
        super(manhwa);
    }

    @Override
    public boolean isValid() {
        return true;
    }

        @Override
    public LocalDateTime getExpiryDate() {
        return null; // Permanent access never expires
    }
}