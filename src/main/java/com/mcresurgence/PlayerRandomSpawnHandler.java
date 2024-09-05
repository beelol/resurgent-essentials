package com.mcresurgence;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import java.util.Random;

public class PlayerRandomSpawnHandler {

    @SubscribeEvent
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.player instanceof EntityPlayerMP)) return;

        EntityPlayerMP playerMP = (EntityPlayerMP) event.player;
        NBTTagCompound playerData = playerMP.getEntityData();

        // Check if this is the player's first join
        if (!playerData.hasKey("HasSpawned")) {
            if (ServerMOTDConfig.isRandomSpawnEnabled()) {
                setRandomSpawn(playerMP);
            }

            // Mark that the player has spawned for the first time
            playerData.setBoolean("HasSpawned", true);
        }
    }

    @SubscribeEvent
    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (!(event.player instanceof EntityPlayerMP)) return;

        EntityPlayerMP playerMP = (EntityPlayerMP) event.player;

        // Check if random spawning is enabled and the player does not have a bed spawn
        if (ServerMOTDConfig.isRandomSpawnEnabled() && !hasValidBedSpawn(playerMP)) {
            setRandomSpawn(playerMP);
        }
    }

    private void setRandomSpawn(EntityPlayerMP player) {
        World world = player.getEntityWorld();
        int radius = ServerMOTDConfig.getSpawnRadius(); // Get the radius from config
        BlockPos spawnPoint = getRandomSpawnPoint(world, player.getPosition(), radius);
        player.setPositionAndUpdate(spawnPoint.getX(), spawnPoint.getY(), spawnPoint.getZ());
    }

    private BlockPos getRandomSpawnPoint(World world, BlockPos center, int radius) {
        Random rand = new Random();
        int x = center.getX() + rand.nextInt(radius * 2) - radius;
        int z = center.getZ() + rand.nextInt(radius * 2) - radius;
        int y = world.getHeight(x, z); // Gets the highest solid block at (x, z)
        return new BlockPos(x, y, z);
    }

    private boolean hasValidBedSpawn(EntityPlayerMP player) {
        // Get the player's spawn dimension
        int dimension = player.dimension;

        // Check if the bed location is still valid and safe
        World world = player.getEntityWorld();
        boolean isSpawnForced = player.isSpawnForced(dimension);
        BlockPos safeSpawn = EntityPlayerMP.getBedSpawnLocation(world, player.getBedLocation(dimension), isSpawnForced);

        // Return true if a valid bed spawn location is found
        return safeSpawn != null;
    }
}
