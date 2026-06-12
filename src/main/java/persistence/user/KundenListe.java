package persistence.user;

import entities.Kunde;

import java.util.HashMap;
import java.util.Map;

public class KundenListe {
    private Map<String, Kunde> kunden;

    public KundenListe() {
        this.kunden = new HashMap<>();
    }

    public Map<String, Kunde> getKunden() {
        return kunden;
    }
    public void setKunden(Map<String, Kunde> kunden) {
        this.kunden = kunden;
    }
}

//Wichtig!!! Getter Methode, damit BenutzerVerwaltung auf die Elemente zugreifen kann für login und
//KundenVerwaltung und MitarbeiterVerwaltung für die Erstellung der Benutzer!!! Creation-Screen!
