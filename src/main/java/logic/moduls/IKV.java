package logic.moduls;

import persistence.user.KundenListe;

public interface IKV {

    KundenListe getKundenListe();

    boolean createNewKunden();
}
