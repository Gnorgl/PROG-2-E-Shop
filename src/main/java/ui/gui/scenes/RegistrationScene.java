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

public class RegistrationScene extends VBox{

    private final Eshop eshop;
    private final SessionManager session;
    private final EshopGUI guiController;

    private final TextField emailFeld = new TextField();
    private final PasswordField passwortFeld = new PasswordField();
    private final TextField vornameFeld = new TextField();
    private final TextField nachnameFeld = new TextField();
    private final TextField adresseFeld = new TextField();

    private final Button registrationButton = new Button("Registrieren");
    private final Button zurueckButton = new Button("Zurück zum Login");
    private final Label infoLabel = new Label();

    public RegistrationScene(Eshop eshop, SessionManager session, EshopGUI guiController) {
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
        adresseFeld.getStyleClass().add("eingabe-feld");
        registrationButton.getStyleClass().add("shop-button");
        zurueckButton.getStyleClass().add("secondary-button");
        infoLabel.getStyleClass().add("fehler-label");

        //Überschrift
        Label titel = new Label("Registrieren Sie ihr Konto!");
        titel.getStyleClass().add("login-titel");

        // Platzhalter-Texte setzen
        emailFeld.setPromptText("E-Mail-Adresse");
        passwortFeld.setPromptText("Passwort");
        vornameFeld.setPromptText("Vorname");
        nachnameFeld.setPromptText("Nachname");
        adresseFeld.setPromptText("Adresse (Straße, Ort)");

        //Elemente hinzufügen
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

        //Event-Handling
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

        //Validierung
        if (email.isEmpty() || passwort.isEmpty() || vorname.isEmpty() || nachname.isEmpty() || adresse.isEmpty()) {
            infoLabel.setText("Bitte alle Felder ausfüllen!");
            return;
        }

        //Validierung: E-Mail-Format
        String emailMuster = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.com$";
        if (!email.matches(emailMuster)) {
            infoLabel.setText("Ungültige E-Mail! (nur ...@domain.com)");
            return;
        }

        //Logik ausführen
        try {
            if (eshop.getBenutzerVerwaltung().istEmailVergeben(email)) {
                infoLabel.setText("Diese E-Mail-Adresse wird bereits verwendet!");
                return; // Abbrechen
            }

            // Wenn hier, dann ist die E-Mail frei
            eshop.getBenutzerVerwaltung().getKundenVerwaltung().createNewKunden(email, passwort, nachname, vorname, adresse);

            // Automatisch einloggen
            Benutzer neuerBenutzer = eshop.getBenutzerVerwaltung().benutzerCheck(email);
            session.login(neuerBenutzer);

            System.out.println("Registrierung und Login erfolgreich!");
            guiController.showMainMenuScene(); //Ins Hauptmenü springen

        } catch (EmailBereitsVergebenException e) {
            infoLabel.setText("Fehler bei der Registrierung: " + e.getMessage());
        } catch (BenutzerExistiertNichtException e) {
            infoLabel.setText("Systemfehler beim automatischen Login.");
        }

    }
}
