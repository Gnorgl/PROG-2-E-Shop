package logic.moduls;

import entities.Kunde;
import entities.Rechnung;
import persistence.shop.WarenkorbListe;
import logic.verwaltung.ArtikelVerwaltung;

public interface ICV {

    // Die aktualisierte checkOut-Methode (mit ArtikelVerwaltung für die Bestandsänderung)
    Rechnung checkOut(Kunde kunde, WarenkorbListe warenkorbListe, ArtikelVerwaltung artikelVerwaltung);

    // Die neue Methode zur Berechnung des Warenkorb-Werts
    double berechneNettoSumme(WarenkorbListe warenkorbListe);

    // Die Methode zum Anzeigen der Rechnung
    void rechnungAnzeigen(Rechnung rechnung);
}
