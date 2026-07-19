package net;

import entities.Benutzer;
import entities.Kunde;
import entities.Mitarbeiter;
import exceptions.user.BenutzerExistiertNichtException;
import exceptions.user.EmailBereitsVergebenException;
import exceptions.user.KundeNichtGefundenException;
import exceptions.user.MitarbeiterNichtGefundenException;
import interfaces.moduls.IBV;

import java.io.IOException;
import java.util.List;

public class BenutzerVerwaltungFassade implements IBV {

    private final ServerVerbindung verbindung;

    public BenutzerVerwaltungFassade(ServerVerbindung verbindung) {
        this.verbindung = verbindung;
    }

    @Override
    public Benutzer benutzerCheck(String email) throws BenutzerExistiertNichtException {
        try {
            String json = verbindung.sendeKommandoMitAntwort("BENUTZER_CHECK", email);
            return verbindung.mapper.readValue(json, Benutzer.class);
        } catch (exceptions.ServerFehlerException | IOException e) {
            throw new BenutzerExistiertNichtException(email);
        }
    }

    @Override
    public boolean passwordCheck(Benutzer benutzer, String password) {
        try {
            String benutzerJson = verbindung.mapper.writeValueAsString(benutzer);
            String antwort = verbindung.sendeKommandoMitAntwort("PASSWORD_CHECK", benutzerJson, password);
            return Boolean.parseBoolean(antwort);
        } catch (exceptions.ServerFehlerException | IOException e) {
            throw new IllegalArgumentException("Fehler bei der Passwortüberprüfung: " + e.getMessage());
        }
    }

    @Override
    public boolean istEmailVergeben(String email) {
        try {
            String antwort = verbindung.sendeKommandoMitAntwort("EMAIL_VERGEBEN", email);
            return Boolean.parseBoolean(antwort);
        } catch (exceptions.ServerFehlerException | IOException e) {
            return false;
        }
    }

    public boolean createNewKunden(String email, String passwort, String nachname, String vorname, String adresse) throws EmailBereitsVergebenException {
        try {
            String antwort = verbindung.sendeKommandoMitAntwort(
                    "KUNDE_REGISTRIEREN", email, passwort, nachname, vorname, adresse
            );
            return Boolean.parseBoolean(antwort);
        } catch (exceptions.ServerFehlerException | IOException e) {
            throw new EmailBereitsVergebenException(email);
        }
    }

    public Mitarbeiter createNewMitarbeiter(String passwort, String nachname, String vorname) {
        try {
            String json = verbindung.sendeKommandoMitAntwort("MITARBEITER_ANLEGEN", passwort, nachname, vorname);
            return verbindung.mapper.readValue(json, Mitarbeiter.class);
        } catch (exceptions.ServerFehlerException | IOException e) {
            throw new RuntimeException("Mitarbeiter konnte nicht angelegt werden: " + e.getMessage());
        }
    }

    public Kunde getKunde(String email) throws KundeNichtGefundenException {
        try {
            String json = verbindung.sendeKommandoMitAntwort("GET_KUNDE", email);
            return verbindung.mapper.readValue(json, Kunde.class);
        } catch (exceptions.ServerFehlerException | IOException e) {
            throw new KundeNichtGefundenException(email);
        }
    }

    public Mitarbeiter getMitarbeiter(String email) throws MitarbeiterNichtGefundenException {
        try {
            String json = verbindung.sendeKommandoMitAntwort("GET_MITARBEITER", email);
            return verbindung.mapper.readValue(json, Mitarbeiter.class);
        } catch (exceptions.ServerFehlerException | IOException e) {
            throw new MitarbeiterNichtGefundenException(email);
        }
    }

    public List<Kunde> getAlleKunden() {
        try {
            String json = verbindung.sendeKommandoMitAntwort("GET_ALLE_KUNDEN");
            return verbindung.mapper.readValue(json,
                    verbindung.mapper.getTypeFactory().constructCollectionType(List.class, Kunde.class));
        } catch (exceptions.ServerFehlerException | IOException e) {
            throw new RuntimeException("Fehler beim Laden der Kunden: " + e.getMessage());
        }
    }

    public List<Mitarbeiter> getAlleMitarbeiter() {
        try {
            String json = verbindung.sendeKommandoMitAntwort("GET_ALLE_MITARBEITER");
            return verbindung.mapper.readValue(json,
                    verbindung.mapper.getTypeFactory().constructCollectionType(List.class, Mitarbeiter.class));
        } catch (exceptions.ServerFehlerException | IOException e) {
            throw new RuntimeException("Fehler beim Laden der Mitarbeiter: " + e.getMessage());
        }
    }
}