package ui.gui.scenes;

import entities.Benutzer;
import exceptions.user.BenutzerExistiertNichtException;
import exceptions.user.EmailBereitsVergebenException;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import logic.Eshop;
import logic.SessionManager;
import ui.gui.EshopGUI;

public class EmployeeCreationScene extends VBox{
    private final Eshop eshop;
    private final SessionManager session;
    private final EshopGUI guiController;

    private final TextField emailFeld = new TextField();
    private final PasswordField passwortFeld = new PasswordField();
    private final TextField vornameFeld = new TextField();
    private final TextField nachnameFeld = new TextField();

    private final Button registrationButton = new Button("Registrieren");
    private final Button zurueckButton = new Button("Zurück zum Login");
    private final Label infoLabel = new Label();

    public EmployeeCreationScene(Eshop eshop, SessionManager session, EshopGUI guiController) {
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
        emailFeld.getStyleClass().add("eingabe-feld");
        passwortFeld.getStyleClass().add("eingabe-feld");
        vornameFeld.getStyleClass().add("eingabe-feld");
        nachnameFeld.getStyleClass().add("eingabe-feld");
        registrationButton.getStyleClass().add("shop-button");
        zurueckButton.getStyleClass().add("secondary-button");
        infoLabel.getStyleClass().add("fehler-label");

        //Überschrift
        Label titel = new Label("Erstellung eines Mitarbeiters!");
        titel.getStyleClass().add("login-titel");

        // Platzhalter-Texte setzen
        emailFeld.setPromptText("E-Mail-Adresse");
        passwortFeld.setPromptText("Passwort");
        vornameFeld.setPromptText("Vorname");
        nachnameFeld.setPromptText("Nachname");

        //Elemente hinzufügen
        this.getChildren().addAll(
                titel,
                emailFeld,
                passwortFeld,
                vornameFeld,
                nachnameFeld,
                registrationButton,
                zurueckButton,
                infoLabel
        );

        //Event-Handling
        registrationButton.setOnAction(event -> createEmployee());
        zurueckButton.setOnAction(event -> guiController.showMainMenuScene());
    }

    private void createEmployee() {
        //Bei erfolgreicher Erstellung:
        guiController.showMainMenuScene();
    }
}
