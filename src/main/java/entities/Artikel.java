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
public int getArtikelNummer() {
    return artikelNummer;
}

public string getBezeichnung() {
    retunr bezeichnung;
}

public void setBezeichnung (string bezeichnung) {
    this.bezeichnung = bezeichnung;
}

public int getBestand() {
    return bestand;
}
public void setBestand(int bestand) {
    this.bestand = bestand;
}

@override
public string