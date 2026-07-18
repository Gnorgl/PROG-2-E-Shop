package interfaces.moduls;

import entities.Artikel;
import exceptions.artikel.ArtikelNichtGefunden;
import exceptions.artikel.BestandNichtAusreichendException;

import java.io.IOException;
import java.util.HashMap;

public interface IWV {//Interface WarenkorbVerwaltung

    HashMap<Artikel, Integer> getAlleWarenkorbArtikel();

    boolean istLeer();

    int getMenge(Artikel artikel);

    void artikelHinzufuegen(Artikel artikel, int menge) throws IOException, BestandNichtAusreichendException, ArtikelNichtGefunden;

    void artikelEntfernen(Artikel artikel) throws IOException;

    // Setzt die Stückzahl eines bereits im Warenkorb liegenden Artikels direkt auf neueMenge
    // (statt wie artikelHinzufuegen() die bisherige Menge nur zu erhöhen). Bei neueMenge <= 0
    // wird der Artikel komplett entfernt.
    void artikelMengeAendern(Artikel artikel, int neueMenge) throws IOException, BestandNichtAusreichendException, ArtikelNichtGefunden;

    void leeren() throws IOException;
}
