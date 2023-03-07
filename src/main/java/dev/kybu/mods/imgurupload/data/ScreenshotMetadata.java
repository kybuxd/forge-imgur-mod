package dev.kybu.mods.imgurupload.data;

import java.io.File;

public class ScreenshotMetadata {

    private final String imgurUrl;
    private final String imageFile;
    private final Location location;
    private final float health;
    private final String name;
    private final String worldName;
    private final int level;
    private final long timestamp;
    private final String serverIp;

    public ScreenshotMetadata(final String imgurUrl, final String imageFile, final Location location, final float health, final String name, final String worldName, final int level, final long timestamp, final String serverIp) {
        this.imgurUrl = imgurUrl;
        this.imageFile = imageFile;
        this.location = location;
        this.health = health;
        this.name = name;
        this.worldName = worldName;
        this.level = level;
        this.timestamp = timestamp;
        this.serverIp = serverIp;
    }

    public String getImgurUrl() {
        return imgurUrl;
    }

    public String getImageFile() {
        return imageFile;
    }

    public Location getLocation() {
        return location;
    }

    public float getHealth() {
        return health;
    }

    public int getLevel() {
        return level;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getServerIp() {
        return serverIp;
    }
}
