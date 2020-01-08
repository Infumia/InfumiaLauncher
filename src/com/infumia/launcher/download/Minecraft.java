package com.infumia.launcher.download;

import com.infumia.launcher.InfumiaLauncher;
import javafx.application.Platform;
import javafx.scene.image.Image;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class Minecraft {

    public static Process proc = null;

    public static String playerName = "";
    public static String accessToken = "0";
    public static String uuid = "0";
    public static Image image;

    File gamedir = new File(System.getenv().get("APPDATA") + "/.infumia\\");
    File assestdir = new File(System.getenv().get("APPDATA") + "/.infumia/assets\\");
    File librarydir = new File(System.getenv().get("APPDATA") + "/.infumia/libraries\\");
    File versionsdir = new File(System.getenv().get("APPDATA") + "/.infumia/versions\\");
    File nativedir = new File(System.getenv().get("APPDATA") + "/.infumia/libraries/native\\");

    public ArrayList<String> names;

    String libraryargument;

    public void launchMinecraft(){
        names = new ArrayList<>(Arrays.asList(Objects.requireNonNull(librarydir.list())));
        names.replaceAll( s -> librarydir.getPath() + "\\" + s);
        libraryargument = names.toString().replace(" ","").replace(",",";").replace("[","").replace("]","");

        try {
            proc = Runtime.getRuntime().exec("javaw -Djava.library.path="+nativedir+" -cp "+libraryargument+";"+versionsdir+"\\1.12.jar -Xms8G -Xmx8G -Dlog4j.configurationFile="+getClass().getClassLoader().getResource("log4j2.xml")+" net.minecraft.client.main.Main --username " + playerName + " --version 1.12 --gameDir "+gamedir+" --assetsDir "+assestdir+" --assetIndex 1.12 --uuid "+ uuid +" --accessToken " + accessToken + " --userType mojang");
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
                    Minecraft.proc.destroy();
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
}
