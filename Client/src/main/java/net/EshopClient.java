package net;

import entities.*;
import exceptions.artikel.*;
import exceptions.user.BenutzerExistiertNichtException;
import exceptions.user.EmailBereitsVergebenException;
import exceptions.user.KundeNichtGefundenException;
import exceptions.user.MitarbeiterNichtGefundenException;
import interfaces.InterfaceEshop;
import interfaces.moduls.IAV;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EshopClient implements InterfaceEshop {

    private final ServerVerbindung verbindung;
    private final ArtikelVerwaltungFassade artikelVerwaltungFassade;
    private final CheckOutVerwaltungFassade checkOutVerwaltungFassade;
    private final WarenkorbVerwaltungFassade warenkorbVerwaltungFassade;
    private final BenutzerVerwaltungFassade benutzerVerwaltungFassade;
    private final PushListener pushListener;

    public EshopClient (String host, int port) throws IOException {
        this.verbindung = new ServerVerbindung(host, port);
        this.artikelVerwaltungFassade = new ArtikelVerwaltungFassade(verbindung);
        this.checkOutVerwaltungFassade = new CheckOutVerwaltungFassade(verbindung);
        this.warenkorbVerwaltungFassade = new WarenkorbVerwaltungFassade(verbindung, artikelVerwaltungFassade);
        this.benutzerVerwaltungFassade = new BenutzerVerwaltungFassade(verbindung);
        this.pushListener = new PushListener(host, port);
    }

    public void aktualisierungAbonnieren(Runnable r) {
        pushListener.aktualisierungAbonnieren(r);
    }

    public void aktualisierungAbmelden(Runnable r) {
        pushListener.abmelden(r);
    }

    // Artikelverwaltung

    @Override
    public boolean legeArtikelAn(String name, int bestand, double preis) throws ArtikelExistiertBereits, ArtikelNullException, IOException {
        return artikelVerwaltungFassade.legeArtikelAn(name, bestand, preis);
    }

    @Override
    public boolean legeMassengutartikelAn(String bezeichnung, int bestand, double preis, int packungsGroesse) throws ArtikelExistiertBereits, MengeUngueltigException, ArtikelNullException, IOException {
        return artikelVerwaltungFassade.legeMassengutartikelAn(bezeichnung, bestand, preis, packungsGroesse);
    }

    @Override
    public void bestandErhoehen(int nr, int anzahl) throws ArtikelNichtGefunden, MengeUngueltigException, ArtikelNullException, IOException {
        artikelVerwaltungFassade.bestandErhoehen(nr, anzahl);
    }

    @Override
    public void bestandReduzieren(int nr, int anzahl) throws ArtikelNichtGefunden, BestandNichtAusreichendException, MengeUngueltigException, ArtikelNullException, IOException {
        artikelVerwaltungFassade.bestandReduzieren(nr, anzahl);
    }

    @Override
    public void loeschen(int nr) throws IOException {
        artikelVerwaltungFassade.loeschen(nr);
    }

    @Override
    public Map<LocalDate, Integer> getBestandsHistorie(int artikelNr) throws ArtikelNichtGefunden {
        return artikelVerwaltungFassade.getBestandsHistorie(artikelNr);
    }

    @Override
    public List<Artikel> getAlleArtikel() {
        return artikelVerwaltungFassade.getAlleArtikel();
    }

    @Override
    public Artikel findeArtikel(int nr) throws ArtikelNichtGefunden {
        return artikelVerwaltungFassade.findeArtikel(nr);
    }

    @Override
    public List<Ereignis> getAlleEreignisse() {
        return artikelVerwaltungFassade.getAlleEreignisse();
    }

    // Benutzerverwaltung

    @Override
    public Benutzer benutzerCheck(String email) throws BenutzerExistiertNichtException {
        return benutzerVerwaltungFassade.benutzerCheck(email);
    }

    @Override
    public boolean passwordCheck(Benutzer benutzer, String password) {
        return benutzerVerwaltungFassade.passwordCheck(benutzer, password);
    }

    @Override
    public boolean istEmailVergeben(String email) {
        return benutzerVerwaltungFassade.istEmailVergeben(email);
    }

    @Override
    public Rechnung checkOut(Kunde kunde, Map<Artikel, Integer> warenkorbInhalt, IAV artikelVerwaltung) throws ArtikelNichtGefunden, ArtikelNullException, IOException {
        return checkOutVerwaltungFassade.checkOut(kunde, warenkorbInhalt, artikelVerwaltung);
    }

    // Warenkorbverwaltung

    @Override
    public double berechneNettoSumme(Map<Artikel, Integer> warenkorbInhalt) {
        return checkOutVerwaltungFassade.berechneNettoSumme(warenkorbInhalt);
    }

    @Override
    public double berechneBruttoSumme(Map<Artikel, Integer> warenkorbInhalt) {
        return checkOutVerwaltungFassade.berechneBruttoSumme(warenkorbInhalt);
    }

    @Override
    public void rechnungAnzeigen(Rechnung rechnung) {
        checkOutVerwaltungFassade.rechnungAnzeigen(rechnung);
    }

    @Override
    public boolean istLeer() {
        return warenkorbVerwaltungFassade.istLeer();
    }
    @Override
    public int getMenge(Artikel artikel) {
        return warenkorbVerwaltungFassade.getMenge(artikel);
    }

    @Override
    public void artikelHinzufuegen(Artikel artikel, int menge) throws IOException {
        warenkorbVerwaltungFassade.artikelHinzufuegen(artikel, menge);
    }

    @Override
    public void artikelEntfernen(Artikel artikel) throws IOException {
        warenkorbVerwaltungFassade.artikelEntfernen(artikel);
    }

    @Override
    public void artikelMengeAendern(Artikel artikel, int neueMenge) throws IOException {
        warenkorbVerwaltungFassade.artikelMengeAendern(artikel, neueMenge);
    }

    @Override
    public void leeren() throws IOException {
        warenkorbVerwaltungFassade.leeren();
    }
    // Rechnungsverwaltung

    @Override
    public String generiereRechnungsText(Rechnung rechnung, String lieferadresse) {
        return checkOutVerwaltungFassade.generiereRechnungsText(rechnung, lieferadresse);
    }

    @Override
    public List<Rechnung> getRechnungenFuerKunde(Kunde kunde) {
        return checkOutVerwaltungFassade.getRechnungenFuerKunde(kunde);
    }

    // Kunden- und Mitarbeiterverwaltung

    @Override
    public List<Kunde> getAlleKunden() {
        return benutzerVerwaltungFassade.getAlleKunden();
    }

    @Override
    public Kunde getKunde(String email) throws KundeNichtGefundenException {
        return benutzerVerwaltungFassade.getKunde(email);
    }

    @Override
    public boolean createNewKunden(String email, String passwort, String nachname, String vorname, String adresse) throws EmailBereitsVergebenException {
        return benutzerVerwaltungFassade.createNewKunden(email, passwort, nachname, vorname, adresse);
    }

    @Override
    public List<Mitarbeiter> getAlleMitarbeiter() {
        return benutzerVerwaltungFassade.getAlleMitarbeiter();
    }

    @Override
    public Mitarbeiter getMitarbeiter(String email) throws MitarbeiterNichtGefundenException {
        return benutzerVerwaltungFassade.getMitarbeiter(email);
    }

    @Override
    public Mitarbeiter createNewMitarbeiter(String passwort, String nachname, String vorname) {
        return benutzerVerwaltungFassade.createNewMitarbeiter(passwort, nachname, vorname);
    }

    @Override
    public HashMap<Artikel, Integer> getAlleWarenkorbArtikel() {
        return warenkorbVerwaltungFassade.getAlleWarenkorbArtikel();
    }

    public void verbindungSchliessen() {
        this.verbindung.schliessen();
        this.pushListener.schliessen();
    }

}
