package com.mcresurgence.eventhandlers;

import com.mcresurgence.ResurgentEssentialsConfig;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashMap;
import java.util.UUID;

public class ProtectionDamageHandler {
    private final HashMap<UUID, Float> originalDamageMap = new HashMap<>(); // Store original damage amounts by entity UUID

    private int getCombinedProtectionLevel(EntityPlayer player) {
        int totalProtectionLevel = 0;

        // Directly iterate over the armor inventory list
        for (ItemStack armorPiece : player.getArmorInventoryList()) {
            totalProtectionLevel += EnchantmentHelper.getEnchantmentLevel(Enchantments.PROTECTION, armorPiece);
        }

        return totalProtectionLevel;
    }

    @SubscribeEvent
    public void onLivingAttack(LivingAttackEvent event) {
        if (event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            // Store the original damage amount before any reductions
            originalDamageMap.putIfAbsent(player.getUniqueID(), event.getAmount());
        }
    }

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {
        if (ResurgentEssentialsConfig.enableProtectionReductionCap && event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            int totalProtectionLevel = getCombinedProtectionLevel(player);

            if (totalProtectionLevel > 0) {
                Float originalDamage = originalDamageMap.remove(player.getUniqueID()); // Get the stored original damage amount

                float finalDamage = getReducedCappedDamage(event, originalDamage);

                // Ensure final damage is not less than 0
                event.setAmount(Math.max(finalDamage, 0));
            }
        }
    }

    private float getReducedCappedDamage(LivingHurtEvent event, Float originalDamage) {
        float reducedDamage = event.getAmount(); // Damage after vanilla reduction is applied

        // Calculate the actual reduction applied by vanilla mechanics
        float vanillaReduction = originalDamage - reducedDamage;

        // Calculate the maximum allowed reduction
        float maxAllowedReduction = ResurgentEssentialsConfig.maxProtectionReduction;

        // Cap the reduction to the maximum allowed by the configuration
        float cappedReduction = Math.min(vanillaReduction, maxAllowedReduction);

        // Apply the capped reduction to calculate the final damage
        return originalDamage - cappedReduction;
    }
}
