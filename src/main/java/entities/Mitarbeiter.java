package entities;

public class Mitarbeiter extends Benutzer{

    public Mitarbeiter(String benutzerName, String nummer, String email, String passwort, String nachname,String vorname) {
        super(benutzerName, nummer, email, passwort, nachname, vorname);
    }

}