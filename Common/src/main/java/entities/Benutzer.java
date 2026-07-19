package entities;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Kunde.class, name = "kunde"),
        @JsonSubTypes.Type(value = Mitarbeiter.class, name = "mitarbeiter")
})

/** Abstrakte Basisklasse für alle Benutzer des eShops mit Login- und Stammdaten. */
public abstract class Benutzer implements Serializable {
    protected String nummer;
    protected String email;
    protected String passwort;
    protected String nachname;
    protected String vorname;
    //....

    /** Konstruktor ohne Parameter für die Jackson-Deserialisierung. */
    public Benutzer() {}

    /**
     * @param nummer Benutzernummer
     * @param email E-Mail-Adresse
     * @param passwort Passwort
     * @param nachname Nachname
     * @param vorname Vorname
     */
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

