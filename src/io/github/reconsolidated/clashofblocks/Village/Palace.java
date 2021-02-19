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

    public int getWidth() {
        return widthFromLevel(level);
    }

    private void buildPalace(){
        middleLocation.clone().add(0, -1, 0).getBlock().setType(Material.COBBLESTONE);
    }

    private int widthFromLevel(int level){
        switch(level){
            case 0,1,2:
                return 11;
            default:
                return 5;
        }

    }
}
