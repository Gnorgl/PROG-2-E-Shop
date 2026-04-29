package entities;

public class Mitarbeiter extends Benutzer{

    protected String mitarbeiterID;


    public Mitarbeiter( String email, String passwort, String nachname,String vorname, String mitarbeiterID) {
        super(email, passwort, nachname, vorname);
        this.mitarbeiterID = mitarbeiterID;
    }
}