package entities;

import java.io.Serializable;

/** Ein Artikel, der nur in Vielfachen einer festen Packungsgröße ein-/ausgelagert werden kann. */
public class Massengutartikel extends Artikel implements Serializable {
    private int packungsGroesse;

    /** Konstruktor ohne Parameter für die Jackson-Deserialisierung. */
    public Massengutartikel() {
        super();
    }

    /**
     * @param artikelnummer eindeutige Artikelnummer
     * @param bezeichnung Bezeichnung des Artikels
     * @param bestand Lagerbestand in Stück
     * @param preis Preis pro Packung
     * @param packungsGroesse Stückzahl pro Packung
     */
    public Massengutartikel(int artikelnummer, String bezeichnung, int bestand, double preis, int packungsGroesse) {
        super(artikelnummer, bezeichnung, bestand, preis);
        this.packungsGroesse = packungsGroesse;
    }
    public int getPackungsGroesse() {
        return packungsGroesse;
    }

    public  void setPackungsGroesse(int packungsGroesse) {
        this.packungsGroesse = packungsGroesse;
    }

    @Override
    public boolean istMengeGueltig(int menge){
        if (menge <= 0) {
            return false;
        }
        return menge % packungsGroesse == 0;
    }

    @Override
    public double berechneGesamtpreis(int menge) {
        return getPreis() * (menge / packungsGroesse);
    }
}
