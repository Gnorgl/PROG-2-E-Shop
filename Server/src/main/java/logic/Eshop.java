package logic;
import entities.Artikel;
import entities.Benutzer;
import entities.Kunde;
import entities.Mitarbeiter;
import entities.Rechnung;
import exceptions.artikel.ArtikelExistiertBereits;
import exceptions.artikel.ArtikelNichtGefunden;
import exceptions.artikel.ArtikelNullException;
import exceptions.artikel.BestandNichtAusreichendException;
import exceptions.artikel.MengeUngueltigException;
import exceptions.user.BenutzerExistiertNichtException;
import exceptions.user.EmailBereitsVergebenException;
import exceptions.user.KundeNichtGefundenException;
import exceptions.user.MitarbeiterNichtGefundenException;
import interfaces.InterfaceEshop;
import logic.verwaltung.EreignisVerwaltung;
import logic.verwaltung.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Eshop implements InterfaceEshop {
    private ArtikelVerwaltung  artikelVerwaltung = new ArtikelVerwaltung();
    private CheckOutVerwaltung checkOutVerwaltung = new CheckOutVerwaltung();
    private EreignisVerwaltung ereignisVerwaltung = new EreignisVerwaltung();
    private WarenkorbVerwaltung warenkorbVerwaltung = new WarenkorbVerwaltung(artikelVerwaltung);

    private final BenutzerVerwaltung benutzerVerwaltung;
    private final KundenVerwaltung kundenVerwaltung;
    private final MitarbeiterVerwaltung mitarbeiterVerwaltung;

    public Eshop() throws IOException {
        this.kundenVerwaltung = new KundenVerwaltung();
        this.mitarbeiterVerwaltung = new MitarbeiterVerwaltung();
        this.benutzerVerwaltung = new BenutzerVerwaltung(this.kundenVerwaltung, this.mitarbeiterVerwaltung);
    }

    // ==========================================
    // ARTIKEL
    // ==========================================

    @Override
    public boolean legeArtikelAn(String name, int bestand, double preis) throws ArtikelExistiertBereits, ArtikelNullException, IOException {
        return this.artikelVerwaltung.legeArtikelAn(name, bestand, preis);
    }

    @Override
    public boolean legeMassengutartikelAn(String bezeichnung, int bestand, double preis, int packungsGroesse) throws ArtikelExistiertBereits, MengeUngueltigException, ArtikelNullException, IOException {
        return this.artikelVerwaltung.legeMassengutartikelAn(bezeichnung, bestand, preis, packungsGroesse);
    }

    @Override
    public void bestandErhoehen(int nr, int anzahl) throws ArtikelNichtGefunden, MengeUngueltigException, ArtikelNullException, IOException {
        this.artikelVerwaltung.bestandErhoehen(nr, anzahl);
    }

    @Override
    public void bestandReduzieren(int nr, int anzahl) throws ArtikelNichtGefunden, BestandNichtAusreichendException, MengeUngueltigException, ArtikelNullException, IOException {
        this.artikelVerwaltung.bestandReduzieren(nr, anzahl);
    }

    @Override
    public void loeschen(int nr) throws IOException {
        this.artikelVerwaltung.loeschen(nr);
    }

    @Override
    public Map<LocalDate, Integer> getBestandsHistorie(int artikelNr) throws ArtikelNichtGefunden {
        return this.artikelVerwaltung.getBestandsHistorie(artikelNr);
    }

    @Override
    public List<Artikel> getAlleArtikel() {
        return this.artikelVerwaltung.getAlleArtikel();
    }

    @Override
    public Artikel findeArtikel(int nr) throws ArtikelNichtGefunden {
        return this.artikelVerwaltung.findeArtikel(nr);
    }

    // ==========================================
    // CHECKOUT
    // ==========================================

    @Override
    public Rechnung checkOut(Kunde kunde, Map<Artikel, Integer> warenkorbInhalt, interfaces.moduls.IAV artikelVerwaltung) throws ArtikelNichtGefunden, ArtikelNullException, IOException {
        return this.checkOutVerwaltung.checkOut(kunde, warenkorbInhalt, artikelVerwaltung);
    }

    @Override
    public double berechneNettoSumme(Map<Artikel, Integer> warenkorbInhalt) {
        return this.checkOutVerwaltung.berechneNettoSumme(warenkorbInhalt);
    }

    @Override
    public double berechneBruttoSumme(Map<Artikel, Integer> warenkorbInhalt) {
        return this.checkOutVerwaltung.berechneBruttoSumme(warenkorbInhalt);
    }

    @Override
    public void rechnungAnzeigen(Rechnung rechnung) {
        this.checkOutVerwaltung.rechnungAnzeigen(rechnung);
    }

    @Override
    public String generiereRechnungsText(Rechnung rechnung, String lieferadresse) {
        return this.checkOutVerwaltung.generiereRechnungsText(rechnung, lieferadresse);
    }

    @Override
    public List<Rechnung> getRechnungenFuerKunde(Kunde kunde) {
        return this.checkOutVerwaltung.getRechnungenFuerKunde(kunde);
    }


    // ==========================================
    // WARENKORB
    // ==========================================

    @Override
    public HashMap<Artikel, Integer> getAlleWarenkorbArtikel() {
        return warenkorbVerwaltung.getAlleWarenkorbArtikel();
    }

    @Override
    public boolean istLeer() {
        return warenkorbVerwaltung.istLeer();
    }

    @Override
    public int getMenge(Artikel artikel) {
        return warenkorbVerwaltung.getMenge(artikel);
    }

    @Override
    public void artikelHinzufuegen(Artikel artikel, int menge) throws IOException {
        warenkorbVerwaltung.artikelHinzufuegen(artikel, menge);
    }

    @Override
    public void artikelEntfernen(Artikel artikel) throws IOException {
        warenkorbVerwaltung.artikelEntfernen(artikel);
    }

    @Override
    public void leeren() throws IOException {
        warenkorbVerwaltung.leeren();
    }


    // ==========================================
    // BENUTZER-CHECKS
    // ==========================================

    @Override
    public Benutzer benutzerCheck(String email) throws BenutzerExistiertNichtException {
        return this.benutzerVerwaltung.benutzerCheck(email);
    }

    @Override
    public boolean passwordCheck(Benutzer benutzer, String password) {
        return this.benutzerVerwaltung.passwordCheck(benutzer, password);
    }

    @Override
    public boolean istEmailVergeben(String email) {
        return this.benutzerVerwaltung.istEmailVergeben(email);
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
