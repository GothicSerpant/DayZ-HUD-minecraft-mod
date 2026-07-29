package net.gothicserpant.dayzgui.network;

import net.gothicserpant.dayzgui.DayzGuiMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModNetwork {
    private static final String PROTOCOL = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(DayzGuiMod.MODID, "network"),
            () -> PROTOCOL,
            PROTOCOL::equals,
            PROTOCOL::equals
    );

    public static void register() {
        int id = 0;
        CHANNEL.messageBuilder(SyncSurvivalPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(SyncSurvivalPacket::encode)
                .decoder(SyncSurvivalPacket::decode)
                .consumerMainThread(SyncSurvivalPacket::handle)
                .add();
    }
}
