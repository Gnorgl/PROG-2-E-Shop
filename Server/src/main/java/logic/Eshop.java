package logic;
import entities.Kunde;
import entities.Mitarbeiter;
import exceptions.user.EmailBereitsVergebenException;
import exceptions.user.KundeNichtGefundenException;
import exceptions.user.MitarbeiterNichtGefundenException;
import interfaces.InterfaceEshop;
import logic.verwaltung.EreignisVerwaltung;
import logic.verwaltung.*;

import java.io.IOException;
import java.util.List;


public class Eshop implements InterfaceEshop { //Es fehlen noch ein paar Interface - Methoden
    private ArtikelVerwaltung  artikelVerwaltung = new ArtikelVerwaltung(); //fehlt
    private CheckOutVerwaltung checkOutVerwaltung = new CheckOutVerwaltung(); //fehlt
    private EreignisVerwaltung ereignisVerwaltung = new EreignisVerwaltung(); //fehlt
    private WarenkorbVerwaltung warenkorbVerwaltung = new WarenkorbVerwaltung(artikelVerwaltung); //fehlt

    private final BenutzerVerwaltung benutzerVerwaltung;
    private final KundenVerwaltung kundenVerwaltung;
    private final MitarbeiterVerwaltung mitarbeiterVerwaltung;

    public Eshop() throws IOException {
        this.kundenVerwaltung = new KundenVerwaltung();
        this.mitarbeiterVerwaltung = new MitarbeiterVerwaltung();
        this.benutzerVerwaltung = new BenutzerVerwaltung(this.kundenVerwaltung, this.mitarbeiterVerwaltung);
    }

    // ==========================================
    // ANMELDUNG & VERWALTUNG KUNDEN
    // ==========================================

    @Override
    public Kunde getKunde(String email) throws KundeNichtGefundenException {
        return this.kundenVerwaltung.getKunde(email);
    }

    @Override
    public boolean createNewKunden(String email, String passwort, String nachname, String vorname, String adresse)
            throws EmailBereitsVergebenException {
        return this.kundenVerwaltung.createNewKunden(email, passwort, nachname, vorname, adresse);
    }

    @Override
    public List<Kunde> getAlleKunden() {
        return this.kundenVerwaltung.getAlleKunden();
    }

    // ==========================================
    // ANMELDUNG & VERWALTUNG MITARBEITER
    // ==========================================

    @Override
    public Mitarbeiter getMitarbeiter(String email) throws MitarbeiterNichtGefundenException {
        return this.mitarbeiterVerwaltung.getMitarbeiter(email);
    }

    @Override
    public Mitarbeiter createNewMitarbeiter(String passwort, String nachname, String vorname) {
        return this.mitarbeiterVerwaltung.createNewMitarbeiter(passwort, nachname, vorname);
    }

    @Override
    public List<Mitarbeiter> getAlleMitarbeiter() {
        return this.mitarbeiterVerwaltung.getAlleMitarbeiter();
    }


    // ==========================================
    // GETTER & SETTER
    // ==========================================

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
