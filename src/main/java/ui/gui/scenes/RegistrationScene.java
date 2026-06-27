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

public class RegistrationScene extends VBox{

    private final Eshop eshop;
    private final SessionManager session;
    private final EshopGUI guiController;

    private final TextField emailFeld = new TextField();
    private final PasswordField passwortFeld = new PasswordField();
    private final Button registrationButton = new Button("Registrieren");
    private final Button loginButton = new Button("Anmelden");
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
    }
}
