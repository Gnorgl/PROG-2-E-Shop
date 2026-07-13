package persistence.shop;
import entities.Ereignis;

import java.util.ArrayList;
import java.util.List;


public class EreignisListe {
    private List<Ereignis> alleEreignisse = new ArrayList<>();

    public EreignisListe() {}

    public void hinzuefuegen(Ereignis ereignis) {
        alleEreignisse.add(ereignis);
    }
    public List<Ereignis> getAlleEreignisse() {
        return alleEreignisse;
    }

    public void setAlleEreignisse(List<Ereignis> alleEreignisse) {
        this.alleEreignisse = alleEreignisse;
    }
}
