package ui.gui.scenes;

import entities.Benutzer;
import exceptions.user.BenutzerExistiertNichtException;
import exceptions.user.EmailBereitsVergebenException;
import interfaces.InterfaceEshop;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import ui.gui.EshopGUI;
import ui.gui.SessionManager;
import ui.gui.components.CustomButton;
import ui.gui.components.CustomInputField;
import ui.gui.components.CustomPasswordField;

public class RegistrationScene extends VBox {

    private final InterfaceEshop eshop;
    private final SessionManager session;
    private final EshopGUI guiController;

    private final CustomInputField emailFeld = new CustomInputField("E-Mail-Adresse");
    private final CustomPasswordField passwortFeld = new CustomPasswordField("Passwort");
    private final CustomInputField vornameFeld = new CustomInputField("Vorname");
    private final CustomInputField nachnameFeld = new CustomInputField("Nachname");
    private final CustomInputField adresseFeld = new CustomInputField("Straße und Hausnummer");

    private final CustomButton registrationButton = new CustomButton("Registrieren", CustomButton.ButtonType.PRIMARY);
    private final CustomButton zurueckButton = new CustomButton("Zurück zum Login", CustomButton.ButtonType.SECONDARY);
    private final Label infoLabel = new Label();

    public RegistrationScene(InterfaceEshop eshop, SessionManager session, EshopGUI guiController) {
        this.eshop = eshop;
        this.session = session;
        this.guiController = guiController;

        try {
            String cssPath = getClass().getResource("/ui/gui/css/style.css").toExternalForm();
            this.getStylesheets().add(cssPath);
        } catch (NullPointerException e) {
            System.err.println("CSS Datei nicht gefunden!");
        }

        this.getStyleClass().add("login-container");
        infoLabel.getStyleClass().add("fehler-label");

        Label titel = new Label("Registrieren Sie ihr Konto!");
        titel.getStyleClass().add("login-titel");

        this.getChildren().addAll(
                titel,
                emailFeld,
                passwortFeld,
                vornameFeld,
                nachnameFeld,
                adresseFeld,
                registrationButton,
                zurueckButton,
                infoLabel
        );

        registrationButton.setOnAction(event -> registrieren());
        zurueckButton.setOnAction(event -> guiController.showLoginScene());
    }

    private void registrieren() {
        String email = emailFeld.getText().trim().toLowerCase();
        String passwort = passwortFeld.getText().trim();
        String vorname = vornameFeld.getText().trim();
        String nachname = nachnameFeld.getText().trim();
        String adresse = adresseFeld.getText().trim();

        infoLabel.setText("");

        if (email.isEmpty() || passwort.isEmpty() || vorname.isEmpty() || nachname.isEmpty() || adresse.isEmpty()) {
            infoLabel.setText("Bitte alle Felder ausfüllen!");
            return;
        }

        String emailMuster = "^[a-z0-9._%+-]+@[a-z0-9.-]+\\.(de|com|net|org)$";
        if (!email.matches(emailMuster)) {
            infoLabel.setText("Ungültige E-Mail! (nur ...@domain.com)");
            return;
        }

        try {
            if (eshop.istEmailVergeben(email)) {
                infoLabel.setText("Diese E-Mail-Adresse wird bereits verwendet!");
                return;
            }

            eshop.createNewKunden(email, passwort, nachname, vorname, adresse);

            Benutzer neuerBenutzer = eshop.benutzerCheck(email);
            session.login(neuerBenutzer);

            System.out.println("Registrierung und Login erfolgreich!");
            guiController.showMainLayoutScene();

        } catch (EmailBereitsVergebenException e) {
            infoLabel.setText("Fehler bei der Registrierung: " + e.getMessage());
        } catch (BenutzerExistiertNichtException e) {
            infoLabel.setText("Systemfehler beim automatischen Login.");
        }
    }
}