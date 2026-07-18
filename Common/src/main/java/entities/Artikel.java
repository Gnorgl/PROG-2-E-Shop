package entities;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "artikelTyp" // Erzeugt ein Unterscheidungs-Feld im JSON, z.B. "artikelTyp": "massengut"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Artikel.class, name = "standard"),
        @JsonSubTypes.Type(value = Massengutartikel.class, name = "massengut")
})

public class Artikel implements Serializable {
    private int artikelNummer;
    private String bezeichnung;
    private int bestand;
    private double preis;

    public Artikel() {}

    public Artikel (int artikelNummer, String bezeichnung, int bestand, double preis) {
        this.artikelNummer = artikelNummer;
        this.bezeichnung = bezeichnung;
        this.bestand = bestand;
        this.preis = preis;
    }
    public int getArtikelNummer() {
        return artikelNummer;
    }
    public void setArtikelNummer(int artikelNummer) {
        this.artikelNummer = artikelNummer;
    }

    public String getBezeichnung() {
        return bezeichnung;
    }
    public void setBezeichnung (String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    public int getBestand() {
        return bestand;
    }
    public void setBestand(int bestand) {
        this.bestand = bestand;
    }

    public double getPreis() {
        return preis;
    }
    public void setPreis(double preis){
        this.preis = preis;
    }

    public boolean istMengeGueltig(int menge) {
        return menge > 0;
    }

    public double berechneGesamtpreis(int menge) {
        return preis * menge;
    }

    @Override
    public String toString() {
    return "Artikelnummer: " + artikelNummer + "Bezeichnung: "
            + bezeichnung + "Bestand: " + bestand + "Preis:  " + preis;
    }

}