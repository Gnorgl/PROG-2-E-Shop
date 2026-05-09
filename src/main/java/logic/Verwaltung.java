package logic;

import logic.management.*;

public abstract class Verwaltung {
    private ArtikelVerwaltung artikelVerwaltung = new ArtikelVerwaltung();
    private KundenVerwaltung kundenVerwaltung = new KundenVerwaltung();
    private MitarbeiterVerwaltung mitarbeiter = new MitarbeiterVerwaltung();
    private ShoppingVerwaltung shoppingVerwaltung = new ShoppingVerwaltung();
    private BenutzerVerwaltung benutzerVerwaltung = new BenutzerVerwaltung();

    public Verwaltung() {

    }

    public void artikelAnlegenOderSo(int nr, String name, int bestand, double preis) {
        boolean test = artikelVerwaltung.legeArtikelAn(nr, name, bestand, preis);
        System.out.println("Status des Anlegens der Artikel: " + test);
    }

}
