package net.gothicserpant.dayzgui.common;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Mod.EventBusSubscriber(modid = "dayzgui")
public class PlayerSurvivalProvider {
    private static final ResourceLocation ID = new ResourceLocation("dayzgui", "survival");

    @SubscribeEvent
    public static void onAttach(AttachCapabilitiesEvent<Entity> event) {
        if (!(event.getObject() instanceof Player)) return;
        PlayerSurvival inst = new PlayerSurvival();
        ICapabilitySerializable<CompoundTag> provider = new ICapabilitySerializable<>() {
            final LazyOptional<IPlayerSurvival> opt = LazyOptional.of(() -> inst);

            @Nonnull
            @Override
            public <T> LazyOptional<T> getCapability(@Nonnull net.minecraftforge.common.capabilities.Capability<T> cap, @Nullable Direction side) {
                return SurvivalCapability.SURVIVAL_CAP.orEmpty(cap, opt.cast());
            }

            @Override
            public CompoundTag serializeNBT() {
                CompoundTag tag = new CompoundTag();
                tag.putInt("blood", inst.getBlood());
                tag.putInt("hunger", inst.getHunger());
                tag.putInt("thirst", inst.getThirst());
                tag.putFloat("temperature", inst.getTemperature());
                tag.putByte("flags", inst.getFlags());
                tag.putBoolean("bleeding", inst.isBleeding());
                tag.putInt("bleedRate", inst.getBleedRate());
                return tag;
            }

            @Override
            public void deserializeNBT(CompoundTag nbt) {
                inst.setBlood(nbt.getInt("blood"));
                inst.setHunger(nbt.getInt("hunger"));
                inst.setThirst(nbt.getInt("thirst"));
                inst.setTemperature(nbt.getFloat("temperature"));
                inst.setFlags(nbt.getByte("flags"));
                inst.setBleeding(nbt.getBoolean("bleeding"));
                inst.setBleedRate(nbt.getInt("bleedRate"));
            }
        };
        event.addCapability(ID, provider);
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        event.getOriginal().getCapability(SurvivalCapability.SURVIVAL_CAP).ifPresent(orig -> {
            event.getEntity().getCapability(SurvivalCapability.SURVIVAL_CAP).ifPresent(newCap -> {
                newCap.setBlood(orig.getBlood());
                newCap.setHunger(orig.getHunger());
                newCap.setThirst(orig.getThirst());
                newCap.setTemperature(orig.getTemperature());
                newCap.setFlags(orig.getFlags());
                newCap.setBleeding(orig.isBleeding());
                newCap.setBleedRate(orig.getBleedRate());
            });
        });
    }
}
