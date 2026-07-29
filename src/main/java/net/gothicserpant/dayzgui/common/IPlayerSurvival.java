package net.gothicserpant.dayzgui.common;

import net.minecraft.server.level.ServerPlayer;

public interface IPlayerSurvival {
    int getBlood();
    void setBlood(int v);

    int getHunger();
    void setHunger(int v);

    int getThirst();
    void setThirst(int v);

    float getTemperature();
    void setTemperature(float t);

    byte getFlags();
    void setFlags(byte f);

    boolean isBleeding();
    void setBleeding(boolean b);
    int getBleedRate();
    void setBleedRate(int r);

    /**
     * Server tick called on interval; returns true if values changed and need to sync to client.
     */
    boolean tickServer(ServerPlayer player, int currentServerTick);

    // NBT methods are handled by storage registration above; we still expose convenience serialize/deserialize if needed
}
