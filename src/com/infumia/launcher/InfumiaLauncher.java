package com.infumia.launcher;

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

import com.infumia.launcher.animations.FadeInSceneTransition;
import com.infumia.launcher.download.Minecraft;
import com.infumia.launcher.objects.AuthThread;
import com.infumia.launcher.util.Metrics;
import com.sun.javafx.PlatformUtil;
import com.sun.javafx.application.LauncherImpl;
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
import javax.xml.bind.DatatypeConverter;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigInteger;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class InfumiaLauncher extends Application {


    public static final String launcherFolderName = "infumia";

    public static Stage stage;
    public static Parent parent;

    public static int step = 1;

    public static String cacheDir = getMineCraftLocation() + "/infumia-usercache.json";
    public static String photoCacheDir = getMineCraftLocation() + "/avatar.png";

    private static final InfumiaLauncher infumiaLauncher = new InfumiaLauncher();

    public static Logger logger = LogManager.getLogger("log4j2.xml");

    public static ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);

    public InfumiaLauncher() {

    }

    @Override
    public void start(Stage stage) throws Exception {
        logger.info("Launcher başlatılıyor...");

        Metrics metrics = new Metrics(6573);

        InfumiaLauncher.stage = stage;

        File minecraftDir = new File(getMineCraftLocation());
        if (!minecraftDir.exists()) {
            try {
                minecraftDir.mkdir();
            }catch (Exception e) {
                e.printStackTrace();
            }
        }

        stage.setTitle("Infumia Launcher");

        File cacheFile = new File(cacheDir);
        InfumiaLauncher.logger.info("Çerezler okunuyor");
        if (!cacheFile.exists()) {
            logger.info("Çerez bulunamadı. Infumia Launcher başlatılıyor.");
            loadLauncherParent();
            logger.info("Infumia Launcher başlatıldı.");
            return;
        }

        FileReader fileInputStream = new FileReader(cacheFile);
        BufferedReader reader = new BufferedReader(fileInputStream);
        String line = reader.readLine();
        JSONObject jsonObject = line == null ? new JSONObject() : new JSONObject(line);

        if (line == null || line.isEmpty()) {
            InfumiaLauncher.logger.info("Çerez bulunamadı giriş sayfası yükleniyor");
            loadLauncherParent();
            return;
        }

        if (jsonObject.isNull("username")) {
            InfumiaLauncher.logger.info("Çerez bulunamadı giriş sayfası yükleniyor");
            loadLauncherParent();
            return;
        }

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
                parent = FXMLLoader.load(getClass().getResource("/parents/InfumiaHomeParent.fxml"));
                Scene scene = new Scene(parent);
                stage.setScene(scene);

                stage.initStyle(StageStyle.TRANSPARENT);
                stage.getIcons().add(new Image("assets/infumia-logo.png"));
                logger.info("OK!");
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

    void loadLauncherParent() {
        try {
            parent = FXMLLoader.load(getClass().getResource("/parents/InfumiaLauncherParent.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene scene = new Scene(parent);
        stage.setScene(scene);

        stage.initStyle(StageStyle.TRANSPARENT);
        stage.getIcons().add(new Image("assets/infumia-logo.png"));
        logger.info("OK!");
        stage.show();
        stage.requestFocus();

        FadeInSceneTransition sceneTransition = new FadeInSceneTransition(stage,scene, Duration.seconds(1));
        sceneTransition.play();
        sceneTransition.loadNextScene();

    }

    public static InfumiaLauncher getInfumiaLauncher() {
        return infumiaLauncher;
    }

    public static String getMineCraftLocation() {
        if (PlatformUtil.isWindows()) {
            return (System.getenv("APPDATA") + File.separator + "." + launcherFolderName);
        }
        if (PlatformUtil.isLinux()) {
            return (System.getProperty("user.home") + File.separator + "." + launcherFolderName);
        }
        if (PlatformUtil.isMac()) {
            return (System.getProperty("user.home") + File.separator + "Library" + File.separator + "Application Support"+ File.separator + launcherFolderName);
        }
        return "N/A";
    }

    public void main(String[] args) {
        String a = "-YnUgxZ9pZnJleWkgw6fDtnplYmlsZW5lIGhlbGFsIG9sc3VuLiBBeXLEsWNhIGFubmVuaXppIHMqa2VyIGhheWF0xLFuxLF6ZGEgYmHFn2FyxLFsYXIgZGlsZXJpbS4gU2lrZXJsZXIgOik=.dW51dG1hZGFu.aGVsYWwgxZ9pZnJleWkgYnVsZHVuIChuYWgpIGtuaw==";

        try {
            String[] fake = {};
            if (args.length == 0) return;
            String sha1 = calcSHA1(new File(getMineCraftLocation() + File.separator + "launcher.jar"));
//            if (args[0].equals(a.substring(0,16) + md5(sha1).toLowerCase() + a.substring(16)))  {
//                System.setProperty("http.agent", "Mozilla/5.0");
//                LauncherImpl.launchApplication(this.getClass(), fake);
//            }
            System.setProperty("http.agent", "Mozilla/5.0");
            LauncherImpl.launchApplication(this.getClass(), fake);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String md5(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(input.getBytes());
        byte[] digest = md.digest();
        String myChecksum = DatatypeConverter
                .printHexBinary(digest).toUpperCase();
        return myChecksum;
    }

    private String calcSHA1(File file) throws IOException, NoSuchAlgorithmException {
        MessageDigest sha1 = MessageDigest.getInstance("SHA-1");

        if (!file.exists()) return "";

        try (InputStream input = new FileInputStream(file)) {

            byte[] buffer = new byte[8192];
            int len = input.read(buffer);

            while (len != -1) {
                sha1.update(buffer, 0, len);
                len = input.read(buffer);
            }

            byte[] digest = sha1.digest();

            return String.format("%0" + (digest.length << 1) + "x", new BigInteger(1,
                    digest));
        }
    }

}
