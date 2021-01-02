package io.github.reconsolidated.clashofblocks.Utils;

import io.github.reconsolidated.clashofblocks.ClashOfBlocks;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class ConfigFileManagement {
    public static FileConfiguration getCustomConfig(ClashOfBlocks plugin, String configFileName){
        File customYml = new File(plugin.getDataFolder()+ "/" + configFileName);
        FileConfiguration customConfig = YamlConfiguration.loadConfiguration(customYml);
        return customConfig;
    }
}
