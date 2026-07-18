package entities;

import java.io.Serializable;

public class Kunde extends Benutzer implements Serializable {

    protected String adresse;

    public Kunde () {
        super();
    }

    public Kunde (String nummer, String email, String passwort, String nachname, String vorname, String adresse) {
        super(nummer, email, passwort, nachname, vorname);
        this.adresse = adresse;
    }

    public void setAdresse(String input) {
        this.adresse = input;
    }

    public String getAdresse() {
        return this.adresse;
    }
}
