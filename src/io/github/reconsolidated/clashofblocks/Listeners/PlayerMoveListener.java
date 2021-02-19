package io.github.reconsolidated.clashofblocks.Listeners;

import io.github.reconsolidated.clashofblocks.ClashOfBlocks;
import io.github.reconsolidated.clashofblocks.ClashPlayer.ClashPlayer;
import io.github.reconsolidated.clashofblocks.Village.STRUCTURES;
import io.github.reconsolidated.clashofblocks.Village.Structure;
import org.bukkit.*;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;

import static io.github.reconsolidated.clashofblocks.Village.Structure.getStructureSizeX;

public class PlayerMoveListener implements Listener {
    private ClashOfBlocks plugin;
    private ArrayList<FallingBlock> fallingBlocks;
    private FallingBlock lastBlock;
    public PlayerMoveListener(ClashOfBlocks plugin ){
        this.fallingBlocks = new ArrayList<>();
        this.plugin = plugin;
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    private void showBuildArea(STRUCTURES type, Player player) {
        Location location = player.getLocation().clone();
        double a1 = location.getDirection().angle(new Vector(0, 0, -1)); // 270
        double a2 = location.getDirection().angle(new Vector(1, 0, 0)); // 180
        double a3 = location.getDirection().angle(new Vector(0, 0, 1)); // 90
        double a4 = location.getDirection().angle(new Vector(-1, 0, 0)); // 360

        double smallest = Math.min(a1, Math.min(a2, Math.min(a3, a4)));

        int r = getStructureSizeX(type);
        Location topLeft = null;
        if (smallest == a1) { // 90 degrees
            topLeft = location.clone().add(-1 * r / 2, 0, -1 * (r) + 1);
        }
        if (smallest == a2) { // 180 degrees
            topLeft = location.clone().add(0, 0, -1 * (r / 2));
        }
        if (smallest == a3) { // 270 degrees
            topLeft = location.clone().add(r / 2 - r + 1, 0, 0);
        }
        if (smallest == a4) { //360 degrees
            topLeft = location.clone().add(-1 * r + 1, 0, r / 2 - r + 1);
        }

        Location bottomRight = topLeft.clone().add(r - 1, 0, r - 1);
        bottomRight.setY(49);
        bottomRight.setX(bottomRight.getBlockX() + 0.5);
        bottomRight.setZ(bottomRight.getBlockZ() + 0.5);

        topLeft.setY(49);
        topLeft.setX(topLeft.getBlockX() + 0.5);
        topLeft.setZ(topLeft.getBlockZ() + 0.5);

        World world = player.getWorld();

        for (int i = 0; i<fallingBlocks.size(); i++){
            fallingBlocks.get(i).remove();
            fallingBlocks.remove(i);
            i--;
        }

        Material material;
        ClashPlayer cp = plugin.getClashPlayer(player);
        if (cp != null){
            if (cp.canPlaceStructure(type)){
                material = Material.GREEN_CONCRETE;
            }
            else{
                material = Material.RED_CONCRETE;
            }
        }
        else{
           return;
        }

        for (double x = topLeft.getX(); x<=bottomRight.getX(); x++) {
            addBlock(world, x, topLeft.getZ(), material);
            addBlock(world, x, bottomRight.getZ(), material);
        }
        for (double z = topLeft.getZ(); z<=bottomRight.getZ(); z++) {
            addBlock(world, topLeft.getX(), z, material);
            addBlock(world, bottomRight.getX(), z, material);
        }
    }

    private void addBlock(World world, double x,double z, Material material){
        Location loc = new Location(world, x, 49, z);
        FallingBlock temp = world.spawnFallingBlock(loc, material.createBlockData());
        temp.setGravity(false);
        temp.setDropItem(false);
        fallingBlocks.add(temp);
    }


    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event){
        ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
        switch (item.getType()){ //ONLY FOR STRUCTURE BUILDING ITEMS DUE TO DEFAULT BEHAVIOUR
            case CLOCK:
                showBuildArea(STRUCTURES.HOUSE, event.getPlayer());
                break;
            case IRON_PICKAXE:
                showBuildArea(STRUCTURES.MINE, event.getPlayer());
                break;
            default:
                for (int i = 0; i<fallingBlocks.size(); i++){
                    fallingBlocks.get(i).remove();
                    fallingBlocks.remove(i);
                    i--;
                }
                break;
        }
    }

}
