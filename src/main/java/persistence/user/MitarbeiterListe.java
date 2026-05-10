package persistence.user;

import entities.Mitarbeiter;

import java.util.HashMap;
import java.util.Map;

public class MitarbeiterListe {
    private final Map<String, Mitarbeiter> mitarbeiter;
    private final Map<String, String> benutzerNameUndEmail;

    public MitarbeiterListe() {
        this.mitarbeiter = new HashMap<>();
        this.benutzerNameUndEmail = new HashMap<>();
    }

    public Map<String, Mitarbeiter> getMitarbeiter() {
        return mitarbeiter;
    }

    public Map<String, String> getBenutzerNameUndEmail() {
        return benutzerNameUndEmail;
    }
}

//Wichtig!!! Getter Methode, damit BenutzerVerwaltung auf die Elemente zugreifen kann für login und
//KundenVerwaltung und MitarbeiterVerwaltung für die Erstellung der Benutzer!!! Creation-Screen!
