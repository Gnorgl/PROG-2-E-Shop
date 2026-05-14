package logic.moduls;

import entities.Mitarbeiter;
import persistence.user.MitarbeiterListe;

public interface IMV {

    MitarbeiterListe getMitarbeiterListe();

    Mitarbeiter getMitarbeiter(String email);

    boolean createNewMitarbeiter(String benutzerName, String email, String passwort, String nachname,String vorname);

}
