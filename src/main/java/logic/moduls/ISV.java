package logic.moduls;

import entities.Kunde;
import entities.Rechnung;
import persistence.WarenkorbListe;

public interface ISV {

    Rechnung checkOut(Kunde kunde, WarenkorbListe warenkorbListe);

}
