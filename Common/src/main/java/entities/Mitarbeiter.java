package entities;

import java.io.Serializable;

/** Ein Mitarbeiter des eShops, erweitert {@link Benutzer} ohne zusätzliche Felder. */
public class Mitarbeiter extends Benutzer implements Serializable {

    /** Konstruktor ohne Parameter für die Jackson-Deserialisierung. */
    public Mitarbeiter() {}

    /**
     * @param nummer Mitarbeiternummer
     * @param email E-Mail-Adresse
     * @param passwort Passwort
     * @param nachname Nachname
     * @param vorname Vorname
     */
    public Mitarbeiter(String nummer, String email, String passwort, String nachname,String vorname) {
        super(nummer, email, passwort, nachname, vorname);
    }

}