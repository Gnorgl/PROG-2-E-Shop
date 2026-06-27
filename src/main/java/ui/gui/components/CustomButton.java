package ui.gui.components;

import javafx.scene.control.Button;

public class CustomButton extends Button {

    public enum ButtonType {
        PRIMARY, SECONDARY
    }

    public CustomButton(String text, ButtonType type) {
        super(text);
        this.setMaxWidth(Double.MAX_VALUE); // responsive design hier

        if (type == ButtonType.PRIMARY) {
            this.getStyleClass().add("shop-button");
        } else {
            this.getStyleClass().add("secondary-button");
        }
    }
}
