package netzwerk;

// Kleine Test-Klasse zum manuellen Ausprobieren, unabhängig von der echten GUI.
// Vorher XYServer.main() in einem separaten Programmlauf starten!
public class NetzwerkTest {
    public static void main(String[] args) throws Exception {
        ServerVerbindung verbindung = new ServerVerbindung("localhost", 6789);
        ArtikelVerwaltungFassade artikelVerwaltung = new ArtikelVerwaltungFassade(verbindung);

        // Testartikel anlegen
        artikelVerwaltung.legeArtikelAn("Testartikel", 50, 9.99);
        System.out.println("Artikel angelegt.");

        // Alle Artikel abrufen
        artikelVerwaltung.getAlleArtikel().forEach(a ->
                System.out.println(a.getArtikelNummer() + ": " + a.getBezeichnung() + " - Bestand: " + a.getBestand())
        );

        verbindung.schliessen();
    }
}
