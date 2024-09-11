package com.mcresurgence.eventhandlers;

import com.mcresurgence.ResurgentEssentialsConfig;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashMap;
import java.util.UUID;

public class SharpnessDamageHandler {
    private final HashMap<UUID, Float> originalDamageMap = new HashMap<>(); // Store original damage amounts by entity UUID

    @SubscribeEvent
    public void onLivingAttack(LivingAttackEvent event) {
        // Check if the damage source is a player
        if (event.getSource().getTrueSource() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getSource().getTrueSource();
            UUID playerId = player.getUniqueID();

            // Store the original damage amount before any modifications
            originalDamageMap.putIfAbsent(playerId, event.getAmount());
        }
    }

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {
        // Ensure the event source is a player with a Sharpness-enchanted weapon
        if (ResurgentEssentialsConfig.enableSharpnessDamageCap && event.getSource().getTrueSource() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getSource().getTrueSource();
            UUID playerId = player.getUniqueID();

            // Retrieve the stored original damage amount
            Float originalDamage = originalDamageMap.get(playerId);
            if (originalDamage != null) {
                int sharpnessLevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.SHARPNESS, player.getHeldItemMainhand());

                if (sharpnessLevel > 0) {
                    float sharpnessDamage = 1.0F + (0.5F * sharpnessLevel); // Sharpness damage calculation

                    if (sharpnessDamage > ResurgentEssentialsConfig.maxSharpnessDamage) {
                        // Calculate the reduced damage based on the original damage
                        float reducedDamage = originalDamage - sharpnessDamage + ResurgentEssentialsConfig.maxSharpnessDamage;
                        event.setAmount(Math.max(reducedDamage, 0)); // Ensure damage isn't negative
                    }
                }

                // Clean up the stored original damage after use
                originalDamageMap.remove(playerId);
            }
        }
    }
}
