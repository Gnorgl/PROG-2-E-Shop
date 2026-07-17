package net;

import com.fasterxml.jackson.core.type.TypeReference;
import entities.Artikel;
import exceptions.artikel.ArtikelNichtGefunden;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

// Sieht für die GUI genauso aus wie die echte WarenkorbVerwaltung (gleiche
// öffentliche Methoden), leitet die Aufrufe aber über die ServerVerbindung
// an den Server weiter.
//
// HINWEIS: Der Warenkorb ist serverseitig aktuell EINE gemeinsame Instanz für
// alle Clients (siehe Kommentar in ClientRequestProcessor). Für echten
// Mehrbenutzerbetrieb muss das noch pro Kunde/Session getrennt werden, sobald
// Login/Session (nicht unser Bereich) so weit ist.
public class WarenkorbVerwaltungFassade {

    private final ServerVerbindung verbindung;
    private final ArtikelVerwaltungFassade artikelVerwaltung;

    public WarenkorbVerwaltungFassade(ServerVerbindung verbindung, ArtikelVerwaltungFassade artikelVerwaltung) {
        this.verbindung = verbindung;
        this.artikelVerwaltung = artikelVerwaltung;
    }

    public void artikelHinzufuegen(Artikel artikel, int menge) throws IOException {
        try {
            verbindung.sendeKommando("WARENKORB_HINZUFUEGEN", String.valueOf(artikel.getArtikelNummer()), String.valueOf(menge));
        } catch (ServerFehlerException e) {
            throw new IOException("Serverfehler: " + e.getMessage());
        }
    }

    public void artikelEntfernen(Artikel artikel) throws IOException {
        try {
            verbindung.sendeKommando("WARENKORB_ENTFERNEN", String.valueOf(artikel.getArtikelNummer()));
        } catch (ServerFehlerException e) {
            throw new IOException("Serverfehler: " + e.getMessage());
        }
    }

    public void leeren() throws IOException {
        try {
            verbindung.sendeKommando("WARENKORB_LEEREN");
        } catch (ServerFehlerException e) {
            throw new IOException("Serverfehler: " + e.getMessage());
        }
    }

    public boolean istLeer() {
        return getAlleArtikel().isEmpty();
    }

    public int getMenge(Artikel artikel) {
        return getAlleArtikel().getOrDefault(artikel, 0);
    }

    public HashMap<Artikel, Integer> getAlleArtikel() {
        try {
            String json = verbindung.sendeKommandoMitAntwort("WARENKORB_ANZEIGEN");
            Map<Integer, Integer> nrMap = verbindung.mapper.readValue(json, new TypeReference<Map<Integer, Integer>>() {
            });

            HashMap<Artikel, Integer> ergebnis = new HashMap<>();
            for (Map.Entry<Integer, Integer> eintrag : nrMap.entrySet()) {
                try {
                    Artikel a = artikelVerwaltung.findeArtikel(eintrag.getKey());
                    ergebnis.put(a, eintrag.getValue());
                } catch (ArtikelNichtGefunden ignored) {
                    // Artikel wurde zwischenzeitlich gelöscht - überspringen
                }
            }
            return ergebnis;
        } catch (IOException | ServerFehlerException e) {
            throw new RuntimeException("Fehler bei der Kommunikation mit dem Server: " + e.getMessage(), e);
        }
    }
}
