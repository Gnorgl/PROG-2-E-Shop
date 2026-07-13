package ui.gui.components;

import javafx.scene.control.PasswordField;

public class CustomPasswordField extends PasswordField {
    public CustomPasswordField(String promptText) {
        super();
        this.setPromptText(promptText);
        this.getStyleClass().add("eingabe-feld");
    }
}