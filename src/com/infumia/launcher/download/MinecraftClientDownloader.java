package com.infumia.launcher.download;

import com.infumia.launcher.InfumiaLauncher;
import com.infumia.launcher.objects.Callback;
import com.sun.javafx.PlatformUtil;
import org.kamranzafar.jddl.DirectDownloader;
import org.kamranzafar.jddl.DownloadListener;
import org.kamranzafar.jddl.DownloadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.text.DecimalFormat;

public class MinecraftClientDownloader implements Runnable{

    private Callback errorCallback;
    private String version;
    private String clientUrl;
    private File versionsDir;
    private File clientFile;

    public MinecraftClientDownloader(String clientUrl, String version, Callback errorCallback) {
        this.errorCallback = errorCallback;
        this.version = version;
        this.clientUrl = clientUrl;
        versionsDir = new File(getMineCraftLocation() + "/versions/" + version + "/");
        clientFile = new File(versionsDir, version + ".jar");
    }

    public static double downloadPercent = 0;


    public void run() {

        if (!versionsDir.exists()) versionsDir.mkdir();

        if (clientFile.exists()) {
            downloadPercent = 100.0d;
            InfumiaLauncher.logger.info("Client indirme islemi bitti.");
            InfumiaLauncher.logger.info("Natives indirme islemi baslatiliyor");
            InfumiaLauncher.step++;
            try {
                new MinecraftLibrariesDownloader(version, errorCallback).run();
            } catch (Exception e) {
                e.printStackTrace();
                errorCallback.response(e.toString());
            }
            return;
        }

        DirectDownloader dd = new DirectDownloader();

        try {

            dd.download(new DownloadTask(new URL(clientUrl), new FileOutputStream(clientFile), new DownloadListener() {

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
                        new MinecraftLibrariesDownloader(version, errorCallback).run();
                    } catch (Exception e) {
                        e.printStackTrace();
                        errorCallback.response(e.toString());
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
            errorCallback.response(ex.toString());
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

}
