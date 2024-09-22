package io.ileukocyte.azuresophia.controllers;

import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class PlanController {
    @FXML
    private VBox planVBox;

    @FXML
    protected void onBackgroundClick() {
        planVBox.requestFocus();
    }

    @FXML
    protected void onEscapeKeyClicked(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ESCAPE)) {
            var stage = (Stage) planVBox.getScene().getWindow();

            stage.close();
        }
    }
}