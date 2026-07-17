package interfaces.moduls;

import entities.Mitarbeiter;
import exceptions.user.MitarbeiterNichtGefundenException;

import java.util.List;

public interface IMV { //Interface MitarbeiterVerwaltung

    List<Mitarbeiter> getAlleMitarbeiter();

    Mitarbeiter getMitarbeiter(String email) throws MitarbeiterNichtGefundenException;

    Mitarbeiter createNewMitarbeiter(String passwort, String nachname, String vorname);

}
