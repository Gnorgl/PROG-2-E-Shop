package logic;

import logic.management.*;

public class Verwaltung{
    private ArtikelVerwaltung artikelVerwaltung = new ArtikelVerwaltung();
    private CheckOutVerwaltung checkOutVerwaltung = new CheckOutVerwaltung();
    private BenutzerVerwaltung benutzerVerwaltung = new BenutzerVerwaltung();

    public Verwaltung() {}

    public ArtikelVerwaltung getArtikelVerwaltung() {
        return artikelVerwaltung;
    }

    public BenutzerVerwaltung getBenutzerVerwaltung() {
        return benutzerVerwaltung;
    }

    public CheckOutVerwaltung getBestellVerwaltungV() {
        return checkOutVerwaltung;
    }

}

//Bündelt alle anderen Logic Komponenten und kommuniziert direkt mit UI.
// Hier werden alle anderen Logik Objekte erstellt. Und Admin Mitarbeiter wird erstellt.
// Konstruktor für alle Interfaces? Attribute werden weiter gegeben, damit man die Methoden nutzen kann.

//Verwaltungsklassen können keine Interfaces sein, da ein Interface keine Attribute haben kann.
//Die Klassen brauchen aber Attribute für die Methoden.
//Nutzen stattdessen Komposition.
