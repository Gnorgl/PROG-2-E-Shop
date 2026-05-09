package logic.moduls;

import exceptions.ArtikelExistiertBereits;

public interface IAV {


    boolean legeArtikelAn(int nr, String name, int bestand, double preis) throws ArtikelExistiertBereits;

    void bestandErhoehen(int nr, int anzahl);

    void loeschen();
}
