package logic;

import entities.Artikel;
import entities.Kunde;
import entities.Rechnung;
import persistence.Warenkorb;

import java.util.List;

public class ShoppingService {

    public Rechnung checkOut(Kunde kunde, Warenkorb warenkorb) {
        List<Artikel> gekaufteArtikel = warenkorb.getAlleArtikel();

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
