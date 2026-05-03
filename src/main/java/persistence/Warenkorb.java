package persistence;

import entities.Artikel;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;




public class Warenkorb {

    private HashMap<Integer, Artikel> datenbankTabelle = new HashMap<>();
    private int idZaehler = 1;



    public void hinzufuegen(Artikel artikel) {

        //Artikel wird mit ID gespeichert
        datenbankTabelle.put(idZaehler, artikel);

        idZaehler++;
    }

    //Nimmt die gespeicherten artikel aus der Map und gibt Array aus
    //IDs gehen dabei aber verloren
    public List<Artikel> getAlleArtikel(){

        return new ArrayList<>(datenbankTabelle.values());
    }

    //Leert HashMap nach dem Kauf
    public void leeren(){

    }




}



//Produkte die aus Lager genommen werden und für den Kauf vorbereitet sind.