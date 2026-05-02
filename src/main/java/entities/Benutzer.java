package entities;

public abstract class Benutzer {
    protected String email;
    protected String passwort;
    protected String nachname;
    protected String vorname;
    //....
    protected Benutzer (String email, String passwort, String nachname,String vorname){
        this.email = email;
        this.passwort = passwort;
        this.nachname = nachname;
        this.vorname = vorname;
    }

    //Setter-Methoden
    public void setNachname(String input) {
        this.nachname = input;
    }

    public void setVorname(String input) {
        this.vorname = input;
    }

    public void setEmail(String input) {
        this.email = input;
    }

    public void setPasswort(String input) {
        this.passwort = input;
    }

    //Getter-Methoden
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

