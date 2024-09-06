package com.mcresurgence;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class ReConfigCommand extends CommandBase {

    @Override
    public String getName() {
        return "reconfig";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/reconfig <randomSpawnEnabled|spawnRadius|maxThornsDamage|enableThornsDamageCap> <value>";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 2) {
            sender.sendMessage(new TextComponentString("Usage: " + getUsage(sender)));
            return;
        }

        String option = args[0];
        String value = args[1];

        try {
            switch (option) {
                case "randomSpawnEnabled":
                    boolean enabled = Boolean.parseBoolean(value);
                    ServerMOTDConfig.setRandomSpawnEnabled(enabled);
                    sender.sendMessage(new TextComponentString("Random spawn enabled set to: " + enabled));
                    break;
                case "spawnRadius":
                    int radius = Integer.parseInt(value);
                    ServerMOTDConfig.setSpawnRadius(radius);
                    sender.sendMessage(new TextComponentString("Spawn radius set to: " + radius));
                    break;
                case "maxThornsDamage":
                    float maxThornsDamage = Float.parseFloat(value);
                    ServerMOTDConfig.setMaxThornsDamage(maxThornsDamage);
                    sender.sendMessage(new TextComponentString("Max Thorns damage set to: " + maxThornsDamage));
                    break;
                case "enableThornsDamageCap":
                    boolean enableThornsDamageCap = Boolean.parseBoolean(value);
                    ServerMOTDConfig.setEnableThornsDamageCap(enableThornsDamageCap);
                    sender.sendMessage(new TextComponentString("Enable Thorns damage cap set to: " + enableThornsDamageCap));
                    break;
                default:
                    sender.sendMessage(new TextComponentString("Unknown config option: " + option));
                    break;
            }
        } catch (Exception e) {
            sender.sendMessage(new TextComponentString("Error setting config: " + e.getMessage()));
        }
    }
    @Override
    public int getRequiredPermissionLevel() {
        return 2; // Only allow operators (OP) and above to use this command
    }
}
