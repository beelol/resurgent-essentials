package com.mcresurgence;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public class PlayerMOTDSender {

    @SubscribeEvent
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.player instanceof EntityPlayerMP) {
            EntityPlayerMP playerMP = (EntityPlayerMP) event.player;
            MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();

            String motd = ServerMOTDConfig.getMotd();
            server.getCommandManager().executeCommand(server, String.format("tellraw %s %s", playerMP.getName(), motd));
        }
    }
}
