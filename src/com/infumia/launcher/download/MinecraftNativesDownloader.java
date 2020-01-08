package com.infumia.launcher.download;

import com.infumia.launcher.InfumiaLauncher;
import org.kamranzafar.jddl.DirectDownloader;
import org.kamranzafar.jddl.DownloadListener;
import org.kamranzafar.jddl.DownloadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;

public class MinecraftNativesDownloader {

    Thread t;

    String[] fileNames = {"OpenAL32.dll", "OpenAL64.dll", "SAPIWrapper_x64.dll", "SAPIWrapper_x86.dll", "jinput-dx8.dll", "jinput-dx8_64.dll", "jinput-raw.dll", "jinput-raw_64.dll", "jinput-wintab.dll", "lwjgl.dll", "lwjgl64.dll"};

    public static int fileStep = 0;

    File nativesDir = new File(System.getenv().get("APPDATA") + "/.infumia/libraries/native/");

    public void run() throws MalformedURLException, FileNotFoundException {
        if (fileStep >= 11) {
            System.out.print("\r");
            InfumiaLauncher.logger.info("OpenGL Natives indirme islemi bitti.");
            InfumiaLauncher.logger.info("Libraries indirme islemi baslatiliyor");
            InfumiaLauncher.step++;
            try {
                new MinecraftLibrariesDownloader().run();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
        File nativeFile = new File(nativesDir, fileNames[fileStep]);

        if (!nativesDir.exists()) nativesDir.mkdir();
        if (nativeFile.exists()) {
            fileStep++;
            try {
                run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (fileStep > 10) return;

        DirectDownloader dd = new DirectDownloader();

        String url = "https://raw.githubusercontent.com/FurkanDGN/deneme/master/native/" + fileNames[fileStep];

        dd.download(new DownloadTask(new URL(url), new FileOutputStream(nativeFile), new DownloadListener() {

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
                System.out.print("\r" + ">Indiriliyor " + fname + " %" + new DecimalFormat("##.#").format(downloadpercent).replace(",", ".") + " " + fileStep + "/" + fileNames.length);
            }

            @Override
            public void onComplete() {
                fileStep++;
                try {
                    run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancel() {
            }
        }));
        t = new Thread(dd);
        t.start();
    }
}
