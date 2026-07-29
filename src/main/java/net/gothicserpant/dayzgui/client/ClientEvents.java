package net.gothicserpant.dayzgui.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.gothicserpant.dayzgui.common.SurvivalCapability;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenOpenEvent;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = "dayzgui")
public class ClientEvents {
    private static final Minecraft mc = Minecraft.getInstance();

    // Atlas resource and frame dimensions (we assume 32x32 frames)
    private static final ResourceLocation ATLAS = new ResourceLocation("dayzgui", "textures/gui/icons_atlas.png");
    private static final int FRAME_W = 32;
    private static final int FRAME_H = 32;
    private static final int COLS = 6;
    private static final int ROWS = 4;
    private static final int ATLAS_W = FRAME_W * COLS;
    private static final int ATLAS_H = FRAME_H * ROWS;

    private static final int HUD_X = 10;
    private static final int HUD_Y = 10;
    private static final int ICON_SPACING = FRAME_H + 4;

    @SubscribeEvent
    public static void onScreenOpen(ScreenOpenEvent event) {
        Screen s = event.getScreen();
        if (s instanceof InventoryScreen) {
            Player player = mc.player;
            if (player != null) {
                DayzInventoryScreen dayzScreen = new DayzInventoryScreen(player, player.getInventory(), Component.translatable("container.inventory"));
                event.setNewScreen(dayzScreen);
            }
        }
    }

    @SubscribeEvent
    public static void onRenderGuiPost(RenderGuiEvent.Post event) {
        GuiGraphics gg = event.getGuiGraphics();
        if (mc.player == null || mc.level == null) return;

        // Read cached values (kept in sync from server)
        int blood = ClientSurvivalCache.getBlood(); // 0..5000
        int hunger = ClientSurvivalCache.getHunger(); // 0..20000
        int thirst = ClientSurvivalCache.getThirst(); // 0..100
        float temp = ClientSurvivalCache.getTemperature(); // e.g., ~37

        // draw icons from atlas
        // blood row = 0, food row = 1, drink row = 2, temp row = 3
        int bloodIndex = pctToIndex(blood / 5000f, COLS);
        int foodIndex = pctToIndex(hunger / 20000f, COLS);
        int drinkIndex = pctToIndex(thirst / 100f, COLS);
        // map temperature from 30..42 to 0..1
        float minT = 30f, maxT = 42f;
        float tempPct = (temp - minT) / (maxT - minT);
        int tempIndex = pctToIndex(tempPct, COLS);

        // render each icon: compute u/v
        renderAtlasFrame(gg, ATLAS, bloodIndex, 0, HUD_X, HUD_Y);
        renderAtlasFrame(gg, ATLAS, foodIndex, 1, HUD_X, HUD_Y + ICON_SPACING);
        renderAtlasFrame(gg, ATLAS, drinkIndex, 2, HUD_X, HUD_Y + ICON_SPACING * 2);
        renderAtlasFrame(gg, ATLAS, tempIndex, 3, HUD_X, HUD_Y + ICON_SPACING * 3);

        // Optional compatibility info
        if (ModList.get().isLoaded("vics_point_blank")) {
            gg.drawString(mc.font, "Vics PB compatibility: active", HUD_X, mc.getWindow().getGuiScaledHeight() - 30, 0xFFAAFFAA);
        }
    }

    private static void renderAtlasFrame(GuiGraphics gg, ResourceLocation atlas, int col, int row, int x, int y) {
        int u = col * FRAME_W;
        int v = row * FRAME_H;
        gg.renderTexture(atlas, x, y, u, v, FRAME_W, FRAME_H, ATLAS_W, ATLAS_H);
    }

    private static int pctToIndex(float pct, int iconsCount) {
        float clamped = Math.max(0f, Math.min(1f, pct));
        int idx = (int) Math.floor((1.0f - clamped) * (iconsCount - 1)); // full -> 0
        return Math.min(iconsCount - 1, Math.max(0, idx));
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        // Client does not run gameplay logic here; server remains authoritative.
        // We can run trivial prediction or smoothing if desired.
    }
}
