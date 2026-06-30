package ui.gui.views;

import entities.Mitarbeiter;
import exceptions.user.EmailBereitsVergebenException;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox; // Geändert von BorderPane zu VBox
import logic.Eshop;
import logic.SessionManager;
import ui.gui.EshopGUI;
import ui.gui.components.CustomButton;
import ui.gui.components.CustomInputField;
import ui.gui.components.CustomPasswordField;

public class EmployeeCreationView extends VBox {

    private final Eshop eshop;
    private final EshopGUI guiController;

    private final CustomPasswordField passwortFeld = new CustomPasswordField("Passwort");
    private final CustomInputField vornameFeld = new CustomInputField("Vorname");
    private final CustomInputField nachnameFeld = new CustomInputField("Nachname");

    private final CustomButton registrationButton = new CustomButton("Mitarbeiter anlegen", CustomButton.ButtonType.PRIMARY);
    private final Label infoLabel = new Label();

    public EmployeeCreationView(Eshop eshop, SessionManager session, EshopGUI guiController) {
        this.eshop = eshop;
        this.guiController = guiController;

        this.getStyleClass().add("dashboard-container");
        this.setAlignment(javafx.geometry.Pos.CENTER);
        infoLabel.getStyleClass().add("fehler-label");

        Label titel = new Label("Erstellung eines Mitarbeiters!");
        titel.getStyleClass().add("login-titel");

        this.getChildren().addAll(
                titel,
                passwortFeld,
                vornameFeld,
                nachnameFeld,
                registrationButton,
                infoLabel
        );

        // Event-Handling
        registrationButton.setOnAction(event -> createEmployee());
    }

    private void createEmployee() {
        String passwort = passwortFeld.getText().trim();
        String vorname = vornameFeld.getText().trim();
        String nachname = nachnameFeld.getText().trim();

        infoLabel.setText("");

        if (passwort.isEmpty() || vorname.isEmpty() || nachname.isEmpty()) {
            infoLabel.setText("Bitte alle Felder ausfüllen!");
            return;
        }

        try {
            // Aufruf ohne das E-Mail-Argument anders als bei Kunden
            Mitarbeiter m = eshop.getBenutzerVerwaltung().getMitarbeiterVerwaltung().createNewMitarbeiter(passwort, nachname, vorname);

            infoLabel.setStyle("-fx-text-fill: #2a9d8f;");
            infoLabel.setText("Mitarbeiter erfolgreich angelegt!\nE-Mail: " + m.getEmail());

            // Felder leeren
            passwortFeld.clear();
            vornameFeld.clear();
            nachnameFeld.clear();

        } catch (Exception e) {
            infoLabel.setStyle("-fx-text-fill: #e76f51;");
            infoLabel.setText("Fehler beim Speichern: " + e.getMessage());
        }
    }
}