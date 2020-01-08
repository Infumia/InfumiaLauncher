package com.infumia.launcher.download;

import com.infumia.launcher.InfumiaLauncher;
import com.infumia.launcher.objects.Callback;
import com.infumia.launcher.util.JSONUrl;
import org.kamranzafar.jddl.DirectDownloader;
import org.kamranzafar.jddl.DownloadListener;
import org.kamranzafar.jddl.DownloadTask;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.text.DecimalFormat;

public class MinecraftAssetsDownloader implements Runnable {

    Callback errorCallback;

    public MinecraftAssetsDownloader(Callback errorCallback) {
        this.errorCallback = errorCallback;
    }

    public static int currentfile=0;

    File objectsDir = new File(System.getenv().get("APPDATA") + "/.infumia/assets/objects/");
    File indexesDir = new File(System.getenv().get("APPDATA") + "/.infumia/assets/indexes/");
    File logconfigsDir = new File(System.getenv().get("APPDATA") + "/.infumia/assets/log_configs/");
    File indexes = new File(indexesDir + "/" +"1.12.json");
    File xmlFile = new File(logconfigsDir + "/" +"client-1.12.xml");

    public static JSONObject objects = null;

    public void run() {
        try {
            if (objects == null) {
                objects = JSONUrl.readURL("https://launchermeta.mojang.com/v1/packages/1584b57c1a0b5e593fad1f5b8f78536ca640547b/1.12.json").getJSONObject("objects");
            }
            if (!indexes.exists()) {
                indexes.createNewFile();
                try (FileWriter file = new FileWriter(indexes)) {
                    file.write(JSONUrl.readURL("https://launchermeta.mojang.com/v1/packages/1584b57c1a0b5e593fad1f5b8f78536ca640547b/1.12.json").toString());
                    file.flush();
                }
            }
            if (!xmlFile.exists()) {
                String url = "https://launcher.mojang.com/v1/objects/ef4f57b922df243d0cef096efe808c72db042149/client-1.12.xml";
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
                currentfile = 0;
                InfumiaLauncher.step++;
                Thread thread = new Thread(new MinecraftClientDownloader(errorCallback));
                thread.run();
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

        if (file.exists()) {
            if (getSize(currentfile) == file.length()) {
                System.out.print("\r");
                System.out.print("Dosya zaten var diger dosyaya geciliyor. " + currentfile + "/" + objects.length());
                currentfile++;
                run();
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

    }
    private String getHash(int size){
        return objects.getJSONObject((String) objects.names().get(size)).get("hash").toString();
    }

    private int getSize(int size){
        return objects.getJSONObject((String) objects.names().get(size)).getInt("size");
    }

}