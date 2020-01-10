package com.infumia.launcher.download;

import com.infumia.launcher.InfumiaLauncher;
import com.infumia.launcher.objects.Callback;
import com.infumia.launcher.util.JSONUrl;
import com.infumia.launcher.util.Utils;
import com.sun.javafx.PlatformUtil;
import javafx.application.Platform;
import org.json.JSONArray;
import org.kamranzafar.jddl.DirectDownloader;
import org.kamranzafar.jddl.DownloadListener;
import org.kamranzafar.jddl.DownloadTask;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
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

    public MinecraftAssetsDownloader(String version, Callback errorCallback) {
        this.errorCallback = errorCallback;
        this.version = version;
        objectsDir = new File(getMineCraftLocation() + "/assets/objects/");
        indexesDir = new File(getMineCraftLocation() + "/versions/" + version + "/");
        logconfigsDir = new File(getMineCraftLocation() + "/assets/log_configs/");
        indexes = new File(indexesDir, version + ".json");
        xmlFile = new File(logconfigsDir + "/" +"client-" + version + ".xml");
    }

    public static int currentfile=0;


    public static JSONObject objects = null;

    private String versionUrl;
    public static JSONObject versionObject;

    public void run() {
        try {
            Utils utils = new Utils();
            String OperatingSystemToUse = utils.getOS();
            if (objects == null) {
                JSONObject manifest = JSONUrl.readURL("https://launchermeta.mojang.com/mc/game/version_manifest.json");
                JSONArray versions = manifest.getJSONArray("versions");
                for (int i = 0; i < versions.length(); i++) {
                    if (versions.getJSONObject(i).getString("id").equals(version)) {
                        versionUrl = versions.getJSONObject(i).getString("url");
                        versionObject = JSONUrl.readURL(versionUrl);
                        JSONObject readedUrl = JSONUrl.readURL(versionObject.getJSONObject("assetIndex").getString("url"));
                        objects = readedUrl.getJSONObject("objects");
                        try (FileWriter file = new FileWriter(utils.getMineCraftAssetsIndexes_X_json(OperatingSystemToUse, versionObject.getJSONObject("assetIndex").getString("id")))) {
                            file.write(readedUrl.toString());
                            file.flush();
                        }
                        break;
                    }
                }
            }
            if (!indexes.exists()) {
                if (!indexesDir.exists()) indexesDir.mkdir();
                indexes.createNewFile();
                try (FileWriter file = new FileWriter(indexes)) {
                    file.write(JSONUrl.readURL(versionUrl).toString());
                    file.flush();
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
                Thread thread = new Thread(()-> {
                    new MinecraftClientDownloader(versionObject.getJSONObject("downloads").getJSONObject("client").getString("url"), version, errorCallback).run();
                });
                InfumiaLauncher.executor.schedule(thread, 1, TimeUnit.MILLISECONDS);
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
            errorCallback.response(e.toString());
        }

        String url = "http://resources.download.minecraft.net/" + getHash(currentfile).substring(0, 2) + "/" + getHash(currentfile);
        File direc = new File(objectsDir + "/" + getHash(currentfile).substring(0, 2) + "/");
        if (!direc.exists()) direc.mkdir();

        File file = new File(objectsDir + "/" + getHash(currentfile).substring(0, 2) + "/" + getHash(currentfile));

        try {
            if (file.exists()) {
                if (getSize(currentfile) == file.length()) {
                    System.out.print("\r");
                    System.out.print("Dosya zaten var diger dosyaya geciliyor. " + currentfile + "/" + objects.length());
                    currentfile++;
                    Platform.runLater(()-> run());
                    return;
                }
            }

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
                            currentfile++;
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