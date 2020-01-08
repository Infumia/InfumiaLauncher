package com.infumia.launcher.screens;

import com.infumia.launcher.InfumiaLauncher;
import com.infumia.launcher.animations.Animation;
import com.infumia.launcher.animations.FadeInSceneTransition;
import com.infumia.launcher.animations.MoveYAnimation;
import com.infumia.launcher.download.*;
import com.infumia.launcher.objects.Callback;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXProgressBar;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.util.Duration;

import java.io.File;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class HomeParentController implements Initializable {

    File gamedir = new File(System.getenv().get("APPDATA") + "/.infumia/");
    File assestdir = new File(System.getenv().get("APPDATA") + "/.infumia/assets/");
    File objectsdir = new File(System.getenv().get("APPDATA") + "/.infumia/assets/objects");
    File librarydir = new File(System.getenv().get("APPDATA") + "/.infumia/libraries/");
    File versionsdir = new File(System.getenv().get("APPDATA") + "/.infumia/versions/");
    File nativedir = new File(System.getenv().get("APPDATA") + "/.infumia/libraries/native/");
    File indexesDir = new File(System.getenv().get("APPDATA") + "/.infumia/assets/indexes/");
    File logconfigsDir = new File(System.getenv().get("APPDATA") + "/.infumia/assets/log_configs/");

    @FXML
    Label percent;

    @FXML
    JFXProgressBar progressbar;

    @FXML
    JFXButton playButton;

    @FXML
    ImageView avatar;

    @FXML
    Label playerName;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (!gamedir.exists()) gamedir.mkdir();
        if (!assestdir.exists()) assestdir.mkdir();
        if (!librarydir.exists()) librarydir.mkdir();
        String name = Minecraft.playerName;
        playerName.setText(name);
        try {
            avatar.setImage(Minecraft.image);
        }catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    HBox exitPaneBox;

    @FXML
    Pane exitScene;

    @FXML
    Pane exitPane;

    @FXML
    Label infoScreenTitle;

    @FXML
    Label infoScreenDescription;

    @FXML
    JFXButton verifyExitButton;

    @FXML
    JFXButton okButton;

    @FXML
    Label cancelLabel;

    @FXML
    Label exitButton;

    @FXML
    public void goExitScene() {
        exitScene.setVisible(true);
        exitPane.setVisible(true);
        exitPaneBox.setVisible(true);
        cancelLabel.setVisible(true);
        verifyExitButton.setVisible(true);
        okButton.setVisible(false);
        infoScreenTitle.setText("ÇIKIŞ YAP");
        infoScreenDescription.setText("Çıkış yapmak istediğinize emin misiniz?");
        infoScreenTitle.setTextFill(Paint.valueOf("ff4c4c"));
        infoScreenDescription.setTextFill(Paint.valueOf("dcddde"));
        FadeTransition fade = Animation.fadeIn(Duration.seconds(0.3), exitScene);
        fade.play();
        FadeTransition fade3 = Animation.fadeIn(Duration.seconds(0.17), exitPane);;
        fade3.play();
        Animation.salla(exitPane);
    }

    @FXML
    public void cancelExit() {
        FadeTransition fade = Animation.fadeOut(Duration.seconds(0.3), exitScene);
        fade.play();
        ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(0.04), exitPane);
        scaleTransition.setFromX(1.0);
        scaleTransition.setFromY(1.0);
        scaleTransition.setToX(0.99);
        scaleTransition.setToY(0.99);
        scaleTransition.play();

        FadeTransition fade3 = Animation.fadeOut(Duration.seconds(0.25), exitPane);
        fade3.play();

        scaleTransition.setOnFinished(event -> {
            ScaleTransition scaleTransition2 = new ScaleTransition(Duration.seconds(0.08), exitPane);
            scaleTransition2.setFromX(0.99);
            scaleTransition2.setFromY(0.99);
            scaleTransition2.setToX(1.03);
            scaleTransition2.setToY(1.03);
            scaleTransition2.play();


            scaleTransition2.setOnFinished(event1 -> {
                ScaleTransition scaleTransition3 = new ScaleTransition(Duration.seconds(0.15), exitPane);
                scaleTransition3.setFromX(1.03);
                scaleTransition3.setFromY(1.03);
                scaleTransition3.setToX(0.2);
                scaleTransition3.setToY(0.2);
                scaleTransition3.play();
                scaleTransition3.setOnFinished(event2 ->  {
                    exitScene.setVisible(false);
                    exitPaneBox.setVisible(false);
                    exitPane.setVisible(false);
                });
            });
        });
    }

    @FXML
    public void goBack() {
        Minecraft.image = null;
        Minecraft.playerName = null;
        try {
            InfumiaLauncher.parent = FXMLLoader.load(getClass().getResource("FakeParent.fxml"), null, new JavaFXBuilderFactory());
            Scene scene = InfumiaLauncher.stage.getScene();
            if (scene == null) {
                scene = new Scene(InfumiaLauncher.parent, 1100, 620);
                InfumiaLauncher.stage.setScene(scene);
            } else {
                InfumiaLauncher.stage.getScene().setRoot(InfumiaLauncher.parent);
            }

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    try {
                        Parent secondParent = FXMLLoader.load(getClass().getResource("InfumiaLauncherParent.fxml"));
                        Scene secondScene = new Scene(secondParent);

                        InfumiaLauncher.stage.setScene(secondScene);

                        FadeInSceneTransition sceneTransition = new FadeInSceneTransition(InfumiaLauncher.stage, secondScene, Duration.seconds(0.3));
                        sceneTransition.play();
                        sceneTransition.makeStageDrageable();
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    public void launch(){
        playButton.setDisable(true);
        exitButton.setDisable(true);

        if(!objectsdir.exists())objectsdir.mkdir();
        if(!versionsdir.exists())versionsdir.mkdir();
        if(!nativedir.exists())nativedir.mkdir();
        if(!indexesDir.exists())indexesDir.mkdir();
        if(!logconfigsDir.exists())logconfigsDir.mkdir();

        MoveYAnimation animation = new MoveYAnimation(progressbar, progressbar.getLayoutY(), 610, Duration.seconds(0.3));
        animation.play();
        AtomicReference<Timeline> animation2 = new AtomicReference<>();
        animation.setOnFinished(event -> {
            animation2.set(new Timeline(
                    new KeyFrame(Duration.millis(1), ((actionEvent) -> {
                        if (InfumiaLauncher.step == 1) {
                            if (MinecraftAssetsDownloader.objects == null) return;
                            double perc = (Double.valueOf(String.valueOf(MinecraftAssetsDownloader.currentfile)) / MinecraftAssetsDownloader.objects.length()) * 25d;
                            percent.setText("%" + new DecimalFormat("##.#").format(perc).replace(",", "."));
                            progressbar.setProgress(perc / 100d);
                            return;
                        }
                        if (InfumiaLauncher.step == 2) {
                            percent.setText("%" + new DecimalFormat("##.#").format((MinecraftClientDownloader.downloadPercent * 0.25d) + 25d).replace(",", "."));
                            progressbar.setProgress(((MinecraftClientDownloader.downloadPercent * 0.25d) + 25d) / 100d);
                            return;
                        }
                        if (InfumiaLauncher.step == 3) {
                            percent.setText("%" + new DecimalFormat("##.#").format((MinecraftNativesDownloader.fileStep / 11d) * 25d + 50d).replace(",", "."));
                            progressbar.setProgress(((Double.valueOf(MinecraftNativesDownloader.fileStep + "") / 11d) * 25d + 50d) / 100d);

                        }
                        if (InfumiaLauncher.step == 4) {
                            if (MinecraftLibrariesDownloader.objects != null) {
                                percent.setText("%" + new DecimalFormat("##.#").format(((Double.valueOf(MinecraftLibrariesDownloader.currentfilelib + MinecraftLibrariesDownloader.currentfilenativelib) / (MinecraftLibrariesDownloader.totalNativeLib + MinecraftLibrariesDownloader.objects.length())) * 25d) + 75d).replace(",", "."));
                                progressbar.setProgress((((Double.valueOf(MinecraftLibrariesDownloader.currentfilelib + MinecraftLibrariesDownloader.currentfilenativelib) / (MinecraftLibrariesDownloader.totalNativeLib + MinecraftLibrariesDownloader.objects.length())) * 25d) + 75d) / 100d);
                            }
                        }
                        if (InfumiaLauncher.step == 5) {
                            percent.setText("%100");
                            progressbar.setProgress(1);
//                        try {
//                            SystemTray tray = SystemTray.getSystemTray();
//                            java.awt.Image trayImage = Toolkit.getDefaultToolkit().createImage(InfumiaLauncher.class.getClassLoader().getResource("images/logo-small.png"));
//                            TrayIcon icon = new TrayIcon(trayImage, "Growler Network");
//                            icon.setToolTip("Growler Network Hile Koruması");
//                            icon.setImageAutoSize(true);
//                            tray.add(icon);
//
//                        }catch (AWTException e){
//                            e.printStackTrace();
//                        }
                            Platform.exit();
                        }
                    })
                    )
            ));
            animation2.get().setCycleCount(Timeline.INDEFINITE);
            animation2.get().play();

            Thread thread = new Thread(new MinecraftAssetsDownloader(response -> Platform.runLater(() -> {
                error("HATA", "Sunucuyla bağlantı kurulurken hata oluştu: " + response);
                MoveYAnimation animation1 = new MoveYAnimation(progressbar, progressbar.getLayoutY(), 620, Duration.seconds(0.3));
                animation1.play();
                playButton.setDisable(false);
                exitButton.setDisable(false);
            })));
            InfumiaLauncher.executor.schedule(thread, 50, TimeUnit.MILLISECONDS);
        });
    }

    public void error(String title, String description) {
        cancelLabel.setVisible(false);
        verifyExitButton.setVisible(false);
        okButton.setVisible(true);
        infoScreenTitle.setText(title);
        infoScreenDescription.setText(description);
        infoScreenTitle.setTextFill(Paint.valueOf("ff4343"));
        infoScreenDescription.setTextFill(Paint.valueOf("dcddde"));
        exitScene.setVisible(true);
        exitPane.setVisible(true);
        exitPaneBox.setVisible(true);
        FadeTransition fade = Animation.fadeIn(Duration.seconds(0.3), exitScene);
        fade.play();
        FadeTransition fade3 = Animation.fadeIn(Duration.seconds(0.17), exitPane);;
        fade3.play();
        Animation.salla(exitPane);
    }

    @FXML
    public void minApp() {
        InfumiaLauncher.stage.setIconified(true);
    }

    @FXML
    public void closeApp() {
        Platform.exit();
        try {
            InfumiaLauncher.getInfumiaLauncher().stop();
            System.exit(0);
        }catch (Exception e) {}
    }
}
