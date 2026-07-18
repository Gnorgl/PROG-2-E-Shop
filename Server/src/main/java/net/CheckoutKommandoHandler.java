package net;

import com.fasterxml.jackson.core.type.TypeReference;
import entities.Artikel;
import entities.Kunde;
import entities.Rechnung;
import exceptions.artikel.ArtikelNichtGefunden;
import exceptions.artikel.ArtikelNullException;
import logic.Eshop;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

// Zuständig für Checkout- und Order-Kommandos (ICV + Bestellverlauf).
public class CheckoutKommandoHandler extends KommandoHandler {

    private static final Set<String> KOMMANDOS = Set.of(
            "CHECKOUT", "NETTOSUMME", "BESTELLVERLAUF"
    );

    public CheckoutKommandoHandler(Eshop eshop, BufferedReader in, PrintWriter out) {
        super(eshop, in, out);
    }

    @Override
    public boolean istZustaendig(String kommando) {
        return KOMMANDOS.contains(kommando);
    }

    @Override
    public void verarbeite(String kommando) throws IOException {
        switch (kommando) {
            case "CHECKOUT" -> checkout();
            case "NETTOSUMME" -> nettosumme();
            case "BESTELLVERLAUF" -> bestellverlauf();
            default -> fehler("Unbekanntes Checkout-Kommando: " + kommando);
        }
    }

    private void checkout() throws IOException {
        Kunde kunde = mapper.readValue(in.readLine(), Kunde.class);
        Map<Integer, Integer> warenkorbNrMap = mapper.readValue(
                in.readLine(), new TypeReference<Map<Integer, Integer>>() {
                });

        try {
            Map<Artikel, Integer> warenkorbInhalt = zuArtikelMap(warenkorbNrMap);
            Rechnung rechnung = eshop.getBestellVerwaltungV().checkOut(kunde, warenkorbInhalt, eshop.getArtikelVerwaltung());
            ok(mapper.writeValueAsString(rechnung));
            // Checkout reduziert den Bestand der gekauften Artikel -> alle Clients informieren
            BroadcastManager.getInstanz().broadcast("ARTIKEL_GEAENDERT");
        } catch (ArtikelNichtGefunden | ArtikelNullException e) {
            fehler(e);
        }
    }

    private void nettosumme() throws IOException {
        Map<Integer, Integer> warenkorbNrMap = mapper.readValue(
                in.readLine(), new TypeReference<Map<Integer, Integer>>() {
                });
        try {
            Map<Artikel, Integer> warenkorbInhalt = zuArtikelMap(warenkorbNrMap);
            double summe = eshop.getBestellVerwaltungV().berechneNettoSumme(warenkorbInhalt);
            ok(String.valueOf(summe));
        } catch (ArtikelNichtGefunden e) {
            fehler(e);
        }
    }

    private void bestellverlauf() throws IOException {
        Kunde kunde = mapper.readValue(in.readLine(), Kunde.class);
        List<Rechnung> rechnungen = eshop.getBestellVerwaltungV().getRechnungenFuerKunde(kunde);
        ok(mapper.writeValueAsString(rechnungen));
    }

    // Wandelt Artikelnummer->Menge in Artikel->Menge um (Nachschlagen im Lager).
    private Map<Artikel, Integer> zuArtikelMap(Map<Integer, Integer> nrMap) throws ArtikelNichtGefunden {
        Map<Artikel, Integer> ergebnis = new HashMap<>();
        for (Map.Entry<Integer, Integer> eintrag : nrMap.entrySet()) {
            Artikel a = eshop.getArtikelVerwaltung().findeArtikel(eintrag.getKey());
            ergebnis.put(a, eintrag.getValue());
        }
        return ergebnis;
    }
}
