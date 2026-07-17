package ui.gui.views;

import exceptions.artikel.ArtikelNichtGefunden;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import logic.verwaltung.ArtikelVerwaltung;

import java.util.List;

public class BestandsHistorieView extends VBox {

    private static final double RAND = 40;
    private final Canvas canvas;

    // Zeichenblock

    public BestandsHistorieView(double breite, double hoehe) {
        canvas = new Canvas(breite, hoehe);
        this.setSpacing(20);
        this.getChildren().addAll(new Label("Bestands-Historie"), canvas);
    }

    public void zeichne(ArtikelVerwaltung artikelVerwaltung, int artikelNr) throws ArtikelNichtGefunden {
        List<Integer> werte = artikelVerwaltung.getBestandsHistorie(artikelNr).values().stream().toList();

        GraphicsContext gc = canvas.getGraphicsContext2D();
        // löscht alles, was vorher schon mal gezeichnet wurde
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        double breite = canvas.getWidth() - 2 * RAND;
        double hoehe = canvas.getHeight() - 2 * RAND;

        // sucht den kleinsten und größten Bestand der letzten 30 Tage
        int min = werte.get(0);
        int max = werte.get(0);
        for (int wert : werte) {
            if (wert < min) min = wert;
            if (wert > max) max = wert;
        }
        if (min == max) {
            max = min + 1;
        }

        // zeichnet das schwarze Koordinatenkreuz (Y-Achse links, X-Achse unten)
        gc.setStroke(Color.BLACK);
        gc.strokeLine(RAND, RAND, RAND, RAND + hoehe);
        gc.strokeLine(RAND, RAND + hoehe, RAND + breite, RAND + hoehe);

        // Abstand zwischen den 30 Punkten auf der X-Achse
        double schrittX = breite / (werte.size() - 1);

        // Verlaufslinie in blau
        gc.setStroke(Color.STEELBLUE);
        gc.setLineWidth(2);

        // erster Punkt (Tag 1) sitzt direkt am linken Rand
        double letztesX = RAND;
        double letztesY = berechneY(werte.get(0), min, max, hoehe);

        // verbindet jeden Punkt mit dem nächsten und ergibt so den Linienzug
        for (int i = 1; i < werte.size(); i++) {
            double x = RAND + i * schrittX;
            double y = berechneY(werte.get(i), min, max, hoehe);

            gc.strokeLine(letztesX, letztesY, x, y);

            letztesX = x;
            letztesY = y;
        }

        int aktuellerBestand = werte.get(werte.size() - 1);
        gc.setFill(Color.BLACK);
        gc.fillText("Aktuell: " + aktuellerBestand, letztesX - 40, letztesY - 10);
    }

    // rechnet einen Bestandswert in eine Y-Pixel-Position um, je höher der Bestand desto weiter oben
    private double berechneY(int wert, int min, int max, double hoehe) {
        double anteil = (wert - min) / (double) (max - min);
        return RAND + hoehe - anteil * hoehe;
    }
}
