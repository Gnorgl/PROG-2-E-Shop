package persistence.user;

import entities.Kunde;

import java.util.HashMap;
import java.util.Map;

public class KundenListe {
    private final Map<String, Kunde> kunden;
    private final Map<String, String> benutzerNameUndEmail;

    public KundenListe() {
        this.kunden = new HashMap<>();
        this.benutzerNameUndEmail = new HashMap<>();
    }

    public Map<String, Kunde> getKunden() {
        return kunden;
    }

    public Map<String, String> getBenutzerNameUndEmail() {
        return benutzerNameUndEmail;
    }

}

//Wichtig!!! Getter Methode, damit BenutzerVerwaltung auf die Elemente zugreifen kann für login und
//KundenVerwaltung und MitarbeiterVerwaltung für die Erstellung der Benutzer!!! Creation-Screen!
