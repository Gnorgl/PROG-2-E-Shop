package entities;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

public class Rechnung implements Serializable {
        private int rechnungsNummer;
        private Kunde kunde;
        private LocalDate datum;
        private double nettoSumme;
        private double mwstBetrag;
        private double bruttoSumme;
        private List<Artikel> artikel;

        public Rechnung() {}

        public Rechnung(int rechnungsNummer, Kunde kunde, List<Artikel> artikel, double nettoSumme,
                        double mwstBetrag, double bruttoSumme) {
        this.datum = LocalDate.now();
        this.kunde = kunde;
        this.rechnungsNummer = rechnungsNummer;
        this.artikel = artikel;
        this.nettoSumme = nettoSumme;
        this.mwstBetrag = mwstBetrag;
        this.bruttoSumme = bruttoSumme;
        }

        public int getRechnungsNummer() { return rechnungsNummer; }
        public void setRechnungsNummer(int rechnungsNummer) {
                this.rechnungsNummer = rechnungsNummer;
        }

        public Kunde getKunde() { return kunde; }
        public void setKunde(Kunde kunde) {
                this.kunde = kunde;
        }

        public List<Artikel> getArtikel() { return artikel;}
        public void setArtikel(List<Artikel> artikel) {
                this.artikel = artikel;
        }

        public LocalDate getDatum() { return datum; }
        public void setDatum(LocalDate datum) { this.datum = datum; }

        public double getNettosumme() { return nettoSumme; }
        public void setNettosumme(double nettosumme) {
                this.nettoSumme = nettosumme;
        }

        public double getMwstBetrag() { return mwstBetrag; }
        public void setMwstBetrag(double mwstBetrag) {
                this.mwstBetrag = mwstBetrag;
        }

        public double getBruttoSumme() { return bruttoSumme; }
        public void setBruttoSumme(double bruttoSumme) {
                this.bruttoSumme = bruttoSumme;
        }

}


