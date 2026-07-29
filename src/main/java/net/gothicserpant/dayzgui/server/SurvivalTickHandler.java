package net.gothicserpant.dayzgui.server;

import net.gothicserpant.dayzgui.common.IPlayerSurvival;
import net.gothicserpant.dayzgui.common.SurvivalCapability;
import net.gothicserpant.dayzgui.network.ModNetwork;
import net.gothicserpant.dayzgui.network.SyncSurvivalPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraft.world.entity.player.Player;

@Mod.EventBusSubscriber(modid = "dayzgui", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SurvivalTickHandler {
    private static final int SERVER_INTERVAL_TICKS = 20; // run logic every 20 player ticks (~1s)

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Player p = event.player;
        if (p.level.isClientSide) return;
        // run only every SERVER_INTERVAL_TICKS player ticks
        if ((p.tickCount % SERVER_INTERVAL_TICKS) != 0) return;

        p.getCapability(SurvivalCapability.SURVIVAL_CAP).ifPresent(cap -> {
            boolean needsSync = cap.tickServer((ServerPlayer) p, p.tickCount / SERVER_INTERVAL_TICKS);
            if (needsSync) {
                // build and send compact packet
                int blood = cap.getBlood();
                int hunger = cap.getHunger();
                int thirst = cap.getThirst();
                short tempScaled = (short) (cap.getTemperature() * 10); // scaled to one decimal
                byte flags = cap.getFlags();
                ModNetwork.CHANNEL.send(net.minecraftforge.network.PacketDistributor.PLAYER.with(() -> (ServerPlayer) p),
                        new SyncSurvivalPacket(blood, hunger, thirst, tempScaled, flags));
            }
        });
    }
}
