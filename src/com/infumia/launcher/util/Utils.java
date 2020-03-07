package com.infumia.launcher.util;

/**
 *    Copyright 2019-2020 Infumia
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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

public class Utils {

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

    public String getMineCraftVersionsLocation(String OS) {
        return (getMineCraftLocation(OS) + File.separator + "versions");
    }

    public String getMineCraftLibrariesLocation(String OS) {
        return (getMineCraftLocation(OS) + File.separator + "libraries");
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

    public String getMineCraftAssetsIndexesLocation(String OS) {
        return (getMineCraftAssetsLocation(OS) + File.separator + "indexes");

    }

    public String getMineCraftAssetsLocation(String OS) {
        return (getMineCraftLocation(OS) + File.separator + "assets");

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

}