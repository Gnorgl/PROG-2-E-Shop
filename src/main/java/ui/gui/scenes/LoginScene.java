package ui.gui.scenes;

import entities.Benutzer;
import exceptions.user.BenutzerExistiertNichtException;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import logic.Eshop;
import logic.SessionManager;
import ui.gui.EshopGUI;
import ui.gui.components.CustomInputField;
import ui.gui.components.CustomPasswordField;


public class LoginScene extends VBox{

    private final Eshop eshop;
    private final SessionManager session;
    private final EshopGUI guiController;

    private final CustomInputField emailFeld = new CustomInputField("E-Mail-Adresse");
    private final CustomPasswordField passwortFeld = new CustomPasswordField("Passwort");

    private final Button loginButton = new Button("Anmelden");
    private final Button zeigeRegistrationSceneButton = new Button("Noch kein Konto? Registrieren");
    private final Label infoLabel = new Label();

    public LoginScene(Eshop eshop, SessionManager session, EshopGUI guiController) {
        this.eshop = eshop;
        this.session = session;
        this.guiController = guiController;

        //CSS hier laden
        try {
            String cssPath = getClass().getResource("/ui/gui/css/style.css").toExternalForm();
            this.getStylesheets().add(cssPath);
        } catch (NullPointerException e) {
            System.err.println("CSS Datei nicht gefunden!");
        }
        //Style-Klasse für VBox
        this.getStyleClass().add("login-container");

        //Den Elementen IDs oder Klassen zuweisen
        loginButton.getStyleClass().add("shop-button");
        zeigeRegistrationSceneButton.getStyleClass().add("secondary-button");
        infoLabel.getStyleClass().add("fehler-label");

        //Überschrift
        Label titel = new Label("Willkommen im E-Shop - Login");
        titel.getStyleClass().add("login-titel");

        //Platzhalter-Texte
        emailFeld.setPromptText("E-Mail-Adresse");
        passwortFeld.setPromptText("Passwort");

        //Elemente hinzufügen
        this.getChildren().addAll(titel, emailFeld, passwortFeld, loginButton, zeigeRegistrationSceneButton, infoLabel);

        //Event-Handling
        loginButton.setOnAction(event -> login());
        zeigeRegistrationSceneButton.setOnAction(event -> guiController.showRegistrationScene());
    }

    private void login() {
        String email = emailFeld.getText().trim().toLowerCase();
        String passwort = passwortFeld.getText();

        infoLabel.setText("");

        try {
            Benutzer benutzer = eshop.getBenutzerVerwaltung().benutzerCheck(email);

            if (eshop.getBenutzerVerwaltung().passwordCheck(benutzer, passwort)) {
                session.login(benutzer);

                System.out.println("Login successful!");
                //Wichtig:
                guiController.showMainMenuScene();
            } else {
                infoLabel.setText("Passwort ist falsch!");
            }

        } catch (BenutzerExistiertNichtException e) {
            infoLabel.setText("Benutzer existiert nicht!");
        } catch (IllegalArgumentException e) {
            infoLabel.setText(e.getMessage());
        }
    }
}
