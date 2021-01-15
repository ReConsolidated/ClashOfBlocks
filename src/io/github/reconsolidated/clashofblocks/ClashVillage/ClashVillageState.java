package io.github.reconsolidated.clashofblocks.ClashVillage;

import io.github.reconsolidated.clashofblocks.Village.Structure;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@SerializableAs("ClashVillageState")
public class ClashVillageState implements ConfigurationSerializable {
    private Location palaceLocation;
    private ArrayList<Structure> structures;

    public ClashVillageState(Location palaceLocation){
        this.structures = new ArrayList<>();
        this.palaceLocation = palaceLocation;
    }

    public Location getPalaceLocation(){
        return palaceLocation;
    }

    public void setPalaceLocation(Location palaceLocation){
        this.palaceLocation = palaceLocation;
    }

    public void removeStructureByName(String name, Player player){
        Structure structure = getStructureByName("HOUSE");
        structure.destroy(player);
        for (int i = 0; i<structures.size(); i++){
            if (structures.get(i).equals(structure)){
                structures.remove(structure);
            }
        }
    }

    public Structure getStructureByName(String name){
        for (int i = 0; i<structures.size(); i++){
            if (structures.get(i).getName().equalsIgnoreCase(name)){
                return structures.get(i);
            }
        }
        return null;
    }

    public void addStructure(Structure structure){
        structures.add(structure);
    }

    @Override
    public String toString(){
        return "Palace location: (" + palaceLocation.getX() + ", " + palaceLocation.getY() + ", " + palaceLocation.getZ() + ") ";
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("PalaceLocation", palaceLocation);
        return map;
    }

    public static ClashVillageState deserialize(Map<String, Object> map) {
        return new ClashVillageState((Location)map.get("PalaceLocation"));
    }
}
