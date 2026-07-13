package entities;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Kunde.class, name = "kunde"),
        @JsonSubTypes.Type(value = Mitarbeiter.class, name = "mitarbeiter")
})

public abstract class Benutzer {
    protected String nummer;
    protected String email;
    protected String passwort;
    protected String nachname;
    protected String vorname;
    //....

    public Benutzer() {}

    protected Benutzer (String nummer, String email, String passwort, String nachname,String vorname){
        this.nummer = nummer;
        this.email = email;
        this.passwort = passwort;
        this.nachname = nachname;
        this.vorname = vorname;
    }

    //Setter-Methoden

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

