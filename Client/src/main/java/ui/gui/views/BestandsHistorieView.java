package ui.gui.views;

import exceptions.artikel.ArtikelNichtGefunden;
import interfaces.InterfaceEshop;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import interfaces.moduls.IAV;

import java.util.List;

public class BestandsHistorieView extends VBox {
    private final InterfaceEshop eshop;
    private static final double RAND = 40;
    private final Canvas canvas;

    // Zeichenblock

    public BestandsHistorieView(InterfaceEshop eshop, double breite, double hoehe) {
        this.eshop = eshop;
        canvas = new Canvas(breite, hoehe);
        this.setSpacing(20);
        this.getChildren().addAll(new Label("Bestands-Historie"), canvas);
    }

    public void zeichne(int artikelNr) throws ArtikelNichtGefunden {
        List<Integer> werte = eshop.getBestandsHistorie(artikelNr).values().stream().toList();

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
        gc.setLineWidth(1);
        gc.strokeLine(RAND, RAND, RAND, RAND + hoehe);
        gc.strokeLine(RAND, RAND + hoehe, RAND + breite, RAND + hoehe);

        // Achsentitel: "Bestand" über der Y-Achse, "Tag" unter der X-Achse rechts außen,
        // beide mit Abstand zu den Zahlen, damit nichts überlappt
        gc.setFont(Font.font(11));
        gc.fillText("Bestand", RAND - 15, RAND - 12);
        gc.fillText("Tag", RAND + breite - 8, RAND + hoehe + 32);

        // Y-Achsen-Beschriftung: zeigt den tatsächlichen Bestand an drei Punkten
        // (unten = min, Mitte, oben = max), rechtsbündig links von der Achse
        gc.setFont(Font.font(10));
        gc.setFill(Color.BLACK);
        int mitte = (min + max) / 2;
        gc.fillText(String.valueOf(max), RAND - 25, RAND + 4);
        gc.fillText(String.valueOf(mitte), RAND - 25, RAND + hoehe / 2 + 4);
        gc.fillText(String.valueOf(min), RAND - 25, RAND + hoehe + 4);

        // Abstand zwischen den 30 Punkten auf der X-Achse
        double schrittX = breite / (werte.size() - 1);

        // Verlaufslinie in blau, extra dick damit sie sich von der schwarzen
        // Achse abhebt, falls der Bestand länger am Minimum (= Achsenhöhe) liegt
        gc.setStroke(Color.STEELBLUE);
        gc.setLineWidth(3);

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

        // X-Achsen-Beschriftung: wie im Beispiel nur Tag 1-5 und die letzten beiden Tage
        // ausschreiben, dazwischen nur "...", damit es bei 30 Tagen nicht überfüllt wird.
        // Kleinere Schrift, damit sich "29" und "30" am rechten Rand nicht überlappen.
        int n = werte.size();
        boolean puenktchenGezeichnet = false;
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);
        gc.setFont(Font.font(9));
        for (int i = 0; i < n; i++) {
            int tag = i + 1;
            double x = RAND + i * schrittX;

            // kleiner Strich für jeden einzelnen Tag auf der Achse
            gc.strokeLine(x, RAND + hoehe, x, RAND + hoehe + 5);

            if (tag <= 5) {
                gc.fillText(String.valueOf(tag), x - 3, RAND + hoehe + 16);
            } else if (tag > n - 2) {
                // die letzten beiden Tage rechtsbündig, damit "30" nicht über den Rand hinausläuft
                gc.fillText(String.valueOf(tag), x - 8, RAND + hoehe + 16);
            } else if (!puenktchenGezeichnet && i >= n / 2) {
                gc.fillText("...", x - 6, RAND + hoehe + 16);
                puenktchenGezeichnet = true;
            }
        }
    }

    // rechnet einen Bestandswert in eine Y-Pixel-Position um, je höher der Bestand desto weiter oben
    private double berechneY(int wert, int min, int max, double hoehe) {
        double anteil = (wert - min) / (double) (max - min);
        return RAND + hoehe - anteil * hoehe;
    }
}
