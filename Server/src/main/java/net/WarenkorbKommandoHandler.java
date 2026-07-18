package net;

import entities.Artikel;
import exceptions.artikel.ArtikelNichtGefunden;
import exceptions.artikel.BestandNichtAusreichendException;
import logic.Eshop;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

// Zuständig nur für Warenkorb-Kommandos.
public class WarenkorbKommandoHandler extends KommandoHandler {

    private static final Set<String> KOMMANDOS = Set.of(
            "WARENKORB_HINZUFUEGEN", "WARENKORB_ENTFERNEN", "WARENKORB_LEEREN", "WARENKORB_ANZEIGEN", "WARENKORB_MENGE_AENDERN"
    );

    public WarenkorbKommandoHandler(Eshop eshop, BufferedReader in, PrintWriter out) {
        super(eshop, in, out);
    }

    @Override
    public boolean istZustaendig(String kommando) {
        return KOMMANDOS.contains(kommando);
    }

    @Override
    public void verarbeite(String kommando) throws IOException {
        switch (kommando) {
            case "WARENKORB_HINZUFUEGEN" -> warenkorbHinzufuegen();
            case "WARENKORB_ENTFERNEN" -> warenkorbEntfernen();
            case "WARENKORB_LEEREN" -> warenkorbLeeren();
            case "WARENKORB_ANZEIGEN" -> warenkorbAnzeigen();
            case "WARENKORB_MENGE_AENDERN" -> warenkorbMengeAendern();
            default -> fehler("Unbekanntes Warenkorb-Kommando: " + kommando);
        }
    }

    private void warenkorbHinzufuegen() throws IOException {
        int nr = Integer.parseInt(in.readLine());
        int menge = Integer.parseInt(in.readLine());
        try {
            Artikel a = eshop.getArtikelVerwaltung().findeArtikel(nr);
            eshop.getWarenkorbVerwaltung().artikelHinzufuegen(a, menge);
            ok();
        } catch (ArtikelNichtGefunden | BestandNichtAusreichendException e) {
            fehler(e);
        }
    }

    private void warenkorbEntfernen() throws IOException {
        int nr = Integer.parseInt(in.readLine());
        try {
            Artikel a = eshop.getArtikelVerwaltung().findeArtikel(nr);
            eshop.getWarenkorbVerwaltung().artikelEntfernen(a);
            ok();
        } catch (ArtikelNichtGefunden e) {
            fehler(e);
        }
    }

    private void warenkorbLeeren() throws IOException {
        eshop.getWarenkorbVerwaltung().leeren();
        ok();
    }

    private void warenkorbMengeAendern() throws IOException {
        int nr = Integer.parseInt(in.readLine());
        int neueMenge = Integer.parseInt(in.readLine());
        try {
            Artikel a = eshop.getArtikelVerwaltung().findeArtikel(nr);
            eshop.getWarenkorbVerwaltung().artikelMengeAendern(a, neueMenge);
            ok();
        } catch (ArtikelNichtGefunden | BestandNichtAusreichendException e) {
            fehler(e);
        }
    }

    private void warenkorbAnzeigen() throws IOException {
        Map<Artikel, Integer> alleArtikel = eshop.getWarenkorbVerwaltung().getAlleWarenkorbArtikel();
        Map<Integer, Integer> nrMap = new LinkedHashMap<>();
        for (Map.Entry<Artikel, Integer> eintrag : alleArtikel.entrySet()) {
            nrMap.put(eintrag.getKey().getArtikelNummer(), eintrag.getValue());
        }
        ok(mapper.writeValueAsString(nrMap));
    }
}
