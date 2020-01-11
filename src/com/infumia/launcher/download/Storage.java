package com.infumia.launcher.download;

import com.infumia.launcher.util.Local;
import com.infumia.launcher.util.Utils;
import org.json.JSONArray;
import org.json.JSONObject;

public class Storage {

    private String version = "1.8";
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
    private int totalLibraries = 0;
    private String operationgSystem = utils.getOS();

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
}
