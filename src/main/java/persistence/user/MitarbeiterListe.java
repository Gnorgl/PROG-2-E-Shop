package persistence.user;

import entities.Mitarbeiter;

import java.util.HashMap;
import java.util.Map;

public class MitarbeiterListe {
    private final Map<String, Mitarbeiter> mitarbeiter;

    public MitarbeiterListe() {
        this.mitarbeiter = new HashMap<>();
    }

    public Map<String, Mitarbeiter> getMitarbeiter() {
        return mitarbeiter;
    }
}

//Wichtig!!! Getter Methode, damit BenutzerVerwaltung auf die Elemente zugreifen kann für login und
//KundenVerwaltung und MitarbeiterVerwaltung für die Erstellung der Benutzer!!! Creation-Screen!
