package io.ileukocyte.azuresophia.controllers;

import io.ileukocyte.azuresophia.database.UserDatabase;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import javax.security.auth.login.LoginException;

public class LogInController {
    @FXML
    private VBox vBox;

    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    @FXML
    protected void onLogInEnterKeyClicked(KeyEvent event) throws IOException {
        if (event.getCode().equals(KeyCode.ENTER)) {
            if (event.getSource().equals(username)) {
                password.requestFocus();
            } else {
                onLogInButtonClick();
            }
        }
    }

    @FXML
    protected void onEscapeKeyClicked(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ESCAPE)) {
            var stage = (Stage) vBox.getScene().getWindow();

            stage.close();
        }
    }

    @FXML
    protected void onLogInButtonClick() throws IOException {
        var usernameValue = username.getText().trim();
        var passwordValue = password.getText();

        try {
            if (usernameValue.isEmpty()) {
                throw new IllegalArgumentException("The username must not be empty!");
            }

            if (passwordValue.isEmpty()) {
                throw new IllegalArgumentException("The password must not be empty!");
            }

            UserDatabase.logIn(usernameValue, passwordValue);

            var stage = (Stage) vBox.getScene().getWindow();
            var ownerStage = (Stage) stage.getOwner();

            stage.close();

            ControllerUtilities.loadHomePage(ownerStage);
        } catch (LoginException | IllegalArgumentException e) {
            var alert = ControllerUtilities.createAlert(Alert.AlertType.ERROR, ControllerUtilities.AlertStyleClass.BLUE_DEFAULT);

            alert.setContentText(e.getMessage());
            alert.show();
        }
    }

    @FXML
    protected void onBackgroundClick() {
        vBox.requestFocus();
    }
}
