package ui.gui.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import logic.SessionManager;
import ui.gui.EshopGUI;

public class NavigationSidebar extends VBox {

    public NavigationSidebar(SessionManager session, EshopGUI guiController) {
        // Basis-Styling für die Sidebar
        this.setSpacing(15);
        this.setPadding(new Insets(20));
        this.getStyleClass().add("sidebar");
        this.setPrefWidth(250);

        // --- Benutzer-Info ---
        if (session.getBenutzer() != null) {
            VBox userInfo = new VBox(5);
            userInfo.setAlignment(Pos.CENTER_LEFT);

            String name = session.getBenutzer().getVorname() + " " + session.getBenutzer().getNachname();
            Label nameLabel = new Label(name);
            nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

            String rollenName = session.istBenutzerEinMitarbeiter() ? "Mitarbeiter" : "Kunde";
            Label rolleLabel = new Label("[" + rollenName + "]");
            rolleLabel.setStyle("-fx-text-fill: #888888; -fx-font-size: 12px;");

            userInfo.getChildren().addAll(nameLabel, rolleLabel);
            this.getChildren().add(userInfo);
            this.getChildren().add(new Label("-------------------------"));
        }

        // --- Dynamische Buttons je nach Rolle ---
        if (session.istBenutzerEinMitarbeiter()) {
            Button infoBtn = new Button("Warenbestand verwalten");
            Button mitarbeiterAnlegenBtn = new Button("Mitarbeiter anlegen");
            Button logBtn = new Button("Historie / Protokoll");

            styleAndAddButtons(infoBtn, mitarbeiterAnlegenBtn, logBtn);

            // Routings über den GUI-Controller anstupsen
            //infoBtn.setOnAction(e -> guiController.showArtikelVerwaltungScene());
            mitarbeiterAnlegenBtn.setOnAction(e -> guiController.showEmployeeCreationScene());
            //logBtn.setOnAction(e -> guiController.showLogScene());
        } else {
            Button shopBtn = new Button("Zum Marktplatz");
            Button warenkorbBtn = new Button("Warenkorb");
            Button historieBtn = new Button("Meine Bestellungen");

            styleAndAddButtons(shopBtn, warenkorbBtn, historieBtn);

            //shopBtn.setOnAction(e -> guiController.showWarenkatalogScene());
            // warenkorbBtn und historieBtn analog verknüpfen
        }

        // --- 3. Logout-Bereich ---
        VBox logoutBox = new VBox();
        logoutBox.setPadding(new Insets(50, 0, 0, 0));

        Button logoutBtn = new Button("Abmelden");
        logoutBtn.getStyleClass().add("logout-button");
        logoutBtn.setOnAction(e -> {
            session.logout();
            guiController.showLoginScene();
        });

        logoutBox.getChildren().add(logoutBtn);
        this.getChildren().add(logoutBox);
    }

    private void styleAndAddButtons(Button... buttons) {
        for (Button btn : buttons) {
            btn.getStyleClass().add("sidebar-button");
            btn.setMaxWidth(Double.MAX_VALUE); // Damit alle Knöpfe gleich breit sind
            this.getChildren().add(btn);
        }
    }
}
