package logic.verwaltung;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import entities.Artikel;
import exceptions.artikel.ArtikelNichtGefunden;
import persistence.shop.WarenkorbListe;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class WarenkorbVerwaltung {
    private final WarenkorbListe warenkorbListe = new WarenkorbListe();
    private final File datei = new File("warenkorb.json");
    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private final ArtikelVerwaltung artikelVerwaltung;

    public WarenkorbVerwaltung(ArtikelVerwaltung artikelVerwaltung) {
        this.artikelVerwaltung = artikelVerwaltung;
        datenLaden(); // Lädt den Korb beim Starten
    }

    public WarenkorbListe getWarenkorbListe() {
        return warenkorbListe;
    }

    public void artikelHinzufuegen(Artikel artikel, int menge) {
        warenkorbListe.speichern(artikel, menge);
        safe(); // Speichert automatisch nach jeder Änderung
    }

    public void artikelEntfernen(Artikel artikel) {
        warenkorbListe.artikelEntfernen(artikel);
        safe();
    }

    public void leeren() {
        warenkorbListe.leeren();
        safe();
    }

    public void safe() {
        try {
            // ID-basiertes Speichern: Wir mappen ArtikelNummer -> Menge
            HashMap<Integer, Integer> speicherMap = new HashMap<>();
            for (Artikel a : warenkorbListe.getAlleArtikel().keySet()) {
                speicherMap.put(a.getArtikelNummer(), warenkorbListe.getAlleArtikel().get(a));
            }
            mapper.writerWithDefaultPrettyPrinter().writeValue(datei, speicherMap);
        } catch (IOException e) {
            System.err.println("Fehler beim Speichern des Warenkorbs: " + e.getMessage());
        }
    }

    private void datenLaden() {
        if (!datei.exists()) return;
        try {
            HashMap<Integer, Integer> geladeneIds = mapper.readValue(
                    datei,
                    new TypeReference<HashMap<Integer, Integer>>() {}
            );

            warenkorbListe.leeren();

            for (Integer artikelNummer : geladeneIds.keySet()) {
                try {
                    Artikel a = artikelVerwaltung.findeArtikel(artikelNummer);
                    warenkorbListe.speichern(a, geladeneIds.get(artikelNummer));
                } catch (ArtikelNichtGefunden e) {
                    System.err.println("Artikel Nr. " + artikelNummer + " aus altem Warenkorb nicht mehr im Lager.");
                }
            }
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("Fehler beim Laden des Warenkorbs: " + e.getMessage());
        }
    }
}