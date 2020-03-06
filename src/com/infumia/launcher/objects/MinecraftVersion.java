package com.infumia.launcher.objects;

public class MinecraftVersion {

    private String id;
    private String clientUrl;
    private String clientSha1;
    private String assetsUrl;

    public MinecraftVersion(String id, String clientUrl, String clientSha1, String assetsUrl) {
        this.id = id;
        this.clientUrl = clientUrl;
        this.clientSha1 = clientSha1;
        this.assetsUrl = assetsUrl;
    }

    public String getId() {
        return id;
    }

    public String getClientUrl() {
        return clientUrl;
    }

    public String getClientSha1() {
        return clientSha1;
    }

    public String getAssetsUrl() {
        return assetsUrl;
    }
}
