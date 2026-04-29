package entities;

public class Kunde extends Benutzer{

    protected String adresse;


    public Kunde (String email, String passwort, String nachname,String vorname,String adresse) {
        super(email, passwort, nachname, vorname);
        this.adresse = adresse;
    }
}
