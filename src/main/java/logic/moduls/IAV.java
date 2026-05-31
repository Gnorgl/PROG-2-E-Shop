package logic.moduls;

import exceptions.artikel.ArtikelExistiertBereits;
import exceptions.artikel.ArtikelNichtGefunden;

public interface IAV {


    boolean legeArtikelAn(int nr, String name, int bestand, double preis) throws ArtikelExistiertBereits;

    void bestandErhoehen(int nr, int anzahl) throws ArtikelNichtGefunden;

    void loeschen(int nr);
}
