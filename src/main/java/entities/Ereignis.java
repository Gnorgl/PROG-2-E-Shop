package entities;
import java.time.LocalDate;

public class Ereignis {
    private int datum; //Jahrestag
    private Artikel artikel;
    private int anzahl;
    private Benutzer benutzer; //Kunde oder Mitarbeiter
    private String typ;

    public Ereignis(Artikel artikel, int anzahl, Benutzer benutzer, String typ) {
        this.datum = LocalDate.now().getDayOfYear();
        this.artikel = artikel;
        this.anzahl = anzahl;
        this.benutzer = benutzer;
        this.typ = typ;
    }
}
