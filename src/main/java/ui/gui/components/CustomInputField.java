package ui.gui.components;

import javafx.scene.control.TextField;

public class CustomInputField extends TextField {

    public CustomInputField(String promptText) {
        super();
        this.setPromptText(promptText);
        this.getStyleClass().add("eingabe-feld");
    }
}
