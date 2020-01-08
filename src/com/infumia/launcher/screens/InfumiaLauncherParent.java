package com.infumia.launcher.screens;

import com.infumia.launcher.InfumiaLauncher;
import com.infumia.launcher.animations.Animation;
import com.infumia.launcher.animations.FadeInSceneTransition;
import com.infumia.launcher.animations.MarginAnimation;
import com.infumia.launcher.animations.ShakeTransition;
import com.infumia.launcher.download.Minecraft;
import com.infumia.launcher.objects.AuthThread;
import com.infumia.launcher.objects.Callback;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.util.Duration;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.ResourceBundle;

public class InfumiaLauncherParent implements Initializable {

    boolean animating = false;

    boolean canSee = false;

    @FXML
    JFXButton loginBtn;

    @FXML
    Label copyright;

    @FXML
    HBox exitPaneBox;

    @FXML
    Pane exitScene;

    @FXML
    Pane exitPane;

    @FXML
    Pane usernamePane;

    @FXML
    JFXCheckBox premiumCheckBox;

    @FXML
    Pane passwordPane;

    @FXML
    Label about;

    @FXML
    ImageView logo;

    @FXML
    ImageView launcher;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (copyright!= null) {
            copyright.setTextFill(Paint.valueOf("dcddde"));
            copyright.setText(
                    "Versiyon: 1.0.0 (Released) \n" +
                            "Kodlama: Dantero\n" +
                            "Tasarım: Dantero\n\n" +
                            "© 2019 - " + Calendar.getInstance().get(Calendar.YEAR) + " Infumia. Tüm hakları saklıdır.\n" +
                            "Infumia markası adı altında üretilmiştir.\n" +
                            "Bu yazılımın izinsiz ticari veya ticari olmayan amaçla \nkopyalanması, düzenlenmesi ve dağıtılması yasaktır.\n"
            );
        }
    }

    @FXML
    public void premiumMode() {
        if (premiumCheckBox.isSelected()) {
            FadeTransition fadeTransition = Animation.fadeIn(Duration.seconds(0.3), passwordPane);
            fadeTransition.setOnFinished(event -> premiumCheckBox.setDisable(false));
            fadeTransition.play();
            premiumCheckBox.setDisable(true);
            Animation.salla(passwordPane);
            passwordPane.setVisible(true);
        }else {
            FadeTransition fadeTransition = Animation.fadeOut(Duration.seconds(0.5), passwordPane);
            ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(0.04), passwordPane);
            scaleTransition.setFromX(1.0);
            scaleTransition.setFromY(1.0);
            scaleTransition.setToX(0.99);
            scaleTransition.setToY(0.99);
            scaleTransition.play();
            premiumCheckBox.setDisable(true);

            FadeTransition fade3 = Animation.fadeOut(Duration.seconds(0.25), passwordPane);
            fade3.play();

            scaleTransition.setOnFinished(event -> {
                ScaleTransition scaleTransition2 = new ScaleTransition(Duration.seconds(0.08), passwordPane);
                scaleTransition2.setFromX(0.99);
                scaleTransition2.setFromY(0.99);
                scaleTransition2.setToX(1.03);
                scaleTransition2.setToY(1.03);
                scaleTransition2.play();


                scaleTransition2.setOnFinished(event1 -> {
                    ScaleTransition scaleTransition3 = new ScaleTransition(Duration.seconds(0.15), passwordPane);
                    scaleTransition3.setFromX(1.03);
                    scaleTransition3.setFromY(1.03);
                    scaleTransition3.setToX(0.2);
                    scaleTransition3.setToY(0.2);
                    scaleTransition3.play();
                    scaleTransition3.setOnFinished(event2 ->  {
                        passwordPane.setVisible(false);
                        premiumCheckBox.setDisable(false);
                    });
                });
            });
            fadeTransition.play();

        }
    }

    @FXML
    public void openAbout() {
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
    public void goBack() {
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
    public void minApp() {
        InfumiaLauncher.stage.setIconified(true);
    }

    @FXML
    public void closeApp() {
        Platform.exit();
        try {
            InfumiaLauncher.getInfumiaLauncher().stop();
        }catch (Exception e) {}
    }

    @FXML
    public void onClick(){
        Label logstat = (Label) InfumiaLauncher.stage.getScene().lookup("#logstat");
        JFXTextField username = (JFXTextField) InfumiaLauncher.stage.getScene().lookup("#username");
        JFXPasswordField password = (JFXPasswordField) InfumiaLauncher.stage.getScene().lookup("#password");

        if (username.getText().isEmpty()) {
            errorText("Kullanıcı adı boş olamaz.");
            canSee =true;
            return;
        }

        if (username.getText().length() < 4) {
            errorText("Lütfen 4 karakterden uzun isim girin.");
            canSee =true;
            return;
        }

        if (premiumCheckBox.isSelected() && password.getText().isEmpty()) {
            errorText("Şifre boş olamaz.");
            canSee =true;
            return;
        }

        loginBtn.setDisable(true);
        premiumCheckBox.setDisable(true);
        String style = loginBtn.styleProperty().get();
        String[] splitted = style.split(";");
        splitted[0] = "-fx-background-color: rgba(196, 144, 21,0.4)";
        String newStyle = "";
        for (String str : splitted) newStyle += str + ";";
        loginBtn.setStyle(newStyle);
        infoText("Giriş yapılıyor...");

        InfumiaLauncher.logger.info("Giriş yapılıyor");

        logstat.requestFocus();

        Thread goNextScene = new Thread() {
            @Override
            public void run() {
                try {
                    InfumiaLauncher.logger.info("Kullanıcı avatarı indiriliyor");
                    URL url = new URL("https://minotar.net/avatar/" + Minecraft.playerName);
                    BufferedImage c = ImageIO.read(url);
                    InfumiaLauncher.logger.info("Avatar indirildi");
                    Minecraft.image = SwingFXUtils.toFXImage(c, null);
                    InfumiaLauncher.logger.info("Avatar ayarlandi");
                }catch (Exception ex) {
                    InfumiaLauncher.logger.info("Avatar indirilemedi. Varsayılan avatar kullanılacak.");
                    Minecraft.image = new Image("assets/steve.png");
                }

                try{
                    InfumiaLauncher.logger.info("Sahne değişimi için hazırlanılıyor");
                    InfumiaLauncher.parent = FXMLLoader.load(getClass().getResource("FakeParent.fxml"), null, new JavaFXBuilderFactory());
                    Scene scene = InfumiaLauncher.stage.getScene();
                    if (scene == null) {
                        InfumiaLauncher.logger.info("Sahte sahne yüklenemedi. Manuel oluşturuluyor.");
                        scene = new Scene(InfumiaLauncher.parent, 1100, 620);
                        InfumiaLauncher.stage.setScene(scene);
                        InfumiaLauncher.logger.info("Oluşturuldu ve yüklendi.");
                    }else {
                        InfumiaLauncher.logger.info("Sahte sahne yüklendi.");
                        InfumiaLauncher.stage.getScene().setRoot(InfumiaLauncher.parent);
                    }


                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                InfumiaLauncher.logger.info("Asıl sahne yükleniyor.");
                                Parent secondParent = FXMLLoader.load(getClass().getResource("InfumiaHomeParent.fxml"));
                                Scene secondScene = new Scene(secondParent);

                                InfumiaLauncher.stage.setScene(secondScene);
                                InfumiaLauncher.logger.info("Yüklendi.");
                                InfumiaLauncher.logger.info("Geçiş animasyonu ayarlanıyor");
                                FadeInSceneTransition sceneTransition = new FadeInSceneTransition(InfumiaLauncher.stage, secondScene, Duration.seconds(0.3));
                                InfumiaLauncher.logger.info("Ayarlandı");
                                sceneTransition.play();
                                InfumiaLauncher.logger.info("Geçiş animasyonu başlatıldı");
                                sceneTransition.makeStageDrageable();
                                InfumiaLauncher.logger.info("Ekran sürüklenebilir yapıldı");
                            }catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                } catch (IOException e) {
                    InfumiaLauncher.logger.info("Sahne değişimi yapılırken hata oluştu:" + e.toString());
                }
            }
        };

        if (premiumCheckBox.isSelected()) {
            try {
                InfumiaLauncher.logger.info("Premium hesap bilgileri için hazırlanılıyor");
                AuthThread authThread = new AuthThread(username.getText(), password.getText(), response -> {
                    InfumiaLauncher.logger.info("Yanıt geldi: " + response);
                    if (response.equals("-1")) {
                        Platform.runLater(()-> {
                            errorText("Kullanıcı adı veya şifre yanlış.");
                            canSee = true;
                            loginBtn.setDisable(false);
                            premiumCheckBox.setDisable(false);
                            splitted[0] = "-fx-background-color: rgba(196, 144, 21, 1.0)";
                            String astyle = "";
                            for (String str : splitted) astyle += str + ";";
                            loginBtn.setStyle(astyle);
                        });
                        return;
                    }

                    if (!response.contains("accessToken") || !response.contains("selectedProfile")) {
                        Platform.runLater(()-> {
                            errorText("Bu hesaba ait bir premium hesap bulunamadı.");
                            canSee = true;
                            loginBtn.setDisable(false);
                            premiumCheckBox.setDisable(false);
                            splitted[0] = "-fx-background-color: rgba(196, 144, 21, 1.0)";
                            String astyle = "";
                            for (String str : splitted) astyle += str + ";";
                            loginBtn.setStyle(astyle);
                        });
                        return;
                    }
                    InfumiaLauncher.logger.info("Hesap onaylandı.");
                    JSONObject jsonObject = new JSONObject(response);
                    String accessToken = (String) jsonObject.get("accessToken");
                    String profileName = (String) jsonObject.getJSONObject("selectedProfile").get("name");
                    String uuid = (String) jsonObject.getJSONObject("selectedProfile").get("id");
                    if (!accessToken.isEmpty() && !profileName.isEmpty() && !uuid.isEmpty()) {
                        Minecraft.playerName = profileName;
                        Minecraft.accessToken = accessToken;
                        Minecraft.uuid = uuid;
                        Platform.runLater(() -> {
                            succText("Lütfen bekleyin.");
                            logstat.requestFocus();
                        });
                        InfumiaLauncher.logger.info("Diğer sahne yükleniyor");
                        goNextScene.start();
                    }else {
                        errorText("Kullanıcı adı veya şifre yanlış.");
                        canSee = true;
                        loginBtn.setDisable(false);
                        premiumCheckBox.setDisable(false);
                        splitted[0] = "-fx-background-color: rgba(196, 144, 21, 1.0)";
                        String astyle = "";
                        for (String str : splitted) astyle += str + ";";
                        loginBtn.setStyle(astyle);
                    }
                });
                authThread.start();
            } catch (Exception e) {
                e.printStackTrace();
                errorText("Bir hata oluştu");
                canSee = true;
                loginBtn.setDisable(false);
                premiumCheckBox.setDisable(false);
                splitted[0] = "-fx-background-color: rgba(196, 144, 21, 1.0)";
                String astyle = "";
                for (String str : splitted) astyle += str + ";";
                loginBtn.setStyle(astyle);
            }
        }else {
            Minecraft.playerName = username.getText();
            succText("Lütfen bekleyin.");
            logstat.requestFocus();
            goNextScene.start();
            InfumiaLauncher.logger.info("Kırılmış mod ile diğer sahne yükleniyor");
        }
    }


    public void infoText(String text) {
        Label logstat = (Label) InfumiaLauncher.stage.getScene().lookup("#logstat");

        logstat.setAlignment(Pos.CENTER);
        logstat.setText(text);
        logstat.setTextFill(Paint.valueOf("36b9cc"));
        logstat.setVisible(true);

        double width = logstat.getWidth();
        logstat.setLayoutX((1100 - width) / 2);
        Animation.fadeIn(Duration.seconds(0.18), logstat).play();
    }

    public void succText(String text) {
        Label logstat = (Label) InfumiaLauncher.stage.getScene().lookup("#logstat");

        logstat.setAlignment(Pos.CENTER);
        logstat.setText(text);
        logstat.setTextFill(Paint.valueOf("6aff44"));
        logstat.setVisible(true);

        double width = logstat.getWidth();
        logstat.setLayoutX((1100 - width) / 2);
        Animation.fadeIn(Duration.seconds(0.18), logstat).play();
    }


    public void errorText(String text) {
        if (animating) return;
        Label logstat = (Label) InfumiaLauncher.stage.getScene().lookup("#logstat");
        if (!text.equals(logstat.getText())) canSee = false;

        logstat.setAlignment(Pos.CENTER);

        logstat.setText(text);
        logstat.setTextFill(Paint.valueOf("ff4343"));
        logstat.setVisible(true);

        if (!canSee) Animation.fadeIn(Duration.seconds(0.23), logstat).play();
        animating = true;
        ShakeTransition shake = Animation.shake(logstat);
        shake.setOnFinished(event -> animating = false);
        shake.playFromStart();
    }
}
