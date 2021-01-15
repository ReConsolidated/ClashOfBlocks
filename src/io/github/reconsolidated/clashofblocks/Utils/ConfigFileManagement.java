package io.github.reconsolidated.clashofblocks.Utils;

import io.github.reconsolidated.clashofblocks.ClashOfBlocks;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ConfigFileManagement {
    public static FileConfiguration getCustomConfig(ClashOfBlocks plugin, String path){
        File customYml = new File(path);
        FileConfiguration customConfig = YamlConfiguration.loadConfiguration(customYml);
        return customConfig;
    }

    public static boolean saveCustomConfig(ClashOfBlocks plugin, String path, FileConfiguration config){
        File customYml = new File(path);
        try{
            config.save(customYml);
        }catch(IOException ex){
            ex.printStackTrace();
            return false;
        }
        return true;
    }
}
