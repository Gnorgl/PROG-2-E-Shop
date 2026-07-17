package logic.verwaltung;

import entities.Artikel;
import entities.Benutzer;
import entities.Ereignis;
import exceptions.artikel.ArtikelNullException;
import persistence.shop.EreignisListe;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class EreignisVerwaltung {
    private final File datei = new File("ereignisse.json");

    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    private EreignisListe ereignisListe = new EreignisListe();


    public EreignisVerwaltung() throws IOException {
        datenLaden();
    }

    public void safe() throws IOException {
        mapper.writerWithDefaultPrettyPrinter().writeValue(datei, this.ereignisListe);
    }

    private void datenLaden() throws IOException {
        if (!datei.exists()) {
            return;
        }
        this.ereignisListe = mapper.readValue(datei, EreignisListe.class);
    }






    public void logEreignis(Artikel artikel, int anzahl, Benutzer benutzer, String typ) throws ArtikelNullException, IOException {

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

        safe();
    }

    public List<Ereignis> getEreignisseFuerArtikel(int artikelNr) {
        return ereignisListe.getAlleEreignisse()
                .stream()
                .filter(e -> e.getArtikel().getArtikelNummer() == artikelNr)
                .collect(Collectors.toList());
    }
}
