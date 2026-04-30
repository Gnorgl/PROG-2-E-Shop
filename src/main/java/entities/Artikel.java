package entities;

public class Artikel {
    private int artikelNummer;
    private string bezeichnung;
    private int bestand;
}

public Artikel (int artikelNummer, string bezeichnung, int bestand) {
    this.artikelNummer = artikelNummer;
    this.bezeichnung = bezeichnung;
    this.bestand = bestand;
}
public int getartikelNummer() {
    return artikelNummer;
}

public string getbezeichnung() {
    retunr bezeichnung;
}

public void setbezeichnung (string bezeichnung) {
    this.bezeichnung = bezeichnung;
}

public int getbestand() {
    return bestand;
}
public void setbestand(int bestand) {
    this.bestand = bestand;
}