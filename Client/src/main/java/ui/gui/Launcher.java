package ui.gui;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import net.EshopClient;
import java.io.IOException;

public class Launcher {

    // Konstanten fuer die Server-Adresse (koennten spaeter auch aus einer config-Datei kommen)
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8080;

    public static void main(String[] args) {
        try {
            // 1. Versuche, die Netzwerkverbindung zum Server aufzubauen
            EshopClient client = new EshopClient(SERVER_HOST, SERVER_PORT);

            // 2. Die fertige Fassade an die GUI uebergeben
            EshopGUI.setEshop(client);

            // 3. Beim Beenden der GUI automatisch die Socket-Verbindung sauber schliessen
            Platform.runLater(() -> {
                // Wartet, bis die JavaFX-Umgebung bereit ist, und haengt sich an den Exit-Prozess
                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    System.out.println("Schliesse Verbindung zum Server...");
                    client.verbindungSchliessen();
                }));
            });

            // 4. GUI starten
            EshopGUI.main(args);

        } catch (IOException e) {
            // Falls der Server nicht erreichbar ist, wird das hier abgefangen
            System.err.println("Verbindung zum Server fehlgeschlagen: " + e.getMessage());

            // Initialisiert das JavaFX-Toolkit im Hintergrund, um einen Dialog ohne voll gestartete App zu zeigen
            Platform.startup(() -> {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Verbindungsfehler");
                alert.setHeaderText("Server nicht erreichbar");
                alert.setContentText("Es konnte keine Verbindung zum Eshop-Server hergestellt werden.\n" +
                        "Bitte stellen Sie sicher, dass der Server gestartet ist.\n\n" +
                        "Details: " + e.getLocalizedMessage());

                alert.showAndWait();
                Platform.exit();
            });
        }
    }
}