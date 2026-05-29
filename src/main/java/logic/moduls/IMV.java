package logic.moduls;

import entities.Mitarbeiter;
import exceptions.user.EmailBereitsVergebenException;
import exceptions.user.MitarbeiterNichtGefundenException;
import persistence.user.MitarbeiterListe;

public interface IMV {

    MitarbeiterListe getMitarbeiterListe();

    Mitarbeiter getMitarbeiter(String email) throws MitarbeiterNichtGefundenException;

    boolean createNewMitarbeiter(String email, String passwort, String nachname,String vorname) throws EmailBereitsVergebenException;

}
