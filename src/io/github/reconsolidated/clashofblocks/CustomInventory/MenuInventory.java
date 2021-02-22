package io.github.reconsolidated.clashofblocks.CustomInventory;

import io.github.reconsolidated.clashofblocks.ClashOfBlocks;
import io.github.reconsolidated.clashofblocks.ClashPlayer.ClashPlayer;
import io.github.reconsolidated.clashofblocks.Village.STRUCTURES;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.libs.jline.internal.Log;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class MenuInventory implements Listener {
    private final Inventory inv;
    private ClashOfBlocks plugin;
    private String structureName;

    public MenuInventory(ClashOfBlocks plugin, String structureName) {

        this.plugin = plugin;
        this.structureName = structureName;

        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
        // Create a new inventory, with no owner (as this isn't a real inventory), a size of nine, called example
        inv = Bukkit.createInventory(null, 9, "Example");

        // Put the items into the inventory
        initializeItems();
    }

    // You can call this whenever you want to put the items in
    public void initializeItems() {
        inv.addItem(createGuiItem(Material.BAT_SPAWN_EGG, "Pick up building", "§aPick up this building to move it somewhere else", "§bMake sure you have space in your inventory"));
        inv.addItem(createGuiItem(Material.BONE_MEAL, "§bLevel up building", "§aCost: 100 wood, 100 grypciocoins", "§bLeveling up a building makes it better POG"));
    }

    // Nice little method to create a gui item with a custom name, and description
    protected ItemStack createGuiItem(final Material material, final String name, final String... lore) {
        final ItemStack item = new ItemStack(material, 1);
        final ItemMeta meta = item.getItemMeta();

        // Set the name of the item
        meta.setDisplayName(name);

        // Set the lore of the item
        meta.setLore(Arrays.asList(lore));

        item.setItemMeta(meta);

        return item;
    }

    // You can open the inventory with this
    public void openInventory(final HumanEntity ent) {
        ent.openInventory(inv);
    }

    // Check for clicks on items
    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
        if (e.getInventory() != inv) return;

        e.setCancelled(true);

        final ItemStack clickedItem = e.getCurrentItem();

        // verify current item is not null
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        final Player player = (Player) e.getWhoClicked();

        ClashPlayer cp = ClashPlayer.loadClashPlayer(plugin, player);

        // Using slots click is a best option for your inventory click's
        switch (e.getRawSlot()){
            case 0:
                if (cp.getVillageState().getStructureByName(structureName) != null){
                    cp.getVillageState().removeStructureByName(structureName, player);
                    cp.saveClashPlayer();
                    Bukkit.broadcastMessage("§b" + cp.getVillageState().toString());
                }
                else{
                    Log.error("ClashPlayer doesn't exist or structure: " + structureName + " doesnt exist.");
                }
                break;
        }
        player.sendMessage("You clicked at slot " + e.getRawSlot());
        player.closeInventory();
    }

    // Cancel dragging in our inventory
    @EventHandler
    public void onInventoryClick(final InventoryDragEvent e) {
        if (e.getInventory() == inv) {
            e.setCancelled(true);
        }
    }
}
