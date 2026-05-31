package entities;

public class Massengutartikel extends Artikel {
    private int packungsGroesse;

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
}
