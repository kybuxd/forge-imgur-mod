package dev.kybu.mods.imgurupload.data;

import net.minecraft.client.entity.EntityPlayerSP;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Location {

    private double x;
    private double z;
    private double y;
    private float yaw;
    private float pitch;

    public Location(final double x, final double y, final double z, final float yaw, final float pitch) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public static Location fromPlayer(final EntityPlayerSP entityPlayerSP) {
        return new Location(round(entityPlayerSP.posX, 2), round(entityPlayerSP.posY, 2), round(entityPlayerSP.posY, 2), round(entityPlayerSP.rotationYaw, 2), round(entityPlayerSP.rotationPitch, 2));
    }

    private static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    private static float round(float value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.floatValue();
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }
}
