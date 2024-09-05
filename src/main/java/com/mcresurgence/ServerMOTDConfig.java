package com.mcresurgence;

import net.minecraftforge.common.config.Configuration;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ServerMOTDConfig {

    private static Configuration serverConfig;
    private static String motd;
    private static boolean randomSpawnEnabled;
    private static int spawnRadius;

    public static void init(File configDir) {
        String serverConfigFilename = "resurgentEssentials.cfg";

        Path serverConfigPath = Paths.get(configDir.toString(), ResurgentEssentialsMod.MODID, serverConfigFilename);

        // Load Server Configuration
        serverConfig = new Configuration(serverConfigPath.toFile());

        loadServerConfig();
    }

    private static void loadServerConfig() {
        serverConfig.load();

        motd = serverConfig.getString("motd", Configuration.CATEGORY_GENERAL, "{\"text\":\"Welcome to the server!\",\"color\":\"white\"}", "Message of the Day displayed to players upon joining.");

        randomSpawnEnabled = serverConfig.getBoolean("randomSpawnEnabled", Configuration.CATEGORY_GENERAL, false, "Enable random spawn on first join and death.");

        spawnRadius = serverConfig.getInt("spawnRadius", Configuration.CATEGORY_GENERAL, 100, 1, 10000, "Radius around the initial spawn point for random spawning.");

        if (serverConfig.hasChanged()) {
            serverConfig.save();
        }
    }

    public static String getMotd() {
        return motd;
    }

    public static boolean isRandomSpawnEnabled() {
        return randomSpawnEnabled;
    }

    public static int getSpawnRadius() {
        return spawnRadius;
    }

    public static void setRandomSpawnEnabled(boolean enabled) {
        randomSpawnEnabled = enabled;
        serverConfig.get(Configuration.CATEGORY_GENERAL, "randomSpawnEnabled", false).set(enabled);
        serverConfig.save();
    }

    public static void setSpawnRadius(int radius) {
        spawnRadius = radius;
        serverConfig.get(Configuration.CATEGORY_GENERAL, "spawnRadius", 100).set(radius);
        serverConfig.save();
    }
}
