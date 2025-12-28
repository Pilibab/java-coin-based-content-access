package domain.user;

import java.util.ArrayList;
import java.util.List;

import domain.content.Manhwa;
import domain.content.access.Access;

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
            if (a.getManhwa() == manhwa && a.isValid()) {
                return true;
            }
        }
        return false;
    }
}

