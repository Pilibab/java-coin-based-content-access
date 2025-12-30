package domain.user;

import domain.content.Manhwa;
import domain.content.access.Access;
import java.util.ArrayList;
import java.util.List;

public class Library {
    private List<Access> accesses = new ArrayList<>();

    public void addAccess(Access access) {
        accesses.add(access);
    }

    public boolean hasValidAccess(Manhwa manhwa) 
    /**
     * is manhwa in user library
     */

    {
        for (Access a : accesses) {
            if (a == null || a.getManhwa() == null) continue;
            Manhwa m = a.getManhwa();
            // Compare by title (case-insensitive) or by link when available to avoid reference-equality bugs
            boolean sameTitle = (m.getTitle() != null && manhwa.getTitle() != null && m.getTitle().equalsIgnoreCase(manhwa.getTitle()));
            boolean sameLink = (m.getLink() != null && manhwa.getLink() != null && m.getLink().equals(manhwa.getLink()));
            if ((sameTitle || sameLink) && a.isValid()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a list of owned Manhwa objects for which the user currently
     * has valid access (either permanent or unexpired rental).
     */
    public java.util.List<Manhwa> getOwnedContent() {
        java.util.List<Manhwa> list = new java.util.ArrayList<>();
        for (Access a : accesses) {
            if (a != null && a.isValid() && a.getManhwa() != null) {
                list.add(a.getManhwa());
            }
        }
        return list;
    }
}
