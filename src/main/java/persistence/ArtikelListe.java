package persistence;

import entities.Artikel;

import java.util.ArrayList;

public class ArtikelListe {
    private ArrayList<Artikel> artikelImLager; //Vielleicht auch HashMap, jedes Item hat Artikelnummer als Key

    public ArtikelListe() {
        this.artikelImLager = new ArrayList<>();
    }

    public ArrayList<Artikel> getArtikelImLager() {
        return artikelImLager;
    }

}

//Gesamter Bestand aller existierenden Produkte sind hier aufgelistet
//Artikel ohne Bestand werden hier gespeichert, wenn neuer Artikel erstellt, also noch nicht
//existierender, dann wird dieser hier gespeichert.

//Um zu wissen, wie viele verschiedene artikel wir im shop haben