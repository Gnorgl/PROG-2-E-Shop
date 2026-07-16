package interfaces.moduls;

import entities.Kunde;
import entities.Rechnung;
import exceptions.artikel.ArtikelNichtGefunden;
import exceptions.artikel.ArtikelNullException;
import persistence.shop.WarenkorbListe;

import java.io.IOException;

public interface ICV { //Interface CheckOutVerwaltung

    // Die aktualisierte checkOut-Methode (mit ArtikelVerwaltung für die Bestandsänderung)
    Rechnung checkOut(Kunde kunde, WarenkorbListe warenkorbListe, IAV artikelVerwaltung) throws ArtikelNichtGefunden, ArtikelNullException, IOException;

    // Die neue Methode zur Berechnung des Warenkorb-Werts
    double berechneNettoSumme(WarenkorbListe warenkorbListe);

    // Die Methode zum Anzeigen der Rechnung
    void rechnungAnzeigen(Rechnung rechnung);
}
