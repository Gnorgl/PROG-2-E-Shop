package interfaces.moduls;

import entities.Mitarbeiter;
import exceptions.user.MitarbeiterNichtGefundenException;
import persistence.user.MitarbeiterListe;

public interface IMV { //Interface MitarbeiterVerwaltung

    MitarbeiterListe getMitarbeiterListe();

    Mitarbeiter getMitarbeiter(String email) throws MitarbeiterNichtGefundenException;

    public Mitarbeiter createNewMitarbeiter(String passwort, String nachname, String vorname);

}
