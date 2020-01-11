package com.infumia.launcher.objects;

public class MinecraftVersion {

    private String id;
    private String url;

    public MinecraftVersion(String id, String url) {
        this.id = id;
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }
}
