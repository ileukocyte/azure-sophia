package io.ileukocyte.azuresophia.controllers;

import io.ileukocyte.azuresophia.database.UserDatabase;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class PasswordController {
    @FXML
    private VBox vBox;

    @FXML
    private PasswordField currentPassword;

    @FXML
    private PasswordField newPassword;

    @FXML
    private PasswordField newPasswordRepeated;

    @FXML
    protected void onEnterKeyClicked(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ENTER)) {
            if (event.getSource().equals(currentPassword)) {
                newPassword.requestFocus();
            } else if (event.getSource().equals(newPassword)) {
                newPasswordRepeated.requestFocus();
            } else {
                onSaveButtonClick();
            }
        }
    }

    @FXML
    protected void onSaveButtonClick() {
        var currentPasswordValue = currentPassword.getText();
        var newPasswordValue = newPassword.getText();
        var repeatedPasswordValue = newPasswordRepeated.getText();

        try {
            var user = UserDatabase.CURRENT_USER;
            var credentials = UserDatabase.ACTIVE_USERS.get(user.getUsername());

            if (!credentials.getKey().equals(currentPasswordValue)) {
                throw new IllegalArgumentException("The current password you have provided is not correct!");
            }

            if (newPasswordValue.length() < 8) {
                throw new IllegalArgumentException("The password length must be at least 8 characters!");
            }

            if (newPasswordValue.equals(user.getUsername())) {
                throw new IllegalArgumentException("The password must not be the same as the username!");
            }

            if (newPasswordValue.equals(currentPasswordValue)) {
                throw new IllegalArgumentException("A new password must be different from the current one!");
            }

            if (!newPasswordValue.equals(repeatedPasswordValue)) {
                throw new IllegalArgumentException("The passwords must match!");
            }

            credentials.setKey(newPasswordValue);

            var alert = ControllerUtilities.createAlert(
                    Alert.AlertType.INFORMATION,
                    ControllerUtilities.AlertStyleClass.BLUE_DEFAULT
            );

            alert.getDialogPane().setHeaderText("Success");
            alert.setTitle("Success");
            alert.setContentText("The password has been changed!");
            alert.showAndWait();

            var stage = (Stage) vBox.getScene().getWindow();

            stage.close();
        } catch (IllegalArgumentException e) {
            var alert = ControllerUtilities.createAlert(
                    Alert.AlertType.ERROR,
                    ControllerUtilities.AlertStyleClass.BLUE_DEFAULT
            );

            alert.setContentText(e.getMessage());
            alert.show();
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
    protected void onBackgroundClick() {
        vBox.requestFocus();
    }
}
