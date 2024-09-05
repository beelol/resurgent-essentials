package com.mcresurgence;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import java.util.Random;

public class PlayerRandomSpawnHandler {

    private boolean isRandomSpawnAllowed(EntityPlayerMP player) {
        return ServerMOTDConfig.isRandomSpawnEnabled() && !hasValidBedSpawn(player);
    }

    @SubscribeEvent
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.player instanceof EntityPlayerMP)) return;

        EntityPlayerMP playerMP = (EntityPlayerMP) event.player;
        NBTTagCompound persistentData = playerMP.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);

        // Check if this is the player's first join
        if (!persistentData.getBoolean("HasSpawned")) {
            if (isRandomSpawnAllowed(playerMP)) {
                BlockPos randomSpawnPoint = getRandomSpawnPoint(playerMP.getEntityWorld(), playerMP.getPosition(), ServerMOTDConfig.getSpawnRadius());

                // Immediately update the player's position
                playerMP.setPositionAndUpdate(randomSpawnPoint.getX(), randomSpawnPoint.getY(), randomSpawnPoint.getZ());
            }

            // Mark that the player has spawned for the first time
            persistentData.setBoolean("HasSpawned", true);
            playerMP.getEntityData().setTag(EntityPlayer.PERSISTED_NBT_TAG, persistentData);

            FMLLog.info("[Resurgent Essentials] Player '%s' has joined for the first time and is being teleported to a random location.", playerMP.getName());
        } else {
            FMLLog.info("[Resurgent Essentials] Player '%s' is rejoining; no random teleportation.", playerMP.getName());
        }
    }

    @SubscribeEvent
    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (!(event.player instanceof EntityPlayerMP)) return;

        EntityPlayerMP playerMP = (EntityPlayerMP) event.player;

        // Log the event
        FMLLog.info("[Resurgent Essentials] Player '%s' has respawned.", playerMP.getName());

        if (isRandomSpawnAllowed(playerMP)) {
            BlockPos randomSpawnPoint = getRandomSpawnPoint(playerMP.getEntityWorld(), playerMP.getPosition(), ServerMOTDConfig.getSpawnRadius());

            // Schedule the teleport to ensure it happens after Minecraft has finished its default spawn handling
            playerMP.getServerWorld().getMinecraftServer().addScheduledTask(() -> {
                playerMP.setPositionAndUpdate(randomSpawnPoint.getX() + 0.5, randomSpawnPoint.getY(), randomSpawnPoint.getZ() + 0.5);
                FMLLog.info("[Resurgent Essentials] Player '%s' successfully teleported to a random location at (%d, %d, %d).", playerMP.getName(), randomSpawnPoint.getX(), randomSpawnPoint.getY(), randomSpawnPoint.getZ());
            });
        }
    }


    private BlockPos getRandomSpawnPoint(World world, BlockPos center, int radius) {
        Random rand = new Random();
        int x = center.getX() + rand.nextInt(radius * 2) - radius;
        int z = center.getZ() + rand.nextInt(radius * 2) - radius;

        // Find the highest block at the random X and Z position
        BlockPos topBlockPos = world.getTopSolidOrLiquidBlock(new BlockPos(x, 0, z));
        int y = topBlockPos.getY(); // Y coordinate of the highest solid or liquid block

        // Check if the top block is air; if so, find the highest non-air block below
        while (world.isAirBlock(topBlockPos) && y > 0) {
            topBlockPos = topBlockPos.down();
            y = topBlockPos.getY();
        }

        IBlockState blockState = world.getBlockState(topBlockPos);

        // Check if the block is lava or on fire
        if (blockState.getMaterial().isLiquid()) {
            if (blockState.getBlock().getUnlocalizedName().equals("tile.lava")
                    || blockState.getBlock().isBurning(world, topBlockPos)) {

                // If block is lava or on fire, retry to find a safe spawn point
                return getRandomSpawnPoint(world, center, radius);
            } else {
                // If it's water, keep raising block position until it's not liquid to spawn in air
                do {
                    topBlockPos = topBlockPos.up();
                    blockState = world.getBlockState(topBlockPos);
                } while (blockState.getMaterial().isLiquid() && topBlockPos.getY() < 255.0D);

                return topBlockPos;
            }
        }

        // Ensure the player is positioned just above the ground level
        return new BlockPos(topBlockPos.getX(), topBlockPos.getY() + 1, topBlockPos.getZ());
    }


    private boolean hasValidBedSpawn(EntityPlayerMP player) {
        // Access the player's persistent NBT data
        NBTTagCompound playerData = player.getEntityData();

        // Check if the bed spawn coordinates are set in the NBT data
        if (playerData.hasKey("SpawnX") && playerData.hasKey("SpawnY") && playerData.hasKey("SpawnZ")) {
            // Retrieve the bed spawn coordinates
            int spawnX = playerData.getInteger("SpawnX");
            int spawnY = playerData.getInteger("SpawnY");
            int spawnZ = playerData.getInteger("SpawnZ");

            // Confirm that the coordinates are not just default values
            if (spawnX != 0 || spawnY != 0 || spawnZ != 0) {
                FMLLog.info("[Resurgent Essentials] Player '%s' has a valid bed spawn set at (%d, %d, %d).", player.getName(), spawnX, spawnY, spawnZ);
                return true; // A valid bed spawn is set
            }
        }

        // No valid bed spawn is set
        FMLLog.info("[Resurgent Essentials] Player '%s' does not have a valid bed spawn set.", player.getName());
        return false;
    }
}
