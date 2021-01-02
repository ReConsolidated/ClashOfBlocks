package io.github.reconsolidated.clashofblocks.Listeners;

import io.github.reconsolidated.clashofblocks.ClashOfBlocks;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class BlockBreakListener implements Listener {
    private ClashOfBlocks plugin;

    public BlockBreakListener(ClashOfBlocks plugin ){
        this.plugin = plugin;
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){
        ItemStack item = event.getPlayer().getItemInHand();
        switch (item.getType()){
            case GOLDEN_PICKAXE:
                event.setCancelled(true);
                break;
        }


    }
}
