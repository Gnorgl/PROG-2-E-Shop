package entities;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** Ein protokolliertes Lagerereignis, z.B. Bestandsänderung oder Artikelanlage. */
public class Ereignis implements Serializable {
    private LocalDateTime zeitstempel;
    private int datum; //Jahrestag
    private Artikel artikel;
    private int anzahl;
    private Benutzer benutzer; //Kunde oder Mitarbeiter
    private String typ;

    /** Konstruktor ohne Parameter für die Jackson-Deserialisierung. */
    public Ereignis() {}

    /**
     * Zeitstempel und Jahrestag werden automatisch auf den aktuellen Zeitpunkt gesetzt.
     *
     * @param artikel betroffener Artikel
     * @param anzahl betroffene Stückzahl
     * @param benutzer auslösender Benutzer
     * @param typ Ereignistyp
     */
    public Ereignis(Artikel artikel, int anzahl, Benutzer benutzer, String typ) {
        this.zeitstempel = LocalDateTime.now();
        this.datum = LocalDate.now().getDayOfYear();
        this.artikel = artikel;
        this.anzahl = anzahl;
        this.benutzer = benutzer;
        this.typ = typ;
    }

    public LocalDateTime getZeitstempel() {
        return zeitstempel;
    }
    public void setZeitstempel(LocalDateTime zeitstempel) {
        this.zeitstempel = zeitstempel;
    }

    public int getDatum() {
        return datum;
    }
    public void setDatum(int datum) {
        this.datum = datum;
    }

    public Artikel getArtikel() {
        return artikel;
    }
    public void setArtikel(Artikel artikel) {
        this.artikel = artikel;
    }

    public int getAnzahl() {
        return anzahl;
    }
    public void setAnzahl(int anzahl) {
        this.anzahl = anzahl;
    }

    public Benutzer getBenutzer() {
        return benutzer;
    }
    public void setBenutzer(Benutzer benutzer) {
        this.benutzer = benutzer;
    }

    public String getTyp() {
        return typ;
    }
    public void setTyp(String typ) {
        this.typ = typ;
    }

    @Override
    public String toString() {
        return zeitstempel + " | " + typ + " | " + artikel.getBezeichnung() + " | " + anzahl;
    }
}
