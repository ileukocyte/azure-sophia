package io.ileukocyte.azuresophia.controllers;

import io.ileukocyte.azuresophia.database.AttachmentDatabase;
import io.ileukocyte.azuresophia.database.UserDatabase;
import io.ileukocyte.azuresophia.entities.*;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;

public class PlanCreationController {
    @FXML
    private VBox planCreationVBox;

    @FXML
    private DatePicker planDatePicker;

    @FXML
    private ComboBox<Plan.Priority> priorityBox;

    @FXML
    private ComboBox<PlanAttachment> attachmentBox;

    @FXML
    private TextArea planContentTextArea;

    @FXML
    protected void onBackgroundClick() {
        planCreationVBox.requestFocus();
    }

    @FXML
    protected void onEscapeKeyClicked(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ESCAPE)) {
            var stage = (Stage) planCreationVBox.getScene().getWindow();

            stage.close();
        }
    }

    @FXML
    protected void onCreationButtonClick() {
        var date = planDatePicker.getValue();
        var offsetDateTime = date != null ? date.atStartOfDay(ZoneId.systemDefault()).toOffsetDateTime() : null;

        var priority = priorityBox.getValue();
        var attachment = attachmentBox.getValue();
        var content = planContentTextArea.getText().trim();

        content = content.isEmpty() ? null : content;

        try {
            var plan = new PlanBuilder()
                    .setPriority(priority)
                    .setAttachment(attachment)
                    .setDescription(content)
                    .setDatePlanned(offsetDateTime)
                    .build();

            UserDatabase.CURRENT_USER.addPlan(plan);

            var stage = (Stage) planCreationVBox.getScene().getWindow();
            var ownerStage = (Stage) stage.getOwner();

            var plansPane = (ScrollPane) ownerStage.getScene().lookup("#plansScrollPane");
            var plansVBox = (VBox) plansPane.getContent();

            var contentLabel = new Label(plan.getDescription() != null ?
                    plan.getDescription().split("\n")[0] :
                    plan.getAttachment().getTitle()
            );

            contentLabel.setFont(Font.font("System", FontWeight.BOLD, 20));
            contentLabel.setTextFill(Color.WHITE);

            var priorityLabel = new Label(priority.toString() + " Priority • ");

            priorityLabel.setTextFill(Color.WHITE);

            var dateFormatter = DateTimeFormatter.RFC_1123_DATE_TIME;

            var dateLabel = new Label(offsetDateTime == null ? "No Date" : dateFormatter.format(offsetDateTime));

            dateLabel.setTextFill(Color.WHITE);

            var priorityAndDateHBox = new HBox(priorityLabel, dateLabel);

            priorityAndDateHBox.setId("priorityAndDateHBox");

            var planCardVBox = new VBox(contentLabel, priorityAndDateHBox);

            planCardVBox.setId("planCardVBox");

            var deleteButton = new Button("X");

            deleteButton.getStyleClass().add("red-button");

            var planCardHBox = new HBox(planCardVBox, deleteButton);

            planCardHBox.setSpacing(10);

            if (plan.getAttachment() != null) {
                var image = AttachmentDatabase.get(plan.getAttachment());
                var imageView = new ImageView(image);

                imageView.setFitHeight(75);
                imageView.setPickOnBounds(true);
                imageView.setPreserveRatio(true);

                planCardHBox.getChildren().add(0, imageView);
            }

            HBox.setHgrow(planCardVBox, Priority.ALWAYS);
            HBox.setHgrow(deleteButton, Priority.NEVER);

            deleteButton.setOnAction(event -> {
                var alert = ControllerUtilities.createAlert(
                        Alert.AlertType.CONFIRMATION,
                        ControllerUtilities.AlertStyleClass.RED_DEFAULT,
                        ControllerUtilities.AlertStyleClass.BLUE_CANCEL
                );

                alert.setContentText("Are you sure you want to delete the plan?");
                alert.showAndWait().ifPresent(option -> {
                    if (option == ButtonType.OK) {
                        UserDatabase.CURRENT_USER.removePlanById(plan.getId());

                        plansVBox.getChildren().remove(planCardHBox);
                    } else {
                        event.consume();
                    }
                });
            });

            planCardHBox.getStyleClass().add("plan-card");
            planCardHBox.setId(String.format("p%d", plan.getId()));
            planCardHBox.setPrefHeight(75);
            planCardHBox.setCursor(Cursor.HAND);
            planCardHBox.setPadding(new Insets(10));
            planCardHBox.setOnMouseClicked(event -> {
                try {
                    ControllerUtilities.loadPlanWindow(plan, ownerStage);
                } catch (IOException ex) {
                    var alert = ControllerUtilities.createAlert(
                            Alert.AlertType.ERROR,
                            ControllerUtilities.AlertStyleClass.BLUE_DEFAULT
                    );

                    alert.setContentText(ex.getMessage());
                    alert.show();

                    ex.printStackTrace();
                }
            });

            plansVBox.getChildren().add(planCardHBox);

            FXCollections.sort(plansVBox.getChildren(), Comparator.comparing(c -> {
                var card = (VBox) c.lookup("#planCardVBox");
                var priorityAndDate = (HBox) card.lookup("#priorityAndDateHBox");
                var label = (Label) priorityAndDate.getChildren().get(1);

                return label.getText().equals("No Date") ?
                        OffsetDateTime.now().truncatedTo(ChronoUnit.DAYS).minusDays(1) :
                        OffsetDateTime.from(dateFormatter.parse(label.getText()));
            }));

            stage.close();

            var tabPane = (TabPane) ownerStage.getScene().lookup("#homeTabPane");

            tabPane.getSelectionModel().select(1);
        } catch (InsufficientPlanDetailException e) {
            var alert = ControllerUtilities.createAlert(
                    Alert.AlertType.ERROR,
                    ControllerUtilities.AlertStyleClass.BLUE_DEFAULT
            );

            alert.setContentText(e.getMessage());
            alert.show();
        }
    }
}
