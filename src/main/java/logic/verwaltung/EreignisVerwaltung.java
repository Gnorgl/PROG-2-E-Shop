package logic.verwaltung;

import entities.Artikel;
import entities.Benutzer;
import entities.Ereignis;
import exceptions.artikel.ArtikelNullException;
import persistence.shop.EreignisListe;

import java.util.List;
import java.util.stream.Collectors;

public class EreignisVerwaltung {
    private EreignisListe ereignisListe = new EreignisListe();

    public EreignisVerwaltung() {
    }

    public void logEreignis(Artikel artikel, int anzahl, Benutzer benutzer, String typ) throws ArtikelNullException {

        // Validierung
        if (artikel == null) {
            throw new ArtikelNullException();
        }
        if (anzahl <= 0) {
            // AnzahlUngueltigException is unchecked; throw it directly
            throw new exceptions.artikel.AnzahlUngueltigException();
        }

        // Erstellen eines neuen Ereignisses mit den übergebenen Informationen
        Ereignis neuesEreignis = new Ereignis(artikel, anzahl, benutzer, typ);
        ereignisListe.hinzuefuegen(neuesEreignis);
        System.out.println("Ereignis geloggt: " +  typ + " für Artikel" + artikel.getBezeichnung() + " Anzahl: " + anzahl);
    }

    public List<Ereignis> getEreignisseFuerArtikel(int artikelNr) {
        return ereignisListe.getAlleEreignisse()
                .stream()
                .filter(e -> e.getArtikel().getArtikelNummer() == artikelNr)
                .collect(Collectors.toList());
    }
}