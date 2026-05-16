package logic.verwaltung;

import entities.Artikel;
import entities.Kunde;
import entities.Rechnung;
import logic.moduls.ICV;
import persistence.shop.WarenkorbListe;

import java.util.List;

public class CheckOutVerwaltung implements ICV {

    @Override
    public Rechnung checkOut(Kunde kunde, WarenkorbListe warenkorbListe) {
        List<Artikel> gekaufteArtikel = warenkorbListe.getAlleArtikel();

        double netto = 0;
        for(Artikel artikel : gekaufteArtikel){
            netto+= artikel.getPreis();
        }


        double mwst = netto * 0.19;
        double brutto = netto + mwst;



        //Placeholder, hier muss noch return neueRechnung.
        //Immer wenn eine Rechnung ausgestellt wird, wird diese in OrderListe gespeichert, in neuer Methode mir checkOut() als Parameter.
        return null;
    }
}

//Funktionen für Warenkauf aus Warenkorb und Bestand aus Lager
