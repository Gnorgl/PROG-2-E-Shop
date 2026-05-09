package logic.moduls;

import entities.Kunde;
import entities.Rechnung;
import persistence.shop.WarenkorbListe;

public interface ICV {

    Rechnung checkOut(Kunde kunde, WarenkorbListe warenkorbListe);

}
