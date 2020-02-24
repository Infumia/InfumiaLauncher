package com.infumia.launcher.download;

import com.infumia.launcher.InfumiaLauncher;
import com.infumia.launcher.objects.Callback;
import com.infumia.launcher.util.JSONUrl;
import com.sun.javafx.PlatformUtil;
import javafx.application.Platform;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.kamranzafar.jddl.DirectDownloader;
import org.kamranzafar.jddl.DownloadListener;
import org.kamranzafar.jddl.DownloadTask;
import org.json.JSONObject;

import java.io.*;
import java.math.BigInteger;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

public class SpecialDownloader implements Runnable {

    private Callback errorCallback;
    private String version;
    private File objectsDir;
    private File indexesDir;
    private File logconfigsDir;
    private File indexes;
    private File xmlFile;
    private Storage storage;

    public SpecialDownloader(Storage storage, Callback errorCallback) {
        this.errorCallback = errorCallback;
        this.storage = storage;
        this.version = storage.getVersion();
        storage.setOperationgSystem(storage.getUtils().getOS());
        objectsDir = new File(getMineCraftLocation() + "/assets/objects/");
        indexesDir = new File(getMineCraftLocation() + "/versions/" + version + "/");
        logconfigsDir = new File(getMineCraftLocation() + "/assets/log_configs/");
        indexes = new File(indexesDir, version + ".json");
        xmlFile = new File(logconfigsDir + "/" + "client-" + version + ".xml");
    }

    int currentfile = 0;

    private JSONObject objects = null;

    private String versionUrl;
    private JSONObject versionObject;

    public void run() {
        try {
            if (objects == null) {
                try {
                    JSONObject manifest = JSONUrl.readURL("https://infumia.com.tr/clients/data.json");
                    JSONArray versions = manifest.getJSONArray("clients");
                    for (int i = 0; i < versions.length(); i++) {
                        if (versions.getJSONObject(i).getString("clientName").equals(version)) {
                            versionUrl = versions.getJSONObject(i).getString("assetsUrl");
                            versionObject = JSONUrl.readURL(versionUrl);
                            storage.setVersionObject(versionObject);
                            JSONObject readedUrl = JSONUrl.readURL(versionObject.getJSONObject("assetIndex").getString("url"));
                            objects = readedUrl.getJSONObject("objects");
                            storage.setAssets(objects);
                            storage.setRemoteHash(versions.getJSONObject(i).getString("clientSha1"));
                            try (FileWriter file = new FileWriter(storage.getUtils().getMineCraftAssetsIndexes_X_json(storage.getOperationgSystem(), versionObject.getJSONObject("assetIndex").getString("id")))) {
                                file.write(readedUrl.toString());
                                file.flush();
                            }
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    try {
                        versionObject = new JSONObject(new String(Files.readAllBytes(Paths.get(indexes.getPath()))));
                        storage.setVersionObject(versionObject);
                        String assetsIndexId = storage.getLocal().readJson_assets(storage.getUtils().getMineCraft_Versions_X_X_json(storage.getOperationgSystem(), versionObject.getJSONObject("assetIndex").getString("id")));
                        File file = new File(storage.getUtils().getMineCraftAssetsIndexes_X_json(storage.getOperationgSystem(), assetsIndexId));
                        if (!file.exists()) {
                            errorCallback.response("İnternet bağlantısı gerekiyor.");
                            return;
                        }
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
                } catch (IOException e) {
                    errorCallback.response("İnternet bağlantısı gerekiyor.");
                }
            }

            if (!xmlFile.exists() && !versionObject.isNull("logging")) {
                try {
                    String url = versionObject.getJSONObject("logging").getJSONObject("client").getJSONObject("file").getString("url");
                    URL oracle = new URL(url);
                    BufferedReader in = new BufferedReader(new InputStreamReader(oracle.openStream()));
                    FileWriter file = new FileWriter(xmlFile);
                    String inputLine;
                    while ((inputLine = in.readLine()) != null)
                        file.write(inputLine + "\n");
                    file.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                    errorCallback.response(e.toString());
                }
            }

            if (currentfile == objects.length()) {
                System.out.print("\r");
                String assetId = versionObject.getString("assets");
                if (assetId.equals("pre-1.6") || assetId.equals("legacy")) {
                    assetsToResources();
                }
                InfumiaLauncher.logger.info("Assets indirme islemi bitti.");
                InfumiaLauncher.logger.info("Client indirme islemi baslatiliyor");
                InfumiaLauncher.step++;

                storage.setClientUrl(versionObject.getJSONObject("downloads").getJSONObject("client").getString("url"));

                Thread thread = new Thread(() -> {
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
                if (getHash(currentfile).equals(calcSHA1(file))) {
                    System.out.print("\r");
                    System.out.print("Dosya zaten var diger dosyaya geciliyor. " + currentfile + "/" + objects.length());
                    storage.setDownloadedAssets(++currentfile);
                    Platform.runLater(() -> run());
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
        } catch (Exception e) {
            e.printStackTrace();
            errorCallback.response(e.toString());
        }
    }

    private void assetsToResources() throws IOException {
        File resourcesDir = new File(getMineCraftLocation() + File.separator + "parents");
        if (!resourcesDir.exists()) resourcesDir.mkdir();
        for (int i = 0; i < objects.length(); i++) {
            File resourceFile = new File((String) objects.names().get(i));
            String name = resourceFile.getName();
            String hash = getHash(i);
            File assetFile = new File(getMineCraftLocation() + File.separator + "assets" + File.separator + "objects" + File.separator + hash.substring(0, 2) + File.separator + hash);
            File finalDir = new File(resourcesDir.getPath() + File.separator + resourceFile.getPath().replace(name, ""));

            finalDir.mkdirs();

            FileUtils.copyFile(assetFile, new File(finalDir, name));
        }
    }

    private String getMineCraftLocation() {
        if (PlatformUtil.isWindows()) {
            return (System.getenv("APPDATA") + File.separator + ".infumia");
        }
        if (PlatformUtil.isLinux()) {
            return (System.getProperty("user.home") + File.separator + ".infumia");
        }
        if (PlatformUtil.isMac()) {
            return (System.getProperty("user.home") + File.separator + "Library" + File.separator + "Application Support" + File.separator + "infumia");
        }
        return "N/A";
    }

    private static byte[] readFileToByteArray(File file) {
        FileInputStream fis = null;
        // Creating a byte array using the length of the file
        // file.length returns long which is cast to int
        byte[] bArray = new byte[(int) file.length()];
        try {
            fis = new FileInputStream(file);
            fis.read(bArray);
            fis.close();

        } catch (IOException ioExp) {
            ioExp.printStackTrace();
        }
        return bArray;
    }

    private String getHash(int size) {
        return objects.getJSONObject((String) objects.names().get(size)).getString("hash");
    }

    private int getSize(int size) {
        return objects.getJSONObject((String) objects.names().get(size)).getInt("size");
    }

    private String calcSHA1(File file) throws FileNotFoundException,
            IOException, NoSuchAlgorithmException {

        MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
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