package com.infumia.launcher.download;

import com.infumia.launcher.objects.Callback;
import com.sun.javafx.PlatformUtil;
import org.json.JSONArray;
import org.kamranzafar.jddl.DirectDownloader;
import org.kamranzafar.jddl.DownloadListener;
import org.kamranzafar.jddl.DownloadTask;
import com.infumia.launcher.InfumiaLauncher;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class MinecraftLibrariesDownloader {

    private Callback errorCallback;
    private String version;
    private JSONObject versionObject;
    private Storage storage;

    public MinecraftLibrariesDownloader(Storage storage, Callback errorCallback) {
        this.errorCallback = errorCallback;
        this.storage = storage;
        this.version = storage.getVersion();
        this.versionObject = storage.getVersionObject();

        //version_url_list_natives
        storage.getLocal().readJson_libraries_downloads_classifiers_natives_X(storage.getUtils().getMineCraft_Versions_X_X_json(storage.getOperationgSystem(), version), storage.getOperationgSystem());
        //version_path_list_natives
        storage.getLocal().readJson_libraries_downloads_classifiers_natives_Y(storage.getUtils().getMineCraft_Versions_X_X_json(storage.getOperationgSystem(), version), storage.getOperationgSystem());
        storage.getLocal().readJson_libraries_downloads_classifiers_natives_size(storage.getUtils().getMineCraft_Versions_X_X_json(storage.getOperationgSystem(), version), storage.getOperationgSystem());
        storage.getLocal().readJson_libraries_name(storage.getUtils().getMineCraft_Version_Json(storage.getOperationgSystem(), version));
        storage.getLocal().readJson_libraries_downloads_artifact_path(storage.getUtils().getMineCraft_Version_Json(storage.getOperationgSystem(), version));
        storage.getLocal().readJson_libraries_downloads_artifact_url(storage.getUtils().getMineCraft_Version_Json(storage.getOperationgSystem(), version));
        storage.getLocal().readJson_libraries_downloads_artifact_size(storage.getUtils().getMineCraft_Version_Json(storage.getOperationgSystem(), version));


//        for (int i = 0; i < storage.getLocal().version_url_list_natives.size(); i++) {
//            storage.getUtils().jarExtract(storage.getOperationgSystem(), storage.getLocal().version_path_list_natives.get(i).toString(), storage.getUtils().getMineCraft_Versions_X_Natives_Location(storage.getOperationgSystem(), version));
//        }

        for (int i = 0; i < storage.getLocal().version_path_list.size(); i++) {
            storage.getLocal().libraries_path.add(storage.getUtils().setMineCraft_librariesLocation(storage.getOperationgSystem(), storage.getLocal().version_path_list.get(i).toString()));
        }

        for (int i = 0; i < storage.getLocal().version_name_list.size(); i++) {
            storage.getLocal().version_path_list.add(storage.getLocal().generateLibrariesPath(storage.getOperationgSystem(), storage.getLocal().version_name_list.get(i).toString()));
        }

        sorted = versionCheck();
    }

    int totalFile = 0;

    int currentfilelib = 0;
    int currentfilenativelib = 0;

    File librariesDir = new File(getMineCraftLocation() + "/libraries/");


    private boolean libsDownloaded = false;
    private boolean nativesDownloaded = false;

    JSONArray objects = null;
    List sorted;

    public void run() throws MalformedURLException, FileNotFoundException, InterruptedException {

        if (objects == null) {
            objects = versionObject.getJSONArray("libraries");
            storage.setLibraries(objects);
            totalFile = storage.getLocal().version_url_list.size() + storage.getLocal().version_url_list_natives.size();
            storage.setTotalLibraries(totalFile);
        }

        if (libsDownloaded && nativesDownloaded) {
            System.out.print("\r");
            InfumiaLauncher.logger.info("Oyun baslatiliyor");
            InfumiaLauncher.step++;
            new Minecraft(storage).launchMinecraft();
            return;
        }


        String dirs = "";
        String fullName = storage.getLocal().version_path_list.get(currentfilelib).toString();
        String[] fullNameSplitted = storage.getLocal().version_path_list.get(currentfilelib).toString().split("/");
        String name = fullName.split("/")[fullNameSplitted.length - 1];
        for (String str : fullNameSplitted) {
            if (!str.contains(".jar")) dirs += str + "/";
        }

        File libDir = new File(storage.getUtils().getMineCraftLibrariesLocation(storage.getOperationgSystem()) + "/" + dirs);
        libDir.mkdirs();

        libDir = new File(storage.getUtils().getMineCraftLibrariesLocation(storage.getOperationgSystem()) + "/" + dirs, name);

        if (currentfilelib == storage.getLocal().version_url_list.size()) {
            System.out.print("\r");
            libsDownloaded = true;
            InfumiaLauncher.logger.info("Natives dosyasi aktiflestirildi isletim sisteminiz kontrol ediliyor");
            runNatives();
            return;
        }

        DirectDownloader dd = new DirectDownloader();
        if (libDir.exists() && !libDir.isDirectory()) {
            if ((long) storage.getLocal().version_size_list.get(currentfilelib) == libDir.length()) {
                System.out.print("\r");
                System.out.print("Dosya zaten var diger dosyaya geciliyor. " + (currentfilelib + currentfilenativelib) + "/" + storage.getLocal().version_url_list.size());
                storage.setDownloadedLib(++currentfilelib);
                run();
                return;
            }
        }


        try {

            dd.download(new DownloadTask(new URL(storage.getLocal().version_url_list.get(currentfilelib).toString()), new FileOutputStream(new File(librariesDir + "/" + dirs, name)), new DownloadListener() {

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
                        storage.setDownloadedLib(++currentfilelib);
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
        } catch (Exception e) {
            e.printStackTrace();
            errorCallback.response(e.toString());
        }
    }

    boolean isInteger(String input) {
        try {
            Integer.parseInt(input);
            return true;
        }catch (NumberFormatException e) {
            return false;
        }
    }

    List versionCheck() {
        List list = new ArrayList<String>();
        list.addAll(storage.getLocal().version_path_list_natives);

        List removeList = new ArrayList<String>();

        Collections.sort(list, (a, b)-> {
            if (a == null || b == null) return 0;
            File aFile = new File((String) a);
            File bFile = new File((String) b);
            String aname = aFile.getName();
            String aremoved = aname.substring(0, aname.lastIndexOf('.'));
            String bname = bFile.getName();
            String bremoved = bname.substring(0, bname.lastIndexOf('.'));
            for (String str : aremoved.split("-")) {
                if (isInteger(str)) {
                    if (Integer.parseInt(str) > 1000) {
                        aremoved = aremoved.replaceAll("-" + str, "");
                    }
                }
            }
            for (String str : bremoved.split("-")) {
                if (isInteger(str)) {
                    if (Integer.parseInt(str) > 1000) {
                        bremoved = bremoved.replaceAll("-" + str, "");
                    }
                }
            }
            int versiona = Integer.parseInt(aremoved.replaceAll("[\\D]", ""));
            int versionB = Integer.parseInt(bremoved.replaceAll("[\\D]", ""));
            // lwjgl-tinyfd-3.2.1-natives-windows lwjgl-tinyfd-3.2.2-natives-windows
            String formattedvera = aremoved.replaceAll(storage.getLocal().getNatives_OS(getOS()), "").replaceAll("[A-Za-z]?", "").replaceAll("-", "");
            String formattedverb = bremoved.replaceAll(storage.getLocal().getNatives_OS(getOS()), "").replaceAll("[A-Za-z]?", "").replaceAll("-", "");
            if (!aname.replaceAll(storage.getLocal().getNatives_OS(getOS()), "").replaceAll(formattedvera, "").equals(
                    bname.replaceAll(storage.getLocal().getNatives_OS(getOS()), "").replaceAll(formattedverb, ""))) return 0;
            if (versiona == versionB) return 0;
            if (versiona > versionB){
                if (!removeList.contains(b)) {
                    removeList.add(b);
                }
                return 1;
            }
            if (versiona < versionB) {
                if (!removeList.contains(a)) removeList.add(a);
                return -1;
            }
            return 0;
        });

        List sortedList = list;
        sortedList.removeAll(removeList);

        return sortedList;
    }


    boolean containsListString(List list, String obj) {
        for (String elmnt : (List<String>) list) {
            if (elmnt.contains(obj)) return true;
        }
        return false;
    }

    public void runNatives() throws MalformedURLException, FileNotFoundException, InterruptedException {


        if (currentfilenativelib == storage.getLocal().version_url_list_natives.size()) {
            System.out.print("\r");
            InfumiaLauncher.logger.info(storage.getOperationgSystem() + " libleri indirildi");
            storage.setDownloadedLib(++currentfilelib);
            nativesDownloaded = true;
            run();
            return;
        }

        String dirs = "";
        String fullName = storage.getLocal().version_path_list_natives.get(currentfilenativelib).toString();
        String[] fullNameSplitted = storage.getLocal().version_path_list_natives.get(currentfilenativelib).toString().split("/");
        String name = fullName.split("/")[fullNameSplitted.length - 1];
        for (String str : fullNameSplitted) {
            if (!str.contains(".jar")) dirs += str + "/";
        }

        File libDir = new File(storage.getUtils().getMineCraftLibrariesLocation(storage.getOperationgSystem()) + "/" + dirs);
        libDir.mkdirs();

        libDir = new File(storage.getUtils().getMineCraftLibrariesLocation(storage.getOperationgSystem()) + "/" + dirs, name);


        DirectDownloader ddnative = new DirectDownloader();
        if (libDir.exists()) {
            if ((long) storage.getLocal().version_size_list_natives.get(currentfilenativelib) == libDir.length()) {
                if (sorted.contains(fullName)) {
                    System.out.print("\r>Dosya çıkartılıyor: " + name);
                    jarExtract(dirs + name, getMineCraft_Versions_X_Natives_Location(version));
                }
                System.out.print("\r");
                storage.setDownloadedNatives(++currentfilenativelib);
                System.out.print("Diger dosyaya geciliyor. " + currentfilenativelib + "/" + storage.getLocal().version_url_list_natives.size());
                runNatives();
                return;
            }
        }

        File downloadedFile = new File(librariesDir + "/" + dirs, name);

        String finalDirs = dirs;
        ddnative.download(new DownloadTask(new URL(storage.getLocal().version_url_list_natives.get(currentfilenativelib).toString()), new FileOutputStream(downloadedFile), new DownloadListener() {

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
                double downloadpercent = (t1 / t2) * 100.0d;
                System.out.print("\r" + ">Indiriliyor " + fname + " %" + new DecimalFormat("##.#").format(downloadpercent).replace(",", ".") + " " + currentfilenativelib + "/" + getNativeLength("windows"));
            }

            @Override
            public void onComplete() {
                try {
                    storage.setDownloadedNatives(++currentfilenativelib);
                    runNatives();
                } catch (Exception e) {
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

    public String getOS() {
        String OS = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);

        if ((OS.indexOf("mac") >= 0) || (OS.indexOf("darwin") >= 0)) {
            return ("osx");
        } else if (OS.indexOf("win") >= 0) {
            return ("windows");
        } else if (OS.indexOf("nux") >= 0) {
            return ("linux");
        } else {
            //bring support to other OS.
            //we will assume that the OS is based on linux.
            return ("linux");
        }
    }


    public void jarExtract(String _jarFile, String destDir) {
        try {
            _jarFile = setMineCraft_Versions_X_NativesLocation(_jarFile);
            File dir = new File(destDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            File jarFile = new File(_jarFile);

            java.util.jar.JarFile jar = new java.util.jar.JarFile(jarFile);
            java.util.Enumeration enumEntries = jar.entries();
            while (enumEntries.hasMoreElements()) {
                java.util.jar.JarEntry file = (java.util.jar.JarEntry) enumEntries.nextElement();
                java.io.File f = new java.io.File(destDir + java.io.File.separator + file.getName());
                if (file.isDirectory()) { // if its a directory, create it
                    f.mkdirs();
                    continue;
                }
                if (!f.exists()) {
                    java.io.InputStream is = jar.getInputStream(file); // get the input stream
                    java.io.FileOutputStream fos = new java.io.FileOutputStream(f);
                    while (is.available() > 0) {  // write contents of 'is' to 'fos'
                        fos.write(is.read());
                    }
                    fos.close();
                    is.close();

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getMineCraftLibrariesLocation() {
        return (getMineCraftLocation() + "/libraries");
    }

    public String setMineCraft_Versions_X_NativesLocation(String _path) {
        return (getMineCraftLibrariesLocation() + "/" + _path);

    }

    public String getMineCraftVersionsLocation() {
        return (getMineCraftLocation() + "/versions");
    }

    public String getMineCraft_Versions_X_Natives_Location(String VersionNumber) {
        return (getMineCraftVersionsLocation() + "/" + VersionNumber + "/natives");
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

    private String getURL(int size){
        return objects.getJSONObject(size).getJSONObject("downloads").getJSONObject("artifact").get("url").toString();
    }

    private boolean isFileDownloaded(String fileName) {
        return new File(librariesDir, fileName).exists();
    }

    private String getLIBName(int size){
        JSONObject control = objects.getJSONObject(size).getJSONObject("downloads");
        boolean control1 = control.toString().contains("natives-" + getOS());
        JSONObject control2 = objects.getJSONObject(size);
        if (control.toString().contains("artifact") && control.toString().contains("classifiers")) {
            if (!control.toString().contains("natives-" + getOS())) {
                return "invalid";
            }
        }
        if (control2.toString().contains("rules") && !control1 && getOS().equals("windows")){
            for (int i = 0; i < control2.getJSONArray("rules").length(); i++) {
                JSONObject obj = control2.getJSONArray("rules").getJSONObject(i);
                if (!obj.isNull("action") && !obj.isNull("os")) {
                    if (obj.getString("action").equals("allow") && obj.getJSONObject("os").getString("name").equals("osx")) {
                        return "invalid";
                    }
                }
            }
        }
        if (!control.toString().contains("artifact"))  {
            return "";
        }
        String[] splitted = String.valueOf(objects.getJSONObject(size).getJSONObject("downloads").getJSONObject("artifact").get("path")).split("/");
        return splitted[splitted.length - 1] + (control1 ? "+native" : "");
    }

    private int getSize(int size){
        return objects.getJSONObject(size).getJSONObject("downloads").getJSONObject("artifact").getInt("size");
    }

    private String getNativeURL(String OS,int size){
        return objects.getJSONObject(size).getJSONObject("downloads").getJSONObject("classifiers").getJSONObject("natives-" + OS.toLowerCase()).getString("url");
        //return String.valueOf(objects.getJSONObject((String) objects.names().get(currentfile)).getJSONObject(OS).getJSONObject("lwjgl-platform-2.9.2-nightly-20140822-natives-windows.jar").getString("url"));
    }
    private int getNativeSize(String OS,int size){
        return objects.getJSONObject(size).getJSONObject("downloads").getJSONObject("classifiers").getJSONObject("natives-" + OS.toLowerCase()).getInt("size");
        //return String.valueOf(objects.getJSONObject((String) objects.names().get(currentfile)).getJSONObject(OS).getJSONObject("lwjgl-platform-2.9.2-nightly-20140822-natives-windows.jar").getString("url"));
    }
    private int getNativeLength(String OS){
        int count = 0;
        for (int i = 0; i < objects.length(); i++) {
            if (objects.getJSONObject(i).toString().contains("natives-" + OS.toLowerCase())) count++;
        }
        return count;
    }
    private String getNativeLibName(String OS, int size){
        String[] splitted = objects.getJSONObject(size).getJSONObject("downloads").getJSONObject("classifiers").getJSONObject("natives-" + OS.toLowerCase()).getString("path").split("/");
        return splitted[splitted.length - 1];
    }

}
