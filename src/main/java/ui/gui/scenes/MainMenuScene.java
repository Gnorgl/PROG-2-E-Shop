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

public class MainMenuScene extends VBox{
    private final Eshop eshop;
    private final SessionManager session;
    private final EshopGUI guiController;

    //Hier die Elemente Objekte erstellen (so wie in LoginScene)

    public MainMenuScene(Eshop eshop, SessionManager session, EshopGUI guiController) {
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
        //Hier unten kommen die ganzen Verknüpfungen zu CSS-Datei hin
        //Elemente hinzufügen
        //Event-Handling
    }

    public void abmelden() {
        //Wenn erfolgreich, dann:
        guiController.showLoginScene();
    }



}
