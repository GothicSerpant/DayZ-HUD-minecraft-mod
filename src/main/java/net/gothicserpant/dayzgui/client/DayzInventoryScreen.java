package net.gothicserpant.dayzgui.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class DayzInventoryScreen extends InventoryScreen {
    private static final ResourceLocation BG = new ResourceLocation("dayzgui", "textures/gui/dayz_inventory.png");

    public DayzInventoryScreen(LocalPlayer player, Inventory inv, Component title) {
        super(player, inv, title);
        this.imageWidth = 256;
        this.imageHeight = 220;
    }

    @Override
    public void render(GuiGraphics gg, int mouseX, int mouseY, float partialTicks) {
        // draw custom background first
        gg.renderTexture(BG, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);
        // draw vanilla slots/items/etc.
        super.render(gg, mouseX, mouseY, partialTicks);
    }
}
