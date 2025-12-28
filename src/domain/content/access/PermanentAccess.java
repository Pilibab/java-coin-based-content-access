package domain.content.access;
import domain.content.Manhwa;

public class PermanentAccess extends Access {

    public PermanentAccess(Manhwa manhwa) {
        super(manhwa);
    }

    @Override
    public boolean isValid() {
        return true;
    }
}