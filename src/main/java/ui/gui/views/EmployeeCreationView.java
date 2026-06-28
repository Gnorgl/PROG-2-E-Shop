package ui.gui.views;

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

    private final CustomInputField emailFeld = new CustomInputField("E-Mail-Adresse");
    private final CustomPasswordField passwortFeld = new CustomPasswordField("Passwort");
    private final CustomInputField vornameFeld = new CustomInputField("Vorname");
    private final CustomInputField nachnameFeld = new CustomInputField("Nachname");

    private final CustomButton registrationButton = new CustomButton("Mitarbeiter anlegen", CustomButton.ButtonType.PRIMARY);
    private final Label infoLabel = new Label();

    public EmployeeCreationView(Eshop eshop, SessionManager session, EshopGUI guiController) {
        this.eshop = eshop;
        this.guiController = guiController;

        this.getStyleClass().add("dashboard-container");
        infoLabel.getStyleClass().add("fehler-label");

        Label titel = new Label("Erstellung eines Mitarbeiters!");
        titel.getStyleClass().add("login-titel");

        this.getChildren().addAll(
                titel,
                emailFeld,
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
        String email = emailFeld.getText().trim().toLowerCase();
        String passwort = passwortFeld.getText().trim();
        String vorname = vornameFeld.getText().trim();
        String nachname = nachnameFeld.getText().trim();

        infoLabel.setText("");

        if (email.isEmpty() || passwort.isEmpty() || vorname.isEmpty() || nachname.isEmpty()) {
            infoLabel.setText("Bitte alle Felder ausfüllen!");
            return;
        }

        String emailMuster = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.com$";
        if (!email.matches(emailMuster)) {
            infoLabel.setText("Ungültiges E-Mail-Format! (nur ...@domain.com)");
            return;
        }

        try {
            if (eshop.getBenutzerVerwaltung().istEmailVergeben(email)) {
                infoLabel.setText("Diese E-Mail-Adresse wird bereits verwendet!");
                return;
            }

            eshop.getBenutzerVerwaltung().getMitarbeiterVerwaltung().createNewMitarbeiter(email, passwort, nachname, vorname);

            infoLabel.setStyle("-fx-text-fill: #2a9d8f;");
            infoLabel.setText("Mitarbeiter erfolgreich angelegt!");

            // Felder leeren für den nächsten Eintrag
            emailFeld.clear();
            passwortFeld.clear();
            vornameFeld.clear();
            nachnameFeld.clear();

        } catch (EmailBereitsVergebenException e) {
            infoLabel.setStyle("-fx-text-fill: #e76f51;");
            infoLabel.setText("Fehler: " + e.getMessage());
        }
    }
}