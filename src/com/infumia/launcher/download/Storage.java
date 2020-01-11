package com.infumia.launcher.download;

import com.infumia.launcher.util.Local;
import com.infumia.launcher.util.Utils;
import org.json.JSONArray;
import org.json.JSONObject;

public class Storage {

    private String version;
    private JSONObject versionObject;
    private String clientUrl;
    private Utils utils;
    private Local local;
    private JSONObject assets;
    private int downloadedAssets;
    private double clientDownloadPercent;
    private JSONArray libraries;
    private int downloadedLib;
    private int downloadedNatives;
    private int totalLibraries;
    private String operationgSystem;

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

    public void setLocal(Local local) {
        this.local = local;
    }

    public void setUtils(Utils utils) {
        this.utils = utils;
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
