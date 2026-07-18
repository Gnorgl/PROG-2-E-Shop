package net;

import entities.Artikel;
import exceptions.artikel.ArtikelExistiertBereits;
import exceptions.artikel.ArtikelNichtGefunden;
import exceptions.artikel.ArtikelNullException;
import exceptions.artikel.BestandNichtAusreichendException;
import exceptions.artikel.MengeUngueltigException;
import logic.Eshop;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

// Zuständig nur für Artikel-Kommandos (IAV).
public class ArtikelKommandoHandler extends KommandoHandler {

    private static final Set<String> KOMMANDOS = Set.of(
            "ARTIKEL_ANLEGEN", "MASSENGUT_ANLEGEN", "BESTAND_ERHOEHEN", "BESTAND_REDUZIEREN",
            "ARTIKEL_LOESCHEN", "BESTANDSHISTORIE", "ALLE_ARTIKEL", "ARTIKEL_FINDEN"
    );

    public ArtikelKommandoHandler(Eshop eshop, BufferedReader in, PrintWriter out) {
        super(eshop, in, out);
    }

    @Override
    public boolean istZustaendig(String kommando) {
        return KOMMANDOS.contains(kommando);
    }

    @Override
    public void verarbeite(String kommando) throws IOException {
        switch (kommando) {
            case "ARTIKEL_ANLEGEN" -> artikelAnlegen();
            case "MASSENGUT_ANLEGEN" -> massengutAnlegen();
            case "BESTAND_ERHOEHEN" -> bestandErhoehen();
            case "BESTAND_REDUZIEREN" -> bestandReduzieren();
            case "ARTIKEL_LOESCHEN" -> artikelLoeschen();
            case "BESTANDSHISTORIE" -> bestandshistorie();
            case "ALLE_ARTIKEL" -> alleArtikel();
            case "ARTIKEL_FINDEN" -> artikelFinden();
            default -> fehler("Unbekanntes Artikel-Kommando: " + kommando);
        }
    }

    private void artikelAnlegen() throws IOException {
        String name = in.readLine();
        int bestand = Integer.parseInt(in.readLine());
        double preis = Double.parseDouble(in.readLine());

        try {
            eshop.getArtikelVerwaltung().legeArtikelAn(name, bestand, preis);
            ok();
            BroadcastManager.getInstanz().broadcast("ARTIKEL_GEAENDERT");
        } catch (ArtikelExistiertBereits | ArtikelNullException e) {
            fehler(e);
        }
    }

    private void massengutAnlegen() throws IOException {
        String bezeichnung = in.readLine();
        int bestand = Integer.parseInt(in.readLine());
        double preis = Double.parseDouble(in.readLine());
        int packungsGroesse = Integer.parseInt(in.readLine());

        try {
            eshop.getArtikelVerwaltung().legeMassengutartikelAn(bezeichnung, bestand, preis, packungsGroesse);
            ok();
            BroadcastManager.getInstanz().broadcast("ARTIKEL_GEAENDERT");
        } catch (ArtikelExistiertBereits | MengeUngueltigException | ArtikelNullException e) {
            fehler(e);
        }
    }

    private void bestandErhoehen() throws IOException {
        int nr = Integer.parseInt(in.readLine());
        int anzahl = Integer.parseInt(in.readLine());

        try {
            eshop.getArtikelVerwaltung().bestandErhoehen(nr, anzahl);
            ok();
            BroadcastManager.getInstanz().broadcast("ARTIKEL_GEAENDERT");
        } catch (ArtikelNichtGefunden | MengeUngueltigException | ArtikelNullException e) {
            fehler(e);
        }
    }

    private void bestandReduzieren() throws IOException {
        int nr = Integer.parseInt(in.readLine());
        int anzahl = Integer.parseInt(in.readLine());

        try {
            eshop.getArtikelVerwaltung().bestandReduzieren(nr, anzahl);
            ok();
            BroadcastManager.getInstanz().broadcast("ARTIKEL_GEAENDERT");
        } catch (ArtikelNichtGefunden | BestandNichtAusreichendException | MengeUngueltigException | ArtikelNullException e) {
            fehler(e);
        }
    }

    private void artikelLoeschen() throws IOException {
        int nr = Integer.parseInt(in.readLine());
        eshop.getArtikelVerwaltung().loeschen(nr);
        ok();
        BroadcastManager.getInstanz().broadcast("ARTIKEL_GEAENDERT");
    }

    private void bestandshistorie() throws IOException {
        int nr = Integer.parseInt(in.readLine());
        try {
            Map<LocalDate, Integer> historie = eshop.getArtikelVerwaltung().getBestandsHistorie(nr);
            ok(mapper.writeValueAsString(new LinkedHashMap<>(historie)));
        } catch (ArtikelNichtGefunden e) {
            fehler(e);
        }
    }

    private void alleArtikel() throws IOException {
        List<Artikel> artikel = eshop.getArtikelVerwaltung().getAlleArtikel();
        // writerFor(...) statt writeValueAsString(artikel): Java löscht bei writeValueAsString(Object)
        // den generischen Typ (List<Artikel> wird zur Laufzeit nur "List"), dadurch findet Jackson
        // die @JsonTypeInfo-Polymorphie auf Artikel nicht zuverlässig. Mit dem expliziten TypeReference
        // weiß Jackson sicher, dass die Elemente vom Typ Artikel sind, und schreibt "artikelTyp" mit.
        String json = mapper.writerFor(new com.fasterxml.jackson.core.type.TypeReference<List<Artikel>>() {})
                .writeValueAsString(artikel);
        ok(json);
    }

    private void artikelFinden() throws IOException {
        int nr = Integer.parseInt(in.readLine());
        try {
            Artikel a = eshop.getArtikelVerwaltung().findeArtikel(nr);
            ok(mapper.writeValueAsString(a));
        } catch (ArtikelNichtGefunden e) {
            fehler(e);
        }
    }
}
