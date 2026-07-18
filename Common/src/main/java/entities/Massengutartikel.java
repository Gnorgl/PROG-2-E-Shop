package entities;

import java.io.Serializable;

public class Massengutartikel extends Artikel implements Serializable {
    private int packungsGroesse;

    public Massengutartikel() {
        super();
    }

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
