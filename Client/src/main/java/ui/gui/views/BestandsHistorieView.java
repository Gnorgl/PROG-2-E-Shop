package ui.gui.views;

import exceptions.artikel.ArtikelNichtGefunden;
import javafx.scene.layout.VBox;

import java.awt.*;
import java.time.LocalDate;
import java.util.Map;

public class BestandsHistorieView extends VBox {

    private static final double RAND = 40;
    private final Canvas canvas;

    // Zeichenblock

    public BestandsHistorieView (double breite, double hoehe) {
        canvas = new Canvas(breite, hoehe);
        this.setSpacing(20);
     this.getChildren().addAll(new Label ("Bestands-Historie"), canvas);
    }

    public void zeichne(ArtikelVerwaltung artikelVerwaltung, int artikelNr) throws ArtikelNichtGefunden {
        Map<LocalDate, Integer> bestandsHistorie = artikelVerwaltung.getBestandsHistorie(artikelNr);
        List<Integer> werte = bestandsHistorie.values().stream().toList();
    }
}

// noch nicht fertig....