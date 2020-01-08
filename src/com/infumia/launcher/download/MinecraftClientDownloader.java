package com.infumia.launcher.download;

import com.infumia.launcher.InfumiaLauncher;
import org.kamranzafar.jddl.DirectDownloader;
import org.kamranzafar.jddl.DownloadListener;
import org.kamranzafar.jddl.DownloadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.text.DecimalFormat;

public class MinecraftClientDownloader implements Runnable{

    public static double downloadPercent = 0;

    File versionsDir = new File(System.getenv().get("APPDATA") + "/.infumia/versions/");
    File clientFile = new File(versionsDir, "1.12.jar");

    public void run() {

        if (clientFile.exists()) {
            InfumiaLauncher.logger.info("Client indirme islemi bitti.");
            InfumiaLauncher.logger.info("Natives indirme islemi baslatiliyor");
            InfumiaLauncher.step++;
            try {
                new MinecraftNativesDownloader().run();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        DirectDownloader dd = new DirectDownloader();

        String url = "https://launcher.mojang.com/v1/objects/909823f9c467f9934687f136bc95a667a0d19d7f/client.jar";

        try {

            dd.download(new DownloadTask(new URL(url), new FileOutputStream(clientFile), new DownloadListener() {

                String fname;
                double fileSize = 0;

                @Override
                public void onStart(String fname, int fsize) {
                    this.fname = fname;
                    fileSize = fsize;
                    System.out.print("\r");
                    InfumiaLauncher.logger.info("Client dosyasi indiriliyor.");
                }

                @Override
                public void onUpdate(int bytes, int totalDownloaded) {
                    double t1 = totalDownloaded + 0.0d;
                    double t2 = fileSize + 0.0d;
                    double downloadpercent = (t1 / t2) * 100.0d;
                    System.out.print("\r" + ">Indiriliyor " + fname + " %" + new DecimalFormat("##.#").format(downloadpercent).replace(",", "."));
                    downloadPercent = downloadpercent;
                }

                @Override
                public void onComplete() {
                    System.out.print("\r");
                    InfumiaLauncher.logger.info("Client indirme islemi bitti.");
                    InfumiaLauncher.logger.info("OpenGL Natives indirme islemi baslatiliyor");
                    InfumiaLauncher.step++;
                    try {
                        new MinecraftNativesDownloader().run();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancel() {
                }
            }));
            Thread t = new Thread(dd);
            t.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
