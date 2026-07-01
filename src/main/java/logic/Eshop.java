package logic;
import logic.verwaltung.EreignisVerwaltung;
import persistence.shop.EreignisListe;
import logic.verwaltung.*;
import persistence.shop.EreignisListe;


public class Eshop {
    private ArtikelVerwaltung  artikelVerwaltung = new ArtikelVerwaltung();
    private CheckOutVerwaltung checkOutVerwaltung = new CheckOutVerwaltung();
    private BenutzerVerwaltung benutzerVerwaltung = new BenutzerVerwaltung();
    private EreignisVerwaltung ereignisVerwaltung = new EreignisVerwaltung();
    private WarenkorbVerwaltung warenkorbVerwaltung = new WarenkorbVerwaltung(artikelVerwaltung);

    public Eshop() {}

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
