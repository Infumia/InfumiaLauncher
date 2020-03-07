package com.infumia.launcher.download;

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
import com.infumia.launcher.objects.Callback;
import com.infumia.launcher.util.JSONUrl;
import com.sun.javafx.PlatformUtil;
import javafx.application.Platform;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.simple.parser.JSONParser;
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
        objectsDir = new File(InfumiaLauncher.getMineCraftLocation() + File.separator + "assets" + File.separator + "objects" + File.separator);
        indexesDir = new File(InfumiaLauncher.getMineCraftLocation() + File.separator + "versions" + File.separator + version + File.separator);
        logconfigsDir = new File(InfumiaLauncher.getMineCraftLocation() + File.separator + "assets" + File.separator + "log_configs" + File.separator);
        indexes = new File(indexesDir, version + ".json");
        xmlFile = new File(logconfigsDir + File.separator + "client-" + version + ".xml");
    }

    int currentfile = 0;

    private JSONObject objects = null;

    private JSONObject versionObject;

    public void run() {
        try {
            if (objects == null) {
                try {
                    JSONObject manifest = JSONUrl.readURL("https://launchermeta.mojang.com/mc/game/version_manifest.json");
                    JSONArray versions = manifest.getJSONArray("versions");
                    String assetVersion = "";
                    boolean set = !storage.getVersionObject().isEmpty();
                    if (version.matches(".*[A-Z]+.*")) {
                        try
                        {
                            if (!set) {
                                FileReader fileInputStream = new FileReader(indexes);
                                BufferedReader reader = new BufferedReader(fileInputStream);

                                StringBuilder builder = new StringBuilder();

                                while (reader.ready()) {
                                    builder.append(reader.readLine());
                                }

                                JSONObject json = new JSONObject(builder.toString());

                                assetVersion = (json.isNull("assets") ? json.getString("inheritsFrom") : json.getString("assets"));
                                storage.setVersionObject(json);
                                storage.setIllegalVersion(true);
                                storage.setAssetVersion(assetVersion);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            errorCallback.response(ex.toString());
                            return;
                        }
                    }

                    if (!set) {
                        for (int i = 0; i < versions.length(); i++) {
                            if (versions.getJSONObject(i).getString("id").equals((assetVersion.isEmpty() ? version : assetVersion))) {
                                String versionUrl = versions.getJSONObject(i).getString("url");
                                versionObject = JSONUrl.readURL(versionUrl);
                                if (!storage.isIllegalVersion()) storage.setVersionObject(versionObject);
                                JSONObject readedUrl = JSONUrl.readURL(versionObject.getJSONObject("assetIndex").getString("url"));
                                objects = readedUrl.getJSONObject("objects");
                                storage.setAssets(objects);
                                storage.setRemoteHash(versionObject.getJSONObject("downloads").getJSONObject("client").getString("sha1"));
                                File indexesLocation = new File(storage.getUtils().getMineCraftAssetsIndexesLocation(storage.getOperationgSystem()));
                                if (!indexesLocation.exists()) {
                                    indexesLocation.mkdirs();
                                }
                                try (FileWriter file = new FileWriter(storage.getUtils().getMineCraftAssetsIndexes_X_json(storage.getOperationgSystem(), versionObject.getJSONObject("assetIndex").getString("id")))) {
                                    file.write(readedUrl.toString());
                                    file.flush();
                                }
                                break;
                            }
                        }
                    }else {
                        versionObject = storage.getVersionObject();
                        JSONObject readedUrl = JSONUrl.readURL(versionObject.getJSONObject("assetIndex").getString("url"));
                        objects = readedUrl.getJSONObject("objects");
                        storage.setAssets(objects);
                        storage.setRemoteHash(versionObject.getJSONObject("downloads").getJSONObject("client").getString("sha1"));
                        File indexesLocation = new File(storage.getUtils().getMineCraftAssetsIndexesLocation(storage.getOperationgSystem()));
                        if (!indexesLocation.exists()) {
                            indexesLocation.mkdirs();
                        }
                        try (FileWriter file = new FileWriter(storage.getUtils().getMineCraftAssetsIndexes_X_json(storage.getOperationgSystem(), versionObject.getJSONObject("assetIndex").getString("id")))) {
                            file.write(readedUrl.toString());
                            file.flush();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    try {
                        versionObject = new JSONObject(new String(Files.readAllBytes(Paths.get(indexes.getPath()))));
                        if (!storage.isIllegalVersion()) storage.setVersionObject(versionObject);
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
                    file.write(versionObject.toString());
                    file.flush();
                } catch (IOException e) {
                    errorCallback.response("İnternet bağlantısı gerekiyor.");
                }
            }


            if (xmlFile != null && !xmlFile.exists() && !versionObject.isNull("logging")) {
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

                if (storage.getClientUrl().isEmpty()) storage.setClientUrl(versionObject.getJSONObject("downloads").getJSONObject("client").getString("url"));

                Thread thread = new Thread(() -> {
                    if (!storage.isIllegalVersion()) new MinecraftClientDownloader(storage, errorCallback).run();
                    else {
                        try {
                            InfumiaLauncher.step++;
                            InfumiaLauncher.logger.info("Client var sanılıyor.");
                            InfumiaLauncher.logger.info("Natives indirme islemi başlatılıyor.");
                            storage.setClientDownloadPercent(100.0d);
                            new MinecraftLibrariesDownloader(storage, errorCallback).run();
                        }catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });
                InfumiaLauncher.executor.schedule(thread, 1, TimeUnit.MILLISECONDS);
                return;
            }

        } catch (Exception e) {
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
        File resourcesDir = new File(InfumiaLauncher.getMineCraftLocation() + File.separator+"resources");
        if (!resourcesDir.exists()) resourcesDir.mkdir();
        for (int i = 0; i < objects.length(); i++) {
            File resourceFile = new File((String) objects.names().get(i));
            String name = resourceFile.getName();
            String hash = getHash(i);
            File assetFile = new File(InfumiaLauncher.getMineCraftLocation() + File.separator + "assets" + File.separator + "objects" + File.separator + hash.substring(0, 2) + File.separator + hash);
            File finalDir = new File(resourcesDir.getPath() + File.separator + resourceFile.getPath().replace(name, ""));

            finalDir.mkdirs();

            FileUtils.copyFile(assetFile, new File(finalDir, name));
        }
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

    private JSONArray mergeArrays(JSONArray array1, JSONArray array2) {
        int i = 0;
        for (Object object : array1.toList()) {
            array2.put(array1.getJSONObject(i));
            i++;
        }
        return array2;
    }

}