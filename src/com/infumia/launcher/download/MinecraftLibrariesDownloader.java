package com.infumia.launcher.download;

import com.infumia.launcher.objects.Callback;
import com.sun.javafx.PlatformUtil;
import org.kamranzafar.jddl.DirectDownloader;
import org.kamranzafar.jddl.DownloadListener;
import org.kamranzafar.jddl.DownloadTask;
import com.infumia.launcher.InfumiaLauncher;
import com.infumia.launcher.util.JSONUrl;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;

public class MinecraftLibrariesDownloader {

    Callback errorCallback;

    public MinecraftLibrariesDownloader(Callback errorCallback) {
        this.errorCallback = errorCallback;

    }

    public static int totalNativeLib = 0;

    public static int currentfilelib=0;
    public static int currentfilenativelib=0;
    File librariesDir = new File(System.getenv().get("APPDATA") + "/.infumia/libraries/");

    public static JSONObject objects = null;

    public void run() throws MalformedURLException, FileNotFoundException, InterruptedException {

        if (PlatformUtil.isWindows()) {
            totalNativeLib = 3;
        }

        if (PlatformUtil.isMac()) {
            totalNativeLib = 5;
        }

        if (PlatformUtil.isLinux()) {
            totalNativeLib = 3;
        }

        try {
            if (objects == null) {
                objects = JSONUrl.readURL("https://raw.githubusercontent.com/FurkanDGN/GLauncher-Cores/master/json/libraries.json").getJSONObject("objects");
            }
        } catch (IOException e) {
            e.printStackTrace();
            errorCallback.response(e.toString());
        }

        if(currentfilelib == objects.length()){
            System.out.print("\r");
            InfumiaLauncher.logger.info("Oyun baslatiliyor");
            InfumiaLauncher.step++;
            new Minecraft().launchMinecraft();
            return;
        }

        DirectDownloader dd = new DirectDownloader();
        File file = new File(librariesDir +  "/" + getLIBName(currentfilelib));
        if (file.exists()) {
            if(getSize(currentfilelib) == file.length()) {
                System.out.print("\r");
                System.out.print("Dosya zaten var diger dosyaya geciliyor. " + (currentfilelib + currentfilenativelib)+ "/"+objects.length());
                currentfilelib++;
                run();
                return;
            }
        }

        if(getLIBName(currentfilelib).equalsIgnoreCase("natives")){
            System.out.print("\r");
            InfumiaLauncher.logger.info("Natives dosyasi aktiflestirildi isletim sisteminiz kontrol ediliyor");
            runNatives();
            return;
        }

        try {

            dd.download(new DownloadTask(new URL(getURL(currentfilelib)), new FileOutputStream(librariesDir + "/" + getLIBName(currentfilelib)), new DownloadListener() {

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
                    System.out.print("\r" + ">Indiriliyor " + fname + " %" + new DecimalFormat("##.#").format(downloadpercent).replace(",", "."));
                }

                @Override
                public void onComplete() {
                    try {
                        currentfilelib++;
                        run();
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
        }catch (Exception e) {
            e.printStackTrace();
            errorCallback.response(e.toString());
        }
    }


    public void runNatives() throws MalformedURLException, FileNotFoundException, InterruptedException {

        if(PlatformUtil.isWindows()){

            if(currentfilenativelib == getNativeLenght("windows")){
                System.out.print("\r");
                InfumiaLauncher.logger.info("Windows libleri indirildi");
                currentfilelib++;
                run();
                return;
            }

            DirectDownloader ddnative = new DirectDownloader();
            File file = new File(librariesDir +  "/" + getNativeLibName("windows",currentfilenativelib));
            if (file.exists()) {
                if(getNativeSize("windows",currentfilenativelib) == file.length()) {
                    System.out.print("\r");
                    System.out.print("Dosya zaten var diger dosyaya geciliyor. " + currentfilenativelib+ "/"+getNativeLenght("windows"));
                    currentfilenativelib++;
                    runNatives();
                    return;
                }
            }

            ddnative.download(new DownloadTask(new URL(getNativeURL("windows",currentfilenativelib)), new FileOutputStream(librariesDir + "/" + getNativeLibName("windows",currentfilenativelib)), new DownloadListener() {

                String fname;
                double fileSize = 0;

                @Override
                public void onStart(String fname, int fsize) {
                    this.fname = fname;
                    fileSize = fsize;
                    //SLauncher.logger.info("Downloading " + fname + " of size " + fsize + " " + getNativeLibName("windows",currentfilenativelib));
                }

                @Override
                public void onUpdate(int bytes, int totalDownloaded) {
                    double t1 = totalDownloaded + 0.0d;
                    double t2 = fileSize + 0.0d;
                    double downloadpercent = (t1/t2)*100.0d;
                    System.out.print("\r" + ">Indiriliyor " + fname + " %"+new DecimalFormat("##.#").format(downloadpercent).replace(",",".") + " " + currentfilenativelib + "/"+getNativeLenght("windows"));
                }

                @Override
                public void onComplete() {
                    try {
                        currentfilenativelib++;
                        runNatives();
                    }catch (Exception e) {
                        e.printStackTrace();
                        errorCallback.response(e.toString());
                    }
                }
                @Override
                public void onCancel() {

                }
            }));
            Thread t = new Thread(ddnative);
            t.start();
            return;
        }

        if(PlatformUtil.isLinux()){

            if(currentfilenativelib == getNativeLenght("linux")){
                System.out.print("\r");
                InfumiaLauncher.logger.info("Linux libleri indirildi");
                currentfilelib++;
                run();
                return;
            }

            DirectDownloader ddnative = new DirectDownloader();
            File file = new File(librariesDir +  "/" + getNativeLibName("linux",currentfilenativelib));
            if (file.exists()) {
                if(getNativeSize("linux",currentfilenativelib) == file.length()) {
                    System.out.print("\r");
                    System.out.print("Dosya zaten var diger dosyaya geciliyor. " + currentfilenativelib+ "/"+getNativeLenght("linux"));
                    currentfilenativelib++;
                    runNatives();
                    return;
                }
            }

            ddnative.download(new DownloadTask(new URL(getNativeURL("linux",currentfilenativelib)), new FileOutputStream(librariesDir + "/" + getNativeLibName("linux",currentfilenativelib)), new DownloadListener() {

                String fname;
                double fileSize = 0;

                @Override
                public void onStart(String fname, int fsize) {
                    this.fname = fname;
                    fileSize = fsize;
                    //SLauncher.logger.info("Downloading " + fname + " of size " + fsize + " " + getNativeLibName("windows",currentfilenativelib));
                }

                @Override
                public void onUpdate(int bytes, int totalDownloaded) {
                    double t1 = totalDownloaded + 0.0d;
                    double t2 = fileSize + 0.0d;
                    double downloadpercent = (t1/t2)*100.0d;
                    System.out.print("\r" + ">Indiriliyor " + fname + " %"+new DecimalFormat("##.#").format(downloadpercent).replace(",",".") + " " + currentfilenativelib + "/"+getNativeLenght("linux"));
                }

                @Override
                public void onComplete() {
                    try {
                        currentfilenativelib++;
                        runNatives();
                    }catch (Exception e) {
                        e.printStackTrace();
                        errorCallback.response(e.toString());
                    }
                }
                @Override
                public void onCancel() {

                }
            }));
            Thread t = new Thread(ddnative);
            t.start();
            return;
        }

        if(PlatformUtil.isMac()){

            if(currentfilenativelib == getNativeLenght("osx")){
                System.out.print("\r");
                InfumiaLauncher.logger.info("OSX libleri indirildi");
                currentfilelib++;
                run();
                return;
            }

            DirectDownloader ddnative = new DirectDownloader();
            File file = new File(librariesDir +  "/" + getNativeLibName("osx",currentfilenativelib));
            if (file.exists()) {
                if(getNativeSize("osx",currentfilenativelib) == file.length()) {
                    System.out.print("\r");
                    System.out.print("Dosya zaten var diger dosyaya geciliyor. " + currentfilenativelib+ "/"+getNativeLenght("osx"));
                    currentfilenativelib++;
                    runNatives();
                    return;
                }
            }

            ddnative.download(new DownloadTask(new URL(getNativeURL("osx",currentfilenativelib)), new FileOutputStream(librariesDir + "/" + getNativeLibName("osx",currentfilenativelib)), new DownloadListener() {

                String fname;
                double fileSize = 0;

                @Override
                public void onStart(String fname, int fsize) {
                    this.fname = fname;
                    fileSize = fsize;
                    //SLauncher.logger.info("Downloading " + fname + " of size " + fsize + " " + getNativeLibName("windows",currentfilenativelib));
                }

                @Override
                public void onUpdate(int bytes, int totalDownloaded) {
                    double t1 = totalDownloaded + 0.0d;
                    double t2 = fileSize + 0.0d;
                    double downloadpercent = (t1/t2)*100.0d;
                    System.out.print("\r" + ">Indiriliyor " + fname + " %"+new DecimalFormat("##.#").format(downloadpercent).replace(",",".") + " " + currentfilenativelib + "/"+getNativeLenght("osx"));
                }

                @Override
                public void onComplete() {
                    try {
                        currentfilenativelib++;
                        runNatives();
                    }catch (Exception e) {
                        e.printStackTrace();
                        errorCallback.response(e.toString());
                    }
                }
                @Override
                public void onCancel() {

                }
            }));
            Thread t = new Thread(ddnative);
            t.start();
            return;
        }
    }


    private String getURL(int size){
        return objects.getJSONObject((String) objects.names().get(size)).get("url").toString();
    }
    private String getLIBName(int size){
        return String.valueOf(objects.names().get(size));
    }
    private int getSize(int size){
        return objects.getJSONObject((String) objects.names().get(size)).getInt("size");
    }


    private String getNativeURL(String OS,int size){
        return String.valueOf(objects.getJSONObject((String) objects.names().get(currentfilelib)).getJSONObject(OS).getJSONObject(String.valueOf(objects.getJSONObject((String) objects.names().get(currentfilelib)).getJSONObject(OS).names().get(size))).getString("url"));
        //return String.valueOf(objects.getJSONObject((String) objects.names().get(currentfile)).getJSONObject(OS).getJSONObject("lwjgl-platform-2.9.2-nightly-20140822-natives-windows.jar").getString("url"));
    }
    private int getNativeSize(String OS,int size){
        return objects.getJSONObject((String) objects.names().get(currentfilelib)).getJSONObject(OS).getJSONObject(String.valueOf(objects.getJSONObject((String) objects.names().get(currentfilelib)).getJSONObject(OS).names().get(size))).getInt("size");
        //return String.valueOf(objects.getJSONObject((String) objects.names().get(currentfile)).getJSONObject(OS).getJSONObject("lwjgl-platform-2.9.2-nightly-20140822-natives-windows.jar").getString("url"));
    }
    private int getNativeLenght(String OS){
        return objects.getJSONObject((String) objects.names().get(currentfilelib)).getJSONObject(OS).length();
    }
    private Object getNativeLibName(String OS, int size){
        return objects.getJSONObject((String) objects.names().get(currentfilelib)).getJSONObject(OS).names().get(size);
    }

}
