package io.ileukocyte.azuresophia.controllers;

import io.ileukocyte.azuresophia.Immutable;
import io.ileukocyte.azuresophia.MainApplication;
import io.ileukocyte.azuresophia.database.AttachmentDatabase;
import io.ileukocyte.azuresophia.database.PlaceDatabase;
import io.ileukocyte.azuresophia.database.TipDatabase;
import io.ileukocyte.azuresophia.database.UserDatabase;
import io.ileukocyte.azuresophia.entities.*;

import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;
import javafx.util.StringConverter;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

public class ControllerUtilities {
    public static void loadHomePage(Stage stage) throws IOException {
        stage.setTitle(String.format("Loading… - %s", Immutable.NAME));

        var fxmlLoader = new FXMLLoader(MainApplication.class.getResource("home-view.fxml"));
        var pane = (TabPane) fxmlLoader.load();

        for (var tab : pane.getTabs()) {
            var iconFileName = String.format("%sIcon.png", tab.getId().substring(0, tab.getId().length() - 3));
            var icon = new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream(iconFileName)));
            var iconView = new ImageView(icon);

            iconView.setPreserveRatio(true);
            iconView.setFitHeight(20);

            tab.setOnSelectionChanged(event -> stage.setTitle(String.format("%s - %s", tab.getText(), Immutable.NAME)));
            tab.setGraphic(iconView);
        }

        var scene = new Scene(pane, stage.getWidth(), stage.getHeight());

        scene.getStylesheets().add(Objects.requireNonNull(MainApplication.class.getResource("stylesheet.css")).toExternalForm());

        var scrollPane = (ScrollPane) scene.lookup("#placesScrollPane");
        var recentlyViewedPane = (ScrollPane) scene.lookup("#recentPlacesScrollPane");
        var tipsScrollPane = (ScrollPane) scene.lookup("#tipsScrollPane");

        scrollPane.addEventHandler(MouseEvent.DRAG_DETECTED, e -> scrollPane.setCursor(Cursor.DEFAULT));
        recentlyViewedPane.addEventHandler(MouseEvent.DRAG_DETECTED, e -> recentlyViewedPane.setCursor(Cursor.DEFAULT));
        tipsScrollPane.addEventHandler(MouseEvent.DRAG_DETECTED, e -> tipsScrollPane.setCursor(Cursor.DEFAULT));

        for (var place : PlaceDatabase.parseAllPlaces()) {
            var scrollPaneVBox = (VBox) scrollPane.getContent();

            if (scrollPaneVBox.getChildren().isEmpty()) {
                var hBox = new HBox();

                hBox.setId("placesHBox");
                hBox.setSpacing(30);
                hBox.setStyle("-fx-background-color: transparent;");

                var placesTitle = new Label("Places");

                placesTitle.setFont(Font.font("System", FontWeight.BOLD, 25));
                placesTitle.setId("placesTitle");
                placesTitle.setTextFill(Color.WHITE);

                scrollPaneVBox.getChildren().addAll(placesTitle, hBox);
            }

            var hBox = (HBox) scrollPaneVBox.lookup("#placesHBox");

            var image = AttachmentDatabase.get(place);
            var imageView = new ImageView(image);

            imageView.setFitHeight(150);
            imageView.setPickOnBounds(true);
            imageView.setPreserveRatio(true);
            imageView.setCache(true);

            var label = new Label(place.getTitle());

            label.setFont(Font.font("System", FontWeight.BOLD, 15));
            label.setTextFill(Color.WHITE);

            var placeBox = new VBox(imageView, label);

            placeBox.setCursor(Cursor.HAND);
            placeBox.setId(place.getId());
            placeBox.setStyle("-fx-background-color: transparent;");

            placeBox.setOnMouseClicked(event -> {
                try {
                    UserDatabase.CURRENT_USER.addRecentlyViewed(place);

                    var recentVBox = (VBox) recentlyViewedPane.getContent();

                    recentVBox.setPadding(new Insets(10));

                    if (recentVBox.getChildren().isEmpty()) {
                        var recentHBox = new HBox();

                        recentHBox.setId("recentHBox");
                        recentHBox.setSpacing(30);
                        recentHBox.setStyle("-fx-background-color: transparent;");

                        var recentTitle = new Label("Recently Viewed");

                        recentTitle.setFont(Font.font("System", FontWeight.BOLD, 25));
                        recentTitle.setId("recentlyViewedTitle");
                        recentTitle.setTextFill(Color.WHITE);

                        recentVBox.getChildren().addAll(recentTitle, recentHBox);
                    }

                    var recentHBox = (HBox) recentVBox.lookup("#recentHBox");

                    recentHBox.getChildren().removeIf(n -> n.getId().equals(place.getId()));

                    var recentImageView = new ImageView(image);

                    recentImageView.setFitHeight(150);
                    recentImageView.setPickOnBounds(true);
                    recentImageView.setPreserveRatio(true);

                    var recentLabel = new Label(place.getTitle());

                    recentLabel.setFont(Font.font("System", FontWeight.BOLD, 15));
                    recentLabel.setTextFill(Color.WHITE);

                    var recentPlaceVBox = new VBox(recentImageView, recentLabel);

                    recentPlaceVBox.setCursor(Cursor.HAND);
                    recentPlaceVBox.setId(place.getId());

                    recentPlaceVBox.setOnMouseClicked(e -> {
                        UserDatabase.CURRENT_USER.addRecentlyViewed(place);

                        recentHBox.getChildren().remove(recentPlaceVBox);
                        recentHBox.getChildren().add(0, recentPlaceVBox);

                        try {
                            loadPlaceWindow(place, pane.getScene().getWindow());
                        } catch (IOException ex) {
                            var alert = createAlert(
                                    Alert.AlertType.ERROR,
                                    AlertStyleClass.BLUE_DEFAULT
                            );

                            alert.setContentText(ex.getMessage());
                            alert.show();

                            ex.printStackTrace();
                        }
                    });

                    recentHBox.getChildren().add(0, recentPlaceVBox);

                    loadPlaceWindow(place, pane.getScene().getWindow());
                } catch (IOException e) {
                    var alert = createAlert(
                            Alert.AlertType.ERROR,
                            AlertStyleClass.BLUE_DEFAULT
                    );

                    alert.setContentText(e.getMessage());
                    alert.show();

                    e.printStackTrace();
                }
            });

            hBox.getChildren().add(placeBox);
        }

        for (var tip : TipDatabase.parseAllTips()) {
            var scrollPaneVBox = (VBox) tipsScrollPane.getContent();

            if (scrollPaneVBox.getChildren().isEmpty()) {
                var hBox = new HBox();

                hBox.setId("tipsHBox");
                hBox.setSpacing(30);
                hBox.setStyle("-fx-background-color: transparent;");

                var tipsTitle = new Label("Tips");

                tipsTitle.setFont(Font.font("System", FontWeight.BOLD, 25));
                tipsTitle.setId("tipsTitle");
                tipsTitle.setTextFill(Color.WHITE);

                scrollPaneVBox.getChildren().addAll(tipsTitle, hBox);
            }

            var hBox = (HBox) scrollPaneVBox.lookup("#tipsHBox");

            var image = AttachmentDatabase.get(tip);
            var imageView = new ImageView(image);

            imageView.setFitHeight(150);
            imageView.setPickOnBounds(true);
            imageView.setPreserveRatio(true);
            imageView.setCache(true);

            var label = new Label(tip.getTitle());

            label.setFont(Font.font("System", FontWeight.BOLD, 15));
            label.setTextFill(Color.WHITE);

            var tipBox = new VBox(imageView, label);

            tipBox.setCursor(Cursor.HAND);
            tipBox.setId(tip.getId());
            tipBox.setStyle("-fx-background-color: transparent;");

            tipBox.setOnMouseClicked(event -> {
                try {
                    UserDatabase.CURRENT_USER.addRecentlyViewed(tip);

                    var recentVBox = (VBox) recentlyViewedPane.getContent();

                    recentVBox.setPadding(new Insets(10));

                    if (recentVBox.getChildren().isEmpty()) {
                        var recentHBox = new HBox();

                        recentHBox.setId("recentHBox");
                        recentHBox.setSpacing(30);
                        recentHBox.setStyle("-fx-background-color: transparent;");

                        var recentTitle = new Label("Recently Viewed");

                        recentTitle.setFont(Font.font("System", FontWeight.BOLD, 25));
                        recentTitle.setId("recentlyViewedTitle");
                        recentTitle.setTextFill(Color.WHITE);

                        recentVBox.getChildren().addAll(recentTitle, recentHBox);
                    }

                    var recentHBox = (HBox) recentVBox.lookup("#recentHBox");

                    recentHBox.getChildren().removeIf(n -> n.getId().equals(tip.getId()));

                    var recentImageView = new ImageView(image);

                    recentImageView.setFitHeight(150);
                    recentImageView.setPickOnBounds(true);
                    recentImageView.setPreserveRatio(true);

                    var recentLabel = new Label(tip.getTitle());

                    recentLabel.setFont(Font.font("System", FontWeight.BOLD, 15));
                    recentLabel.setTextFill(Color.WHITE);

                    var recentTipVBox = new VBox(recentImageView, recentLabel);

                    recentTipVBox.setCursor(Cursor.HAND);
                    recentTipVBox.setId(tip.getId());

                    recentTipVBox.setOnMouseClicked(e -> {
                        UserDatabase.CURRENT_USER.addRecentlyViewed(tip);

                        recentHBox.getChildren().remove(recentTipVBox);
                        recentHBox.getChildren().add(0, recentTipVBox);

                        try {
                            loadTipWindow(tip, pane.getScene().getWindow());
                        } catch (IOException ex) {
                            var alert = createAlert(
                                    Alert.AlertType.ERROR,
                                    AlertStyleClass.BLUE_DEFAULT
                            );

                            alert.setContentText(ex.getMessage());
                            alert.show();

                            ex.printStackTrace();
                        }
                    });

                    recentHBox.getChildren().add(0, recentTipVBox);

                    loadTipWindow(tip, pane.getScene().getWindow());
                } catch (IOException e) {
                    var alert = createAlert(
                            Alert.AlertType.ERROR,
                            AlertStyleClass.BLUE_DEFAULT
                    );

                    alert.setContentText(e.getMessage());
                    alert.show();

                    e.printStackTrace();
                }
            });

            hBox.getChildren().add(tipBox);
        }

        var user = UserDatabase.CURRENT_USER;

        for (var recentlyViewed : user.getRecentlyViewed()) {
            var recentVBox = (VBox) recentlyViewedPane.getContent();

            recentVBox.setPadding(new Insets(10));

            if (recentVBox.getChildren().isEmpty()) {
                var recentHBox = new HBox();

                recentHBox.setId("recentHBox");
                recentHBox.setSpacing(30);
                recentHBox.setStyle("-fx-background-color: transparent;");

                var recentTitle = new Label("Recently Viewed");

                recentTitle.setFont(Font.font("System", FontWeight.BOLD, 25));
                recentTitle.setId("recentlyViewedTitle");
                recentTitle.setTextFill(Color.WHITE);

                recentVBox.getChildren().addAll(recentTitle, recentHBox);
            }

            var recentHBox = (HBox) recentVBox.lookup("#recentHBox");

            var image = AttachmentDatabase.get(recentlyViewed);
            var recentImageView = new ImageView(image);

            recentImageView.setFitHeight(150);
            recentImageView.setPickOnBounds(true);
            recentImageView.setPreserveRatio(true);

            var recentLabel = new Label(recentlyViewed.getTitle());

            recentLabel.setFont(Font.font("System", FontWeight.BOLD, 15));
            recentLabel.setTextFill(Color.WHITE);

            var recentPlaceVBox = new VBox(recentImageView, recentLabel);

            recentPlaceVBox.setCursor(Cursor.HAND);
            recentPlaceVBox.setId(recentlyViewed.getId());

            recentPlaceVBox.setOnMouseClicked(e -> {
                UserDatabase.CURRENT_USER.addRecentlyViewed(recentlyViewed);

                recentHBox.getChildren().remove(recentPlaceVBox);
                recentHBox.getChildren().add(0, recentPlaceVBox);

                try {
                    if (recentlyViewed instanceof Place) {
                        loadPlaceWindow((Place) recentlyViewed, pane.getScene().getWindow());
                    } else {
                        loadTipWindow((Tip) recentlyViewed, pane.getScene().getWindow());
                    }
                } catch (IOException ex) {
                    var alert = createAlert(
                            Alert.AlertType.ERROR,
                            AlertStyleClass.BLUE_DEFAULT
                    );

                    alert.setContentText(ex.getMessage());
                    alert.show();

                    ex.printStackTrace();
                }
            });

            recentHBox.getChildren().add(0, recentPlaceVBox);
        }

        var plansPane = (ScrollPane) scene.lookup("#plansScrollPane");
        var plansVBox = (VBox) plansPane.getContent();
        var dateFormatter = DateTimeFormatter.RFC_1123_DATE_TIME;

        for (var plan : user.getPlans()) {
            var contentLabel = new Label(plan.getDescription() != null ?
                    plan.getDescription().split("\n")[0] :
                    plan.getAttachment().getTitle()
            );

            contentLabel.setFont(Font.font("System", FontWeight.BOLD, 20));
            contentLabel.setTextFill(Color.WHITE);

            var priorityLabel = new Label(plan.getPriority().toString() + " Priority • ");

            priorityLabel.setTextFill(Color.WHITE);

            var dateLabel = new Label(plan.getDatePlanned() == null ?
                    "No Date" :
                    dateFormatter.format(plan.getDatePlanned())
            );

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
            planCardHBox.setOnMouseClicked(e -> {
                try {
                    loadPlanWindow(plan, pane.getScene().getWindow());
                } catch (IOException ex) {
                    var alert = createAlert(
                            Alert.AlertType.ERROR,
                            AlertStyleClass.BLUE_DEFAULT
                    );

                    alert.setContentText(ex.getMessage());
                    alert.show();

                    ex.printStackTrace();
                }
            });

            plansVBox.getChildren().add(planCardHBox);
        }

        FXCollections.sort(plansVBox.getChildren(), Comparator.comparing(c -> {
            var card = (VBox) c.lookup("#planCardVBox");
            var priorityAndDate = (HBox) card.lookup("#priorityAndDateHBox");
            var label = (Label) priorityAndDate.getChildren().get(1);

            return label.getText().equals("No Date") ?
                    OffsetDateTime.now().truncatedTo(ChronoUnit.DAYS).minusDays(1) :
                    OffsetDateTime.from(dateFormatter.parse(label.getText()));
        }));

        var nameLabel = (Label) scene.lookup("#nameLabel");
        var usernameHyperlink = (Hyperlink) scene.lookup("#usernameHyperlink");

        nameLabel.setText(user.getName());
        usernameHyperlink.setText("@" + user.getUsername());

        if (user.getBio() != null) {
            var bioText = (Text) scene.lookup("#bioText");

            bioText.setText(user.getBio());
            bioText.setStyle("-fx-font-style: normal;");
        }

        var copyTooltip = new Tooltip("Copy the username");

        copyTooltip.setShowDelay(Duration.millis(100));

        usernameHyperlink.setTooltip(copyTooltip);
        usernameHyperlink.setBorder(Border.EMPTY);
        usernameHyperlink.setPadding(new Insets(4, 0, 4, 0));
        usernameHyperlink.setOnAction(event -> {
            usernameHyperlink.setVisited(false);

            var clipboard = Clipboard.getSystemClipboard();
            var content = new ClipboardContent();

            content.putString(user.getUsername());

            clipboard.setContent(content);
        });

        var pfp = (Circle) scene.lookup("#pfpCircle");
        var image = new Image(
                Objects.requireNonNull(MainApplication.class.getResourceAsStream("user.png")),
                2 * pfp.getRadius(),
                2 * pfp.getRadius(),
                true,
                true
        );

        pfp.setFill(new ImagePattern(image));

        var visitedPane = (ScrollPane) scene.lookup("#visitedPlacesScrollPane");
        var visitedPlacesSorted = UserDatabase.CURRENT_USER.getPlacesVisited()
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toCollection(LinkedHashSet::new));

        for (var visited : visitedPlacesSorted) {
            var visitedVBox = (VBox) visitedPane.getContent();

            visitedVBox.setPadding(new Insets(10));

            if (visitedVBox.getChildren().isEmpty()) {
                var visitedHBox = new HBox();

                visitedHBox.setId("visitedHBox");
                visitedHBox.setSpacing(30);
                visitedHBox.setStyle("-fx-background-color: transparent;");

                var visitedTitle = new Label("Recently Visited");

                visitedTitle.setFont(Font.font("System", FontWeight.BOLD, 25));
                visitedTitle.setId("recentlyVisitedTitle");
                visitedTitle.setTextFill(Color.WHITE);

                visitedVBox.getChildren().addAll(visitedTitle, visitedHBox);
            }

            var visitedHBox = (HBox) visitedVBox.lookup("#visitedHBox");

            var visitedImage = AttachmentDatabase.get(visited.getKey());
            var visitedImageView = new ImageView(visitedImage);

            visitedImageView.setFitHeight(150);
            visitedImageView.setPickOnBounds(true);
            visitedImageView.setPreserveRatio(true);

            var visitedLabel = new Label(visited.getKey().getTitle());

            visitedLabel.setFont(Font.font("System", FontWeight.BOLD, 15));
            visitedLabel.setTextFill(Color.WHITE);

            var visitedPlaceVBox = new VBox(visitedImageView, visitedLabel);

            visitedPlaceVBox.setCursor(Cursor.HAND);
            visitedPlaceVBox.setId(visited.getKey().getId());

            visitedPlaceVBox.setOnMouseClicked(e -> {
                UserDatabase.CURRENT_USER.addRecentlyViewed(visited.getKey());

                var recentlyViewedVBox = (VBox) recentlyViewedPane.getContent();

                if (recentlyViewedVBox.getChildren().isEmpty()) {
                    var recentlyViewedHBox = new HBox();

                    recentlyViewedHBox.setId("recentHBox");
                    recentlyViewedHBox.setSpacing(30);
                    recentlyViewedHBox.setStyle("-fx-background-color: transparent;");

                    var recentlyViewedTitle = new Label("Recently Viewed");

                    recentlyViewedTitle.setFont(Font.font("System", FontWeight.BOLD, 25));
                    recentlyViewedTitle.setId("recentlyViewedTitle");
                    recentlyViewedTitle.setTextFill(Color.WHITE);

                    recentlyViewedVBox.getChildren().addAll(recentlyViewedTitle, recentlyViewedHBox);
                }

                var recentlyViewedHBox = (HBox) recentlyViewedVBox.lookup("#recentHBox");

                recentlyViewedHBox.getChildren().removeIf(n -> n.getId().equals(visited.getKey().getId()));

                var recentImageView = new ImageView(visitedImage);

                recentImageView.setFitHeight(150);
                recentImageView.setPickOnBounds(true);
                recentImageView.setPreserveRatio(true);

                var recentLabel = new Label(visited.getKey().getTitle());

                recentLabel.setFont(Font.font("System", FontWeight.BOLD, 15));
                recentLabel.setTextFill(Color.WHITE);

                var recentPlaceVBox = new VBox(recentImageView, recentLabel);

                recentPlaceVBox.setCursor(Cursor.HAND);
                recentPlaceVBox.setId(visited.getKey().getId());

                recentPlaceVBox.setOnMouseClicked(ev -> {
                    UserDatabase.CURRENT_USER.addRecentlyViewed(visited.getKey());

                    recentlyViewedHBox.getChildren().remove(recentPlaceVBox);
                    recentlyViewedHBox.getChildren().add(0, recentPlaceVBox);

                    try {
                        loadPlaceWindow(visited.getKey(), pane.getScene().getWindow());
                    } catch (IOException ex) {
                        var alert = createAlert(
                                Alert.AlertType.ERROR,
                                AlertStyleClass.BLUE_DEFAULT
                        );

                        alert.setContentText(ex.getMessage());
                        alert.show();

                        ex.printStackTrace();
                    }
                });

                recentlyViewedHBox.getChildren().add(0, recentPlaceVBox);

                try {
                    loadPlaceWindow(visited.getKey(), pane.getScene().getWindow());
                } catch (IOException ex) {
                    var alert = createAlert(
                            Alert.AlertType.ERROR,
                            AlertStyleClass.BLUE_DEFAULT
                    );

                    alert.setContentText(ex.getMessage());
                    alert.show();

                    ex.printStackTrace();
                }
            });

            visitedHBox.getChildren().add(0, visitedPlaceVBox);
        }

        stage.setMinWidth(700);
        stage.setMinHeight(770);
        stage.setTitle(String.format("%s - %s", pane.getTabs().get(0).getText(), Immutable.NAME));
        stage.setScene(scene);
    }

    /**
     * Opens a window that contains information about a specific place
     *
     * @param place the place
     * @param owner the owner window to attach the place window to
     */
    @SuppressWarnings("unchecked")
    public static void loadPlaceWindow(Place place, Window owner) throws IOException {
        var fxmlLoader = new FXMLLoader(MainApplication.class.getResource("place-view.fxml"));
        var placePane = (VBox) fxmlLoader.load();

        var image = AttachmentDatabase.get(place);
        var banner = (Rectangle) placePane.lookup("#imageBanner");

        if (image.getProgress() != 1.0) {
            image.progressProperty().addListener((observable, oldValue, progress) -> {
                if ((double) progress == 1.0 && !image.isError()) {
                    var ratio = image.getWidth() / image.getHeight();
                    var height = banner.getWidth() / ratio;
                    var pattern = new ImagePattern(image, 0, -height * 0.25, banner.getWidth(), height, false);

                    banner.setFill(pattern);
                }
            });
        } else {
            var ratio = image.getWidth() / image.getHeight();
            var height = banner.getWidth() / ratio;
            var pattern = new ImagePattern(image, 0, -height * 0.25, banner.getWidth(), height, false);

            banner.setFill(pattern);
        }

        var placeNameText = (Text) placePane.lookup("#placeName");
        var placeDescriptionScrollPane = (ScrollPane) placePane.lookup("#descriptionScrollPane");
        var placeDescriptionText = (Text) placeDescriptionScrollPane.getContent();
        var placeAddressLink = (Hyperlink) placePane.lookup("#addressHyperlink");

        placeNameText.setText(place.getTitle());
        placeDescriptionText.setText(place.getDescription());

        var addressTooltip = new Tooltip("Open Google Maps");

        addressTooltip.setShowDelay(Duration.millis(100));

        placeAddressLink.setTooltip(addressTooltip);
        placeAddressLink.setText(place.getLocation().humanizedAddress());
        placeAddressLink.setBorder(Border.EMPTY);
        placeAddressLink.setPadding(new Insets(4, 0, 4, 0));
        placeAddressLink.setOnAction(event -> {
            placeAddressLink.setVisited(false);

            var locationUrl = "https://maps.google.com/maps?q=" +
                    place.getLocation().latitude() +
                    "," +
                    place.getLocation().longitude();

            try {
                var hostServices = MainApplication.getInstance().getHostServices();

                hostServices.showDocument(new URL(locationUrl).toExternalForm());
            } catch (Exception e) {
                var alert = createAlert(
                        Alert.AlertType.ERROR,
                        AlertStyleClass.BLUE_DEFAULT
                );

                alert.setContentText(e.getMessage());
                alert.show();

                e.printStackTrace();
            }
        });

        var visitedPane = (ScrollPane) owner.getScene().lookup("#visitedPlacesScrollPane");

        var markAsVisitedButton = (Button) placePane.lookup("#markAsVisitedButton");

        if (UserDatabase.CURRENT_USER.getPlacesVisited().containsKey(place)) {
            markAsVisitedButton.getStyleClass().add("green-button");
            markAsVisitedButton.setText("Visited");
        }

        markAsVisitedButton.setOnAction(event -> {
            if (UserDatabase.CURRENT_USER.getPlacesVisited().containsKey(place)) {
                UserDatabase.CURRENT_USER.removePlaceVisited(place);

                var visitedVBox = (VBox) visitedPane.getContent();
                var visitedHBox = (HBox) visitedVBox.lookup("#visitedHBox");

                visitedHBox.getChildren().removeIf(n -> n.getId().equals(place.getId()));

                if (visitedHBox.getChildren().isEmpty()) {
                    visitedVBox.getChildren().clear();
                }

                markAsVisitedButton.getStyleClass().remove("green-button");
                markAsVisitedButton.setText("Mark as Visited");
            } else {
                UserDatabase.CURRENT_USER.addPlaceVisited(place);

                var visitedVBox = (VBox) visitedPane.getContent();

                visitedVBox.setPadding(new Insets(10));

                if (visitedVBox.getChildren().isEmpty()) {
                    var visitedHBox = new HBox();

                    visitedHBox.setId("visitedHBox");
                    visitedHBox.setSpacing(30);
                    visitedHBox.setStyle("-fx-background-color: transparent;");

                    var visitedTitle = new Label("Recently Visited");

                    visitedTitle.setFont(Font.font("System", FontWeight.BOLD, 25));
                    visitedTitle.setId("recentlyVisitedTitle");
                    visitedTitle.setTextFill(Color.WHITE);

                    visitedVBox.getChildren().addAll(visitedTitle, visitedHBox);
                }

                var visitedHBox = (HBox) visitedVBox.lookup("#visitedHBox");

                var visitedImageView = new ImageView(image);

                visitedImageView.setFitHeight(150);
                visitedImageView.setPickOnBounds(true);
                visitedImageView.setPreserveRatio(true);

                var visitedLabel = new Label(place.getTitle());

                visitedLabel.setFont(Font.font("System", FontWeight.BOLD, 15));
                visitedLabel.setTextFill(Color.WHITE);

                var visitedPlaceVBox = new VBox(visitedImageView, visitedLabel);

                visitedPlaceVBox.setCursor(Cursor.HAND);
                visitedPlaceVBox.setId(place.getId());

                visitedPlaceVBox.setOnMouseClicked(e -> {
                    UserDatabase.CURRENT_USER.addRecentlyViewed(place);

                    var recentlyViewedPane = (ScrollPane) owner.getScene().lookup("#recentPlacesScrollPane");
                    var recentlyViewedVBox = (VBox) recentlyViewedPane.getContent();

                    if (recentlyViewedVBox.getChildren().isEmpty()) {
                        var recentlyViewedHBox = new HBox();

                        recentlyViewedHBox.setId("recentHBox");
                        recentlyViewedHBox.setSpacing(30);
                        recentlyViewedHBox.setStyle("-fx-background-color: transparent;");

                        var recentlyViewedTitle = new Label("Recently Viewed");

                        recentlyViewedTitle.setFont(Font.font("System", FontWeight.BOLD, 25));
                        recentlyViewedTitle.setId("recentlyViewedTitle");
                        recentlyViewedTitle.setTextFill(Color.WHITE);

                        recentlyViewedVBox.getChildren().addAll(recentlyViewedTitle, recentlyViewedHBox);
                    }

                    var recentlyViewedHBox = (HBox) recentlyViewedVBox.lookup("#recentHBox");

                    recentlyViewedHBox.getChildren().removeIf(n -> n.getId().equals(place.getId()));

                    var recentImageView = new ImageView(image);

                    recentImageView.setFitHeight(150);
                    recentImageView.setPickOnBounds(true);
                    recentImageView.setPreserveRatio(true);

                    var recentLabel = new Label(place.getTitle());

                    recentLabel.setFont(Font.font("System", FontWeight.BOLD, 15));
                    recentLabel.setTextFill(Color.WHITE);

                    var recentPlaceVBox = new VBox(recentImageView, recentLabel);

                    recentPlaceVBox.setCursor(Cursor.HAND);
                    recentPlaceVBox.setId(place.getId());

                    recentPlaceVBox.setOnMouseClicked(ev -> {
                        UserDatabase.CURRENT_USER.addRecentlyViewed(place);

                        recentlyViewedHBox.getChildren().remove(recentPlaceVBox);
                        recentlyViewedHBox.getChildren().add(0, recentPlaceVBox);

                        try {
                            loadPlaceWindow(place, owner);
                        } catch (IOException ex) {
                            var alert = createAlert(
                                    Alert.AlertType.ERROR,
                                    AlertStyleClass.BLUE_DEFAULT
                            );

                            alert.setContentText(ex.getMessage());
                            alert.show();

                            ex.printStackTrace();
                        }
                    });

                    recentlyViewedHBox.getChildren().add(0, recentPlaceVBox);

                    try {
                        loadPlaceWindow(place, owner);
                    } catch (IOException ex) {
                        var alert = createAlert(
                                Alert.AlertType.ERROR,
                                AlertStyleClass.BLUE_DEFAULT
                        );

                        alert.setContentText(ex.getMessage());
                        alert.show();

                        ex.printStackTrace();
                    }
                });

                visitedHBox.getChildren().add(0, visitedPlaceVBox);

                markAsVisitedButton.getStyleClass().add("green-button");
                markAsVisitedButton.setText("Visited");
            }
        });

        var createPlanButton = (Button) placePane.lookup("#createPlanButton");

        var placeScene = new Scene(placePane);
        var placeStage = new Stage();

        createPlanButton.setOnAction(event -> {
            try {
                var planCreationLoader = new FXMLLoader(MainApplication.class.getResource("plan-creation-view.fxml"));
                var pane = (VBox) planCreationLoader.load();

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
                attachmentBox.getSelectionModel().select(place);

                var datePicker = (DatePicker) pane.lookup("#planDatePicker");

                datePicker.setDayCellFactory(picker -> new DateCell() {
                    public void updateItem(LocalDate date, boolean empty) {
                        super.updateItem(date, empty);

                        var today = LocalDate.now();

                        setDisable(empty || date.isBefore(today));
                    }
                });

                var scene = new Scene(pane);

                placeStage.setTitle("Create Plan - " + Immutable.NAME);
                placeStage.setScene(scene);
                placeStage.centerOnScreen();
            } catch (IOException ex) {
                var alert = createAlert(
                        Alert.AlertType.ERROR,
                        AlertStyleClass.BLUE_DEFAULT
                );

                alert.setContentText(ex.getMessage());
                alert.show();

                ex.printStackTrace();
            }
        });

        placeStage.getIcons().add(new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream("icon.png"))));
        placeStage.initModality(Modality.WINDOW_MODAL);
        placeStage.initOwner(owner);
        placeStage.setTitle(String.format("%s - %s", place.getTitle(), Immutable.NAME));
        placeStage.setScene(placeScene);
        placeStage.setResizable(false);
        placeStage.show();
    }

    /**
     * Opens a window that contains information about a specific tip
     *
     * @param tip the tip
     * @param owner the owner window to attach the tip window to
     */
    @SuppressWarnings("unchecked")
    public static void loadTipWindow(Tip tip, Window owner) throws IOException {
        var fxmlLoader = new FXMLLoader(MainApplication.class.getResource("tip-view.fxml"));
        var tipPane = (VBox) fxmlLoader.load();

        var image = AttachmentDatabase.get(tip);
        var banner = (Rectangle) tipPane.lookup("#imageBanner");

        if (image.getProgress() != 1.0) {
            image.progressProperty().addListener((observable, oldValue, progress) -> {
                if ((double) progress == 1.0 && !image.isError()) {
                    var ratio = image.getWidth() / image.getHeight();
                    var height = banner.getWidth() / ratio;
                    var pattern = new ImagePattern(image, 0, -height * 0.25, banner.getWidth(), height, false);

                    banner.setFill(pattern);
                }
            });
        } else {
            var ratio = image.getWidth() / image.getHeight();
            var height = banner.getWidth() / ratio;
            var pattern = new ImagePattern(image, 0, -height * 0.25, banner.getWidth(), height, false);

            banner.setFill(pattern);
        }

        var tipNameText = (Text) tipPane.lookup("#tipName");
        var tipDescriptionScrollPane = (ScrollPane) tipPane.lookup("#descriptionScrollPane");
        var tipDescriptionText = (Text) tipDescriptionScrollPane.getContent();

        tipNameText.setText(tip.getTitle());
        tipDescriptionText.setText(tip.getDescription());

        if (tip.getPlace() != null) {
            var place = tip.getPlace();

            var placeImage = AttachmentDatabase.get(place);
            var imageView = new ImageView(placeImage);

            imageView.setFitHeight(65);
            imageView.setPickOnBounds(true);
            imageView.setPreserveRatio(true);

            var label = new Label(place.getTitle());

            label.setFont(Font.font("System", FontWeight.BOLD, 20));
            label.setTextFill(Color.WHITE);

            var placeHBox = new HBox(imageView, label);

            placeHBox.getStyleClass().add("plan-card");
            placeHBox.setAlignment(Pos.CENTER);
            placeHBox.setCursor(Cursor.HAND);
            placeHBox.setPadding(new Insets(10));
            placeHBox.setSpacing(10);
            placeHBox.setMaxWidth(tipPane.getPrefWidth());
            placeHBox.setOnMouseClicked(event -> {
                UserDatabase.CURRENT_USER.addRecentlyViewed(place);

                var recentVBox = (VBox) ((ScrollPane) owner.getScene().lookup("#recentPlacesScrollPane")).getContent();

                recentVBox.setPadding(new Insets(10));

                if (recentVBox.getChildren().isEmpty()) {
                    var recentHBox = new HBox();

                    recentHBox.setId("recentHBox");
                    recentHBox.setSpacing(30);
                    recentHBox.setStyle("-fx-background-color: transparent;");

                    var recentTitle = new Label("Recently Viewed");

                    recentTitle.setFont(Font.font("System", FontWeight.BOLD, 25));
                    recentTitle.setId("recentlyViewedTitle");
                    recentTitle.setTextFill(Color.WHITE);

                    recentVBox.getChildren().addAll(recentTitle, recentHBox);
                }

                var recentHBox = (HBox) recentVBox.lookup("#recentHBox");

                recentHBox.getChildren().removeIf(n -> n.getId().equals(place.getId()));

                var recentImageView = new ImageView(AttachmentDatabase.get(place));

                recentImageView.setFitHeight(150);
                recentImageView.setPickOnBounds(true);
                recentImageView.setPreserveRatio(true);

                var recentLabel = new Label(place.getTitle());

                recentLabel.setFont(Font.font("System", FontWeight.BOLD, 15));
                recentLabel.setTextFill(Color.WHITE);

                var recentPlaceVBox = new VBox(recentImageView, recentLabel);

                recentPlaceVBox.setCursor(Cursor.HAND);
                recentPlaceVBox.setId(place.getId());

                recentPlaceVBox.setOnMouseClicked(e -> {
                    UserDatabase.CURRENT_USER.addRecentlyViewed(place);

                    recentHBox.getChildren().remove(recentPlaceVBox);
                    recentHBox.getChildren().add(0, recentPlaceVBox);

                    try {
                        loadPlaceWindow(place, owner);
                    } catch (IOException ex) {
                        var alert = createAlert(
                                Alert.AlertType.ERROR,
                                AlertStyleClass.BLUE_DEFAULT
                        );

                        alert.setContentText(ex.getMessage());
                        alert.show();

                        ex.printStackTrace();
                    }
                });

                recentHBox.getChildren().add(0, recentPlaceVBox);

                try {
                    loadPlaceWindow(place, owner);
                } catch (IOException e) {
                    var alert = createAlert(
                            Alert.AlertType.ERROR,
                            AlertStyleClass.BLUE_DEFAULT
                    );

                    alert.setContentText(e.getMessage());
                    alert.show();

                    e.printStackTrace();
                }
            });

            var belowBannerVBox = (VBox) tipPane.lookup("#belowBannerVBox");
            var index = belowBannerVBox.getChildren().indexOf(belowBannerVBox.lookup("#buttonVBox"));

            belowBannerVBox.getChildren().add(index, placeHBox);
        }

        var createPlanButton = (Button) tipPane.lookup("#createPlanButton");

        var placeScene = new Scene(tipPane);
        var placeStage = new Stage();

        createPlanButton.setOnAction(event -> {
            try {
                var planCreationLoader = new FXMLLoader(MainApplication.class.getResource("plan-creation-view.fxml"));
                var pane = (VBox) planCreationLoader.load();

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
                attachmentBox.getSelectionModel().select(tip);

                var datePicker = (DatePicker) pane.lookup("#planDatePicker");

                datePicker.setDayCellFactory(picker -> new DateCell() {
                    public void updateItem(LocalDate date, boolean empty) {
                        super.updateItem(date, empty);

                        var today = LocalDate.now();

                        setDisable(empty || date.isBefore(today));
                    }
                });

                var scene = new Scene(pane);

                placeStage.setTitle("Create Plan - " + Immutable.NAME);
                placeStage.setScene(scene);
                placeStage.centerOnScreen();
            } catch (IOException ex) {
                var alert = createAlert(
                        Alert.AlertType.ERROR,
                        AlertStyleClass.BLUE_DEFAULT
                );

                alert.setContentText(ex.getMessage());
                alert.show();

                ex.printStackTrace();
            }
        });

        placeStage.getIcons().add(new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream("icon.png"))));
        placeStage.initModality(Modality.WINDOW_MODAL);
        placeStage.initOwner(owner);
        placeStage.setTitle(String.format("%s - %s", tip.getTitle(), Immutable.NAME));
        placeStage.setScene(placeScene);
        placeStage.setResizable(false);
        placeStage.show();
    }

    /**
     * Opens a window that contains information about a specific plan
     *
     * @param plan the plan
     * @param owner the owner window to attach the plan window to
     */
    @SuppressWarnings("unchecked")
    public static void loadPlanWindow(Plan plan, Window owner) throws IOException {
        var fxmlLoader = new FXMLLoader(MainApplication.class.getResource("plan-view.fxml"));
        var planPane = (VBox) fxmlLoader.load();

        var textArea = (TextArea) planPane.lookup("#planContentTextArea");

        textArea.setText(plan.getDescription());

        var priorityBox = (ComboBox<Plan.Priority>) planPane.lookup("#priorityBox");

        priorityBox.setItems(FXCollections.observableList(Arrays.stream(Plan.Priority.values()).toList()));
        priorityBox.getSelectionModel().select(plan.getPriority());

        var datePicker = (DatePicker) planPane.lookup("#planDatePicker");

        datePicker.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);

                var today = LocalDate.now();

                setDisable(empty || date.isBefore(today));
            }
        });

        if (plan.getDatePlanned() != null) {
            datePicker.setValue(plan.getDatePlanned().toLocalDate());
        }

        var button = (Button) planPane.lookup("#saveButton");

        var planScene = new Scene(planPane);
        var planStage = new Stage();

        if (plan.getAttachment() != null) {
            var attachment = plan.getAttachment();

            var image = AttachmentDatabase.get(attachment);
            var imageView = new ImageView(image);

            imageView.setFitHeight(75);
            imageView.setPickOnBounds(true);
            imageView.setPreserveRatio(true);

            var label = new Label(attachment.getTitle());

            label.setFont(Font.font("System", FontWeight.BOLD, 20));
            label.setTextFill(Color.WHITE);

            var attachmentHBox = new HBox(imageView, label);

            attachmentHBox.getStyleClass().add("plan-card");
            attachmentHBox.setAlignment(Pos.CENTER);
            attachmentHBox.setCursor(Cursor.HAND);
            attachmentHBox.setPadding(new Insets(10));
            attachmentHBox.setSpacing(10);
            attachmentHBox.setMaxWidth(textArea.getPrefWidth());
            attachmentHBox.setOnMouseClicked(event -> {
                UserDatabase.CURRENT_USER.addRecentlyViewed(attachment);

                var recentVBox = (VBox) ((ScrollPane) owner.getScene().lookup("#recentPlacesScrollPane")).getContent();

                recentVBox.setPadding(new Insets(10));

                if (recentVBox.getChildren().isEmpty()) {
                    var recentHBox = new HBox();

                    recentHBox.setId("recentHBox");
                    recentHBox.setSpacing(30);
                    recentHBox.setStyle("-fx-background-color: transparent;");

                    var recentTitle = new Label("Recently Viewed");

                    recentTitle.setFont(Font.font("System", FontWeight.BOLD, 25));
                    recentTitle.setId("recentlyViewedTitle");
                    recentTitle.setTextFill(Color.WHITE);

                    recentVBox.getChildren().addAll(recentTitle, recentHBox);
                }

                var recentHBox = (HBox) recentVBox.lookup("#recentHBox");

                recentHBox.getChildren().removeIf(n -> n.getId().equals(attachment.getId()));

                var recentImageView = new ImageView(image);

                recentImageView.setFitHeight(150);
                recentImageView.setPickOnBounds(true);
                recentImageView.setPreserveRatio(true);

                var recentLabel = new Label(attachment.getTitle());

                recentLabel.setFont(Font.font("System", FontWeight.BOLD, 15));
                recentLabel.setTextFill(Color.WHITE);

                var recentPlaceVBox = new VBox(recentImageView, recentLabel);

                recentPlaceVBox.setCursor(Cursor.HAND);
                recentPlaceVBox.setId(attachment.getId());

                recentPlaceVBox.setOnMouseClicked(e -> {
                    UserDatabase.CURRENT_USER.addRecentlyViewed(attachment);

                    recentHBox.getChildren().remove(recentPlaceVBox);
                    recentHBox.getChildren().add(0, recentPlaceVBox);

                    try {
                        if (attachment instanceof Place) {
                            loadPlaceWindow((Place) attachment, owner);
                        } else {
                            loadTipWindow((Tip) attachment, owner);
                        }
                    } catch (IOException ex) {
                        var alert = createAlert(
                                Alert.AlertType.ERROR,
                                AlertStyleClass.BLUE_DEFAULT
                        );

                        alert.setContentText(ex.getMessage());
                        alert.show();

                        ex.printStackTrace();
                    }
                });

                recentHBox.getChildren().add(0, recentPlaceVBox);

                try {
                    if (attachment instanceof Place) {
                        loadPlaceWindow((Place) attachment, owner);
                    } else {
                        loadTipWindow((Tip) attachment, owner);
                    }
                } catch (IOException e) {
                    var alert = createAlert(
                            Alert.AlertType.ERROR,
                            AlertStyleClass.BLUE_DEFAULT
                    );

                    alert.setContentText(e.getMessage());
                    alert.show();

                    e.printStackTrace();
                }
            });

            planPane.getChildren().add(0, attachmentHBox);
        }

        textArea.textProperty().addListener((observable, oldValue, newValue) -> {
            button.setDisable(newValue.trim().equals(plan.getDescription() == null ? "" : plan.getDescription().trim())
                    && priorityBox.getValue().equals(plan.getPriority())
                    && (plan.getDatePlanned() == null ? datePicker.getValue() == null : plan.getDatePlanned().toLocalDate().equals(datePicker.getValue())));
        });

        priorityBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            button.setDisable(newValue.equals(plan.getPriority())
                    && (plan.getDescription() == null || plan.getDescription().isBlank() ? textArea.getText() == null || textArea.getText().isBlank() : plan.getDescription().trim().equals(textArea.getText().trim()))
                    && (plan.getDatePlanned() == null ? datePicker.getValue() == null : plan.getDatePlanned().toLocalDate().equals(datePicker.getValue())));
        });

        datePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            button.setDisable(plan.getDatePlanned() == null ? newValue == null : newValue.equals(plan.getDatePlanned().toLocalDate())
                    && (plan.getDescription() == null || plan.getDescription().isBlank() ? textArea.getText() == null || textArea.getText().isBlank() : plan.getDescription().trim().equals(textArea.getText().trim()))
                    && priorityBox.getValue().equals(plan.getPriority()));
        });

        button.setOnAction(event -> {
            try {
                var newPlan = new PlanBuilder(plan)
                        .setPriority(priorityBox.getValue())
                        .setDescription(textArea.getText() != null && !textArea.getText().isBlank() ? textArea.getText().trim() : null)
                        .setDatePlanned(datePicker.getValue() != null ? datePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toOffsetDateTime() : null)
                        .build();

                var plansPane = (ScrollPane) owner.getScene().lookup("#plansScrollPane");
                var plansVBox = (VBox) plansPane.getContent();

                UserDatabase.CURRENT_USER.removePlan(plan);
                UserDatabase.CURRENT_USER.addPlan(newPlan);

                plansVBox.getChildren().removeIf(n -> n.getId().equals("p" + newPlan.getId()));

                var contentLabel = new Label(newPlan.getDescription() != null ?
                        newPlan.getDescription().split("\n")[0] :
                        newPlan.getAttachment().getTitle()
                );

                contentLabel.setFont(Font.font("System", FontWeight.BOLD, 20));
                contentLabel.setTextFill(Color.WHITE);

                var priorityLabel = new Label(newPlan.getPriority().toString() + " Priority • ");

                priorityLabel.setTextFill(Color.WHITE);

                var dateFormatter = DateTimeFormatter.RFC_1123_DATE_TIME;

                var dateLabel = new Label(newPlan.getDatePlanned() == null ?
                        "No Date" :
                        dateFormatter.format(newPlan.getDatePlanned())
                );

                dateLabel.setTextFill(Color.WHITE);

                var priorityAndDateHBox = new HBox(priorityLabel, dateLabel);

                priorityAndDateHBox.setId("priorityAndDateHBox");

                var planCardVBox = new VBox(contentLabel, priorityAndDateHBox);

                planCardVBox.setId("planCardVBox");

                var deleteButton = new Button("X");

                deleteButton.getStyleClass().add("red-button");

                var planCardHBox = new HBox(planCardVBox, deleteButton);

                planCardHBox.setSpacing(10);

                if (newPlan.getAttachment() != null) {
                    var image = AttachmentDatabase.get(newPlan.getAttachment());
                    var imageView = new ImageView(image);

                    imageView.setFitHeight(75);
                    imageView.setPickOnBounds(true);
                    imageView.setPreserveRatio(true);

                    planCardHBox.getChildren().add(0, imageView);
                }

                HBox.setHgrow(planCardVBox, Priority.ALWAYS);
                HBox.setHgrow(deleteButton, Priority.NEVER);

                deleteButton.setOnAction(e -> {
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
                planCardHBox.setId(String.format("p%d", newPlan.getId()));
                planCardHBox.setPrefHeight(75);
                planCardHBox.setCursor(Cursor.HAND);
                planCardHBox.setPadding(new Insets(10));
                planCardHBox.setOnMouseClicked(e -> {
                    try {
                        ControllerUtilities.loadPlanWindow(newPlan, owner);
                    } catch (IOException ex) {
                        var alert = createAlert(
                                Alert.AlertType.ERROR,
                                AlertStyleClass.BLUE_DEFAULT
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

                planStage.close();
            } catch (InsufficientPlanDetailException e) {
                var alert = createAlert(Alert.AlertType.ERROR, AlertStyleClass.BLUE_DEFAULT);

                alert.setContentText(e.getMessage());
                alert.show();
            }
        });

        planStage.setOnCloseRequest(event -> {
            if (!button.isDisabled()) {
                var alert = ControllerUtilities.createAlert(
                        Alert.AlertType.CONFIRMATION,
                        ControllerUtilities.AlertStyleClass.RED_DEFAULT,
                        ControllerUtilities.AlertStyleClass.BLUE_CANCEL
                );

                alert.setContentText("Are you sure you want to close the window without saving any changes?");
                alert.showAndWait().ifPresent(option -> {
                    if (option == ButtonType.OK) {
                        planStage.close();
                    } else {
                        event.consume();
                    }
                });
            } else {
                planStage.close();
            }
        });
        planStage.getIcons().add(new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream("icon.png"))));
        planStage.initModality(Modality.WINDOW_MODAL);
        planStage.initOwner(owner);
        planStage.setTitle("Plan - " + Immutable.NAME);
        planStage.setScene(planScene);
        planStage.setResizable(false);
        planStage.show();
    }

    /**
     * @param type a JavaFX alert type
     * @param styleClasses button style classes
     *
     * @return a styled alert of the specified type
     */
    public static Alert createAlert(Alert.AlertType type, AlertStyleClass... styleClasses) {
        var iconFileName = String.format("%sIcon.png", type.name().toLowerCase());
        var icon = new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream(iconFileName)));
        var iconView = new ImageView(icon);

        iconView.setPreserveRatio(true);
        iconView.setFitHeight(40);

        var alert = new Alert(type);

        alert.getDialogPane().getStylesheets().add(Objects.requireNonNull(MainApplication.class.getResource("stylesheet.css")).toExternalForm());
        alert.getDialogPane().getStyleClass().addAll(Arrays.stream(styleClasses).map(AlertStyleClass::getValue).toList());
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.setGraphic(iconView);

        var stage = (Stage) alert.getDialogPane().getScene().getWindow();

        stage.getIcons().add(new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream("icon.png"))));

        return alert;
    }

    public enum AlertStyleClass {
        BLUE_DEFAULT("blue-default-dialog-pane"),
        BLUE_CANCEL("blue-cancel-dialog-pane"),
        RED_DEFAULT("red-default-dialog-pane"),
        RED_CANCEL("red-cancel-dialog-pane");

        private final String value;

        AlertStyleClass(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}