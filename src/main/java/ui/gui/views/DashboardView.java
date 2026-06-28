package ui.gui.views; // Neues Package für die "Inhalte" in der Mitte

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import logic.SessionManager;

public class DashboardView extends VBox {

    public DashboardView(SessionManager session) {
        // Styling wird komplett aus der CSS geladen
        this.getStyleClass().add("dashboard-container");

        String name = "Gast";
        if (session.getBenutzer() != null) {
            name = session.getBenutzer().getVorname() + " " + session.getBenutzer().getNachname();
        }

        Label willkommenLabel = new Label("Hallo, " + name + "!");
        willkommenLabel.getStyleClass().add("dashboard-willkommen");

        Label infoText = new Label("Willkommen im Eshop");
        infoText.getStyleClass().add("dashboard-infotext");

        this.getChildren().addAll(willkommenLabel, infoText);

        if (session.istBenutzerEinMitarbeiter()) {
            VBox mitarbeiterInfo = new VBox();
            mitarbeiterInfo.getStyleClass().add("mitarbeiter-infobox");

            Label hinweisTitel = new Label("System-Hinweis:");
            hinweisTitel.getStyleClass().add("mitarbeiter-infobox-titel");

            Label hinweisText = new Label("Angemeldet als Mitarbeiter");
            hinweisText.setWrapText(true);

            mitarbeiterInfo.getChildren().addAll(hinweisTitel, hinweisText);
            this.getChildren().add(mitarbeiterInfo);
        }
    }
}
