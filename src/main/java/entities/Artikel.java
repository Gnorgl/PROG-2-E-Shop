package entities;

public class Artikel {
    final int artikelNummer;
    private String bezeichnung;
    private int bestand;

    public Artikel (int artikelNummer, String bezeichnung, int bestand) {
        this.artikelNummer = artikelNummer;
        this.bezeichnung = bezeichnung;
        this.bestand = bestand;
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

    @Override
    public String toString() {
    return "Artikelnummer: " + artikelNummer + "Bezeichnung: "
            + bezeichnung + "Bestand: " + bestand;
    }

}