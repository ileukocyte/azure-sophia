package io.ileukocyte.azuresophia.controllers;

import io.ileukocyte.azuresophia.Immutable;
import io.ileukocyte.azuresophia.MainApplication;
import io.ileukocyte.azuresophia.database.UserDatabase;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class WelcomeController {
    @FXML
    private VBox vBox;

    @FXML
    private TextField name;

    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    @FXML
    private PasswordField repeatPassword;

    @FXML
    private Hyperlink logInHere;

    @FXML
    protected void onSignUpButtonClick() throws IOException {
        var nameValue = name.getText().trim();
        var usernameValue = username.getText().trim();
        var passwordValue = password.getText();
        var repeatedPassword = repeatPassword.getText();

        try {
            // name validation
            if (nameValue.isEmpty()) {
                throw new IllegalArgumentException("The name must not be empty!");
            }

            // username validation
            if (usernameValue.length() < 4) {
                throw new IllegalArgumentException("The username must contain at least 4 characters!");
            }

            if (usernameValue.length() > 32) {
                throw new IllegalArgumentException("The length of the username must not exceed 32 characters!");
            }

            if (!usernameValue.matches("[A-Za-z0-9_.]+")) {
                throw new IllegalArgumentException("The username must only contain English letters, digits, underscores, and full stops!");
            }

            // password validation
            if (passwordValue.length() < 8) {
                throw new IllegalArgumentException("The password length must be at least 8 characters!");
            }

            if (passwordValue.equals(usernameValue)) {
                throw new IllegalArgumentException("The password must not be the same as the username!");
            }

            if (!passwordValue.equals(repeatedPassword)) {
                throw new IllegalArgumentException("The passwords must match!");
            }

            UserDatabase.addUser(usernameValue, passwordValue, nameValue);

            ControllerUtilities.loadHomePage((Stage) vBox.getScene().getWindow());
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
    protected void onSignUpEnterKeyClicked(KeyEvent event) throws IOException {
        if (event.getCode().equals(KeyCode.ENTER)) {
            if (event.getSource().equals(name)) {
                username.requestFocus();
            } else if (event.getSource().equals(username)) {
                password.requestFocus();
            } else if (event.getSource().equals(password)) {
                repeatPassword.requestFocus();
            } else {
                onSignUpButtonClick();
            }
        }
    }

    @FXML
    protected void onLogInHereAction() throws IOException {
        logInHere.setVisited(false);

        var fxmlLoader = new FXMLLoader(MainApplication.class.getResource("login-view.fxml"));
        var pane = (VBox) fxmlLoader.load();
        var scene = new Scene(pane);
        var stage = new Stage();

        stage.getIcons().add(new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream("icon.png"))));
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(vBox.getScene().getWindow());
        stage.setTitle("Log In - " + Immutable.NAME);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    @FXML
    protected void onBackgroundClick() {
        vBox.requestFocus();
    }
}