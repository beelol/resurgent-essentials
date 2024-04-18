package com.mcresurgence;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = ResurgentEssentialsMod.MODID, name = ResurgentEssentialsMod.NAME, version = ResurgentEssentialsMod.VERSION, acceptableRemoteVersions = "*", serverSideOnly = true)
public class ResurgentEssentialsMod {
    public static final String MODID = "resurgent-essentials";
    public static final String NAME = "Resurgent Essentials";
    public static final String VERSION = "0.1.0";

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ServerMOTDConfig.init(event.getModConfigurationDirectory());
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new PlayerMOTDSender());
    }
}