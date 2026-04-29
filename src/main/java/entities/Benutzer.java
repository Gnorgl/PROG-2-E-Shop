package entities;

public abstract class Benutzer {
    protected String email;
    protected String passwort;
    protected String nachname;
    protected String vorname;
    //....
    protected Benutzer ( String email, String passwort, String nachname,String vorname){
        this.email = email;
        this.passwort = passwort;
        this.nachname = nachname;
        this.vorname = vorname;
    }

}

