package ui.gui;

import com.sun.prism.ReadbackGraphics;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import logic.Eshop;
import logic.SessionManager;
import ui.gui.scenes.LoginScene;
import ui.gui.scenes.MainMenuScene;
import ui.gui.scenes.RegistrationScene;


public class EshopGUI extends Application {

    private Eshop eshop;
    private SessionManager session;
    private Stage primaryStage;

    @Override
    public void init() throws Exception {
        this.eshop = new Eshop();
        this.session = new SessionManager();
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Eshop");

        //Hier die erste Szene also vielleicht login mit registrierungsmöglichkeit
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

    public void showMainMenuScene() {
        MainMenuScene mainScene = new MainMenuScene(eshop, session, this);
        changeScene(mainScene);
    }

    public void showRegistrationScene() {
        RegistrationScene registrationScene = new RegistrationScene(eshop, session, this);
        changeScene(registrationScene);
    }

    public static void main(String[] args) {
        launch(args);
    }

}
