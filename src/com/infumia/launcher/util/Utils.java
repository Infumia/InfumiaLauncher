package com.infumia.launcher.util;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.security.SecureRandom;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.Enumeration;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
/**
 *
 * @author Ammar Ahmad
 */
public class Utils {

    //String versions_linux = getMineCraftLocation("Linux") + "/versions";
    public String getMineCraftLocation(String OS) {
        if (OS.equals("Windows")) {
            return (System.getenv("APPDATA") + File.separator + ".infumia");
        }
        if (OS.equals("Linux")) {
            return (System.getProperty("user.home") + File.separator + ".infumia");
        }
        if (OS.equals("Mac")) {
            return (System.getProperty("user.home") + File.separator + "Library" + File.separator + "Application Support" + File.separator + "infumia");
        }
        return "N/A";
    }

    public String getMineCraftGameDirectoryLocation(String OS) {
        if (OS.equals("Windows")) {
            return (System.getenv("APPDATA") + File.separator + ".infumia");
        }
        if (OS.equals("Linux")) {
            return (System.getProperty("user.home") + File.separator + ".infumia");
        }
        if (OS.equals("Mac")) {
            return (System.getProperty("user.home") + File.separator + "Library" + File.separator + "Application Support" + File.separator + "infumia");
        }
        return "N/A";
    }

    public String getMineCraft_APIMeta(String OS) {
        return (getMineCraftLocation(OS) + File.separator + "api_meta");
    }

    public String getMineCraft_ServersDat(String OS) {
        return (getMineCraftLocation(OS) + File.separator + "servers.dat");
    }

    public String getMineCraftVersionsLocation(String OS) {
        return (getMineCraftLocation(OS) + File.separator + "versions");
    }

    public String getMineCraftTmpLocation(String OS) {
        return (getMineCraftLocation(OS) + File.separator + "tmp");
    }

    public String getMineCraft_Launcherlogs_txt(String OS) {
        return (getMineCraftLocation(OS) + File.separator + "Launcherlogs.txt");
    }

    public String getMineCraftLibrariesLocation(String OS) {
        return (getMineCraftLocation(OS) + File.separator + "libraries");
    }

    public String getMineCraftLibrariesComMojangNettyLocation(String OS) {
        return (getMineCraftLibrariesLocation(OS) + File.separator + "com" + File.separator + "mojang" + File.separator + "netty");
    }

    public String getMineCraftTmpIoNettyBootstrapLocation(String OS) {
        return (getMineCraftTmpLocation(OS) + File.separator + "io" + File.separator + "netty" + File.separator + "bootstrap");
    }

    public String getMineCraftTmpIoNettyBootstrapBootstrap_class(String OS) {
        return (getMineCraftTmpIoNettyBootstrapLocation(OS) + File.separator + "Bootstrap.class");
    }

    public Map getMineCraftLibrariesComMojangNetty_jar(String OS) {
        Map<String, String> results = new HashMap<>();

        Utils utils = new Utils();
        File[] directories = new File(getMineCraftLibrariesComMojangNettyLocation(OS)).listFiles(File::isDirectory);
        for (File en : directories) {
            //check if file exists.
            File[] files = new File(en.toString()).listFiles();
            for (File file : files) {
                if (file.isFile()) {
                    if (file.toString().endsWith(".jar")) {
                        results.put(file.getPath(), file.getName());
                    }
                }
            }
        }

        return (results);
    }


