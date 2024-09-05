package com.mcresurgence;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

@Mod(modid = ResurgentEssentialsMod.MODID, name = ResurgentEssentialsMod.NAME, version = ResurgentEssentialsMod.VERSION, acceptableRemoteVersions = "*", serverSideOnly = true)
public class ResurgentEssentialsMod {
    public static final String MODID = "resurgent-essentials";
    public static final String NAME = "Resurgent Essentials";
    public static final String VERSION = "0.2.0";

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ServerMOTDConfig.init(event.getModConfigurationDirectory());
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new PlayerMOTDSender());
        MinecraftForge.EVENT_BUS.register(new PlayerRandomSpawnHandler());
    }

    @Mod.EventHandler
    public void onServerStarting(FMLServerStartingEvent event) {
        // Register the reconfig command
        event.registerServerCommand(new ReConfigCommand());
    }
}