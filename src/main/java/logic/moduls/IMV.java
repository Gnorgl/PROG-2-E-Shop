package logic.moduls;

import persistence.user.MitarbeiterListe;

public interface IMV {

    MitarbeiterListe getMitarbeiterListe();

    boolean createNewMitarbeiter(String benutzerName, String email, String passwort, String nachname,String vorname);

}
