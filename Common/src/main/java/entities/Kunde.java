package entities;

import java.io.Serializable;

/** Ein Kunde des eShops, erweitert {@link Benutzer} um eine Lieferadresse. */
public class Kunde extends Benutzer implements Serializable {

    protected String adresse;

    /** Konstruktor ohne Parameter für die Jackson-Deserialisierung. */
    public Kunde () {
        super();
    }

    /**
     * @param nummer Kundennummer
     * @param email E-Mail-Adresse
     * @param passwort Passwort
     * @param nachname Nachname
     * @param vorname Vorname
     * @param adresse Lieferadresse
     */
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
