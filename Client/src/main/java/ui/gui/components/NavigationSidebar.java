package ui.gui.components;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import logic.Eshop;
import logic.SessionManager;
import ui.gui.EshopGUI;
import ui.gui.scenes.MainLayoutScene;
import ui.gui.views.EmployeeCreationView;
import ui.gui.views.KatalogView;

public class NavigationSidebar extends VBox {

    private final MainLayoutScene mainLayout;
    private final Eshop eshop;

    public NavigationSidebar(Eshop eshop, SessionManager session, EshopGUI guiController, MainLayoutScene mainLayout) {
        this.eshop = eshop;
        this.mainLayout = mainLayout;

        // Basis-Styling wird komplett aus der CSS geladen
        this.getStyleClass().add("sidebar");

        // --- Benutzer-Info ---
        if (session.getBenutzer() != null) {
            VBox userInfo = new VBox();
            userInfo.getStyleClass().add("sidebar-user-info");

            String name = session.getBenutzer().getVorname() + " " + session.getBenutzer().getNachname();
            Label nameLabel = new Label(name);
            nameLabel.getStyleClass().add("sidebar-username");

            String rollenName = session.istBenutzerEinMitarbeiter() ? "Mitarbeiter" : "Kunde";
            Label rolleLabel = new Label("[" + rollenName + "]");
            rolleLabel.getStyleClass().add("sidebar-userrole");

            userInfo.getChildren().addAll(nameLabel, rolleLabel);
            this.getChildren().add(userInfo);
        }

        // --- Globaler Home-Button (Für alle Rollen) ---
        Button homeBtn = new Button("Dashboard / Home");
        homeBtn.getStyleClass().add("sidebar-button");
        homeBtn.setOnAction(e -> mainLayout.showDashboard());
        this.getChildren().add(homeBtn);

        // --- Dynamische Buttons je nach Rolle ---
        if (session.istBenutzerEinMitarbeiter()) {
            Button infoBtn = new Button("Warenbestand verwalten");
            Button mitarbeiterAnlegenBtn = new Button("Mitarbeiter anlegen");
            Button logBtn = new Button("Historie / Protokoll");

            infoBtn.setOnAction(e -> mainLayout.setCenterView(new ui.gui.views.ArtikelVerwaltungView(eshop, session, guiController)));
            addSidebarButtons(infoBtn, mitarbeiterAnlegenBtn, logBtn);

            mitarbeiterAnlegenBtn.setOnAction(e -> mainLayout.setCenterView(new EmployeeCreationView(eshop, session, guiController)));
            //Hier noch die event listener, wenn die anderen views erstellt wurden.
        } else {
            Button shopBtn = new Button("Zum Marktplatz");
            Button warenkorbBtn = new Button("Warenkorb");
            Button historieBtn = new Button("Meine Bestellungen");

            addSidebarButtons(shopBtn, warenkorbBtn, historieBtn);
            shopBtn.setOnAction(e -> mainLayout.setCenterView(new KatalogView(eshop, session, guiController)));
            warenkorbBtn.setOnAction(e -> mainLayout.setCenterView(new ui.gui.views.WarenkorbView(eshop, session, guiController, mainLayout)));
        }

        // --- Logout-Bereich ---
        VBox logoutBox = new VBox();
        logoutBox.getStyleClass().add("sidebar-logout-box");

        Button logoutBtn = new Button("Abmelden");
        logoutBtn.getStyleClass().add("logout-button");
        logoutBtn.setOnAction(e -> {
            session.logout();
            guiController.showLoginScene();
        });

        logoutBox.getChildren().add(logoutBtn);
        this.getChildren().add(logoutBox);
    }

    private void addSidebarButtons(Button... buttons) {
        for (Button btn : buttons) {
            btn.getStyleClass().add("sidebar-button");
            this.getChildren().add(btn);
        }
    }
}