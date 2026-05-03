package entities;

public class Artikel {
    final int artikelNummer;
    private String bezeichnung;
    private int bestand;
    private double preis;

    public Artikel (int artikelNummer, String bezeichnung, int bestand, double preis) {
        this.artikelNummer = artikelNummer;
        this.bezeichnung = bezeichnung;
        this.bestand = bestand;
        this.preis = preis;
    }
    public int getArtikelNummer() {
        return artikelNummer;
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

    @Override
    public String toString() {
    return "Artikelnummer: " + artikelNummer + "Bezeichnung: "
            + bezeichnung + "Bestand: " + bestand + "Preis:  " + preis;
    }

}