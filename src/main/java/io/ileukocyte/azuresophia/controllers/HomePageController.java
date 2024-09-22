package io.ileukocyte.azuresophia.controllers;

import io.ileukocyte.azuresophia.Immutable;
import io.ileukocyte.azuresophia.MainApplication;
import io.ileukocyte.azuresophia.database.UserCredentials;
import io.ileukocyte.azuresophia.database.UserDatabase;
import io.ileukocyte.azuresophia.entities.Plan;
import io.ileukocyte.azuresophia.entities.PlanAttachment;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Objects;

@SuppressWarnings("unchecked")
public class HomePageController {
    @FXML
    private VBox planTabVBox;

    @FXML
    private TabPane homeTabPane;

    @FXML
    protected void onCreatePlanButtonClick() throws IOException {
        var fxmlLoader = new FXMLLoader(MainApplication.class.getResource("plan-creation-view.fxml"));
        var pane = (VBox) fxmlLoader.load();

        var priorityBox = (ComboBox<Plan.Priority>) pane.lookup("#priorityBox");

        priorityBox.setItems(FXCollections.observableList(Arrays.stream(Plan.Priority.values()).toList()));
        priorityBox.getSelectionModel().select(Plan.Priority.STANDARD);

        var attachmentBox = (ComboBox<PlanAttachment>) pane.lookup("#attachmentBox");

        attachmentBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(PlanAttachment object) {
                return object.getTitle();
            }

            @Override
            public PlanAttachment fromString(String string) {
                return UserDatabase.CURRENT_USER.getRecentlyViewed().stream().filter(p -> p.getTitle().equals(string)).findAny().orElseThrow();
            }
        });
        attachmentBox.setItems(FXCollections.observableArrayList(UserDatabase.CURRENT_USER.getRecentlyViewed()));
        attachmentBox.getItems().add(null);

        var datePicker = (DatePicker) pane.lookup("#planDatePicker");

        datePicker.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);

                var today = LocalDate.now();

                setDisable(empty || date.isBefore(today));
            }
        });

        var scene = new Scene(pane);
        var stage = new Stage();

        stage.getIcons().add(new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream("icon.png"))));
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(planTabVBox.getScene().getWindow());
        stage.setTitle("Create Plan - " + Immutable.NAME);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    @FXML
    protected void onLogOutButtonClick() {
        var alert = ControllerUtilities.createAlert(
                Alert.AlertType.CONFIRMATION,
                ControllerUtilities.AlertStyleClass.RED_DEFAULT,
                ControllerUtilities.AlertStyleClass.BLUE_CANCEL
        );

        alert.setContentText("Are you sure you want to log out?");
        alert.showAndWait().ifPresent(option -> {
            if (option == ButtonType.OK) {
                try {
                    UserDatabase.logOut();

                    var stage = (Stage) homeTabPane.getScene().getWindow();

                    var fxmlLoader = new FXMLLoader(MainApplication.class.getResource("welcome-view.fxml"));
                    var parent = (Parent) fxmlLoader.load();
                    var scene = new Scene(parent, stage.getWidth(), stage.getHeight());

                    stage.setTitle(Immutable.NAME);
                    stage.setScene(scene);
                    stage.setMinWidth(720);
                    stage.setMinHeight(480);
                    stage.show();
                } catch (IOException e) {
                    var exceptionAlert = ControllerUtilities.createAlert(
                            Alert.AlertType.ERROR,
                            ControllerUtilities.AlertStyleClass.BLUE_DEFAULT
                    );

                    exceptionAlert.setContentText(e.getMessage());
                    exceptionAlert.show();

                    e.printStackTrace();
                }
            }
        });
    }

    @FXML
    protected void onEditProfileButtonClick() throws IOException {
        var user = UserDatabase.CURRENT_USER;

        var fxmlLoader = new FXMLLoader(MainApplication.class.getResource("profile-editing-view.fxml"));
        var pane = (VBox) fxmlLoader.load();

        var nameField = (TextField) pane.lookup("#nameField");
        var usernameField = (TextField) pane.lookup("#usernameField");
        var bioTextArea = (TextArea) pane.lookup("#bioTextArea");

        nameField.setText(user.getName());
        usernameField.setText(user.getUsername());
        bioTextArea.setText(user.getBio());

        var stage = new Stage();

        var button = (Button) pane.lookup("#saveButton");

        nameField.textProperty().addListener((observable, oldValue, newValue) -> {
            button.setDisable(newValue.trim().equals(user.getName())
            && usernameField.getText().trim().equals(user.getUsername())
            && (((bioTextArea.getText() == null || bioTextArea.getText().isBlank()) && user.getBio() == null) || bioTextArea.getText().trim().equals(user.getBio())));
        });

        usernameField.textProperty().addListener((observable, oldValue, newValue) -> {
            button.setDisable(newValue.trim().equals(user.getUsername())
                    && nameField.getText().trim().equals(user.getName())
                    && (((bioTextArea.getText() == null || bioTextArea.getText().isBlank()) && user.getBio() == null) || bioTextArea.getText().trim().equals(user.getBio())));
        });

        bioTextArea.textProperty().addListener((observable, oldValue, newValue) -> {
            button.setDisable(usernameField.getText().trim().equals(user.getUsername())
                    && nameField.getText().trim().equals(user.getName())
                    && (((newValue == null || newValue.isBlank()) && user.getBio() == null) || newValue.trim().equals(user.getBio())));
        });

        button.setOnAction(event -> {
            try {
                var name = nameField.getText().trim();
                var username = usernameField.getText().trim();

                // name validation
                if (name.isBlank()) {
                    throw new IllegalArgumentException("The name must not be empty!");
                }

                // username validation
                if (username.length() < 4) {
                    throw new IllegalArgumentException("The username must contain at least 4 characters!");
                }

                if (username.length() > 32) {
                    throw new IllegalArgumentException("The length of the username must not exceed 32 characters!");
                }

                if (!username.matches("[A-Za-z0-9_.]+")) {
                    throw new IllegalArgumentException("The username must only contain English letters, digits, underscores, and full stops!");
                }

                if (UserDatabase.ACTIVE_USERS.containsKey(username) && !user.getUsername().equals(username)) {
                    throw new IllegalArgumentException("The provided username is already taken!");
                }

                var nameLabel = (Label) homeTabPane.lookup("#nameLabel");
                var usernameHyperlink = (Hyperlink) homeTabPane.lookup("#usernameHyperlink");
                var bioText = (Text) homeTabPane.lookup("#bioText");

                user.setName(name);
                nameLabel.setText(name);

                var oldUsername = user.getUsername();

                user.setUsername(username);
                usernameHyperlink.setText("@" + username);

                var bio = bioTextArea.getText() == null || bioTextArea.getText().isBlank() ? null : bioTextArea.getText().trim();

                user.setBio(bio);
                bioText.setText(bio == null ? "Tell the world about yourself!" : bio);

                var credentials = UserDatabase.ACTIVE_USERS.get(oldUsername);

                UserDatabase.ACTIVE_USERS.remove(oldUsername);
                UserDatabase.ACTIVE_USERS.put(username, new UserCredentials(credentials.getKey(), user));

                if (bio == null) {
                    bioText.setStyle("-fx-font-style: italic;");
                } else {
                    bioText.setStyle("-fx-font-style: normal;");
                }

                stage.close();
            } catch (IllegalArgumentException e) {
                var alert = ControllerUtilities.createAlert(
                        Alert.AlertType.ERROR,
                        ControllerUtilities.AlertStyleClass.BLUE_DEFAULT
                );

                alert.setContentText(e.getMessage());
                alert.show();
            }
        });

        var scene = new Scene(pane);

        stage.getIcons().add(new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream("icon.png"))));
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(homeTabPane.getScene().getWindow());
        stage.setTitle("Edit Profile - " + Immutable.NAME);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    @FXML
    protected void onClearHistoryButtonClick() {
        var user = UserDatabase.CURRENT_USER;

        if (user.getRecentlyViewed().isEmpty()) {
            var alert = ControllerUtilities.createAlert(
                    Alert.AlertType.ERROR,
                    ControllerUtilities.AlertStyleClass.BLUE_DEFAULT
            );

            alert.setContentText("The history is already empty!");
            alert.show();

            return;
        }

        var alert = ControllerUtilities.createAlert(
                Alert.AlertType.CONFIRMATION,
                ControllerUtilities.AlertStyleClass.RED_DEFAULT,
                ControllerUtilities.AlertStyleClass.BLUE_CANCEL
        );

        alert.setContentText("Are you sure you want to clear the \"Recently Viewed\" section?");
        alert.showAndWait().ifPresent(option -> {
            if (option == ButtonType.OK) {
                UserDatabase.CURRENT_USER.clearRecentlyViewed();

                var recentScrollPane = (VBox) ((ScrollPane) homeTabPane.lookup("#recentPlacesScrollPane")).getContent();

                recentScrollPane.getChildren().clear();

                var successAlert = ControllerUtilities.createAlert(
                        Alert.AlertType.INFORMATION,
                        ControllerUtilities.AlertStyleClass.BLUE_DEFAULT
                );

                successAlert.getDialogPane().setHeaderText("Success");
                successAlert.setTitle("Success");
                successAlert.setContentText("The history has been cleared!");
                successAlert.show();
            }
        });
    }

    @FXML
    protected void onChangePasswordButtonClick() throws IOException {
        var fxmlLoader = new FXMLLoader(MainApplication.class.getResource("password-change-view.fxml"));
        var pane = (VBox) fxmlLoader.load();
        var scene = new Scene(pane);
        var stage = new Stage();

        stage.getIcons().add(new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream("icon.png"))));
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(homeTabPane.getScene().getWindow());
        stage.setTitle("Change Password - " + Immutable.NAME);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    @FXML
    protected void onAboutButtonClick() {
        var description = """
            Azure Sofia’s Advisor is an application that is meant to acquaint a traveler with every fascinating sight spread over the area of 840 km² that belongs to Kyiv—the capital city of Ukraine and the cradle of Eastern European culture.
            The purpose is to give insight into the city’s mix of historical and modern architecture, its landmarks and monuments which have absorbed more than a thousand of years of historic events; its nature, entertainment options, cultural events, and a bunch of sights to explore for tourists of all ages for affordable prices.
            The app's another objective is to let its users organize and schedule a plan for the trip based on a place or a tip they are interested in."""
                .stripIndent();

        var alert = ControllerUtilities.createAlert(
                Alert.AlertType.INFORMATION,
                ControllerUtilities.AlertStyleClass.BLUE_DEFAULT
        );

        alert.getDialogPane().setHeaderText("About");
        alert.setTitle(String.format("About - %s (%s)", Immutable.NAME, Immutable.VERSION));
        alert.setContentText(description);
        alert.show();
    }
}