package persistence;

import entities.Kunde;
import entities.Mitarbeiter;

import java.util.HashMap;
import java.util.Map;

public class BenutzerList {
    private Map<String, Kunde> kunden;
    private Map<String, Mitarbeiter> mitarbeiter;

    public BenutzerList() {
        this.kunden = new HashMap<>();
        this.mitarbeiter = new HashMap<>();
    }
}
//Wichtig!!! Getter Methode, damit UserManager auf die Elemente zugreifen kann für login und
//KundenVerwaltung und MitarbeiterVerwaltung für die Erstellung der Benutzer!!!

