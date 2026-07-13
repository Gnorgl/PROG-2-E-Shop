package interfaces.moduls;

import entities.Mitarbeiter;
import exceptions.user.MitarbeiterNichtGefundenException;

public interface IMV { //Interface MitarbeiterVerwaltung

    Mitarbeiter getMitarbeiter(String email) throws MitarbeiterNichtGefundenException;

    public Mitarbeiter createNewMitarbeiter(String passwort, String nachname, String vorname);

}
