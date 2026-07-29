package net.gothicserpant.dayzgui.client;

public class ClientSurvivalCache {
    private static volatile int blood = 5000;
    private static volatile int hunger = 20000;
    private static volatile int thirst = 100;
    private static volatile float temperature = 37f;
    private static volatile byte flags = 0;

    public static void set(int b, int h, int t, float temp, byte f) {
        blood = b; hunger = h; thirst = t; temperature = temp; flags = f;
    }

    public static int getBlood() { return blood; }
    public static int getHunger() { return hunger; }
    public static int getThirst() { return thirst; }
    public static float getTemperature() { return temperature; }
    public static byte getFlags() { return flags; }
}
