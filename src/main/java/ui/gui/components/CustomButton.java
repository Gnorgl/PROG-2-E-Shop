package ui.gui.components;

import javafx.scene.control.Button;

public class CustomButton extends Button {

    public enum ButtonType {
        PRIMARY, SECONDARY
    }

    public CustomButton(String text, ButtonType type) {
        super(text);

        if (type == ButtonType.PRIMARY) {
            this.getStyleClass().add("shop-button");
        } else {
            this.getStyleClass().add("secondary-button");
        }
    }
}
