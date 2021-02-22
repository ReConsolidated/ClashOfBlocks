package io.github.reconsolidated.clashofblocks.ClashVillage;

import io.github.reconsolidated.clashofblocks.Village.STRUCTURES;
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

    public ClashVillageState(Location palaceLocation, ArrayList<Structure> structures){
        this.structures = structures;
        this.palaceLocation = palaceLocation;
    }

    public boolean containsStructure(STRUCTURES structureType){
        for (int i = 0; i<structures.size(); i++){
            if (structures.get(i).getType().equals(structureType)){
                return true;
            }
        }
        return false;
    }

    public Structure getStructureWithLocation(Location location){
        for (int i = 0; i<structures.size(); i++){
            if (structures.get(i).containsLocation(location)){
                return structures.get(i);
            }
        }
        return null;
    }

    public boolean isOverlappingAnyStructure(STRUCTURES structure, Location location){
        for (int i = 0; i<structures.size(); i++){
            if(structures.get(i).isOverridingStructure(structure, location)){
                return true;
            }
        }
        return false;
    }

    public Location getPalaceLocation(){
        return palaceLocation;
    }

    public void setPalaceLocation(Location palaceLocation){
        this.palaceLocation = palaceLocation;
    }

    public void removeStructureByName(String name, Player player){
        Structure structure = getStructureByName(name);
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
        switch (structure.getType()){
            case HOUSE:
                palaceLocation = structure.getLocation();
                break;
        }
    }

    @Override
    public String toString(){
        String s = "";
        for (int i = 0; i<structures.size(); i++){
            s += structures.get(i).toString() + "\n";
        }
        return "Palace location: (" + palaceLocation.getX() + ", " + palaceLocation.getY() + ", " + palaceLocation.getZ() + ") \n" + s;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("PalaceLocation", palaceLocation);
        map.put("structures", structures);
        return map;
    }

    public static ClashVillageState deserialize(Map<String, Object> map) {
        return new ClashVillageState((Location)map.get("PalaceLocation"), (ArrayList<Structure>)map.get("structures"));
    }
}
