package net.gothicserpant.dayzgui.common;

import net.gothicserpant.dayzgui.network.ModNetwork;
import net.gothicserpant.dayzgui.network.SyncSurvivalPacket;
import net.minecraft.core.SoundSource;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PacketDistributor;

public class BandageItem extends Item {
    public BandageItem(Properties props) {
        super(props);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            player.getCapability(SurvivalCapability.SURVIVAL_CAP).ifPresent(cap -> {
                cap.setBleeding(false);
                cap.setBleedRate(0);
                cap.setBlood(cap.getBlood() + 500); // restore some blood
                if (player instanceof ServerPlayer sp) {
                    ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> sp),
                            new SyncSurvivalPacket(cap.getBlood(), cap.getHunger(), cap.getThirst(), (short) (cap.getTemperature() * 10), cap.getFlags()));
                }
            });

            level.playSound(null, player.blockPosition(), SoundEvents.HONEY_DRINK, SoundSource.PLAYERS, 1.0f, 1.0f);
            if (!player.getAbilities().instabuild) stack.shrink(1);
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
        }
        return InteractionResultHolder.success(stack);
    }
}
