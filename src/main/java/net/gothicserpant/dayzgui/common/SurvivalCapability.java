package net.gothicserpant.dayzgui.common;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public final class SurvivalCapability {
    public static final Capability<IPlayerSurvival> SURVIVAL_CAP = CapabilityManager.get(new CapabilityToken<>() {});

    public static void register() {
        CapabilityManager.INSTANCE.register(IPlayerSurvival.class, new Capability.IStorage<IPlayerSurvival>() {
            @Override
            public CompoundTag writeNBT(Capability<IPlayerSurvival> capability, IPlayerSurvival instance, net.minecraft.core.Direction side) {
                CompoundTag tag = new CompoundTag();
                tag.putInt("blood", instance.getBlood());
                tag.putInt("hunger", instance.getHunger());
                tag.putInt("thirst", instance.getThirst());
                tag.putFloat("temperature", instance.getTemperature());
                tag.putByte("flags", instance.getFlags());
                tag.putBoolean("bleeding", instance.isBleeding());
                tag.putInt("bleedRate", instance.getBleedRate());
                return tag;
            }

            @Override
            public void readNBT(Capability<IPlayerSurvival> capability, IPlayerSurvival instance, net.minecraft.core.Direction side, net.minecraft.nbt.INBT nbt) {
                CompoundTag tag = (CompoundTag) nbt;
                instance.setBlood(tag.getInt("blood"));
                instance.setHunger(tag.getInt("hunger"));
                instance.setThirst(tag.getInt("thirst"));
                instance.setTemperature(tag.getFloat("temperature"));
                instance.setFlags(tag.getByte("flags"));
                instance.setBleeding(tag.getBoolean("bleeding"));
                instance.setBleedRate(tag.getInt("bleedRate"));
            }
        }, PlayerSurvival::new);
    }
}
