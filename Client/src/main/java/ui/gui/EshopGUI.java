package ui.gui;

import interfaces.InterfaceEshop;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import net.EshopClient;
import ui.gui.views.ArtikelVerwaltungView;
import ui.gui.views.EmployeeCreationView;
import ui.gui.scenes.LoginScene;
import ui.gui.scenes.MainLayoutScene;
import ui.gui.scenes.RegistrationScene;

import java.io.IOException;

public class EshopGUI extends Application {

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8080;

    private InterfaceEshop eshop;
    private SessionManager session;
    private Stage primaryStage;
    private boolean verbindungErfolgreich = false;

    @Override
    public void init() {
        this.session = new SessionManager();

        try {
            System.out.println("Verbinde mit EShop-Server auf Port " + SERVER_PORT + "...");
            this.eshop = new EshopClient(SERVER_HOST, SERVER_PORT);
            this.verbindungErfolgreich = true;
        } catch (IOException e) {
            System.err.println("Netzwerkfehler beim Starten: " + e.getMessage());
            this.verbindungErfolgreich = false;
        }
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Eshop");

        // 1. Verbindung ist fehlgeschlagen
        if (!verbindungErfolgreich) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Verbindungsfehler");
            alert.setHeaderText("Server nicht erreichbar");
            alert.setContentText("Es konnte keine Verbindung zum Eshop-Server hergestellt werden.\n" +
                    "Bitte stellen Sie sicher, dass der Server gestartet ist.");
            alert.showAndWait();
            Platform.exit();
            return;
        }

        // 2. Verbindung steht
        this.primaryStage.setOnCloseRequest(event -> {
            if (eshop instanceof EshopClient client) {
                System.out.println("Schliesse Verbindung zum Server...");
                client.verbindungSchliessen();
            }
            Platform.exit();
            System.exit(0);
        });

        // Erste Szene zeigen
        showLoginScene();

        this.primaryStage.setWidth(1280);
        this.primaryStage.setHeight(800);
        this.primaryStage.show();
    }

    public void changeScene(Pane newScene) {
        Scene aktuelleScene = primaryStage.getScene();
        if (aktuelleScene == null) {
            primaryStage.setScene(new Scene(newScene));
        } else {
            primaryStage.getScene().setRoot(newScene);
        }
    }

    public void showLoginScene() {
        LoginScene loginScene = new LoginScene(eshop, session, this);
        changeScene(loginScene);
    }

    public void showMainLayoutScene() {
        MainLayoutScene mainScene = new MainLayoutScene(eshop, session, this);
        changeScene(mainScene);
    }

    public void showRegistrationScene() {
        RegistrationScene registrationScene = new RegistrationScene(eshop, session, this);
        changeScene(registrationScene);
    }

    public void showEmployeeCreationScene() {
        EmployeeCreationView employeeScene = new EmployeeCreationView(eshop, session, this);
        changeScene(employeeScene);
    }

    public void showArtikelVerwaltungView() {
        ArtikelVerwaltungView artikelVerwaltungView = new ArtikelVerwaltungView(eshop, session, this);
        changeScene(artikelVerwaltungView);
    }

    public static void main(String[] args) {
        launch(args);
    }
}