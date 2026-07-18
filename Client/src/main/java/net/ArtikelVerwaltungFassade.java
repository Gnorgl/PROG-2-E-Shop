package net;

import com.fasterxml.jackson.core.type.TypeReference;
import entities.Artikel;
import entities.Ereignis;
import exceptions.artikel.ArtikelExistiertBereits;
import exceptions.artikel.ArtikelNichtGefunden;
import exceptions.artikel.ArtikelNullException;
import exceptions.artikel.BestandNichtAusreichendException;
import exceptions.artikel.MengeUngueltigException;
import interfaces.moduls.IAV;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;


public class ArtikelVerwaltungFassade implements IAV {

    private final ServerVerbindung verbindung;

    public ArtikelVerwaltungFassade(ServerVerbindung verbindung) {
        this.verbindung = verbindung;
    }

    @Override
    public boolean legeArtikelAn(String name, int bestand, double preis) throws ArtikelExistiertBereits, ArtikelNullException, IOException {
        try {
            verbindung.sendeKommando("ARTIKEL_ANLEGEN", name, String.valueOf(bestand), String.valueOf(preis));
            return true;
        } catch (ServerFehlerException e) {
            switch (e.getExceptionName()) {
                case "ArtikelExistiertBereits" -> throw ArtikelExistiertBereits.mitFertigerNachricht(e.getNachricht());
                case "ArtikelNullException" -> throw new ArtikelNullException();
                default -> throw new IOException("Serverfehler: " + e.getMessage());
            }
        }
    }

    @Override
    public boolean legeMassengutartikelAn(String bezeichnung, int bestand, double preis, int packungsGroesse) throws ArtikelExistiertBereits, MengeUngueltigException, ArtikelNullException, IOException {
        try {
            verbindung.sendeKommando("MASSENGUT_ANLEGEN", bezeichnung, String.valueOf(bestand), String.valueOf(preis), String.valueOf(packungsGroesse));
            return true;
        } catch (ServerFehlerException e) {
            switch (e.getExceptionName()) {
                case "ArtikelExistiertBereits" -> throw ArtikelExistiertBereits.mitFertigerNachricht(e.getNachricht());
                case "MengeUngueltigException" -> throw MengeUngueltigException.mitFertigerNachricht(e.getNachricht());
                case "ArtikelNullException" -> throw new ArtikelNullException();
                default -> throw new IOException("Serverfehler: " + e.getMessage());
            }
        }
    }

    @Override
    public void bestandErhoehen(int nr, int anzahl) throws ArtikelNichtGefunden, MengeUngueltigException, ArtikelNullException, IOException {
        try {
            verbindung.sendeKommando("BESTAND_ERHOEHEN", String.valueOf(nr), String.valueOf(anzahl));
        } catch (ServerFehlerException e) {
            switch (e.getExceptionName()) {
                case "ArtikelNichtGefunden" -> throw ArtikelNichtGefunden.mitFertigerNachricht(e.getNachricht());
                case "MengeUngueltigException" -> throw MengeUngueltigException.mitFertigerNachricht(e.getNachricht());
                case "ArtikelNullException" -> throw new ArtikelNullException();
                default -> throw new IOException("Serverfehler: " + e.getMessage());
            }
        }
    }

    @Override
    public void bestandReduzieren(int nr, int anzahl) throws ArtikelNichtGefunden, BestandNichtAusreichendException, MengeUngueltigException, ArtikelNullException, IOException {
        try {
            verbindung.sendeKommando("BESTAND_REDUZIEREN", String.valueOf(nr), String.valueOf(anzahl));
        } catch (ServerFehlerException e) {
            switch (e.getExceptionName()) {
                case "ArtikelNichtGefunden" -> throw ArtikelNichtGefunden.mitFertigerNachricht(e.getNachricht());
                case "BestandNichtAusreichendException" -> throw new BestandNichtAusreichendException(0, 0);
                case "MengeUngueltigException" -> throw MengeUngueltigException.mitFertigerNachricht(e.getNachricht());
                case "ArtikelNullException" -> throw new ArtikelNullException();
                default -> throw new IOException("Serverfehler: " + e.getMessage());
            }
        }
    }

    @Override
    public void loeschen(int nr) throws IOException {
        try {
            verbindung.sendeKommando("ARTIKEL_LOESCHEN", String.valueOf(nr));
        } catch (ServerFehlerException e) {
            throw new IOException(e.getMessage());
        }
    }

    @Override
    public Map<LocalDate, Integer> getBestandsHistorie(int artikelNr) throws ArtikelNichtGefunden {
        try {
            String json = verbindung.sendeKommandoMitAntwort("BESTANDSHISTORIE", String.valueOf(artikelNr));
            return verbindung.mapper.readValue(json, new TypeReference<Map<LocalDate, Integer>>() {
            });
        } catch (ServerFehlerException e) {
            werfeArtikelFehlerOhneIO(e);
            return null; // unerreichbar
        } catch (IOException e) {
            throw new RuntimeException("Fehler bei der Kommunikation mit dem Server: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Artikel> getAlleArtikel() {
        try {
            String json = verbindung.sendeKommandoMitAntwort("ALLE_ARTIKEL");
            return verbindung.mapper.readValue(json, new TypeReference<List<Artikel>>() {
            });
        } catch (IOException | ServerFehlerException e) {
            throw new RuntimeException("Fehler bei der Kommunikation mit dem Server: " + e.getMessage(), e);
        }
    }

    @Override
    public Artikel findeArtikel(int nr) throws ArtikelNichtGefunden {
        try {
            String json = verbindung.sendeKommandoMitAntwort("ARTIKEL_FINDEN", String.valueOf(nr));
            return verbindung.mapper.readValue(json, Artikel.class);
        } catch (ServerFehlerException e) {
            werfeArtikelFehlerOhneIO(e);
            return null; // unerreichbar
        } catch (IOException e) {
            throw new RuntimeException("Fehler bei der Kommunikation mit dem Server: " + e.getMessage(), e);
        }
    }

    // Kein @Override: diese Fassade implementiert nur IAV, nicht IEV. Wird von
    // EshopClient.getAlleEreignisse() (Override von IEV) aufgerufen.
    public List<Ereignis> getAlleEreignisse() {
        try {
            String json = verbindung.sendeKommandoMitAntwort("ALLE_EREIGNISSE");
            return verbindung.mapper.readValue(json, new TypeReference<List<Ereignis>>() {
            });
        } catch (IOException | ServerFehlerException e) {
            throw new RuntimeException("Fehler bei der Kommunikation mit dem Server: " + e.getMessage(), e);
        }
    }

    private void werfeArtikelFehlerOhneIO(ServerFehlerException e) throws ArtikelNichtGefunden {
        if ("ArtikelNichtGefunden".equals(e.getExceptionName())) {
            throw ArtikelNichtGefunden.mitFertigerNachricht(e.getNachricht());
        }
        throw new RuntimeException("Serverfehler: " + e.getMessage());
    }
}