    public void extractJarContent(String destinationDir, String jarPath) throws IOException {
        File file = new File(jarPath);
        JarFile jar = new JarFile(file);

        // fist get all directories,
        // then make those directory on the destination Path
        for (Enumeration<JarEntry> enums = jar.entries(); enums.hasMoreElements();) {
            JarEntry entry = (JarEntry) enums.nextElement();

            String fileName = destinationDir + File.separator + entry.getName();
            File f = new File(fileName);

            if (fileName.endsWith("/")) {
                f.mkdirs();
            }

        }

        //now create all files
        for (Enumeration<JarEntry> enums = jar.entries(); enums.hasMoreElements();) {
            JarEntry entry = (JarEntry) enums.nextElement();

            String fileName = destinationDir + File.separator + entry.getName();
            File f = new File(fileName);

            if (!fileName.endsWith("/")) {
                InputStream is = jar.getInputStream(entry);
                FileOutputStream fos = new FileOutputStream(f);

                // write contents of 'is' to 'fos'
                while (is.available() > 0) {
                    fos.write(is.read());
                }

                fos.close();
                is.close();
            }
        }
    }

    public String getMineCraftLibrariesComMojangPatchyLocation(String OS) {
        return (getMineCraftLibrariesLocation(OS) + File.separator + "com" + File.separator + "mojang" + File.separator + "patchy");
    }

    public String getMineCraftTmpIoPatchyBootstrapBootstrap_class(String OS) {
        return (getMineCraftTmpIoNettyBootstrapLocation(OS) + "/Bootstrap.class");
    }

    public Map getMineCraftLibrariesComMojangPatchy_jar(String OS) {
        Map<String, String> results = new HashMap<>();

        Utils utils = new Utils();
        File[] directories = new File(getMineCraftLibrariesComMojangPatchyLocation(OS)).listFiles(File::isDirectory);
        for (File en : directories) {
            //check if file exists.
            File[] files = new File(en.toString()).listFiles();
            for (File file : files) {
                if (file.isFile()) {
                    if (file.toString().endsWith(".jar")) {
                        results.put(file.getPath(), file.getName());
                    }
                }
            }
        }

        return (results);
    }



    public String getMineCraft_Version_Manifest_json(String OS) {
        return (getMineCraftLocation(OS) + "/version_manifest.json");

    }

    public String getMineCraft_Launcher_Profiles_json(String OS) {
        return (getMineCraftLocation(OS) + File.separator + "launcher_profiles.json");
    }

    public String getMineCraft_Version_Json(String OS, String VersionNumber) {
        return (getMineCraftVersionsLocation(OS) + File.separator + VersionNumber + File.separator + VersionNumber + ".json");
    }

    public String getMineCraft_Versions_X_X_json(String OS, String VersionNumber) {
        return (getMineCraftVersionsLocation(OS) + File.separator + VersionNumber + File.separator + VersionNumber + ".json");

    }

    public String getMineCraft_Versions_X_X_jar(String OS, String VersionNumber) {
        return (getMineCraftVersionsLocation(OS) + File.separator + VersionNumber + File.separator + VersionNumber + ".jar");

    }

    public String getMineCraft_Versions_X_X_jar_Location(String OS, String VersionNumber) {
        return (getMineCraftVersionsLocation(OS) + File.separator + VersionNumber + File.separator + VersionNumber + ".jar");

    }

    public String getMineCraftAssetsRootLocation(String OS) {
        return (getMineCraftLocation(OS) + File.separator + "assets");

    }

    public String getMineCraft_Versions_X_Natives_Location(String OS, String VersionNumber) {
        return (getMineCraftVersionsLocation(OS) + File.separator + VersionNumber + File.separator + "natives");

    }

    public String getMineCraft_Versions_X_Natives(String OS, String VersionNumber) {
        return (getMineCraftVersionsLocation(OS) + File.separator + VersionNumber + File.separator + "natives");

    }

    public String getMineCraftAssetsIndexes_X_json(String OS, String VersionNumber) {

        return (getMineCraftAssetsIndexesLocation(OS) + File.separator + VersionNumber + ".json");
    }

    public String getMineCraft_X_json(String OS, String Username) {

        return (getMineCraftLocation(OS) + "/" + Username + ".json");

    }

    public String getMineCraftAssetsIndexesLocation(String OS) {
        return (getMineCraftAssetsLocation(OS) + File.separator + "indexes");

    }

