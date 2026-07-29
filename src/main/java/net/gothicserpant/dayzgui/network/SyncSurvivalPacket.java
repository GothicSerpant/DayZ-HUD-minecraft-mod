package net.gothicserpant.dayzgui.network;

import net.gothicserpant.dayzgui.client.ClientSurvivalCache;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record SyncSurvivalPacket(int blood, int hunger, int thirst, short tempScaled, byte flags) {
    public static void encode(SyncSurvivalPacket pkt, FriendlyByteBuf buf) {
        buf.writeShort(pkt.blood);
        buf.writeShort(pkt.hunger);
        buf.writeShort(pkt.thirst);
        buf.writeShort(pkt.tempScaled);
        buf.writeByte(pkt.flags);
    }

    public static SyncSurvivalPacket decode(FriendlyByteBuf buf) {
        return new SyncSurvivalPacket(buf.readShort(), buf.readShort(), buf.readShort(), buf.readShort(), buf.readByte());
    }

    public static void handle(SyncSurvivalPacket pkt, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> {
            // Update client-side cache used by HUD
            ClientSurvivalCache.set(pkt.blood(), pkt.hunger(), pkt.thirst(), pkt.tempScaled() / 10f, pkt.flags());
        });
        ctx.setPacketHandled(true);
    }
}
