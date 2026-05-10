package entities;

public abstract class Benutzer {
    protected String benutzerName;
    protected String nummer;
    protected String email;
    protected String passwort;
    protected String nachname;
    protected String vorname;
    //....
    protected Benutzer (String benutzerName, String nummer, String email, String passwort, String nachname,String vorname){
        this.benutzerName = benutzerName;
        this.nummer = nummer;
        this.email = email;
        this.passwort = passwort;
        this.nachname = nachname;
        this.vorname = vorname;
    }

    //Setter-Methoden
    public void setBenutzerName(String benutzerName){
        this.benutzerName = benutzerName;
    }

    public void setNummer (String nummer) {
        this.nummer = nummer;
    }

    public void setNachname(String input) {
        this.nachname = input;
    }

    public void setVorname(String input) {
        this.vorname = input;
    }

    public void setEmail(String input) {
        this.email = input;
    }

    //wichtig für password reset funktion später
    public void setPasswort(String input) {
        this.passwort = input;
    }

    //Getter-Methoden

    public String getBenutzerName() {
        return this.benutzerName;
    }

    public String getNummer () {
        return this.nummer;
    }

    public String getNachname() {
        return this.nachname;
    }

    public String getVorname() {
        return this.vorname;
    }

    public String getEmail() {
        return this.email;
    }

    public String getPasswort() {
        return this.passwort;
    }
}

