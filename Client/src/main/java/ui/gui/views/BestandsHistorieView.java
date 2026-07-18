package ui.gui.views;

import exceptions.artikel.ArtikelNichtGefunden;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import interfaces.moduls.IAV;

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

    public void zeichne(IAV artikelVerwaltung, int artikelNr) throws ArtikelNichtGefunden {
        List<Integer> werte = artikelVerwaltung.getBestandsHistorie(artikelNr).values().stream().toList();
        zeichneWerte(werte);
    }

    // TESTCODE - zeichnet eine beliebige Werteliste direkt (z.B. pro Ereignis statt pro Tag),
    public void zeichneWerte(List<Integer> werte) {
        if (werte.isEmpty()) {
            return;
        }
        if (werte.size() == 1) {
            // Nur ein Punkt vorhanden - keine Linie zeichenbar, verhindert Division durch 0
            werte = List.of(werte.get(0), werte.get(0));
        }

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

        // Abstand zwischen den Punkten auf der X-Achse
        double schrittX = breite / (werte.size() - 1);

        // Y-Achse: Zwischen-Ticks mit kleinen Strichmarken und Werten. Bei einer
        gc.setFill(Color.BLACK);
        int anzahlTicksY = Math.min(5, max - min);
        for (int i = 0; i <= anzahlTicksY; i++) {
            double anteil = i / (double) anzahlTicksY;
            int wert = min + (int) Math.round((max - min) * anteil);
            double y = RAND + hoehe - anteil * hoehe;

            gc.strokeLine(RAND - 5, y, RAND, y); // kleine Strichmarke
            gc.fillText(String.valueOf(wert), RAND - 28, y + 4);
        }

        // X-Achse: Tick alle paar Tage (abhängig von der Anzahl Punkte), damit es
        // bei 30 Werten nicht überladen wirkt, bei wenigen aber jeder Tag zu sehen ist
        int anzahlPunkte = werte.size();
        int schrittTage = Math.max(1, (int) Math.ceil(anzahlPunkte / 8.0));
        for (int i = 0; i < anzahlPunkte; i += schrittTage) {
            double x = RAND + i * schrittX;
            gc.strokeLine(x, RAND + hoehe, x, RAND + hoehe + 5); // kleine Strichmarke
            gc.fillText(String.valueOf(i + 1), x - 5, RAND + hoehe + 18);
        }
        // letzten Tag immer mit anzeigen, auch wenn er nicht genau auf einen Tick fällt
        double letzterX = RAND + (anzahlPunkte - 1) * schrittX;
        gc.strokeLine(letzterX, RAND + hoehe, letzterX, RAND + hoehe + 5);
        gc.fillText(String.valueOf(anzahlPunkte), letzterX - 8, RAND + hoehe + 18);

        // Verlaufslinie in blau, als Treppenlinie (wie in der Vorlage):
        // Bestand bleibt bis zum nächsten Tag konstant (waagerecht), dann
        // Sprung auf den neuen Wert (senkrecht) - keine schräge Verbindung.
        gc.setStroke(Color.STEELBLUE);
        gc.setLineWidth(2);

        double letztesX = RAND;
        double letztesY = berechneY(werte.get(0), min, max, hoehe);

        for (int i = 1; i < werte.size(); i++) {
            double x = RAND + i * schrittX;
            double y = berechneY(werte.get(i), min, max, hoehe);

            // waagerechtes Stück bis zum nächsten Tag ...
            gc.strokeLine(letztesX, letztesY, x, letztesY);
            // ... dann senkrechter Sprung auf den neuen Bestand
            gc.strokeLine(x, letztesY, x, y);

            letztesX = x;
            letztesY = y;
        }

        // Beschriftung + Pfeil zum letzten Punkt (Tag 30 / aktueller Bestand)
        int aktuellerBestand = werte.get(werte.size() - 1);
        gc.setFill(Color.BLACK);

        // Text kommt schräg über den Punkt, außer der Punkt liegt zu nah am oberen
        // Rand - dann kommt er stattdessen schräg unter den Punkt
        boolean textUnten = letztesY - 40 < RAND;
        double textX = letztesX - 70;
        double textY = textUnten ? letztesY + 30 : letztesY - 30;

        // GraphicsContext.fillText kann keine Zeilenumbrüche - deshalb zwei Zeilen einzeln
        gc.fillText("Aktueller", textX, textY);
        gc.fillText("Bestand: " + aktuellerBestand, textX, textY + 14);

        // Kurze, gerade Linie vom Text zum Punkt, mit kleiner Pfeilspitze am Ende
        double pfeilStartX = textX + 55;
        double pfeilStartY = textUnten ? textY + 8 : textY + 6;
        gc.strokeLine(pfeilStartX, pfeilStartY, letztesX, letztesY);

        // Feste, einfache Pfeilspitze (zwei kurze Striche), keine Winkelberechnung
        if (textUnten) {
            gc.strokeLine(letztesX, letztesY, letztesX - 6, letztesY + 3);
            gc.strokeLine(letztesX, letztesY, letztesX - 3, letztesY + 7);
        } else {
            gc.strokeLine(letztesX, letztesY, letztesX - 6, letztesY - 3);
            gc.strokeLine(letztesX, letztesY, letztesX - 3, letztesY - 7);
        }
    }

    // rechnet einen Bestandswert in eine Y-Pixel-Position um, je höher der Bestand desto weiter oben
    private double berechneY(int wert, int min, int max, double hoehe) {
        double anteil = (wert - min) / (double) (max - min);
        return RAND + hoehe - anteil * hoehe;
    }
}
