package interfaces.moduls;

import entities.Ereignis;

import java.util.List;

public interface IEV { //Interface EreignisVerwaltung

    // Alle Lagerereignisse (Ein-/Auslagerungen) für die allgemeine Ereignisübersicht in der GUI
    List<Ereignis> getAlleEreignisse();
}
