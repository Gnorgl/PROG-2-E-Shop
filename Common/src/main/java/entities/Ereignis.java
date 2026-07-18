package entities;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Ereignis implements Serializable {
    private LocalDateTime zeitstempel;
    private int datum; //Jahrestag
    private Artikel artikel;
    private int anzahl;
    private Benutzer benutzer; //Kunde oder Mitarbeiter
    private String typ;

    public Ereignis() {}

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
