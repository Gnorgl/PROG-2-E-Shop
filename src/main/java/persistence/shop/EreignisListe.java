package persistence.shop;
import entities.Ereignis;
import java.util.List;


public class EreignisListe {
    private List<Ereignis> alleEreignisse = new Arraylist<>();
    public void hinzuefuegen(Ereignis ereignis) {
        alleEreignisse.add(ereignis);
    }
    public List<Ereignis> getAlleEreignisse() {
        return alleEreignisse;
    }
}
