package entities;

public class Mitarbeiter extends Benutzer{
    public Mitarbeiter( String email, String passwort, String nachname,String vorname) {
        super(email, passwort, nachname, vorname);
    }
}