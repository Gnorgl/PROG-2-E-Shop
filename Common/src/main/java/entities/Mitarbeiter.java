package entities;

import java.io.Serializable;

public class Mitarbeiter extends Benutzer implements Serializable {

    public Mitarbeiter() {}

    public Mitarbeiter(String nummer, String email, String passwort, String nachname,String vorname) {
        super(nummer, email, passwort, nachname, vorname);
    }

}