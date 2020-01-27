package com.infumia.launcher;

import com.infumia.launcher.animations.FadeInSceneTransition;
import com.infumia.launcher.download.Minecraft;
import com.infumia.launcher.objects.AuthThread;
import com.sun.javafx.PlatformUtil;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.Base64;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class InfumiaLauncher extends Application {

    public static Stage stage;
    public static Parent parent;

    public static int step = 1;

    public static String cacheDir = getMineCraftLocation() + "/infumia-usercache.json";
    public static String photoCacheDir = getMineCraftLocation() + "/avatar.png";

    private static final InfumiaLauncher infumiaLauncher = new InfumiaLauncher();

    public static Logger logger = LogManager.getLogger("log4j2.xml");

    public static ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);

    @Override
    public void start(Stage stage) throws Exception {
        logger.info("Launcher başlatılıyor...");

        InfumiaLauncher.stage = stage;

        stage.setTitle("Infumia Launcher v1.0-ALPHA");

        File cacheFile = new File(cacheDir);
        InfumiaLauncher.logger.info("Çerezler okunuyor");
        if (!cacheFile.exists()) {
            loadLauncherParent();
            logger.info("Infumia Launcher başlatıldı.");
            return;
        }

        FileReader fileInputStream = new FileReader(cacheFile);
        BufferedReader reader = new BufferedReader(fileInputStream);
        String line = reader.readLine();
        if (line == null || line.isEmpty()) {
            InfumiaLauncher.logger.info("Çerez bulunamadı giriş sayfası yükleniyor");
            loadLauncherParent();
            return;
        }

        JSONObject jsonObject = new JSONObject(line);
        if (jsonObject.isNull("username")) return;
        InfumiaLauncher.logger.info("Bir hesap bulundu");
        if (!jsonObject.isNull("password") && !jsonObject.isNull("e-mail")) {
            String password = new String(Base64.getDecoder().decode((String) jsonObject.get("password")));
            InfumiaLauncher.logger.info("Hesap bilgileri için istek gönderiliyor");
            AuthThread authThread = new AuthThread((String) jsonObject.get("e-mail"), password, response -> {
                if (response.equals("-1")) {
                    InfumiaLauncher.logger.info("Hesap bulunamadı giriş sayfası açılıyor");
                    Platform.runLater(() -> {
                        loadLauncherParent();
                    });
                    return;
                }
                if (!response.contains("accessToken") || !response.contains("selectedProfile")) {
                    InfumiaLauncher.logger.info("Profil bulunamadı giriş sayfası açılıyor");
                    Platform.runLater(() -> {
                        loadLauncherParent();
                    });
                    return;
                }
                InfumiaLauncher.logger.info("Hesap onaylandı");
                JSONObject responJson = new JSONObject(response);
                String accessToken = (String) responJson.get("accessToken");
                String profileName = (String) responJson.getJSONObject("selectedProfile").get("name");
                String uuid = (String) responJson.getJSONObject("selectedProfile").get("id");
                if (!accessToken.isEmpty() && !profileName.isEmpty() && !uuid.isEmpty()) {
                    InfumiaLauncher.logger.info("Kullanıcı bilgileri yükleniyor");
                    Minecraft.playerName = profileName;
                    Minecraft.accessToken = accessToken;
                    Minecraft.uuid = uuid;
                    InfumiaLauncher.logger.info("Accesstoken ve uuid ayarlandı");
                    loadHomeParent();
                }
            });
            authThread.start();
        } else {
            Minecraft.playerName = (String) jsonObject.get("username");
            loadHomeParent();
        }

    }

    void loadHomeParent() {
        try {
            File avatar = new File(photoCacheDir);
            if (!avatar.exists()) {
                InfumiaLauncher.logger.info("Kullanıcı avatarı indiriliyor");
                URL url = new URL("https://minotar.net/avatar/" + Minecraft.playerName);
                BufferedImage c = ImageIO.read(url);
                InfumiaLauncher.logger.info("Avatar indirildi");
                Minecraft.image = SwingFXUtils.toFXImage(c, null);
            }else {
                BufferedImage image = ImageIO.read(avatar);
                if (image.getWidth() != 180 || image.getHeight() != 180) {
                    InfumiaLauncher.logger.info("Kullanıcı avatarı indiriliyor");
                    URL url = new URL("https://minotar.net/avatar/" + Minecraft.playerName);
                    BufferedImage c = ImageIO.read(url);
                    InfumiaLauncher.logger.info("Avatar indirildi");
                    Minecraft.image = SwingFXUtils.toFXImage(c, null);
                } else {
                    Minecraft.image = SwingFXUtils.toFXImage(ImageIO.read(avatar), null);
                }
            }
            InfumiaLauncher.logger.info("Avatar ayarlandı");
        } catch (Exception ex) {
            InfumiaLauncher.logger.info("Avatar indirilemedi varsayılan avatar kullanılacak");
            Minecraft.image = new Image("assets/steve.png");
        }
        Platform.runLater(() -> {
            try {
                InfumiaLauncher.logger.info("Ana sayfa yükleniyor");
                parent = FXMLLoader.load(InfumiaLauncher.class.getResource("screens/InfumiaHomeParent.fxml"));

                Scene scene = new Scene(parent);
                stage.setScene(scene);

                stage.initStyle(StageStyle.TRANSPARENT);
                stage.getIcons().add(new Image("assets/infumia-logo.png"));

                stage.show();
                stage.requestFocus();

                FadeInSceneTransition sceneTransition = new FadeInSceneTransition(stage,scene, Duration.seconds(1));
                sceneTransition.play();
                sceneTransition.loadNextScene();
                InfumiaLauncher.logger.info("Ana sayfa yüklendi");
            } catch (IOException e) {
                InfumiaLauncher.logger.info("Sahne değişimi yapılırken hata oluştu:");
                e.printStackTrace();
            }
        });
    }

    private static String getMineCraftLocation() {
        if (PlatformUtil.isWindows()) {
            return (System.getenv("APPDATA") + "/.infumia");
        }
        if (PlatformUtil.isLinux()) {
            return (System.getProperty("user.home") + "/.infumia");
        }
        if (PlatformUtil.isMac()) {
            return (System.getProperty("user.home") + "/Library/Application Support/infumia");
        }
        return "N/A";
    }

    void loadLauncherParent() {
        try {
            parent = FXMLLoader.load(InfumiaLauncher.class.getResource("screens/InfumiaLauncherParent.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene scene = new Scene(parent);
        stage.setScene(scene);

        stage.initStyle(StageStyle.TRANSPARENT);
        stage.getIcons().add(new Image("assets/infumia-logo.png"));

        stage.show();
        stage.requestFocus();

        FadeInSceneTransition sceneTransition = new FadeInSceneTransition(stage,scene, Duration.seconds(1));
        sceneTransition.play();
        sceneTransition.loadNextScene();

    }

    public static InfumiaLauncher getInfumiaLauncher() {
        return infumiaLauncher;
    }

    public static void main() {
        String[] args = {};
        launch(args);
    }

}
