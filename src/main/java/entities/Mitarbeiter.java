package entities;

public class Mitarbeiter extends Benutzer{

    public Mitarbeiter() {}

    public Mitarbeiter(String nummer, String email, String passwort, String nachname,String vorname) {
        super(nummer, email, passwort, nachname, vorname);
    }

}