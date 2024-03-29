package io.github.reconsolidated.clashofblocks;

import io.github.reconsolidated.clashofblocks.ClashPlayer.ClashPlayer;
import io.github.reconsolidated.clashofblocks.ClashVillage.ClashVillageState;
import io.github.reconsolidated.clashofblocks.Listeners.*;
import io.github.reconsolidated.clashofblocks.Utils.ConfigFileManagement;
import io.github.reconsolidated.clashofblocks.Village.Structure;
import io.github.reconsolidated.clashofblocks.customzombie.CustomZombie;
import net.minecraft.server.v1_16_R2.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.craftbukkit.v1_16_R2.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;


public class ClashOfBlocks extends JavaPlugin implements Listener {
    private ArrayList<CustomZombie> zombies = new ArrayList<CustomZombie>();

    private ArrayList<ClashPlayer> activeClashPlayers = new ArrayList<>();

    public Location gPickaxeLeft;
    public Location gPickaxeRight;

    static {
        ConfigurationSerialization.registerClass(ClashVillageState.class, "ClashVillageState");
        ConfigurationSerialization.registerClass(Structure.class, "Structure");
    }

    @Override
    public void onEnable() {
        Commands commandsPlugin = new Commands(this);

        new BlockPlaceListener(this);
        new PlayerInteractListener(this);
        new BlockBreakListener(this);
        new PlayerJoinListener(this);
        new PlayerInteractEntityListener(this);
        new PlayerMoveListener(this);
        new ChunkUnloadListener(this);

    }


    public void showClashPlayers(){
        for (int i = 0; i<activeClashPlayers.size(); i++){
            Bukkit.broadcastMessage(activeClashPlayers.get(i).toString());
        }
    }

    public void addClashPlayer(ClashPlayer cp){
        activeClashPlayers.add(cp);
    }

    public ClashPlayer getClashPlayer(Player player){
        for (int i = 0; i<activeClashPlayers.size(); i++){
            if (activeClashPlayers.get(i).getPlayer().equals(player)){
                return activeClashPlayers.get(i);
            }
        }
        return null;
    }

    public ArrayList<CustomZombie> getZombies(){
        return this.zombies;
    }

    public void killAllZombies(){
        for (int i = 0; i<zombies.size();){
            zombies.get(i).killEntity();
            zombies.remove(i);
        }
    }

    public void readyAllZombies(){
        for (int i = 0; i<zombies.size(); i++){
            zombies.get(i).setIsReadyToFight(true);
        }
    }

    public void moveAllZombiesToLocation(Location location){
        for (int i = 0; i<zombies.size(); i++){
            zombies.get(i).setCurrentDestiny(location);
        }
    }

    public void clearZombiesDestiny(){
        for (int i = 0; i<zombies.size(); i++){
            zombies.get(i).setCurrentDestiny(null);
        }
    }


    public void spawnNpc(Location location){
        NPC npc = new NPC(location, "Kopacz", "ewogICJ0aW1lc3RhbXAiIDogMTYwOTI0MjIxOTI0MCwKICAicHJvZmlsZUlkIiA6ICI5MDY1OGFlOTE4OTg0MzBhOTg2MjMyZDVlNzNiNGQ2MSIsCiAgInByb2ZpbGVOYW1lIiA6ICJKb3JkYW5Pc3RlcmJlcmciLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODI0NDYzMzNkMzg1MTAxZDQ5NzA4Y2E0NWMwYjIwMjE1NjA1ZTM4NDkwNzgzY2I5NDY5NzlhZTliZmIwNjllMCIKICAgIH0sCiAgICAiQ0FQRSIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTdkZmVhMTZkYzgzYzk3ZGYwMWExMmZhYmJkMTIxNjM1OWMwY2QwZWE0MmY5OTk5YjZlOTdjNTg0OTYzZTk4MCIKICAgIH0KICB9Cn0=" ,"qCfBHZ3XztEzTD+eRa8NA7rHqMRQDYH384PdOwAkq7eCylXX2cceA7Cm0wnDNE8+BsDvhufjAganGj7ajSFiH95wLp3hLh8FaBdXPDZLRPNqvWwHqYaMpOoKvmF/DzKHPYSAoIUVe4Z1GruBDxPZWB5GzAsU5odjn8yqBM/kdx9b+Wepy1+mZft7ZI9KvbRizuTB3ItPk/XZF0QUBRW22eEQVXfkggKQFzijwvdGuzONJTx3b6A9WBXdTJHrHQA/MS1AXjYbBtTRFyLAm1DgDz7CWRAjxy+/25pmbAHhtay3V2jUqCmgnrXKBWGR5jAL7kC7PdpcmiIQHXXVtulnv7YgXejlH9S/Jg5+f8s6IQVkMuGELvupYRx8IgR5Pn4NNhVOMSfaci04pSNcIRU4OkYwWsMDMYyvZajATRZ/i2BHg7R4GrHL8QaFVNWas6A1Qi5XQx1I6o6y5xDqCzSHGgywxlmtROyJBaIPUaWnUI3pVc/0QBQma9DvKDYPJVzhzO8wq+etO00KN0nxqNEpV1hgwSHGV4dh9BDEo1FlJlEhRY0oTTvdE1Ah1N46JZcQKQQ5W7jygdG3B9pmPx2Rifw6NnYw8nri6zW1C5+OBAckvpKLqeyprG8ewDtESIC5Iigr2C1ZZfqbcTFCjnZzeR0TDGFkEoOt+m6kP0mG+a4=");
        npc.spawn();

        this.showNPCToAll(npc);

    }

    public void spawnZombie(Player player, Location location){
        CustomZombie z = new CustomZombie(location.getWorld(), player);
        ((CraftWorld)location.getWorld()).addEntity(z, CreatureSpawnEvent.SpawnReason.CUSTOM);
        z.setPosition(location.getX(), location.getY(), location.getZ());
        z.setOwner(player);
        z.setCustomName(new ChatComponentText(player.getName()));
        zombies.add(z);
    }

    public void showNPC(NPC npc, Player player){
        npc.show(player);
    }

    public void showNPCToAll(NPC npc){
        for (Player player : Bukkit.getOnlinePlayers()){
            this.showNPC(npc, player);
        }
    }

}
