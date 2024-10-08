package com.mcresurgence;

import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ThornsDamageHandler {
    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {
        // Check if the damage source is Thorns
        if (event.getSource().damageType.equals("thorns")) {
            // Apply damage cap only if enabled in the config
            if (ServerMOTDConfig.enableThornsDamageCap && event.getAmount() > ServerMOTDConfig.maxThornsDamage) {
                event.setAmount(ServerMOTDConfig.maxThornsDamage); // Set the damage to the maximum allowed
            }
        }
    }
}