package io.github.reconsolidated.clashofblocks.ClashPlayer;

import io.github.reconsolidated.clashofblocks.ClashOfBlocks;
import io.github.reconsolidated.clashofblocks.ClashVillage.ClashVillageState;
import io.github.reconsolidated.clashofblocks.Utils.ConfigFileManagement;
import io.github.reconsolidated.clashofblocks.Village.STRUCTURES;
import io.github.reconsolidated.clashofblocks.Village.Structure;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public class ClashPlayer {

    private ClashOfBlocks plugin;
    private Player player;
    private Location villageLocation;

    private ClashVillageState villageState;

    public ClashPlayer(ClashOfBlocks plugin, Player player){
        FileConfiguration config = ConfigFileManagement.getCustomConfig(plugin, plugin.getDataFolder() + File.separator + "config.yml");
        this.plugin = plugin;
        this.villageLocation = (Location) config.get("NextVillageLocation");
        Location nextVillageLocation = villageLocation.clone();
        nextVillageLocation.add(1000, 0,0);

        config.set("NextVillageLocation", nextVillageLocation);
        ConfigFileManagement.saveCustomConfig(plugin, plugin.getDataFolder() + File.separator + "config.yml", config);

        this.player = player;
        this.villageState = new ClashVillageState(villageLocation.clone().add(10, 0, 0));

        player.teleport(villageLocation);
        new Structure("Village", STRUCTURES.VILLAGE);

        saveClashPlayer();
    }

    public ClashPlayer(ClashOfBlocks plugin, Player player, Location villageLocation, ClashVillageState villageState){
        this.plugin = plugin;
        this.villageLocation = villageLocation;
        this.player = player;
        this.villageState = villageState;

        saveClashPlayer();
    }

    public Structure getStructureWithLocation(Location location){
        return villageState.getStructureWithLocation(location);
    }

    public boolean canPlaceStructure(STRUCTURES candidateStructure){
        if (villageState.containsStructure(candidateStructure)){
            return false;
        }
        if (villageState.isOverlappingAnyStructure(candidateStructure, this.player.getLocation())){
            return false;
        }
        return true;
    }

    public void saveClashPlayer(){
        FileConfiguration playersConfig = ConfigFileManagement.getCustomConfig(this.plugin,
                this.plugin.getDataFolder()
                        + File.separator
                        + "ClashPlayers.yml");

        ConfigurationSection section = playersConfig.getConfigurationSection(player.getUniqueId().toString());
        if (section == null){
            section = playersConfig.createSection(player.getUniqueId().toString());
        }
        section.set("VillageLocation", villageLocation);
        section.set("VillageState", villageState);

        ConfigFileManagement.saveCustomConfig(this.plugin,
                this.plugin.getDataFolder()
                        + File.separator
                        + "ClashPlayers.yml", playersConfig);
    }

    public static ClashPlayer loadClashPlayer(ClashOfBlocks plugin, Player player){
        FileConfiguration playersConfig = ConfigFileManagement.getCustomConfig(plugin,
                plugin.getDataFolder()
                        + File.separator
                        + "ClashPlayers.yml");

        ConfigurationSection section = playersConfig.getConfigurationSection(player.getUniqueId().toString());
        if (section == null){
            section = playersConfig.createSection(player.getUniqueId().toString());
        }

        Location villageLocation = (Location) section.get("VillageLocation");
        ClashVillageState villageState = (ClashVillageState) section.get("VillageState");

        ClashPlayer cp = new ClashPlayer(plugin, player, villageLocation, villageState);
        return cp;
    }

    public void addStructure(Structure structure){
        this.villageState.addStructure(structure);
        this.saveClashPlayer();
    }

    public ClashVillageState getVillageState(){
        return this.villageState;
    }

    @Override
    public String toString(){
        return "Player UUID: " + player.getUniqueId() + ", " + villageState.toString();
    }

    public Location getVillageLocation(){
        return villageLocation;
    }

    public Player getPlayer(){
        return this.player;
    }
}
