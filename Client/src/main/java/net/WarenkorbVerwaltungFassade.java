package net;

import com.fasterxml.jackson.core.type.TypeReference;
import entities.Artikel;
import exceptions.artikel.ArtikelNichtGefunden;
import interfaces.moduls.IWV;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class WarenkorbVerwaltungFassade implements IWV {

    private final ServerVerbindung verbindung;
    private final ArtikelVerwaltungFassade artikelVerwaltung;

    public WarenkorbVerwaltungFassade(ServerVerbindung verbindung, ArtikelVerwaltungFassade artikelVerwaltung) {
        this.verbindung = verbindung;
        this.artikelVerwaltung = artikelVerwaltung;
    }

    @Override
    public void artikelHinzufuegen(Artikel artikel, int menge) throws IOException {
        try {
            verbindung.sendeKommando("WARENKORB_HINZUFUEGEN", String.valueOf(artikel.getArtikelNummer()), String.valueOf(menge));
        } catch (ServerFehlerException e) {
            throw new IOException("Serverfehler: " + e.getMessage());
        }
    }

    @Override
    public void artikelEntfernen(Artikel artikel) throws IOException {
        try {
            verbindung.sendeKommando("WARENKORB_ENTFERNEN", String.valueOf(artikel.getArtikelNummer()));
        } catch (ServerFehlerException e) {
            throw new IOException("Serverfehler: " + e.getMessage());
        }
    }

    @Override
    public void artikelMengeAendern(Artikel artikel, int neueMenge) throws IOException {
        try {
            verbindung.sendeKommando("WARENKORB_MENGE_AENDERN", String.valueOf(artikel.getArtikelNummer()), String.valueOf(neueMenge));
        } catch (ServerFehlerException e) {
            throw new IOException("Serverfehler: " + e.getMessage());
        }
    }

    @Override
    public void leeren() throws IOException {
        try {
            verbindung.sendeKommando("WARENKORB_LEEREN");
        } catch (ServerFehlerException e) {
            throw new IOException("Serverfehler: " + e.getMessage());
        }
    }

    @Override
    public boolean istLeer() {
        return getAlleWarenkorbArtikel().isEmpty();
    }

    @Override
    public int getMenge(Artikel artikel) {
        return getAlleWarenkorbArtikel().getOrDefault(artikel, 0);
    }

    @Override
    public HashMap<Artikel, Integer> getAlleWarenkorbArtikel() {
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
                    // Artikel wurde gelöscht
                }
            }
            return ergebnis;
        } catch (IOException | ServerFehlerException e) {
            throw new RuntimeException("Fehler bei der Kommunikation mit dem Server: " + e.getMessage(), e);
        }
    }
}
