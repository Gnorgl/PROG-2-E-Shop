package ui.gui.components;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class FormRow extends HBox {

    private final TextField inputField;

    public FormRow(String labelText, TextField inputField) {
        this.inputField = inputField;
        this.setSpacing(10);
        this.setAlignment(Pos.CENTER_LEFT);

        Label label = new Label(labelText);
        label.setPrefWidth(120); // Damit alle Labels untereinander exakt gleich lang sind
        label.setStyle("-fx-font-weight: bold;");

        // Das Feld streckt sich automatisch
        inputField.setMaxWidth(Double.MAX_VALUE);

        this.getChildren().addAll(label, inputField);
    }

    // Getter
    public String getText() {
        return inputField.getText();
    }
}
