package ui.gui.scenes;

import entities.Artikel;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import logic.Eshop;
import logic.SessionManager;
import ui.gui.EshopGUI;

public class CheckoutScene extends VBox {
    private final Eshop eshop;
    private final SessionManager session;
    private final EshopGUI guiController;


    private TableView<Artikel> warenkorbTable;
    private ObservableList<Artikel> warenkorbListe;
    private Label gesamtPreisLabel;
    private Button kaufenBtn;

    public CheckoutScene(Eshop eshop, SessionManager session, EshopGUI guiController) {
        this.eshop = eshop;
        this.session = session;
        this.guiController = guiController;

        // CSS laden (Pfad anpassen, falls nötig)
        try {
            String cssPath = getClass().getResource("/ui/gui/css/style.css").toExternalForm();
            this.getStylesheets().add(cssPath);
        } catch (NullPointerException e) {
            System.err.println("CSS Datei nicht gefunden!");
        }

        this.setSpacing(15);
        this.setPadding(new Insets(20));
    }
}