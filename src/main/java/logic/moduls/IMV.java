package logic.moduls;

import persistence.user.MitarbeiterListe;

public interface IMV {

    MitarbeiterListe getMitarbeiterListe();

    boolean createNewMitarbeiter();

}
