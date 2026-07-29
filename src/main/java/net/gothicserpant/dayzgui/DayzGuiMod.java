package net.gothicserpant.dayzgui;

import net.gothicserpant.dayzgui.common.SurvivalCapability;
import net.gothicserpant.dayzgui.network.ModNetwork;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Mod(DayzGuiMod.MODID)
public class DayzGuiMod {
    public static final String MODID = "dayzgui";

    public DayzGuiMod() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::commonSetup);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Register capability & network on common setup
        SurvivalCapability.register();
        ModNetwork.register();
    }
}
