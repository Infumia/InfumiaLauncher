package com.infumia.launcher.screens;

import com.infumia.launcher.InfumiaLauncher;
import com.infumia.launcher.animations.Animation;
import com.infumia.launcher.animations.FadeInSceneTransition;
import com.infumia.launcher.animations.MoveYAnimation;
import com.infumia.launcher.download.*;
import com.infumia.launcher.util.JSONUrl;
import com.jfoenix.controls.*;
import com.sun.javafx.PlatformUtil;
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
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.util.Callback;
import javafx.util.Duration;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class HomeParentController implements Initializable {

    private HashMap<String, String> versionList = new HashMap<>();
    private HashMap<String, String> specialVersionList = new HashMap<>();

    private File gamedir = new File(getMineCraftLocation() + "/");
    private File assestdir = new File(getMineCraftLocation() + "/assets/");
    private File objectsdir = new File(getMineCraftLocation() + "/assets/objects");
    private File librarydir = new File(getMineCraftLocation() + "/libraries/");
    private File versionsdir = new File(getMineCraftLocation() + "/versions/");
    private File indexesDir = new File(getMineCraftLocation() + "/assets/indexes/");
    private File logconfigsDir = new File(getMineCraftLocation() + "/assets/log_configs/");

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

    @FXML
    JFXComboBox comboBox;

    @FXML
    JFXSlider ramSlider;

    @FXML
    TextField ramField;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (!gamedir.exists()) gamedir.mkdir();
        if (!assestdir.exists()) assestdir.mkdir();
        if (!librarydir.exists()) librarydir.mkdir();
        String name = Minecraft.playerName;
        playerName.setText(name);
        avatar.setImage(Minecraft.image);
        try {
            JSONObject manifest = JSONUrl.readURL("https://launchermeta.mojang.com/mc/game/version_manifest.json");
            JSONArray versions = manifest.getJSONArray("versions");
            for (int i = 0; i < versions.length(); i++) {
                JSONObject versionObject = versions.getJSONObject(i);
                if (versionObject.getString("type").equals("release")) {
                    versionList.put(versionObject.getString("id"), versionObject.getString("url"));
                }
            }
            JSONObject specialManifest = JSONUrl.readURL("https://infumia.com.tr/clients/data.json");
            JSONArray specialVersions = specialManifest.getJSONArray("clients");
            for (int i = 0; i < specialVersions.length(); i++) {
                JSONObject versionObject = specialVersions.getJSONObject(i);
                specialVersionList.put(versionObject.getString("clientName"), versionObject.getString("clientUrl"));
            }
        } catch (IOException ex) {
            InfumiaLauncher.logger.warn("Sunucuya bağlanılamadı. Yerel sürümler okunuyor.");
            File[] dirs = versionsdir.listFiles(File::isDirectory);
            if (dirs != null) {
                for (File dir : dirs) {
                    if (new File(dir + File.separator + dir.getName() + ".json").exists())
                        versionList.put(dir.getName(), "");
                }
            }
            clientMode.setDisable(true);
        }
//        File[] dirs = versionsdir.listFiles(File::isDirectory);
//        if (dirs != null) {
//            for (File dir : dirs) {
//                if (new File(dir + File.separator + dir.getName() + ".json").exists()
//                        && !specialVersionList.containsKey(dir.getName())
//                        && !versionList.containsKey(dir.getName()))
//                    versionList.put(dir.getName(), "");
//            }
//        }
        JSONObject savedVersion = getVersionStat();
        if (savedVersion != null && !savedVersion.isNull("version") && !savedVersion.isNull("special")) {
            String version = savedVersion.getString("version");
            boolean special = savedVersion.getBoolean("special");
            if (special && specialVersionList.containsKey(version)) {
                loadSpecials();
                clientMode.setSelected(true);
                for (int i = 0; i < comboBox.getItems().size(); i++) {
                    if (((Label)(comboBox.getItems().get(i))).getText().equals(version)) {
                        comboBox.getSelectionModel().select(i);
                    }
                }
            }
            else if (versionList.containsKey(version)) {
                loadNormalVersions();
                clientMode.setSelected(false);
                for (int i = 0; i < comboBox.getItems().size(); i++) {
                    if (((Label)(comboBox.getItems().get(i))).getText().equals(version)) {
                        comboBox.getSelectionModel().select(i);
                    }
                }
            }
        }else {
            loadNormalVersions();
        }

        ramSlider.valueProperty().addListener((observable, oldValue, newValue) ->
                ramField.setText((Math.round(observable.getValue().doubleValue())) + " MB"));
        playerName.requestFocus();

        long memorySize = ((com.sun.management.OperatingSystemMXBean) ManagementFactory
                .getOperatingSystemMXBean()).getTotalPhysicalMemorySize();
        ramSlider.setMax((double) (memorySize / 1000000));

        long freeMemorySize = ((com.sun.management.OperatingSystemMXBean) ManagementFactory
                .getOperatingSystemMXBean()).getFreePhysicalMemorySize();
        double rounded = Math.round((double) freeMemorySize / 1000000) * 0.6;
        ramSlider.setValue(Math.round(rounded / 10) * 10);
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
    Pane progressBarPane;

    @FXML
    JFXCheckBox clientMode;

    @FXML
    public void syncSlider() {
        if (ramField.getText().isEmpty()) return;
        try {
            int formatted = Integer.parseInt(ramField.getText().replaceAll(" MB", ""));
            if (formatted < ramSlider.getMin()) formatted = (int) ramSlider.getMin();
            if (formatted > ramSlider.getMax()) formatted = (int) ramSlider.getMax();
            ramSlider.setValue(formatted);
            ramField.setText(ramField.getText().replaceAll(" MB", "") + " MB");
        }catch (NumberFormatException e) {
            ramSlider.setValue((int) ramSlider.getMin());
            ramField.setText((int) (ramSlider.getMin()) + " MB");
        }
    }

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
        FadeTransition fade3 = Animation.fadeIn(Duration.seconds(0.17), exitPane);
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
            InfumiaLauncher.parent = FXMLLoader.load(
                    getClass().getResource("/parents/FakeParent.fxml")
                    ,
                    null
                    , new JavaFXBuilderFactory());
            Scene scene = InfumiaLauncher.stage.getScene();
            if (scene == null) {
                scene = new Scene(InfumiaLauncher.parent, 1100, 620);
                InfumiaLauncher.stage.setScene(scene);
            } else {
                InfumiaLauncher.stage.getScene().setRoot(InfumiaLauncher.parent);
            }

            Platform.runLater(() -> {
                try {
                    FileWriter writer = new FileWriter(InfumiaLauncher.cacheDir);
                    writer.write("");
                    Parent secondParent = FXMLLoader.load(getClass().getResource("/parents/InfumiaLauncherParent.fxml"));
                    Scene secondScene = new Scene(secondParent);

                    InfumiaLauncher.stage.setScene(secondScene);

                    FadeInSceneTransition sceneTransition = new FadeInSceneTransition(InfumiaLauncher.stage, secondScene, Duration.seconds(0.3));
                    sceneTransition.play();
                    sceneTransition.makeStageDrageable();
                }catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    public void launch(){
        if (comboBox.getValue() == null){
            error("HATA", "Lütfen sürüm seçin.");
            return;
        }

        playButton.setDisable(true);
        exitButton.setDisable(true);
        ramSlider.setDisable(true);
        ramField.setDisable(true);
        comboBox.setDisable(true);
        clientMode.setDisable(true);

        if(!objectsdir.exists())objectsdir.mkdir();
        if(!versionsdir.exists())versionsdir.mkdir();
        if(!indexesDir.exists())indexesDir.mkdir();
        if(!logconfigsDir.exists())logconfigsDir.mkdir();

        saveVersionStat(((Label) (comboBox.getSelectionModel().getSelectedItem())).getText(), clientMode.isSelected());

        Storage storage = new Storage();
        storage.setVersion(((Label)comboBox.getValue()).getText());
        storage.setPrefRAM((int) Math.round(ramSlider.getValue()));

        MoveYAnimation animation = new MoveYAnimation(progressBarPane, progressBarPane.getLayoutY(), 606, Duration.seconds(0.3));
        animation.play();
        AtomicReference<Timeline> animation2 = new AtomicReference<>();
        animation.setOnFinished(event -> {
            animation2.set(new Timeline(
                    new KeyFrame(Duration.millis(1), ((actionEvent) -> {
                        try {
                            if (InfumiaLauncher.step == 1) {
                                if (storage.getAssets() == null) return;
                                double perc = (Double.parseDouble(String.valueOf(storage.getDownloadedAssets())) / (storage.getAssets().length() == 0 ? 1 : storage.getAssets().length())) * 25d;
                                percent.setText("%" + new DecimalFormat("##.#").format(perc).replace(",", "."));
                                progressbar.setProgress(perc / 100d);
                                return;
                            }

                            if (InfumiaLauncher.step == 2) {
                                percent.setText("%" + new DecimalFormat("##.#").format((storage.getClientDownloadPercent() * 0.25d) + 25d).replace(",", "."));
                                progressbar.setProgress(((storage.getClientDownloadPercent() * 0.25d) + 25) / 100d);
                                return;
                            } if (InfumiaLauncher.step == 3) {
                                if (storage.getLibraries() != null) {
                                    double currentDownloaded = storage.getDownloadedLib() + storage.getDownloadedNatives();
                                    double totalFile = storage.getTotalLibraries();
                                    double calc = (((currentDownloaded / totalFile) * 25d) + 75d) / 100d;
                                    percent.setText("%" + new DecimalFormat("##.#").format(calc * 100d).replace(",", "."));
                                    progressbar.setProgress(calc);
                                }
                            }
                            if (InfumiaLauncher.step == 4) {
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
                        }catch (Exception e ){
                            e.printStackTrace();
                        }
                    })
                    )
            ));

            animation2.get().setCycleCount(Timeline.INDEFINITE);
            animation2.get().play();

            if (clientMode.isSelected()) {
                Thread thread = new Thread(new SpecialDownloader(storage, response -> Platform.runLater(() -> {
                    error("HATA", "Dosyalar indirilirken hata oluştu: " + response);
                    MoveYAnimation animation1 = new MoveYAnimation(progressBarPane, progressBarPane.getLayoutY(), 620, Duration.seconds(0.3));
                    animation1.play();
                    playButton.setDisable(false);
                    exitButton.setDisable(false);
                    ramSlider.setDisable(false);
                    ramField.setDisable(false);
                    comboBox.setDisable(false);
                    clientMode.setDisable(false);
                })));

                InfumiaLauncher.executor.schedule(thread, 50, TimeUnit.MILLISECONDS);
            }else {
                Thread thread = new Thread(new MinecraftAssetsDownloader(storage, response -> Platform.runLater(() -> {
                    error("HATA", "Dosyalar indirilirken hata oluştu: " + response);
                    MoveYAnimation animation1 = new MoveYAnimation(progressBarPane, progressBarPane.getLayoutY(), 620, Duration.seconds(0.3));
                    animation1.play();
                    playButton.setDisable(false);
                    exitButton.setDisable(false);
                    ramSlider.setDisable(false);
                    ramField.setDisable(false);
                    comboBox.setDisable(false);
                    clientMode.setDisable(false);
                })));

                InfumiaLauncher.executor.schedule(thread, 50, TimeUnit.MILLISECONDS);
            }
        });
    }

    @FXML
    public void onClientModeChange() {
        if (clientMode.isSelected()) {
            loadSpecials();
        }else {
            loadNormalVersions();
        }
    }

    private void loadSpecials() {
        comboBox.getItems().clear();
        for (String str : specialVersionList.keySet()) {
            Label label = new Label(str);

            label.setTextFill(Paint.valueOf("#00ff00"));
            label.setFont(Font.font(12.0));

            comboBox.getItems().add(label);
        }

        restyleComboBox();
        setCellFactory();
    }

    private void loadNormalVersions() {
        comboBox.getItems().clear();
        List<String> sorted = new ArrayList<>(versionList.keySet());

        sorted.sort((a, b) -> {
            if (a.isEmpty() || b.isEmpty()) return 0;
            if (a.equals(b)) return 0;
            if (a.matches(".*[A-Z]+.*")) return -1;
            if (b.matches(".*[A-Z]+.*")) return 1;
            String[] splittedA = a.replace(".", " ").split(" ");
            String[] splittedB = b.replace(".", " ").split(" ");
            if (splittedA[1].equals(splittedB[1])) {
                if (splittedA.length == 3 && splittedB.length == 3) {
                    if (Integer.parseInt(splittedA[2]) > Integer.parseInt(splittedB[2])) return -1;
                    if (Integer.parseInt(splittedA[2]) < Integer.parseInt(splittedB[2])) return 1;
                }
                if (splittedA.length > splittedB.length) return -1;
                if (splittedA.length < splittedB.length) return 1;
            }
            return Integer.compare(Integer.parseInt(splittedB[1]), Integer.parseInt(splittedA[1]));
        });

        for (String str : sorted) {
            Label label = new Label(str);

            label.setTextFill(Paint.valueOf("#00ff00"));
            label.setFont(Font.font(12.0));

            comboBox.getItems().add(label);
        }

        restyleComboBox();

        setCellFactory();
    }

    private void restyleComboBox() {
        comboBox.setPromptText("Sürüm Seçin");
        comboBox.setStyle("-fx-font-size: 18px;-fx-text-fill: red;");
    }

    private void setCellFactory() {
        comboBox.setCellFactory(new Callback<ListView, ListCell>() {
            @Override
            public ListCell call(ListView param) {
                return new ListCell<Label>() {
                    @Override
                    protected void updateItem(Label item, boolean empty) {
                        super.updateItem(item, empty);
                        setStyle("-fx-font-size: 14px;");
                        if (item == null || empty) {
                            setGraphic(null);
                        }else {
                            setText(item.getText());
                            setTextFill(item.getTextFill());
                            setFont(Font.font(14.0));

                            String versionId = item.getText();
                            if (new File(versionsdir + File.separator + versionId + File.separator + versionId + ".json").exists()) {
                                Image icon = new Image("assets/correct.png");
                                ImageView iconImageView = new ImageView(icon);
                                iconImageView.setFitHeight(18);
                                iconImageView.setPreserveRatio(true);
                                setGraphic(iconImageView);
                            }else {
                                setGraphic(null);
                            }
                        }
                    }
                };
            }
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
        FadeTransition fade3 = Animation.fadeIn(Duration.seconds(0.17), exitPane);
        fade3.play();
        Animation.salla(exitPane);
    }

    private JSONObject getVersionStat() {
        try {
            FileReader fileInputStream = new FileReader(InfumiaLauncher.cacheDir);
            BufferedReader reader = new BufferedReader(fileInputStream);
            String line = reader.readLine();
            JSONObject json = new JSONObject(line);
            return json;
        }catch (Exception e) {
            InfumiaLauncher.logger.info("Cache dosyası bulunamadı. Varsayılan ayarlar yükleniyor.");
            return null;
        }
    }

    private boolean saveVersionStat(String version, boolean isSpecial) {
        try {
            File cacheFile = new File(InfumiaLauncher.cacheDir);
            String line = "{}";
            if (cacheFile.exists()) {
                FileReader fileInputStream = new FileReader(cacheFile);
                BufferedReader reader = new BufferedReader(fileInputStream);
                String output = reader.readLine();
                if (output != null) line = output;
            }
            org.json.simple.JSONObject jsonObject = (org.json.simple.JSONObject) new JSONParser().parse(line);
            jsonObject.put("version", version);
            jsonObject.put("special", isSpecial);
            try (FileWriter writer = new FileWriter(InfumiaLauncher.cacheDir)) {
                writer.write(jsonObject.toString());
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public String getMineCraftLocation() {
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
        }catch (Exception ignored) {}
    }
}
