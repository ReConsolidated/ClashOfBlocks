package io.github.reconsolidated.clashofblocks.Listeners;

import io.github.reconsolidated.clashofblocks.ClashOfBlocks;
import io.github.reconsolidated.clashofblocks.ClashPlayer.ClashPlayer;
import io.github.reconsolidated.clashofblocks.CustomInventory.MenuInventory;
import io.github.reconsolidated.clashofblocks.Village.STRUCTURES;
import io.github.reconsolidated.clashofblocks.Village.Structure;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

import static io.github.reconsolidated.clashofblocks.CustomInventory.MenuInventory.createCustomItem;

public class PlayerInteractListener implements Listener {
    private ClashOfBlocks plugin;

    public PlayerInteractListener(ClashOfBlocks plugin ){
        this.plugin = plugin;
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    private boolean containsEncryptedMetaData(ItemStack item, int line, String data){
        final ItemMeta meta = item.getItemMeta();
        if (meta.hasLore()){
            List<String> lore = meta.getLore();
            return lore.get(lore.size() - line).equalsIgnoreCase(data);
        }
        return false;
    }

    private Integer getItemLevel(ItemStack item){
        final ItemMeta meta = item.getItemMeta();
        if (meta.hasLore()){
            List<String> lore = meta.getLore();
            String line = lore.get(lore.size() - 1);
            line = line.replace("LVL", "");
            return Integer.parseInt(line);
        }
        return null;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event){
        ItemStack item = event.getItem();
        if (item != null){
            switch (item.getType()){
                // VILLAGE-RELATED ITEMS
                case BOOK:
                    tryToOpenStructureEditMode(event);
                    break;
                case CLOCK:
                    tryBuildHouse(event, item);
                    break;
                case IRON_PICKAXE:
                    tryBuildMine(event);
                    break;

                // PVP-RELATED ITEMS
                case BLAZE_ROD:
                    useBlazeRod(event);
                    break;
                case ZOMBIE_SPAWN_EGG:
                    trySpawnCustomZombie(event, item);
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

    private void tryBuildHouse(PlayerInteractEvent event, ItemStack item){
        if (containsEncryptedMetaData(item, 2, "ID0001")){
            if (((int) event.getPlayer().getLocation().getY()) != 50){
                event.getPlayer().sendMessage("You have to stand on your Village level to build!");
            }
            else {
                ClashPlayer cp = plugin.getClashPlayer(event.getPlayer());
                if (cp != null){
                    if (cp.canPlaceStructure(STRUCTURES.HOUSE)){
                        Integer level = getItemLevel(item);
                        if (level != null){
                            Structure baseStructure = new Structure("HOUSE", STRUCTURES.HOUSE, level);
                            baseStructure.build(event.getPlayer().getLocation());
                            cp.addStructure(baseStructure);
                        }
                        else{
                            cp.getPlayer().sendMessage("Something went wrong and we couldn't read level of this item, please contact an admin");
                        }


                    }
                    else{
                        cp.getPlayer().sendMessage("Structure cannot be placed");
                    }
                }
            }
        }
        else {
            event.getPlayer().getInventory().addItem(createCustomItem(
                    Material.CLOCK,
                    "HOUSE" + " lvl " + "1",
                    "Use this item to create a " + "HOUSE" + " in front of you.",
                    "ID0001",
                    "LVL1"
            ));
        }
    }

    private void tryBuildMine(PlayerInteractEvent event){
        if (((int) event.getPlayer().getLocation().getY()) != 50){
            event.getPlayer().sendMessage("You have to stand on your Village level to build!");
        }
        else{
            ClashPlayer cp = plugin.getClashPlayer(event.getPlayer());
            if (cp != null){
                if (cp.canPlaceStructure(STRUCTURES.MINE)){
                    Structure baseStructure = new Structure("MINE", STRUCTURES.MINE);
                    baseStructure.build(event.getPlayer().getLocation());
                    cp.addStructure(baseStructure);
                }
                else{
                    cp.getPlayer().sendMessage("Structure cannot be placed");
                }
            }
        }
    }


    private void useBlazeRod(PlayerInteractEvent event){

        ClashPlayer cp = plugin.getClashPlayer(event.getPlayer());
        if (cp.getCurrentlyMovedMob() != null && event.getClickedBlock() != null){
            Location loc = event.getClickedBlock().getLocation().clone().add(0,1,0);
            event.getPlayer().sendMessage("Setting mob destination to: " + loc.toString());
            cp.getCurrentlyMovedMob().setDestination(loc);
        }
    }

    private void trySpawnCustomZombie(PlayerInteractEvent event, ItemStack item){
        if (containsEncryptedMetaData(item, 2, "ID1001")){
            event.setCancelled(true);
            Player player = event.getPlayer();
            player.sendMessage("Spawning a zombie...");
            plugin.spawnZombie(player, event.getClickedBlock().getLocation().clone().add(0,1,0));
        }
        else {
            event.getPlayer().getInventory().addItem(createCustomItem(
                    Material.ZOMBIE_SPAWN_EGG,
                    "Zombie" + " lvl " + "1",
                    "Use this item to spawn a " + "Zombie" + " in front of you.",
                    "ID1001",
                    "LVL1"
            ));
        }
    }
}
