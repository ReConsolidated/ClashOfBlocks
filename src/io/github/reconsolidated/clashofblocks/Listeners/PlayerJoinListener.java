package io.github.reconsolidated.clashofblocks.Listeners;


import com.sun.security.auth.login.ConfigFile;
import io.github.reconsolidated.clashofblocks.ClashOfBlocks;
import io.github.reconsolidated.clashofblocks.ClashPlayer.ClashPlayer;
import io.github.reconsolidated.clashofblocks.Utils.ConfigFileManagement;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.File;
import java.util.Set;

public class PlayerJoinListener implements Listener {
    private ClashOfBlocks plugin;

    public PlayerJoinListener(ClashOfBlocks plugin ){
        this.plugin = plugin;
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        FileConfiguration config = ConfigFileManagement.getCustomConfig(plugin, this.plugin.getDataFolder()
                + File.separator
                + "ClashPlayers.yml");

        Set<String> keys = config.getKeys(false);

        ClashPlayer cp = null;

        boolean isANewPlayer = true;

        for (String s : keys){
            if (s.equalsIgnoreCase(event.getPlayer().getUniqueId().toString())){
                isANewPlayer = false;
                cp = ClashPlayer.loadClashPlayer(plugin, event.getPlayer());
                break;
            }
        }

        if (isANewPlayer){
            cp = new ClashPlayer(this.plugin, event.getPlayer());
        }

        event.getPlayer().teleport(cp.getVillageLocation());
        plugin.addClashPlayer(cp);
    }
}
