package com.infumia.launcher.download;

import com.infumia.launcher.InfumiaLauncher;
import com.infumia.launcher.objects.Callback;
import com.infumia.launcher.util.JSONUrl;
import com.sun.javafx.PlatformUtil;
import javafx.application.Platform;
import org.json.JSONArray;
import org.json.simple.parser.JSONParser;
import org.kamranzafar.jddl.DirectDownloader;
import org.kamranzafar.jddl.DownloadListener;
import org.kamranzafar.jddl.DownloadTask;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

public class MinecraftAssetsDownloader implements Runnable {

    private Callback errorCallback;
    private String version;
    private File objectsDir;
    private File indexesDir;
    private File logconfigsDir;
    private File indexes;
    private File xmlFile;
    private Storage storage;

    public MinecraftAssetsDownloader(Storage storage, Callback errorCallback) {
        this.errorCallback = errorCallback;
        this.storage = storage;
        this.version = storage.getVersion();
        storage.setOperationgSystem(storage.getUtils().getOS());
        objectsDir = new File(getMineCraftLocation() + "/assets/objects/");
        indexesDir = new File(getMineCraftLocation() + "/versions/" + version + "/");
        logconfigsDir = new File(getMineCraftLocation() + "/assets/log_configs/");
        indexes = new File(indexesDir, version + ".json");
        xmlFile = new File(logconfigsDir + "/" +"client-" + version + ".xml");
    }

    int currentfile=0;

    private JSONObject objects = null;

    private String versionUrl;
    private JSONObject versionObject;

    public void run() {
        try {
            if (objects == null) {
                try {
                    JSONObject manifest = JSONUrl.readURL("https://launchermeta.mojang.com/mc/game/version_manifest.json");
                    JSONArray versions = manifest.getJSONArray("versions");
                    for (int i = 0; i < versions.length(); i++) {
                        if (versions.getJSONObject(i).getString("id").equals(version)) {
                            versionUrl = versions.getJSONObject(i).getString("url");
                            versionObject = JSONUrl.readURL(versionUrl);
                            storage.setVersionObject(versionObject);
                            JSONObject readedUrl = JSONUrl.readURL(versionObject.getJSONObject("assetIndex").getString("url"));
                            objects = readedUrl.getJSONObject("objects");
                            storage.setAssets(objects);
                            try (FileWriter file = new FileWriter(storage.getUtils().getMineCraftAssetsIndexes_X_json(storage.getOperationgSystem(), versionObject.getJSONObject("assetIndex").getString("id")))) {
                                file.write(readedUrl.toString());
                                file.flush();
                            }
                            break;
                        }
                    }
                }catch (Exception e) {
                    try
                    {
                        String assetsIndexId = storage.getLocal().readJson_assets(storage.getUtils().getMineCraft_Versions_X_X_json(storage.getOperationgSystem(), version));
                        File file = new File(storage.getUtils().getMineCraftAssetsIndexes_X_json(storage.getOperationgSystem(), assetsIndexId));
                        if (!file.exists()) {
                            errorCallback.response("İnternet bağlantısı gerekiyor.");
                            return;
                        }
                        versionObject = new JSONObject(new String(Files.readAllBytes(Paths.get(indexes.getPath()))));
                        storage.setVersionObject(versionObject);
                        JSONObject obj = new JSONObject(new String(Files.readAllBytes(Paths.get(file.getPath()))));
                        objects = obj.getJSONObject("objects");
                        storage.setAssets(objects);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        errorCallback.response(ex.toString());
                        return;
                    }
                }
            }
            if (!indexes.exists()) {
                if (!indexesDir.exists()) indexesDir.mkdir();
                indexes.createNewFile();
                try (FileWriter file = new FileWriter(indexes)) {
                    file.write(JSONUrl.readURL(versionUrl).toString());
                    file.flush();
                }catch (IOException e) {
                    errorCallback.response("İnternet bağlantısı gerekiyor.");
                }
            }
            if (!xmlFile.exists()) {
                String url = versionObject.getJSONObject("logging").getJSONObject("client").getJSONObject("file").getString("url");
                URL oracle = new URL(url);
                BufferedReader in = new BufferedReader(new InputStreamReader(oracle.openStream()));
                FileWriter file = new FileWriter(xmlFile);
                String inputLine;
                while ((inputLine = in.readLine()) != null)
                    file.write(inputLine + "\n");
                file.flush();
            }
            if (currentfile == objects.length()) {
                System.out.print("\r");
                InfumiaLauncher.logger.info("Assets indirme islemi bitti.");
                InfumiaLauncher.logger.info("Client indirme islemi baslatiliyor");
                InfumiaLauncher.step++;

                storage.setClientUrl(versionObject.getJSONObject("downloads").getJSONObject("client").getString("url"));

                Thread thread = new Thread(()-> {
                    new MinecraftClientDownloader(storage, errorCallback).run();
                });
                InfumiaLauncher.executor.schedule(thread, 1, TimeUnit.MILLISECONDS);
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
            errorCallback.response(e.toString());
        }

        File direc = new File(objectsDir + "/" + getHash(currentfile).substring(0, 2) + "/");
        if (!direc.exists()) direc.mkdir();

        File file = new File(objectsDir + "/" + getHash(currentfile).substring(0, 2) + "/" + getHash(currentfile));

        try {
            if (file.exists()) {
                if (getSize(currentfile) == file.length()) {
                    System.out.print("\r");
                    System.out.print("Dosya zaten var diger dosyaya geciliyor. " + currentfile + "/" + objects.length());
                    storage.setDownloadedAssets(++currentfile);
                    Platform.runLater(()-> run());
                    return;
                }
            }

            String url = "http://resources.download.minecraft.net/" + getHash(currentfile).substring(0, 2) + "/" + getHash(currentfile);

            DirectDownloader dd = new DirectDownloader();
            try {
                dd.download(new DownloadTask(new URL(url), new FileOutputStream(objectsDir + "/" + getHash(currentfile).substring(0, 2) + "/" + getHash(currentfile)), new DownloadListener() {

                    String fname;
                    double fileSize = 0;

                    @Override
                    public void onStart(String fname, int fsize) {
                        this.fname = fname;
                        fileSize = fsize;
                    }

                    @Override
                    public void onUpdate(int bytes, int totalDownloaded) {
                        double t1 = totalDownloaded + 0.0d;
                        double t2 = fileSize + 0.0d;
                        double downloadpercent = (t1 / t2) * 100.0d;
                        System.out.print("\r" + ">Indiriliyor " + fname + " %" + new DecimalFormat("##.#").format(downloadpercent).replace(",", ".") + " " + currentfile + "/" + objects.length());
                    }

                    @Override
                    public void onComplete() {
                        try {
                            storage.setDownloadedAssets(++currentfile);
                            run();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancel() {

                    }
                }));
            } catch (Exception e) {
                e.printStackTrace();
            }
            Thread t = new Thread(dd);
            t.start();
        }catch (Exception e) {
            e.printStackTrace();
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

    private String getHash(int size){
        return objects.getJSONObject((String) objects.names().get(size)).getString("hash");
    }

    private int getSize(int size){
        return objects.getJSONObject((String) objects.names().get(size)).getInt("size");
    }

}