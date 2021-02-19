package io.github.reconsolidated.clashofblocks.Listeners;

import io.github.reconsolidated.clashofblocks.ClashOfBlocks;
import io.github.reconsolidated.clashofblocks.ClashPlayer.ClashPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class PlayerInteractEntityListener implements Listener {
    private ClashOfBlocks plugin;

    public PlayerInteractEntityListener(ClashOfBlocks plugin ){
        this.plugin = plugin;
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEntityEvent event) {
        if (event.getRightClicked() instanceof ItemFrame){
            ItemFrame frame = (ItemFrame) event.getRightClicked();
            ClashPlayer cp = plugin.getClashPlayer(event.getPlayer());
            switch (frame.getItem().getType()){
                case CLOCK:
                    if (cp != null && cp.getVillageState().getStructureByName("HOUSE") != null){
                        cp.getVillageState().removeStructureByName("HOUSE", event.getPlayer());
                        cp.saveClashPlayer();
                    }
                    break;
                case IRON_PICKAXE:
                    if (cp != null && cp.getVillageState().getStructureByName("MINE") != null){
                        cp.getVillageState().removeStructureByName("MINE", event.getPlayer());
                        cp.saveClashPlayer();
                    }
                    break;
            }

            frame.remove();
        }
    }
}
