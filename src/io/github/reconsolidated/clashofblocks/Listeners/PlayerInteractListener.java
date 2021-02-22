package io.github.reconsolidated.clashofblocks.Listeners;

import io.github.reconsolidated.clashofblocks.ClashOfBlocks;
import io.github.reconsolidated.clashofblocks.ClashPlayer.ClashPlayer;
import io.github.reconsolidated.clashofblocks.CustomInventory.MenuInventory;
import io.github.reconsolidated.clashofblocks.Village.STRUCTURES;
import io.github.reconsolidated.clashofblocks.Village.Structure;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
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
                case BOOK:
                    tryToOpenStructureEditMode(event);
                    break;
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
                    else {
                        ClashPlayer cp = plugin.getClashPlayer(event.getPlayer());
                        if (cp != null){
                            if (cp.canPlaceStructure(STRUCTURES.HOUSE)){
                                Structure baseStructure = new Structure("HOUSE", STRUCTURES.HOUSE);
                                baseStructure.build(event.getPlayer());
                                cp.addStructure(baseStructure);
                            }
                            else{
                                Bukkit.broadcastMessage(cp.getVillageState().toString());
                                cp.getPlayer().sendMessage("Structure cannot be placed");
                            }
                        }
                    }

                    break;
                case IRON_PICKAXE:
                    if (((int) event.getPlayer().getLocation().getY()) != 50){
                        event.getPlayer().sendMessage("You have to stand on your Village level to build!");
                    }
                    else{
                        ClashPlayer cp = plugin.getClashPlayer(event.getPlayer());
                        if (cp != null){
                            if (cp.canPlaceStructure(STRUCTURES.MINE)){
                                Structure baseStructure = new Structure("MINE", STRUCTURES.MINE);
                                baseStructure.build(event.getPlayer());
                                cp.addStructure(baseStructure);
                            }
                            else{
                                Bukkit.broadcastMessage(cp.getVillageState().toString());
                                cp.getPlayer().sendMessage("Structure cannot be placed");
                            }
                        }
                    }
                    break;
            }
        }
    }


    private void tryToOpenStructureEditMode(PlayerInteractEvent event){
        ClashPlayer cp = ClashPlayer.loadClashPlayer(plugin, event.getPlayer());
        if (cp == null || event.getClickedBlock() == null)
            return;

        Structure s = cp.getStructureWithLocation(event.getClickedBlock().getLocation());

        if (s == null){
            cp.getPlayer().sendMessage("You have to click on a building to open editing menu");
            return;
        }

        MenuInventory structureEditMenu = new MenuInventory(plugin, s.getName());
        structureEditMenu.openInventory(cp.getPlayer());
    }
}
