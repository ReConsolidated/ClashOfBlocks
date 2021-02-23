package io.github.reconsolidated.clashofblocks.Listeners;

import io.github.reconsolidated.clashofblocks.ClashOfBlocks;
import io.github.reconsolidated.clashofblocks.ClashPlayer.ClashPlayer;
import io.github.reconsolidated.clashofblocks.customzombie.CustomZombie;
import io.github.reconsolidated.clashofblocks.customzombie.MovableByPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import java.util.ArrayList;

public class PlayerInteractEntityListener implements Listener {
    private ClashOfBlocks plugin;

    public PlayerInteractEntityListener(ClashOfBlocks plugin ){
        this.plugin = plugin;
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEntityEvent event) {
        event.getPlayer().sendMessage("PlayerInteractEntityEvent fired");
        switch(event.getPlayer().getInventory().getItemInMainHand().getType()){
            case BLAZE_ROD:
                ArrayList<CustomZombie> zombies = plugin.getZombies();
                CustomZombie clickedZombie = null;
                for (int i = 0; i<zombies.size(); i++){
                    if (zombies.get(i).getUniqueID().equals(event.getRightClicked().getUniqueId())){
                        clickedZombie = zombies.get(i);
                    }
                }

                if (clickedZombie != null){
                    ClashPlayer cp = plugin.getClashPlayer(event.getPlayer());
                    cp.setCurrentlyMovedMob(clickedZombie);
                    cp.getPlayer().sendMessage("Setting current mob.");
                }
                break;
        }


    }
}
