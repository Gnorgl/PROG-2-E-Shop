package netzwerk;

import com.fasterxml.jackson.core.type.TypeReference;
import entities.Artikel;
import entities.Kunde;
import entities.Rechnung;
import exceptions.artikel.ArtikelNichtGefunden;
import exceptions.artikel.ArtikelNullException;
import interfaces.moduls.ICV;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Sieht für die GUI genauso aus wie die echte CheckOutVerwaltung (implementiert
// dasselbe Interface ICV), leitet Checkout/Bestellverlauf aber über die
// ServerVerbindung an den Server weiter.
public class CheckOutVerwaltungFassade implements ICV {

    private final ServerVerbindung verbindung;

    public CheckOutVerwaltungFassade(ServerVerbindung verbindung) {
        this.verbindung = verbindung;
    }

    @Override
    public Rechnung checkOut(Kunde kunde, Map<Artikel, Integer> warenkorbInhalt, interfaces.moduls.IAV artikelVerwaltung) throws ArtikelNichtGefunden, ArtikelNullException, IOException {
        try {
            String kundeJson = verbindung.mapper.writeValueAsString(kunde);
            String warenkorbJson = verbindung.mapper.writeValueAsString(zuNrMap(warenkorbInhalt));

            String json = verbindung.sendeKommandoMitAntwort("CHECKOUT", kundeJson, warenkorbJson);
            return verbindung.mapper.readValue(json, Rechnung.class);
        } catch (ServerFehlerException e) {
            switch (e.getExceptionName()) {
                case "ArtikelNichtGefunden" -> throw new ArtikelNichtGefunden(e.getNachricht());
                case "ArtikelNullException" -> throw new ArtikelNullException();
                default -> throw new IOException("Serverfehler: " + e.getMessage());
            }
        }
    }

    @Override
    public double berechneNettoSumme(Map<Artikel, Integer> warenkorbInhalt) {
        try {
            String warenkorbJson = verbindung.mapper.writeValueAsString(zuNrMap(warenkorbInhalt));
            String antwort = verbindung.sendeKommandoMitAntwort("NETTOSUMME", warenkorbJson);
            return Double.parseDouble(antwort);
        } catch (IOException | ServerFehlerException e) {
            throw new RuntimeException("Fehler bei der Kommunikation mit dem Server: " + e.getMessage(), e);
        }
    }

    // Nicht Teil von ICV, aber auf CheckOutVerwaltung vorhanden - für die
    // Kassen-Anzeige (Brutto = Netto + 19% MwSt.) wird kein extra Server-Aufruf
    // gebraucht, das lässt sich lokal aus der Nettosumme ableiten.
    public double berechneBruttoSumme(Map<Artikel, Integer> warenkorbInhalt) {
        return berechneNettoSumme(warenkorbInhalt) * 1.19;
    }

    // Nicht Teil von ICV, aber auf CheckOutVerwaltung vorhanden (Bestellverlauf).
    public List<Rechnung> getRechnungenFuerKunde(Kunde kunde) {
        try {
            String kundeJson = verbindung.mapper.writeValueAsString(kunde);
            String json = verbindung.sendeKommandoMitAntwort("BESTELLVERLAUF", kundeJson);
            return verbindung.mapper.readValue(json, new TypeReference<List<Rechnung>>() {
            });
        } catch (IOException | ServerFehlerException e) {
            throw new RuntimeException("Fehler bei der Kommunikation mit dem Server: " + e.getMessage(), e);
        }
    }

    @Override
    public void rechnungAnzeigen(Rechnung rechnung) {
        // Reine Anzeige-Funktion, dafür braucht es keinen Server-Aufruf -
        // die Rechnung liegt dem Client durch checkOut(...) bereits vollständig vor.
        if (rechnung == null) {
            System.out.println("Keine Rechnung vorhanden!");
            return;
        }
        System.out.println("\n========== RECHNUNG ==========");
        System.out.println("Rechnungsnummer: " + rechnung.getRechnungsNummer());
        System.out.println("Datum: " + rechnung.getDatum());
        System.out.println("Kunde: " + rechnung.getKunde().getNachname() + ", " + rechnung.getKunde().getVorname());
        System.out.printf("Netto:  %.2f€%n", rechnung.getNettosumme());
        System.out.printf("MwSt:   %.2f€%n", rechnung.getMwstBetrag());
        System.out.printf("Brutto: %.2f€%n", rechnung.getBruttoSumme());
        System.out.println("==============================\n");
    }

    private Map<Integer, Integer> zuNrMap(Map<Artikel, Integer> warenkorbInhalt) {
        Map<Integer, Integer> nrMap = new LinkedHashMap<>();
        for (Map.Entry<Artikel, Integer> eintrag : warenkorbInhalt.entrySet()) {
            nrMap.put(eintrag.getKey().getArtikelNummer(), eintrag.getValue());
        }
        return nrMap;
    }
}
