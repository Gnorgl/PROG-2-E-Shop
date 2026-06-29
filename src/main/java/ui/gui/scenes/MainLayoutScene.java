package ui.gui.scenes;

import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import logic.Eshop;
import logic.SessionManager;
import ui.gui.EshopGUI;
import ui.gui.components.NavigationSidebar;
import ui.gui.views.DashboardView; // Die neue Home-Page

public class MainLayoutScene extends BorderPane {

    private final SessionManager session;
    private final EshopGUI guiController;

    public MainLayoutScene(Eshop eshop, SessionManager session, EshopGUI guiController) {
        this.session = session;
        this.guiController = guiController;

        try {
            String cssPath = getClass().getResource("/ui/gui/css/style.css").toExternalForm();
            this.getStylesheets().add(cssPath);
        } catch (NullPointerException e) {
            System.err.println("CSS Datei in MainLayoutScene nicht gefunden!");
        }


        this.setLeft(new NavigationSidebar(eshop, session, guiController, this)); // this wird an sidebar übergeben

        // dashboard ist quasi der home-screen
        showDashboard();
    }

    public void showDashboard() {
        this.setCenter(new DashboardView(session));
    }

    public void setCenterView(Node neueView) {
        this.setCenter(neueView);
    }
}