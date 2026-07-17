package interfaces.moduls;

import entities.Artikel;

import java.util.HashMap;

public interface IWV {//Interface WarenkorbVerwaltung

    HashMap<Artikel, Integer> getAlleWarenkorbArtikel();
}
