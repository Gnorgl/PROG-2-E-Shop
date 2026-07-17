package interfaces.moduls;

import entities.Artikel;
import exceptions.artikel.ArtikelExistiertBereits;
import exceptions.artikel.ArtikelNichtGefunden;
import exceptions.artikel.ArtikelNullException;
import exceptions.artikel.BestandNichtAusreichendException;
import exceptions.artikel.MengeUngueltigException;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface IAV { //Interface ArtikelVerwaltung


    boolean legeArtikelAn(String name, int bestand, double preis) throws ArtikelExistiertBereits, ArtikelNullException, IOException;

    boolean legeMassengutartikelAn(String bezeichnung, int bestand, double preis, int packungsGroesse)
            throws ArtikelExistiertBereits, MengeUngueltigException, ArtikelNullException, IOException;

    void bestandErhoehen(int nr, int anzahl) throws ArtikelNichtGefunden, MengeUngueltigException, ArtikelNullException, IOException;

    void bestandReduzieren(int nr, int anzahl)
            throws ArtikelNichtGefunden, BestandNichtAusreichendException, MengeUngueltigException, ArtikelNullException, IOException;


    void loeschen(int nr) throws IOException;

    // Berechnet den täglichen Bestandsverlauf eines Artikels über die letzten 30 Tage
    Map<LocalDate, Integer> getBestandsHistorie(int artikelNr) throws ArtikelNichtGefunden;

    // Statt: ArtikelListe getArtikelListe();
    List<Artikel> getAlleArtikel();

    Artikel findeArtikel(int nr) throws ArtikelNichtGefunden;
}
