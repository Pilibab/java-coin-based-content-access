package domain.user;

import domain.content.Manhwa;
import domain.content.access.Access;
import domain.content.access.PermanentAccess;

import java.util.ArrayList;
import java.util.List;

public class Library {
    private List<Access> accesses = new ArrayList<>();

    public void addAccess(Access access) {
        accesses.add(access);
    }

    public boolean hasPermanentAccess(Manhwa manhwa) {
        for (Access a : accesses) {
            if (a != null 
                && a.getManhwa() != null 
                && a.getManhwa().equals(manhwa)
                && a instanceof PermanentAccess
                && a.isValid()) {
                return true;
            }
        }
        return false;
    }

    public boolean hasValidAccess(Manhwa manhwa) {
        for (Access a : accesses) {
            if (a != null 
                && a.getManhwa() != null 
                && a.getManhwa().equals(manhwa) 
                && a.isValid()) {
                return true;
            }
        }
        return false;
    }

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

