package logic.verwaltung;

import entities.Artikel;
import entities.Benutzer;
import entities.Ereignis;
import persistence.shop.EreignisListe;

public class EreignisVerwaltung {
    private EreignisListe ereignisListe;

    public EreignisVerwaltung(EreignisListe ereignisListe) {
        this.ereignisListe = ereignisListe;
    }

    public void logEreignis(Artikel artikel, int anzahl, Benutzer benutzer, String typ) {
        Ereignis neuesEreignis = new Ereignis(artikel, anzahl, benutzer, typ);
        ereignisListe.hinzuefuegen(neuesEreignis);
        System.out.println("Ereignis geloggt: " +  typ + " für Artikel" + artikel.getBezeichnung() + " Anzahl: " + anzahl);
    }
}