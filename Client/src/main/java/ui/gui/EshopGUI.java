package ui.gui;

import interfaces.InterfaceEshop;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import ui.gui.views.ArtikelVerwaltungView;
import ui.gui.views.EmployeeCreationView;
import ui.gui.scenes.LoginScene;
import ui.gui.scenes.MainLayoutScene;
import ui.gui.scenes.RegistrationScene;


public class EshopGUI extends Application {


    private static InterfaceEshop eshopFuerStart;

    private InterfaceEshop eshop;
    private SessionManager session;
    private Stage primaryStage;

    public static void setEshop(InterfaceEshop eshop) {
        eshopFuerStart = eshop;
    }

    @Override
    public void init() throws Exception {
        this.eshop = eshopFuerStart;
        this.session = new SessionManager();
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Eshop");

        this.primaryStage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);
        });

        showLoginScene();

        this.primaryStage.setWidth(1280);
        this.primaryStage.setHeight(800);
        this.primaryStage.show();
    }

    //Methode für den Wechsel der einzelnen Scenes
    public void changeScene(Pane newScene) {
        Scene aktuelleScene = primaryStage.getScene();
        if (aktuelleScene == null) {
            primaryStage.setScene(new Scene(newScene));
        } else {
            primaryStage.getScene().setRoot(newScene);
        }
    }

    //Show-Methoden der einzelnen Scenen, die wir erstellen.
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
