package logic;
import interfaces.InterfaceEshop;
import logic.verwaltung.EreignisVerwaltung;
import logic.verwaltung.*;

import java.io.IOException;


public class Eshop implements InterfaceEshop {
    private ArtikelVerwaltung  artikelVerwaltung = new ArtikelVerwaltung();
    private CheckOutVerwaltung checkOutVerwaltung = new CheckOutVerwaltung();
    private BenutzerVerwaltung benutzerVerwaltung = new BenutzerVerwaltung();
    private EreignisVerwaltung ereignisVerwaltung = new EreignisVerwaltung();
    private WarenkorbVerwaltung warenkorbVerwaltung = new WarenkorbVerwaltung(artikelVerwaltung);

    public Eshop() throws IOException {}

    public ArtikelVerwaltung getArtikelVerwaltung() {
        return artikelVerwaltung;
    }

    public BenutzerVerwaltung getBenutzerVerwaltung() {
        return benutzerVerwaltung;
    }

    public CheckOutVerwaltung getBestellVerwaltungV() {
        return checkOutVerwaltung;
    }

    public WarenkorbVerwaltung getWarenkorbVerwaltung() {
        return warenkorbVerwaltung;
    }
    public EreignisVerwaltung getEreignisVerwaltung() { return ereignisVerwaltung; }

}

//Bündelt alle anderen Logic Komponenten und kommuniziert direkt mit UI.
// Hier werden alle anderen Logik Objekte erstellt. Und Admin Mitarbeiter wird erstellt.
// Konstruktor für alle Interfaces? Attribute werden weiter gegeben, damit man die Methoden nutzen kann.

//Verwaltungsklassen können keine Interfaces sein, da ein Interface keine Attribute haben kann.
//Die Klassen brauchen aber Attribute für die Methoden.
//Nutzen stattdessen Komposition.
