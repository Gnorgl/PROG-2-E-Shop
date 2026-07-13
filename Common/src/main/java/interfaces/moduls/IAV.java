package interfaces.moduls;

import exceptions.artikel.ArtikelExistiertBereits;
import exceptions.artikel.ArtikelNichtGefunden;
import exceptions.artikel.BestandNichtAusreichendException;
import exceptions.artikel.MengeUngueltigException;

public interface IAV { //Interface ArtikelVerwaltung


    boolean legeArtikelAn(String name, int bestand, double preis) throws ArtikelExistiertBereits;

    void bestandErhoehen(int nr, int anzahl) throws ArtikelNichtGefunden, MengeUngueltigException;

    void bestandReduzieren(int nr, int anzahl)
            throws ArtikelNichtGefunden, BestandNichtAusreichendException, MengeUngueltigException;


    void loeschen(int nr);

}
