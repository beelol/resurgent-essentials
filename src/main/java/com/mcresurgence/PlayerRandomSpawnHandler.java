package com.mcresurgence;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLLog;
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
                // Set the spawn point before the player is fully initialized in the world
                BlockPos randomSpawnPoint = getRandomSpawnPoint(playerMP.getEntityWorld(), playerMP.getPosition(), ServerMOTDConfig.getSpawnRadius());
                playerMP.setSpawnPoint(randomSpawnPoint, true);  // Set the new spawn point for the player

                playerMP.connection.setPlayerLocation(randomSpawnPoint.getX() + 0.5, randomSpawnPoint.getY(), randomSpawnPoint.getZ() + 0.5, playerMP.rotationYaw, playerMP.rotationPitch);

                playerMP.attemptTeleport(randomSpawnPoint.getX() + 0.5, randomSpawnPoint.getY(), randomSpawnPoint.getZ() + 0.5);

            }

            // Mark that the player has spawned for the first time
            playerData.setBoolean("HasSpawned", true);
        } else {
            FMLLog.info("[Resurgent Essentials] Player '{}' is rejoining; no random teleportation.", playerMP.getName());
        }
    }


    @SubscribeEvent
    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (!(event.player instanceof EntityPlayerMP)) return;

        EntityPlayerMP playerMP = (EntityPlayerMP) event.player;

        // Log the event to confirm it is being triggered
        FMLLog.info("[Resurgent Essentials] Player '{}' has respawned.", playerMP.getName());

        // Check if random spawning is enabled and the player does not have a bed spawn
        if (ServerMOTDConfig.isRandomSpawnEnabled() && !hasValidBedSpawn(playerMP)) {
            FMLLog.info("[Resurgent Essentials] Random spawn is enabled and player '{}' does not have a valid bed spawn. Teleporting to random location.", playerMP.getName());

            // Set the spawn point before the player respawns
            BlockPos randomSpawnPoint = getRandomSpawnPoint(playerMP.getEntityWorld(), playerMP.getPosition(), ServerMOTDConfig.getSpawnRadius());

            playerMP.connection.setPlayerLocation(randomSpawnPoint.getX() + 0.5, randomSpawnPoint.getY(), randomSpawnPoint.getZ() + 0.5, playerMP.rotationYaw, playerMP.rotationPitch);

            playerMP.attemptTeleport( randomSpawnPoint.getX() + 0.5, randomSpawnPoint.getY(), randomSpawnPoint.getZ() + 0.5);

        } else {
            FMLLog.info("[Resurgent Essentials] Random spawn is disabled or player '{}' has a valid bed spawn. No random teleportation.", playerMP.getName());
        }
    }


//    private void setRandomSpawn(EntityPlayerMP player) {
//        World world = player.getEntityWorld();
//        int radius = ServerMOTDConfig.getSpawnRadius(); // Get the radius from config
//        BlockPos spawnPoint = getRandomSpawnPoint(world, player.getPosition(), radius);
//        player.setPositionAndUpdate(spawnPoint.getX(), spawnPoint.getY(), spawnPoint.getZ());
//    }

    private BlockPos getRandomSpawnPoint(World world, BlockPos center, int radius) {
        Random rand = new Random();
        int x = center.getX() + rand.nextInt(radius * 2) - radius;
        int z = center.getZ() + rand.nextInt(radius * 2) - radius;

        // Get the highest solid or liquid block at the random X and Z position
        BlockPos topBlockPos = world.getTopSolidOrLiquidBlock(new BlockPos(x, 0, z));

        int y = topBlockPos.getY(); // Y coordinate of the highest solid or liquid block

        if (world.getBlockState(topBlockPos).getMaterial().isLiquid() || world.getBlockState(topBlockPos).getBlock().isBurning(world, topBlockPos)) {
            // Retry finding a safe spawn if the top block is liquid or burning
            return getRandomSpawnPoint(world, center, radius);
        }

        // Ensure a safe height above ground level
        return new BlockPos(x, y, z);
    }

    private boolean hasValidBedSpawn(EntityPlayerMP player) {
        // Get the player's spawn dimension
        int dimension = player.dimension;

        // Check if the player has a valid world instance
        World world = player.getEntityWorld();
        if (world == null) {
            FMLLog.warning("[Resurgent Essentials] Player '%s' is in an invalid world. Cannot check for a valid bed spawn.", player.getName());
            return false; // No valid world, cannot have a valid bed spawn
        }

        // Get the bed location and check if it exists
        BlockPos bedLocation = player.getBedLocation(dimension);
        if (bedLocation == null) {
            FMLLog.warning("[Resurgent Essentials] Player '%s' does not have a bed location set in dimension %d.", player.getName(), dimension);
            return false; // No bed location set
        }

        // Check if the bed location is still valid and safe
        boolean isSpawnForced = player.isSpawnForced(dimension);
        BlockPos safeSpawn = EntityPlayerMP.getBedSpawnLocation(world, bedLocation, isSpawnForced);
        if (safeSpawn == null) {
            FMLLog.warning("[Resurgent Essentials] Player '%s' has a bed location set in dimension %d, but it is not a valid or safe spawn point.", player.getName(), dimension);
            return false; // Bed location is not valid or safe
        }

        // Return true if a valid bed spawn location is found
        return true;
    }

}
