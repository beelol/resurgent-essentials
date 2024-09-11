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
        return "/reconfig <randomSpawnEnabled|spawnRadius|maxThornsDamage|enableThornsDamageCap|maxSharpnessDamage|enableSharpnessDamageCap|maxProtectionReduction|enableProtectionReductionCap> <value>";
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
                    ResurgentEssentialsConfig.setRandomSpawnEnabled(enabled);
                    sender.sendMessage(new TextComponentString("Random spawn enabled set to: " + enabled));
                    break;
                case "spawnRadius":
                    int radius = Integer.parseInt(value);
                    ResurgentEssentialsConfig.setSpawnRadius(radius);
                    sender.sendMessage(new TextComponentString("Spawn radius set to: " + radius));
                    break;
                case "maxThornsDamage":
                    float maxThornsDamage = Float.parseFloat(value);
                    ResurgentEssentialsConfig.setMaxThornsDamage(maxThornsDamage);
                    sender.sendMessage(new TextComponentString("Max Thorns damage set to: " + maxThornsDamage));
                    break;
                case "enableThornsDamageCap":
                    boolean enableThornsDamageCap = Boolean.parseBoolean(value);
                    ResurgentEssentialsConfig.setEnableThornsDamageCap(enableThornsDamageCap);
                    sender.sendMessage(new TextComponentString("Enable Thorns damage cap set to: " + enableThornsDamageCap));
                    break;
                case "maxSharpnessDamage":
                    float maxSharpnessDamage = Float.parseFloat(value);
                    ResurgentEssentialsConfig.setMaxSharpnessDamage(maxSharpnessDamage);
                    sender.sendMessage(new TextComponentString("Max Sharpness damage set to: " + maxSharpnessDamage));
                    break;
                case "enableSharpnessDamageCap":
                    boolean enableSharpnessDamageCap = Boolean.parseBoolean(value);
                    ResurgentEssentialsConfig.setEnableSharpnessDamageCap(enableSharpnessDamageCap);
                    sender.sendMessage(new TextComponentString("Enable Sharpness damage cap set to: " + enableSharpnessDamageCap));
                    break;
                case "maxProtectionReduction":
                    float maxProtectionReduction = Float.parseFloat(value);
                    ResurgentEssentialsConfig.setMaxProtectionReduction(maxProtectionReduction);
                    sender.sendMessage(new TextComponentString("Max Protection reduction set to: " + maxProtectionReduction));
                    break;
                case "enableProtectionReductionCap":
                    boolean enableProtectionReductionCap = Boolean.parseBoolean(value);
                    ResurgentEssentialsConfig.setEnableProtectionReductionCap(enableProtectionReductionCap);
                    sender.sendMessage(new TextComponentString("Enable Protection reduction cap set to: " + enableProtectionReductionCap));
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
