package io.github.reconsolidated.clashofblocks.Listeners;

import io.github.reconsolidated.clashofblocks.ClashOfBlocks;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;

public class EntityTargetLivingEntityListener implements Listener {



    public EntityTargetLivingEntityListener(ClashOfBlocks plugin){
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }


    @EventHandler
    public void onEntityTarget(EntityTargetLivingEntityEvent event){
        Entity e1 = event.getEntity();
        Entity e2 = event.getTarget();

        if (e1.getCustomName() != null && e2.getCustomName() != null){
            if (e1.getCustomName().equalsIgnoreCase(e2.getCustomName()))
            {
                event.setCancelled(true);
            }
        }
    }
}
