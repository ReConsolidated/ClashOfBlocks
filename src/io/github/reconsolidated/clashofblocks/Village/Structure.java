package io.github.reconsolidated.clashofblocks.Village;


import io.github.reconsolidated.clashofblocks.ClashOfBlocks;
import io.github.reconsolidated.clashofblocks.Utils.ConfigFileManagement;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;

public class Structure {
    private ArrayList<Block> structureBlocks;

    private Location loc1, loc2, middle;
    private String name;

    public Structure(Location loc1, Location loc2, Location middle, String name, ArrayList<Block> blocks){
        this.loc1 = loc1;
        this.loc2 = loc2;
        this.middle = middle;
        this.name = name;
        this.structureBlocks = blocks;
    }

    public static void createStructure(ClashOfBlocks plugin, Player player, String name, Location loc1, Location loc2, Location middle){
        ArrayList<Block> blocks = new ArrayList<Block>();
        for (int i = Math.min(loc1.getBlockX(), loc2.getBlockX()); i<= Math.max(loc1.getBlockX(), loc2.getBlockX()); i++) {
            for (int j = Math.min(loc1.getBlockY(), loc2.getBlockY()); j <= Math.max(loc1.getBlockY(), loc2.getBlockY()); j++) {
                for (int k = Math.min(loc1.getBlockZ(), loc2.getBlockZ()); k <= Math.max(loc1.getBlockZ(), loc2.getBlockZ()); k++) {
                    blocks.add(loc1.getWorld().getBlockAt(i, j, k));
                }
            }
        }

        FileConfiguration structureConfig = ConfigFileManagement.getCustomConfig(plugin, name + ".yml");
        structureConfig.set("blocks", blocks);
        structureConfig.set("loc1", loc1);
        structureConfig.set("loc2", loc2);
        structureConfig.set("middle", middle);
    }

    public static Structure loadBlocks(ClashOfBlocks plugin, String name){
        FileConfiguration structureConfig = ConfigFileManagement.getCustomConfig(plugin, name + ".yml");
        Location loc1 = (Location) structureConfig.get("loc1");
        Location loc2 = (Location) structureConfig.get("loc2");
        Location mid = (Location) structureConfig.get("middle");
        ArrayList<Block> blocks = (ArrayList<Block>) structureConfig.get("blocks");

        if (loc1 == null || loc2 == null || mid == null || blocks == null){
            return null;
        }

        return new Structure(loc1, loc2, mid, name, blocks);
    }
}
