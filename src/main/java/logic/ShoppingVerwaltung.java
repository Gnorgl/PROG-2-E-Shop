package logic;

import entities.Artikel;
import entities.Kunde;
import entities.Rechnung;
import persistence.WarenkorbList;

import java.util.List;

public class ShoppingVerwaltung {

    public Rechnung checkOut(Kunde kunde, WarenkorbList warenkorbList) {
        List<Artikel> gekaufteArtikel = warenkorbList.getAlleArtikel();

        double netto = 0;
        for(Artikel artikel : gekaufteArtikel){
            netto+= artikel.getPreis();
        }


        double mwst = netto * 0.19;
        double brutto = netto + mwst;



        //Placeholder, hier muss noch return neueRechnung
        return null;
    }
}

//Funktionen für Warenkauf aus Warenkorb und Bestand aus Lager
