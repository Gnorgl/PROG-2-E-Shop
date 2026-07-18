package persistence.user;

import entities.Mitarbeiter;

import java.util.HashMap;
import java.util.Map;

public class MitarbeiterListe {
    private Map<String, Mitarbeiter> mitarbeiter;

    public MitarbeiterListe() {
        this.mitarbeiter = new HashMap<>();
    }

    public Map<String, Mitarbeiter> getMitarbeiter() {
        return mitarbeiter;
    }

    public void setMitarbeiter(Map<String, Mitarbeiter> mitarbeiter) {
        this.mitarbeiter = mitarbeiter;
    }

}


