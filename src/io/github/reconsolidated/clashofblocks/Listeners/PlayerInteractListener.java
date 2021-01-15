package io.github.reconsolidated.clashofblocks.Listeners;

import io.github.reconsolidated.clashofblocks.ClashOfBlocks;
import io.github.reconsolidated.clashofblocks.ClashPlayer.ClashPlayer;
import io.github.reconsolidated.clashofblocks.Village.Structure;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerInteractListener implements Listener {
    private ClashOfBlocks plugin;

    public PlayerInteractListener(ClashOfBlocks plugin ){
        this.plugin = plugin;
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event){
        ItemStack item = event.getItem();
        if (item != null){
            switch (item.getType()){
                case GOLDEN_PICKAXE:
                    if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)){
                        if (event.getClickedBlock() != null){
                            plugin.gPickaxeLeft = event.getClickedBlock().getLocation();
                            event.getPlayer().sendMessage(ChatColor.AQUA + "First location set to: " + plugin.gPickaxeLeft.toString());
                        }
                    }
                    else if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
                        if (event.getClickedBlock() != null){
                            plugin.gPickaxeRight = event.getClickedBlock().getLocation();
                            event.getPlayer().sendMessage(ChatColor.AQUA + "Second location set to: " + plugin.gPickaxeRight.toString());
                        }
                    }

                    break;
                case CLOCK:
                    if (((int) event.getPlayer().getLocation().getY()) != 50){
                        event.getPlayer().sendMessage("You have to stand on your Village level to build!");
                    }
                    else{
                        Structure baseStructure = new Structure(this.plugin, "HOUSE", event.getPlayer());
                        ClashPlayer cp = plugin.getClashPlayer(event.getPlayer());
                        if (cp != null){
                            cp.addStructure(baseStructure);
                        }
                    }
                    break;
                case DIAMOND_HOE:
                    ClashPlayer cp = plugin.getClashPlayer(event.getPlayer());
                    if (cp != null && cp.getVillageState().getStructureByName("HOUSE") != null){
                        cp.getVillageState().removeStructureByName("HOUSE", event.getPlayer());
                    }

                    break;
            }
        }



    }
}
