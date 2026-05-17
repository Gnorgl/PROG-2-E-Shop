package persistence.shop;

import entities.Artikel;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import logic.verwaltung.ArtikelVerwaltung;




public class WarenkorbListe {

    private HashMap<Artikel, Integer> warenkorb = new HashMap<>();

    //Speichert einen Artikel mit seiner Menge
    public void speichern(Artikel artikel, int menge) {
        if (menge > 0) {
            warenkorb.put(artikel, menge);
        }
    }

    public void artikelEntfernen(Artikel artikel) {
        warenkorb.remove(artikel);
    }
    //Gibt alle Artikel im Warenkorb mit ihren Mengen zurück
    public HashMap<Artikel, Integer> getAlleArtikel() {
        return warenkorb;
    }

    //Gibt die Menge eines bestimmten Artikels zurück
    public int getMenge(Artikel artikel) {
        return warenkorb.getOrDefault(artikel, 0);
    }

    // Leert den kompletten Warenkorb
    public void leeren() {
        warenkorb.clear();
    }

    //Prüft, ob Warenkorb leer ist
    public boolean istLeer() {
        return warenkorb.isEmpty();
    }


}



//Produkte die aus Lager genommen werden und für den Kauf vorbereitet sind.