    public String getMineCraftAssetsLocation(String OS) {
        return (getMineCraftLocation(OS) + File.separator + "assets");

    }

    public String getMineCraftAssetsObjectsLocation(String OS) {
        return (getMineCraftAssetsLocation(OS) + "/objects");
    }

    public String setMineCraft_Versions_X_NativesLocation(String OS, String _path) {
        Utils utils = new Utils();
        return (utils.getMineCraftLibrariesLocation(OS) + File.separator + _path);

    }
    

    public String setMineCraft_librariesLocation(String OS, String _path) {
        Utils utils = new Utils();
        return (utils.getMineCraftLibrariesLocation(OS) + File.separator + _path);
    }

    public String getArgsDiv(String OS) {
        if (OS.equals("Windows")) {
            return (";");
        }
        if (OS.equals("Linux")) {
            return (":");
        }
        if (OS.equals("Mac")) {
            return (":");
        }

        return "N/A";
    }

    @SuppressWarnings("empty-statement")
    public String getSHA_1(String _path) {

        try {

            MessageDigest md = MessageDigest.getInstance("SHA1");
            FileInputStream fis = new FileInputStream(_path);
            byte[] dataBytes = new byte[1024];

            int nread;

            while ((nread = fis.read(dataBytes)) != -1) {
                md.update(dataBytes, 0, nread);
            };

            byte[] mdbytes = md.digest();

            //convert the byte to hex format
            StringBuilder sb = new StringBuilder("");
            for (int i = 0; i < mdbytes.length; i++) {
                sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
            }

            return (sb.toString());

        } catch (NoSuchAlgorithmException | IOException ex) {
            System.out.println(ex);
            return "N/A";
        }

    }

    public void jarExtract(String OS, String _jarFile, String destDir) {
        try {
            Utils utils = new Utils();
            _jarFile = utils.setMineCraft_Versions_X_NativesLocation(OS, _jarFile);
            //_jarFile = _jarFile.replace("https://libraries.minecraft.net", "/home/ammar/NetBeansProjects/TagAPI_3/testx/libraries");
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
            System.out.println(e);
        }
    }

    public String getMineCraftAssetsVirtualLocation(String OS) {
        return (getMineCraftAssetsLocation(OS) + File.separator + "virtual");
    }

    public String getMineCraftAssetsVirtualLegacyLocation(String OS) {
        return (getMineCraftAssetsVirtualLocation(OS) + File.separator + "legacy");
    }

    public String getOS() {
        String OS = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);

        if ((OS.indexOf("mac") >= 0) || (OS.indexOf("darwin") >= 0)) {
            return ("Mac");
        } else if (OS.indexOf("win") >= 0) {
            return ("Windows");
        } else if (OS.indexOf("nux") >= 0) {
            return ("Linux");
        } else {
            //bring support to other OS.
            //we will assume that the OS is based on linux.
            return ("Linux");
        }
    }

    private SecureRandom random = new SecureRandom();

    public String nextSessionId() {
        return new BigInteger(130, random).toString(32);
    }

    public ArrayList removeDuplicates(ArrayList list) {

        // Store unique items in result.
        ArrayList result = new ArrayList();

        // Record encountered Strings in HashSet.
        HashSet set = new HashSet();

        // Loop over argument list.
        for (Object item : list) {

            // If String is not in set, add it to the list and the set.
            if (!set.contains(item)) {
                result.add(item);
                set.add(item);
            }
        }
        return result;
    }

    public void writeLogs(String OS, ArrayList list) {
        try {
            Utils utils = new Utils();
            //get the entire list and append it to string

            File file = new File(utils.getMineCraft_Launcherlogs_txt(OS));

            //recreate the file no matter what
            file.createNewFile();

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            String content = "Logs: \n";
            for (Object item : list) {
                content = content + item + "\n";
            }

            bw.write(content);
            bw.close();

            System.out.println("Done");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}