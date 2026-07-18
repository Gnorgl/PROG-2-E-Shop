package logic.verwaltung;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import entities.Artikel;
import exceptions.artikel.ArtikelNichtGefunden;
import interfaces.moduls.IWV;
import persistence.shop.WarenkorbListe;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class WarenkorbVerwaltung implements IWV {
    private final WarenkorbListe warenkorbListe = new WarenkorbListe();
    private final File datei = new File("warenkorb.json");
    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private final ArtikelVerwaltung artikelVerwaltung;

    public WarenkorbVerwaltung(ArtikelVerwaltung artikelVerwaltung) throws IOException {
        this.artikelVerwaltung = artikelVerwaltung;
        datenLaden(); // Lädt den Korb beim Starten
    }

    public WarenkorbListe getWarenkorbListe() {
        return warenkorbListe;
    }

    // synchronized: der Warenkorb wird aktuell von allen Client-Threads gemeinsam genutzt
    // (eine WarenkorbVerwaltung pro Eshop-Instanz), deshalb müssen gleichzeitige
    // Hinzufuegen/Entfernen/Lesen-Zugriffe auf das interne HashMap serialisiert werden.
    public synchronized boolean istLeer() {
        return warenkorbListe.istLeer();
    }

    public synchronized int getMenge(Artikel artikel) {
        return warenkorbListe.getMenge(artikel);
    }

    @Override
    public synchronized HashMap<Artikel, Integer> getAlleWarenkorbArtikel() {
        // Kopie zurückgeben, damit ein anderer Thread die Map nicht mehr verändern kann,
        // während der Aufrufer (z.B. beim Senden ans GUI/JSON) noch darüber iteriert
        return new HashMap<>(warenkorbListe.getAlleArtikel());
    }

    public synchronized void artikelHinzufuegen(Artikel artikel, int menge) throws IOException {
        if (menge > 0) {
            // 1. Logik: Bisherige Menge aus der Datenhaltung abfragen
            int alteMenge = warenkorbListe.getMenge(artikel);

            // 2. Logik: Die neue Endmenge berechnen
            int neueMenge = alteMenge + menge;

            // 3. Persistenz: Nur noch den fertigen Endwert zum Speichern übergeben
            warenkorbListe.speichern(artikel, neueMenge);

            safe(); // Speichert in die JSON
        }
    }

    public synchronized void artikelEntfernen(Artikel artikel) throws IOException {
        warenkorbListe.artikelEntfernen(artikel);
        safe();
    }

    // Setzt die Stückzahl direkt auf neueMenge (im Gegensatz zu artikelHinzufuegen(),
    // das die bisherige Menge nur erhöht). Bei neueMenge <= 0 wird der Artikel entfernt.
    public synchronized void artikelMengeAendern(Artikel artikel, int neueMenge) throws IOException {
        if (neueMenge <= 0) {
            warenkorbListe.artikelEntfernen(artikel);
        } else {
            warenkorbListe.speichern(artikel, neueMenge);
        }
        safe();
    }

    public synchronized void leeren() throws IOException {
        warenkorbListe.leeren();
        safe();
    }

    private void safe() throws IOException {
        // ID-basiertes Speichern: Wir mappen ArtikelNummer -> Menge
        HashMap<Integer, Integer> speicherMap = new HashMap<>();
        for (Artikel a : warenkorbListe.getAlleArtikel().keySet()) {
            speicherMap.put(a.getArtikelNummer(), warenkorbListe.getAlleArtikel().get(a));
        }
        mapper.writerWithDefaultPrettyPrinter().writeValue(datei, speicherMap);
    }

    private void datenLaden() throws IOException, IllegalArgumentException {
        if (!datei.exists()) return;
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
    }
}
