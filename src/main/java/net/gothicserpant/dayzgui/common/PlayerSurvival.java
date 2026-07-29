package net.gothicserpant.dayzgui.common;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;

public class PlayerSurvival implements IPlayerSurvival {
    // Defaults and maxima
    private static final int MAX_BLOOD = 5000;
    private static final int MAX_HUNGER = 20000;
    private static final int MAX_THIRST = 100;

    private int blood = MAX_BLOOD;
    private int hunger = MAX_HUNGER;
    private int thirst = MAX_THIRST;
    private float temperature = 37.0f;
    private byte flags = 0;

    private boolean bleeding = false;
    private int bleedRate = 0; // blood lost per interval when bleeding

    // Internal change tracking
    private int lastSyncedBlood = blood;
    private int lastSyncedHunger = hunger;
    private int lastSyncedThirst = thirst;
    private float lastSyncedTemp = temperature;

    @Override public int getBlood() { return blood; }
    @Override public void setBlood(int v) { blood = Math.max(0, Math.min(MAX_BLOOD, v)); }
    @Override public int getHunger() { return hunger; }
    @Override public void setHunger(int v) { hunger = Math.max(0, Math.min(MAX_HUNGER, v)); }
    @Override public int getThirst() { return thirst; }
    @Override public void setThirst(int v) { thirst = Math.max(0, Math.min(MAX_THIRST, v)); }
    @Override public float getTemperature() { return temperature; }
    @Override public void setTemperature(float t) { temperature = t; }

    @Override public byte getFlags() { return flags; }
    @Override public void setFlags(byte f) { flags = f; }

    @Override public boolean isBleeding() { return bleeding; }
    @Override public void setBleeding(boolean b) { bleeding = b; if (!b) bleedRate = 0; }
    @Override public int getBleedRate() { return bleedRate; }
    @Override public void setBleedRate(int r) { bleedRate = Math.max(0, r); }

    /**
     * Called on server every configured interval (default 20 ticks = 1s).
     * Returns true if any major value moved enough to warrant a sync.
     */
    @Override
    public boolean tickServer(ServerPlayer player, int currentServerTick) {
        boolean dirty = false;

        // Basic decay rates (defaults; you can make them configurable):
        final int hungerDecay = 1; // per interval
        final int thirstDecay = 1; // per interval
        final int bleedingLoss = bleeding ? Math.max(1, bleedRate) : 0;

        // Apply decay
        int oldHunger = hunger;
        int oldThirst = thirst;
        int oldBlood = blood;
        float oldTemp = temperature;

        hunger = Math.max(0, hunger - hungerDecay);
        thirst = Math.max(0, thirst - thirstDecay);
        blood = Math.max(0, blood - bleedingLoss);

        // Temperature drift slowly to comfortable 37.0 (simple model)
        float ambient = estimateAmbientTemperature(player);
        float diff = ambient - temperature;
        temperature += Math.signum(diff) * 0.05f; // small drift per tick

        // If extremely low thirst/hunger, drain blood slowly
        if (thirst <= 5) blood = Math.max(0, blood - 2);
        if (hunger <= 100) blood = Math.max(0, blood - 1);

        // If blood is below critical threshold, apply health damage
        if (blood <= 1000) {
            // small direct damage to vanilla health to simulate shock
            player.hurt(DamageSource.GENERIC, 0.5f);
        }

        // determine if values changed enough to sync (delta thresholds)
        if (Math.abs(blood - lastSyncedBlood) >= 10) { dirty = true; lastSyncedBlood = blood; }
        if (Math.abs(hunger - lastSyncedHunger) >= 200) { dirty = true; lastSyncedHunger = hunger; }
        if (Math.abs(thirst - lastSyncedThirst) >= 2) { dirty = true; lastSyncedThirst = thirst; }
        if (Math.abs(temperature - lastSyncedTemp) >= 0.5f) { dirty = true; lastSyncedTemp = temperature; }

        // If blood hit zero, let vanilla damage kill the player over time; you could set instant death if desired

        // Return true if we need to sync the client
        return dirty || (currentServerTick % 100 == 0); // heartbeat every 100 ticks (5s if interval is 20 ticks)
    }

    private float estimateAmbientTemperature(ServerPlayer player) {
        // Lightweight ambient estimate: basic mapping by biome temperature where available
        try {
            return (float) player.level.getBiome(player.blockPosition()).value().getBaseTemperature() * 10f + 27f;
        } catch (Exception e) {
            return 37.0f;
        }
    }
}
