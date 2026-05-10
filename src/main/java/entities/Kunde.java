package entities;

public class Kunde extends Benutzer{

    protected String adresse;

    public Kunde (String benutzerName, String nummer, String email, String passwort, String nachname, String vorname, String adresse) {
        super(benutzerName, nummer, email, passwort, nachname, vorname);
        this.adresse = adresse;
    }

    public void setAdresse(String input) {
        this.adresse = input;
    }

    public String getAdresse() {
        return this.adresse;
    }
}
