package persistence.shop;

import entities.Rechnung;
import java.util.ArrayList;
import java.util.List;

public class OrderListe {
    private List<Rechnung> alleRechnungen;

    public OrderListe() {
        this.alleRechnungen = new ArrayList<>();
    }

    // Fügt eine neue Rechnung zur Liste der Bestellungen hinzu.

    public void addRechnung(Rechnung rechnung) {
        alleRechnungen.add(rechnung);
    }

   //Gibt alle gespeicherten Rechnungen zurück.

    public List<Rechnung> getAlleRechnungen() {
        return alleRechnungen;
    }
    public void setAlleRechnungen(List<Rechnung> alleRechnungen) {
        this.alleRechnungen = alleRechnungen;
    }

    //Sucht eine Rechnung anhand ihrer Rechnungsnummer.

    public Rechnung getRechnungByNummer(int nummer) {
        for (Rechnung r : alleRechnungen) {
            if (r.getRechnungsNummer() == nummer) {
                return r;
            }
        }
        return null;
    }
}
