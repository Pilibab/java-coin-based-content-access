package domain.content.access;
import java.time.LocalDateTime; // Import the LocalTime class
import domain.content.Manhwa;

public class RentalAccess extends Access {
    private LocalDateTime expiry;

    public RentalAccess(int hours, Manhwa manhwa) {
        super(manhwa);
        this.expiry = LocalDateTime.now().plusHours(hours);
    }

    // default value for hr would this work regardless of mismatch in params?
    public RentalAccess(Manhwa manhwa) {this(72, manhwa);}

    @Override
    public boolean isValid() {
        return LocalDateTime.now().isBefore(expiry);
    }
}
