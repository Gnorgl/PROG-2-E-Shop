package entities;
import java.time.LocalDate;
import java.util.List;

public class Rechnung {
        private int rechnungsNummer;
        private Kunde kunde;
        private LocalDate datum;
        private double nettoSumme;
        private double mwstBetrag;
        private double bruttoSumme;
        private List<Artikel> artikel;

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

        public Kunde getKunde() { return kunde; }

        public List<Artikel> getArtikel() { return artikel;}

        public LocalDate getDatum() { return datum; }

        public double getNettosumme() { return nettoSumme; }

        public double getMwstBetrag() { return mwstBetrag; }

        public double getBruttoSumme() { return bruttoSumme; }

}


