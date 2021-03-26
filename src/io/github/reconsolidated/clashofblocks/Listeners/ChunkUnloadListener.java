package io.github.reconsolidated.clashofblocks.Listeners;

import io.github.reconsolidated.clashofblocks.ClashOfBlocks;
import io.github.reconsolidated.clashofblocks.customzombie.CustomZombie;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.ArrayList;

public class ChunkUnloadListener implements Listener {

    private ClashOfBlocks plugin;

    public ChunkUnloadListener(ClashOfBlocks plugin ){
        this.plugin = plugin;
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event){
        ArrayList<CustomZombie> zombies = plugin.getZombies();
        for (int i = 0; i<zombies.size(); i++){
            int locX = (int)zombies.get(i).locX();
            int locZ = (int)zombies.get(i).locZ();
            if (zombies.get(i).getWorld().getChunkAt(locX, locZ).equals(event.getChunk())){
                BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
                scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        Bukkit.broadcastMessage("Chunk unloaded, loading it again");
                        event.getChunk().load();
                    }
                }, 20L);
            }
        }
    }



}
