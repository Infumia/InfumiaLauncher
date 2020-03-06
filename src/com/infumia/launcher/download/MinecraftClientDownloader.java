package com.infumia.launcher.download;

import com.infumia.launcher.InfumiaLauncher;
import com.infumia.launcher.objects.Callback;
import com.sun.javafx.PlatformUtil;
import org.kamranzafar.jddl.DirectDownloader;
import org.kamranzafar.jddl.DownloadListener;
import org.kamranzafar.jddl.DownloadTask;

import java.io.*;
import java.math.BigInteger;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;

public class MinecraftClientDownloader implements Runnable{

    private Callback errorCallback;
    private String version;
    private String clientUrl;
    private File versionsDir;
    private File clientFile;
    private Storage storage;

    public MinecraftClientDownloader(Storage storage, Callback errorCallback) {
        this.errorCallback = errorCallback;
        this.storage = storage;
        this.version = storage.getVersion();
        this.clientUrl = storage.getClientUrl();
        this.versionsDir = new File(InfumiaLauncher.getMineCraftLocation() + File.separator + "versions" + File.separator + version + File.separator);
        this.clientFile = new File(versionsDir, version + ".jar");
    }

    double downloadPercent = 0;


    public void run() {
        if (!versionsDir.exists()) versionsDir.mkdir();

        if (clientFile.exists()) {
            try {
                String localHash = calcSHA1(clientFile);
                String remoteHash = storage.getRemoteHash();

                if (localHash.equals(remoteHash)) {
                    downloadPercent = 100.0d;
                    storage.setClientDownloadPercent(downloadPercent);
                    InfumiaLauncher.logger.info("Client indirme islemi bitti.");
                    InfumiaLauncher.logger.info("Natives indirme islemi baslatiliyor");
                    InfumiaLauncher.step++;
                    try {
                        new MinecraftLibrariesDownloader(storage, errorCallback).run();
                    } catch (Exception e) {
                        e.printStackTrace();
                        errorCallback.response(e.toString());
                    }
                    return;
                }
            }catch (Exception e) {
                e.printStackTrace();
                errorCallback.response(e.toString());
            }
        }

        DirectDownloader dd = new DirectDownloader();
        dd.setConnectionTimeout(25);

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
                    storage.setClientDownloadPercent(downloadPercent);
                }

                @Override
                public void onComplete() {
                    System.out.print("\r");
                    InfumiaLauncher.logger.info("Client indirme islemi bitti.");
                    InfumiaLauncher.logger.info("OpenGL Natives indirme islemi baslatiliyor");
                    InfumiaLauncher.step++;
                    try {
                        new MinecraftLibrariesDownloader(storage, errorCallback).run();
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

    private static String calcSHA1(File file) throws FileNotFoundException,
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
