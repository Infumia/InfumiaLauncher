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
package com.infumia.launcher.download;

import com.infumia.launcher.util.Local;
import com.infumia.launcher.util.Utils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

public class Storage {

    private String version = "1.15";
    private String assetVersion = "1.15";
    private JSONObject versionObject = new JSONObject();
    private String clientUrl = "";
    private Utils utils = new Utils();
    private Local local = new Local();
    private JSONObject assets = new JSONObject();
    private int downloadedAssets = 0;
    private double clientDownloadPercent = 0.0D;
    private JSONArray libraries = new JSONArray();
    private int downloadedLib = 0;
    private int downloadedNatives = 0;
    private int totalLibraries = 1;
    private String operationgSystem = utils.getOS();
    private HashMap<String, String> versionsList = new HashMap<>();
    private int prefRAM = 1024;
    private String remoteHash = "";
    private boolean illegalVersion = false;

    public String getVersion() {
        return version;
    }

    public String getClientUrl() {
        return clientUrl;
    }

    public void setClientUrl(String clientUrl) {
        this.clientUrl = clientUrl;
    }

    public JSONObject getVersionObject() {
        return versionObject;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setVersionObject(JSONObject versionObject) {
        this.versionObject = versionObject;
    }

    public Local getLocal() {
        return local;
    }

    public Utils getUtils() {
        return utils;
    }

    public JSONObject getAssets() {
        return assets;
    }

    public void setAssets(JSONObject assets) {
        this.assets = assets;
    }

    public int getDownloadedAssets() {
        return downloadedAssets;
    }

    public double getClientDownloadPercent() {
        return clientDownloadPercent;
    }

    public void setClientDownloadPercent(double clientDownloadPercent) {
        this.clientDownloadPercent = clientDownloadPercent;
    }

    public void setDownloadedAssets(int downloadedAssets) {
        this.downloadedAssets = downloadedAssets;
    }

    public JSONArray getLibraries() {
        return libraries;
    }

    public void setLibraries(JSONArray libraries) {
        this.libraries = libraries;
    }

    public void setDownloadedLib(int downloadedLib) {
        this.downloadedLib = downloadedLib;
    }

    public void setDownloadedNatives(int downloadedNatives) {
        this.downloadedNatives = downloadedNatives;
    }

    public int getDownloadedLib() {
        return downloadedLib;
    }

    public int getDownloadedNatives() {
        return downloadedNatives;
    }

    public int getTotalLibraries() {
        return totalLibraries;
    }

    public void setTotalLibraries(int totalLibraries) {
        this.totalLibraries = totalLibraries;
    }

    public void setOperationgSystem(String operationgSystem) {
        this.operationgSystem = operationgSystem;
    }

    public String getOperationgSystem() {
        return operationgSystem;
    }

    public HashMap<String, String> getVersionsList() {
        return versionsList;
    }

    public void setVersionsList(HashMap<String, String> versionsList) {
        this.versionsList = versionsList;
    }

    public int getPrefRAM() {
        return prefRAM;
    }

    public void setPrefRAM(int prefRAM) {
        this.prefRAM = prefRAM;
    }

    public String getRemoteHash() {
        return remoteHash;
    }

    public void setRemoteHash(String remoteHash) {
        this.remoteHash = remoteHash;
    }

    public void setIllegalVersion(boolean illegalVersion) {
        this.illegalVersion = illegalVersion;
    }

    public boolean isIllegalVersion() {
        return illegalVersion;
    }

    public String getAssetVersion() {
        return assetVersion;
    }

    public void setAssetVersion(String assetVersion) {
        this.assetVersion = assetVersion;
    }

}

