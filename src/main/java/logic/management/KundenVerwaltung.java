package logic.management;

import logic.moduls.IKV;
import persistence.user.KundenListe;

public class KundenVerwaltung implements IKV {
    private final KundenListe kundenListe = new KundenListe();

    @Override
    public KundenListe getKundenListe() {
        return kundenListe;
    }

    @Override
    public boolean createNewKunden() {
        return true;
    }
}
