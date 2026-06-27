package logic.verwaltung;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import entities.Benutzer;
import exceptions.user.BenutzerExistiertNichtException;
import logic.moduls.IBV;
import exceptions.user.KundeNichtGefundenException;
import exceptions.user.MitarbeiterNichtGefundenException;

public class BenutzerVerwaltung implements IBV {
    private final KundenVerwaltung kundenVerwaltung = new KundenVerwaltung();
    private final MitarbeiterVerwaltung mitarbeiterVerwaltung = new MitarbeiterVerwaltung();

    public BenutzerVerwaltung() {}

    //Getter-Methoden

    public KundenVerwaltung getKundenVerwaltung() {
        return kundenVerwaltung;
    }

    public MitarbeiterVerwaltung getMitarbeiterVerwaltung() {
        return mitarbeiterVerwaltung;
    }

    //Methode um zu überprüfen, ob ein Benutzer existiert. Überprüfung anhand der eingegebenen E-Mail.
    //Gibt ein Benutzer Objekt zurück.

    @Override
    public Benutzer benutzerCheck(String email) throws BenutzerExistiertNichtException {
        try {
            return kundenVerwaltung.getKunde(email);
        } catch (KundeNichtGefundenException e) {
            try {
                return mitarbeiterVerwaltung.getMitarbeiter(email);
            } catch (MitarbeiterNichtGefundenException ex) {
                throw new BenutzerExistiertNichtException(email);
            }
        }
    }

    //Methode um zu überprüfen, ob ein eingegebenes Password mit dem Password des aktuellen Benutzers übereinstimmt.
    //Gibt einen Boolean Wert zurück.

    @Override
    public boolean passwordCheck(Benutzer benutzer, String password) {
        if (benutzer == null) {
            throw new IllegalArgumentException("Der Benutzer existiert nicht.");
        } else if (password == null) {
            throw new IllegalArgumentException("Das Passwort existiert nicht.");
        } else if (password.isEmpty()) {
            throw new IllegalArgumentException("Das Passwort Feld darf nicht leer sein.");
        }
        return benutzer.getPasswort().equals(password);
    }

    @Override
    public boolean istEmailVergeben(String email) {
        try {
            benutzerCheck(email);
            return true;
        } catch (BenutzerExistiertNichtException e) {
            return false;
        }
    }

}

