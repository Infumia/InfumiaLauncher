package com.infumia.launcher;

import com.infumia.launcher.animations.FadeInSceneTransition;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ScheduledThreadPoolExecutor;

public class InfumiaLauncher extends Application {

    public static Stage stage;
    public static Parent parent;

    public static int step = 1;

    private static InfumiaLauncher infumiaLauncher = new InfumiaLauncher();

    public static Logger logger = LogManager.getLogger("log4j2.xml");

    public static ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);

    @Override
    public void start(Stage stage) throws Exception {
        logger.info("Launcher başlatılıyor...");
        parent = FXMLLoader.load(InfumiaLauncher.class.getResource("screens/InfumiaLauncherParent.fxml"));
        Scene scene = new Scene(parent);
        stage.setScene(scene);

        stage.initStyle(StageStyle.TRANSPARENT);
        stage.getIcons().add(new Image("assets/infumia-logo.png"));

        stage.show();
        stage.requestFocus();
        InfumiaLauncher.stage = stage;

        FadeInSceneTransition sceneTransition = new FadeInSceneTransition(stage,scene, Duration.seconds(1));
        sceneTransition.play();
        sceneTransition.loadNextScene();

        logger.info("Infumia Launcher başlatıldı.");
    }

    public static InfumiaLauncher getInfumiaLauncher() {
        return infumiaLauncher;
    }

    public static void main(String[] args) {
        launch(args);
    }

}
