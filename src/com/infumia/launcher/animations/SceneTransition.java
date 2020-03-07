package com.infumia.launcher.animations;

/**
 *    Copyright 2019-2020 Infumia
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

import com.infumia.launcher.InfumiaLauncher;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SceneTransition {

    double xOffset = 0;
    double yOffset = 0;

    private Stage stage;
    private Scene tScreen;

    public EventHandler<ActionEvent> eventHandler;

    public SceneTransition(Stage stage, Scene tScreen, EventHandler<ActionEvent> event) {
        this.stage = stage;
        this.tScreen = tScreen;
        this.eventHandler = event;
    }

    FadeTransition fadeInTransition;
    FadeTransition fadeOutTransition;

    public void play() {
        fadeOutTransition = Animation.fadeIn(Duration.seconds(0.2), stage.getScene().getRoot());
        AnchorPane anchorPane = (AnchorPane) stage.getScene().lookup("#anchorpane");
        int size = anchorPane.getChildren().size();
        for (int i = 0; i < size; i++) {
            if (anchorPane.getChildren().get(i).getTypeSelector().equals("FontAwesomeIcon")) continue;
            fadeOutTransition = Animation.fadeIn(Duration.seconds(0.3), anchorPane.getChildren().get(i));
            fadeOutTransition.setFromValue(1);
            fadeOutTransition.setToValue(0);
            fadeOutTransition.play();
        }
        fadeOutTransition.setOnFinished(eventHandler);

        Parent parent = tScreen.getRoot();

        parent.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            }
        });
        parent.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                stage.setX(event.getScreenX() - xOffset);
                stage.setY(event.getScreenY() - yOffset);
            }
        });
        parent.setOnDragDone((e) -> {
            if (stage.getY() < 0) {
                stage.setY(0);
            }
            stage.setOpacity(1.0f);
        });
        parent.setOnMouseReleased((e) -> {
            if (stage.getY() < 0) {
                stage.setY(0);
            }
            stage.setOpacity(1.0f);
        });
    }

    public void loadNextScene() {
        try {
            Parent parent = tScreen.getRoot();
            Stage curStage = (Stage) InfumiaLauncher.stage.getScene().getWindow();

            curStage.setScene(null);

            curStage.setScene(parent.getScene());
            AnchorPane anchorPane = (AnchorPane) parent.getScene().lookup("#anchorpane");
            int size = anchorPane.getChildren().size();
            for (int i=0; i < size; i++) {
                if (anchorPane.getChildren().get(i).getTypeSelector().equals("FontAwesomeIcon")) continue;
                anchorPane.getChildren().get(i).setOpacity(0);
                fadeInTransition = Animation.fadeIn(Duration.seconds(1), anchorPane.getChildren().get(i));
                fadeInTransition.setFromValue(0);
                fadeInTransition.setToValue(1);
                fadeInTransition.play();
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        if (fadeInTransition != null) fadeInTransition.stop();
        if (fadeInTransition != null) fadeOutTransition.stop();
    }

    public void setEventHandler(EventHandler e) {
        this.eventHandler = e;
    }

}
