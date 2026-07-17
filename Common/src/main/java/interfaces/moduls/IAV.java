package interfaces.moduls;

import exceptions.artikel.ArtikelExistiertBereits;
import exceptions.artikel.ArtikelNichtGefunden;
import exceptions.artikel.ArtikelNullException;
import exceptions.artikel.BestandNichtAusreichendException;
import exceptions.artikel.MengeUngueltigException;

import java.io.IOException;

public interface IAV { //Interface ArtikelVerwaltung


    boolean legeArtikelAn(String name, int bestand, double preis) throws ArtikelExistiertBereits, ArtikelNullException, IOException;

    void bestandErhoehen(int nr, int anzahl) throws ArtikelNichtGefunden, MengeUngueltigException, ArtikelNullException, IOException;

    void bestandReduzieren(int nr, int anzahl)
            throws ArtikelNichtGefunden, BestandNichtAusreichendException, MengeUngueltigException, ArtikelNullException, IOException;


    void loeschen(int nr) throws IOException;

}
