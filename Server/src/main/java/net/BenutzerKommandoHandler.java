package net;

import entities.Benutzer;
import entities.Kunde;
import entities.Mitarbeiter;
import exceptions.user.BenutzerExistiertNichtException;
import exceptions.user.EmailBereitsVergebenException;
import exceptions.user.KundeNichtGefundenException;
import exceptions.user.MitarbeiterNichtGefundenException;
import logic.Eshop;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;

/** Verarbeitet Login-/Registrierungs-Kommandos für Kunden und Mitarbeiter. */
public class BenutzerKommandoHandler extends KommandoHandler {

    private static final Set<String> KOMMANDOS = Set.of(
            "BENUTZER_CHECK", "PASSWORD_CHECK", "EMAIL_VERGEBEN",
            "KUNDE_REGISTRIEREN", "MITARBEITER_ANLEGEN",
            "GET_KUNDE", "GET_MITARBEITER", "GET_ALLE_KUNDEN", "GET_ALLE_MITARBEITER"
    );

    public BenutzerKommandoHandler(Eshop eshop, BufferedReader in, PrintWriter out) {
        super(eshop, in, out);
    }

    @Override
    public boolean istZustaendig(String kommando) {
        return KOMMANDOS.contains(kommando);
    }

    @Override
    public void verarbeite(String kommando) throws IOException {
        switch (kommando) {
            case "BENUTZER_CHECK" -> benutzerCheck();
            case "PASSWORD_CHECK" -> passwordCheck();
            case "EMAIL_VERGEBEN" -> emailVergeben();
            case "KUNDE_REGISTRIEREN" -> createNewKunden();
            case "MITARBEITER_ANLEGEN" -> createNewMitarbeiter();
            case "GET_KUNDE" -> getKunde();
            case "GET_MITARBEITER" -> getMitarbeiter();
            case "GET_ALLE_KUNDEN" -> getAlleKunden();
            case "GET_ALLE_MITARBEITER" -> getAlleMitarbeiter();
            default -> fehler("Unbekanntes Benutzer-Kommando: " + kommando);
        }
    }

    private void benutzerCheck() throws IOException {
        String email = in.readLine();
        try {
            Benutzer benutzer = eshop.benutzerCheck(email);
            ok(mapper.writeValueAsString(benutzer));
        } catch (BenutzerExistiertNichtException e) {
            fehler(e);
        }
    }

    private void passwordCheck() throws IOException {
        // Liest das polymorphe Benutzer-JSON-Objekt ein
        Benutzer benutzer = mapper.readValue(in.readLine(), Benutzer.class);
        String passwort = in.readLine();

        try {
            boolean stimmt = eshop.passwordCheck(benutzer, passwort);
            ok(String.valueOf(stimmt));
        } catch (IllegalArgumentException e) {
            fehler(e);
        }
    }

    private void emailVergeben() throws IOException {
        String email = in.readLine();
        boolean vergeben = eshop.istEmailVergeben(email);
        ok(String.valueOf(vergeben));
    }

    private void createNewKunden() throws IOException {
        String email = in.readLine();
        String passwort = in.readLine();
        String nachname = in.readLine();
        String vorname = in.readLine();
        String adresse = in.readLine();

        try {
            boolean erfolg = eshop.createNewKunden(email, passwort, nachname, vorname, adresse);
            ok(String.valueOf(erfolg));
        } catch (EmailBereitsVergebenException e) {
            fehler(e);
        }
    }

    private void createNewMitarbeiter() throws IOException {
        String passwort = in.readLine();
        String nachname = in.readLine();
        String vorname = in.readLine();

        Mitarbeiter neuerMitarbeiter = eshop.createNewMitarbeiter(passwort, nachname, vorname);
        ok(mapper.writeValueAsString(neuerMitarbeiter));
    }

    private void getKunde() throws IOException {
        String email = in.readLine();
        try {
            Kunde kunde = eshop.getKunde(email);
            ok(mapper.writeValueAsString(kunde));
        } catch (KundeNichtGefundenException e) {
            fehler(e);
        }
    }

    private void getMitarbeiter() throws IOException {
        String email = in.readLine();
        try {
            Mitarbeiter mitarbeiter = eshop.getMitarbeiter(email);
            ok(mapper.writeValueAsString(mitarbeiter));
        } catch (MitarbeiterNichtGefundenException e) {
            fehler(e);
        }
    }

    private void getAlleKunden() throws IOException {
        List<Kunde> kunden = eshop.getAlleKunden();
        ok(mapper.writeValueAsString(kunden));
    }

    private void getAlleMitarbeiter() throws IOException {
        List<Mitarbeiter> mitarbeiter = eshop.getAlleMitarbeiter();
        ok(mapper.writeValueAsString(mitarbeiter));
    }
}