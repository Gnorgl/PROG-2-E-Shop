package ui.gui.scenes;

import entities.Benutzer;
import exceptions.user.BenutzerExistiertNichtException;
import interfaces.InterfaceEshop;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import ui.gui.EshopGUI;
import ui.gui.SessionManager;
import ui.gui.components.CustomButton;
import ui.gui.components.CustomInputField;
import ui.gui.components.CustomPasswordField;

public class LoginScene extends VBox {

    private final InterfaceEshop eshop;
    private final SessionManager session;
    private final EshopGUI guiController;

    // Custom-Komponenten setzen PromptText und Klassen intern
    private final CustomInputField emailFeld = new CustomInputField("E-Mail-Adresse");
    private final CustomPasswordField passwortFeld = new CustomPasswordField("Passwort");

    private final CustomButton loginButton = new CustomButton("Anmelden", CustomButton.ButtonType.PRIMARY);
    private final CustomButton zeigeRegistrationSceneButton = new CustomButton("Noch kein Konto? Registrieren", CustomButton.ButtonType.SECONDARY);
    private final Label infoLabel = new Label();

    public LoginScene(InterfaceEshop eshop, SessionManager session, EshopGUI guiController) {
        this.eshop = eshop;
        this.session = session;
        this.guiController = guiController;

        try {
            String cssPath = getClass().getResource("/ui/gui/css/style.css").toExternalForm();
            this.getStylesheets().add(cssPath);
        } catch (NullPointerException e) {
            System.err.println("CSS Datei nicht gefunden!");
        }

        this.getStyleClass().add("login-container"); //An sich sollte es keine getStyleClass() in den views oder scenen geben, alles als component
        infoLabel.getStyleClass().add("fehler-label");

        Label titel = new Label("Willkommen im E-Shop - Login"); //Das auch als Component darstellen
        titel.getStyleClass().add("login-titel");

        // Keine manuellen Button-Styles oder Field-Prompts
        this.getChildren().addAll(titel, emailFeld, passwortFeld, loginButton, zeigeRegistrationSceneButton, infoLabel);

        loginButton.setOnAction(event -> login());
        zeigeRegistrationSceneButton.setOnAction(event -> guiController.showRegistrationScene());
    }

    private void login() {
        String email = emailFeld.getText().trim().toLowerCase();
        String passwort = passwortFeld.getText();

        infoLabel.setText("");

        try {
            Benutzer benutzer = eshop.benutzerCheck(email);

            if (eshop.passwordCheck(benutzer, passwort)) {
                session.login(benutzer);
                System.out.println("Login successful!");
                guiController.showMainLayoutScene();
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