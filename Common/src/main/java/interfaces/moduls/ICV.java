package interfaces.moduls;

import entities.Artikel;
import entities.Kunde;
import entities.Rechnung;
import exceptions.artikel.ArtikelNichtGefunden;
import exceptions.artikel.ArtikelNullException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface ICV { //Interface CheckOutVerwaltung

    // Die aktualisierte checkOut-Methode (mit ArtikelVerwaltung für die Bestandsänderung)
    Rechnung checkOut(Kunde kunde, Map<Artikel, Integer> warenkorbInhalt, IAV artikelVerwaltung) throws ArtikelNichtGefunden, ArtikelNullException, IOException;

    // Die neue Methode zur Berechnung des Warenkorb-Werts
    double berechneNettoSumme(Map<Artikel, Integer> warenkorbInhalt);

    double berechneBruttoSumme(Map<Artikel, Integer> warenkorbInhalt);

    // Die Methode zum Anzeigen der Rechnung
    void rechnungAnzeigen(Rechnung rechnung);

    String generiereRechnungsText(Rechnung rechnung, String lieferadresse);

    List<Rechnung> getRechnungenFuerKunde(Kunde kunde);
}
