package ui.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import logic.Eshop;
import logic.SessionManager;



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

        this.primaryStage.setWidth(800);
        this.primaryStage.setHeight(600);
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

    //Login Scene hier
    //Main Menu Scene hier
    //Quasi Show-Methoden der einzelnen Scenen, die wir erstellen.


    public static void main(String[] args) {
        launch(args);
    }

}
