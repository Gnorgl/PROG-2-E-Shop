package persistence.shop;

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

    public void setArtikelImLager(ArrayList<Artikel> artikelImLager) {
        this.artikelImLager = artikelImLager;
    }
}
