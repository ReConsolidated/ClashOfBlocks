package io.github.reconsolidated.clashofblocks.Village;

import org.bukkit.Location;
import org.bukkit.Material;

public class Palace {
    private Location middleLocation;
    private int level = 0;

    public Palace(Location middleLocation, int level){
        this.middleLocation = middleLocation;
        this.level = level;
        buildPalace();
    }

    private void buildPalace(){
        middleLocation.clone().add(0, -1, 0).getBlock().setType(Material.COBBLESTONE);
    }
}
