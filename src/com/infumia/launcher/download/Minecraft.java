package com.infumia.launcher.download;

import com.infumia.launcher.InfumiaLauncher;
import com.infumia.launcher.util.Local;
import com.infumia.launcher.util.Utils;
import com.sun.javafx.PlatformUtil;
import javafx.application.Platform;
import javafx.scene.image.Image;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class Minecraft {

    private String version;
    private Utils utils;
    private Local local;
    private Storage storage;

    public Minecraft(Storage storage) {
        this.version = storage.getVersion();
        this.utils = storage.getUtils();
        this.local = storage.getLocal();
        this.storage = storage;
    }

    private Process proc = null;

    public static String playerName = "";
    public static String accessToken = "0";
    public static String uuid = "0";
    public static Image image;

    private File librarydir = new File(getMineCraftLocation() + "/libraries\\");

    public ArrayList<String> names;


    public void launchMinecraft(){
        names = new ArrayList<>(Arrays.asList(Objects.requireNonNull(librarydir.list())));
        names.replaceAll( s -> librarydir.getPath() + "\\" + s);

        String OperatingSystemToUse = utils.getOS();
        String gameDirectory = utils.getMineCraftGameDirectoryLocation(OperatingSystemToUse);
        String AssetsRoot = utils.getMineCraftAssetsRootLocation(OperatingSystemToUse);
        setMemory(storage.getPrefRAM());
        setMinMemory(storage.getPrefRAM());

        //String HalfArgumentTemplate = local.readJson_minecraftArguments(utils.getMineCraft_Versions_X_X_json(OperatingSystemToUse, VersionToUse));
        int Xmx = this.getMemory();
        int Xms = this.getMinMemory();
        int Width = this.getWidth();
        int Height = this.getHeight();
        String JavaPath = this.getJavaPath();
        String JVMArgument = this.getJVMArgument();
        String versionName = local.readJson_id(utils.getMineCraft_Versions_X_X_json(OperatingSystemToUse, version));
        String assetsIndexId = local.readJson_assets(utils.getMineCraft_Versions_X_X_json(OperatingSystemToUse, version));

        String VersionType = this.getVersionData();
        String GameAssets = utils.getMineCraftAssetsVirtualLegacyLocation(OperatingSystemToUse);
        String AuthSession = "OFFLINE";

        String[] HalfArgument = local.generateMinecraftArguments(OperatingSystemToUse, playerName, versionName, gameDirectory, AssetsRoot, assetsIndexId, uuid, accessToken, "{}", "mojang", VersionType, GameAssets, AuthSession);
        String MinecraftJar = utils.getMineCraft_Versions_X_X_jar(OperatingSystemToUse, version);
        String FullLibraryArgument = local.generateLibrariesArguments(OperatingSystemToUse) + utils.getArgsDiv(OperatingSystemToUse) + MinecraftJar;
        String mainClass = local.readJson_mainClass(utils.getMineCraft_Versions_X_X_json(OperatingSystemToUse, version));
        String NativesDir = utils.getMineCraft_Versions_X_Natives(OperatingSystemToUse, version);

        String cmds[] = {"-Xms" + Xms + "M", "-Xmx" + Xmx + "M", "-XX:HeapDumpPath=MojangTricksIntelDriversForPerformance_javaw.exe_minecraft.exe.heapdump", "-Djava.library.path=" + NativesDir, "-cp", FullLibraryArgument, mainClass, "--width", String.valueOf(Width), "--height", String.valueOf(Height)};
        //put jvm arguments here
        String[] JVMArguments = JVMArgument.split(" ");
        //we now have all the arguments. merge cmds with JVMArguments
        if (!JVMArgument.isEmpty()) {
            //no need to join.
            cmds = Stream.concat(Arrays.stream(JVMArguments), Arrays.stream(cmds)).toArray(String[]::new);
        }
        String javaPathArr[] = {JavaPath};
        //merge javapath back to cmds
        cmds = Stream.concat(Arrays.stream(javaPathArr), Arrays.stream(cmds)).toArray(String[]::new);

        String[] finalArgs = Stream.concat(Arrays.stream(cmds), Arrays.stream(HalfArgument)).toArray(String[]::new);

        try {
            proc = Runtime.getRuntime().exec(finalArgs);
        } catch (IOException e) {
            e.printStackTrace();
        }


        if(proc != null){
            BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line;
            try {
                while((line = reader.readLine()) != null) {
                    System.out.println(line);
                }

                if (reader.readLine() == null) {
                    proc.destroy();
                    InfumiaLauncher.getInfumiaLauncher().stop();
                    Platform.exit();
                    System.exit(0);
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(0);
            }
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

    private String jvmArgument = "";
    public void setJVMArgument(String jvmArgument_) {
        jvmArgument = jvmArgument_;
    }

    private String getJVMArgument() {
        return jvmArgument;
    }

    private String javaPath = "java";
    public void setJavaPath(String javaPath_) {
        javaPath = javaPath_;
    }

    private String getJavaPath() {
        return javaPath;
    }

    private int width = 854;

    public void setWidth(int width_) {
        width = width_;
    }

    private int getWidth() {
        return width;
    }

    private int height = 480;

    public void setHeight(int height_) {
        height = height_;
    }

    private int getHeight() {
        return height;
    }

    private int memory = 2048;

    public void setMemory(int memory_) {
        memory = memory_;
    }

    private int getMemory() {
        return memory;
    }

    private int minMemory = 1024;

    public void setMinMemory(int memory_) {
        minMemory = memory_;
    }

    private int getMinMemory() {
        return minMemory;
    }

    private String versionData = "#infumialauncher";

    public void setVersionData(String versionData_) {
        versionData = versionData_;
    }

    private String getVersionData() {
        return versionData;
    }
}
