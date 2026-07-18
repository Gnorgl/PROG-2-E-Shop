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

    public EshopClient (String host, int port) throws IOException {
        this.verbindung = new ServerVerbindung(host, port);
        this.artikelVerwaltungFassade = new ArtikelVerwaltungFassade(verbindung);
        this.checkOutVerwaltungFassade = new CheckOutVerwaltungFassade(verbindung);
        this.warenkorbVerwaltungFassade = new WarenkorbVerwaltungFassade(verbindung, artikelVerwaltungFassade);
    }

    // Artikelverwaltung

    @Override
    public boolean legeArtikelAn(String name, int bestand, double preis) throws ArtikelExistiertBereits, ArtikelNullException, IOException {
        return false;
    }

    @Override
    public boolean legeMassengutartikelAn(String bezeichnung, int bestand, double preis, int packungsGroesse) throws ArtikelExistiertBereits, MengeUngueltigException, ArtikelNullException, IOException {
        return false;
    }

    @Override
    public void bestandErhoehen(int nr, int anzahl) throws ArtikelNichtGefunden, MengeUngueltigException, ArtikelNullException, IOException {

    }

    @Override
    public void bestandReduzieren(int nr, int anzahl) throws ArtikelNichtGefunden, BestandNichtAusreichendException, MengeUngueltigException, ArtikelNullException, IOException {

    }

    @Override
    public void loeschen(int nr) throws IOException {

    }

    @Override
    public Map<LocalDate, Integer> getBestandsHistorie(int artikelNr) throws ArtikelNichtGefunden {
        return Map.of();
    }

    @Override
    public List<Artikel> getAlleArtikel() {
        return List.of();
    }

    @Override
    public Artikel findeArtikel(int nr) throws ArtikelNichtGefunden {
        return null;
    }

    // Benutzerverwaltung

    @Override
    public Benutzer benutzerCheck(String email) throws BenutzerExistiertNichtException {
        return null;
    }

    @Override
    public boolean passwordCheck(Benutzer benutzer, String password) {
        return false;
    }

    @Override
    public boolean istEmailVergeben(String email) {
        return false;
    }

    @Override
    public Rechnung checkOut(Kunde kunde, Map<Artikel, Integer> warenkorbInhalt, IAV artikelVerwaltung) throws ArtikelNichtGefunden, ArtikelNullException, IOException {
        return null;
    }

    // Warenkorbverwaltung

    @Override
    public double berechneNettoSumme(Map<Artikel, Integer> warenkorbInhalt) {
        return 0;
    }

    @Override
    public double berechneBruttoSumme(Map<Artikel, Integer> warenkorbInhalt) {
        return 0;
    }

    @Override
    public void rechnungAnzeigen(Rechnung rechnung) {

    }

    @Override
    public boolean istLeer() {
        return false;
    }
    @Override
    public int getMenge(Artikel artikel) {
        return 0;
    }

    @Override
    public void artikelHinzufuegen(Artikel artikel, int menge) throws IOException {

    }

    @Override
    public void artikelEntfernen(Artikel artikel) throws IOException {

    }

    @Override
    public void leeren() throws IOException {

    }
    // Rechnungsverwaltung

    @Override
    public String generiereRechnungsText(Rechnung rechnung, String lieferadresse) {
        return "";
    }

    @Override
    public List<Rechnung> getRechnungenFuerKunde(Kunde kunde) {
        return List.of();
    }

    // Kunden- und Mitarbeiterverwaltung

    @Override
    public List<Kunde> getAlleKunden() {
        return List.of();
    }

    @Override
    public Kunde getKunde(String email) throws KundeNichtGefundenException {
        return null;
    }

    @Override
    public boolean createNewKunden(String email, String passwort, String nachname, String vorname, String adresse) throws EmailBereitsVergebenException {
        return false;
    }

    @Override
    public List<Mitarbeiter> getAlleMitarbeiter() {
        return List.of();
    }

    @Override
    public Mitarbeiter getMitarbeiter(String email) throws MitarbeiterNichtGefundenException {
        return null;
    }

    @Override
    public Mitarbeiter createNewMitarbeiter(String passwort, String nachname, String vorname) {
        return null;
    }

    @Override
    public HashMap<Artikel, Integer> getAlleWarenkorbArtikel() {
        return null;
    }

}



