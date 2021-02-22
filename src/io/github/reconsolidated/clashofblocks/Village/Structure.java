package io.github.reconsolidated.clashofblocks.Village;


import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.math.transform.Transform;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.session.PasteBuilder;
import com.sk89q.worldedit.world.World;
import io.github.reconsolidated.clashofblocks.ClashOfBlocks;
import io.github.reconsolidated.clashofblocks.ClashVillage.ClashVillageState;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


@SerializableAs("Structure")
public class Structure implements ConfigurationSerializable  {

    private String name;
    private Location pos1;
    private Location pos2;
    private STRUCTURES type;
    private Location location;
    private Location topLeftLocation;
    private int level;

    public Structure(String name, STRUCTURES type){
        this.name = name;
        this.type = type;
        this.level = 1;
    }

    public Structure(String name, Location pos1, Location pos2, STRUCTURES type, Location location, Location topLeftLocation, int level){
        this.name = name;
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.type = type;
        this.location = location;
        this.topLeftLocation = topLeftLocation;
        this.level = level;
    }

    public static int getStructureSizeX(STRUCTURES structure){
        // TODO STRUCTURES sizes from file
        switch (structure){
            case HOUSE:
                return 9;
            case MINE:
                return 11;
            default:
                return 1;
        }

    }

    public static int getStructureSizeZ(STRUCTURES structure){
        // TODO STRUCTURES sizes from file
        switch (structure){
            case HOUSE:
                return 9;
            case MINE:
                return 11;
            default:
                return 1;
        }

    }

    public STRUCTURES getType(){
        return this.type;
    }

    public boolean isOverridingStructure(STRUCTURES otherStructure, Location location){
        Location topLeft1 = this.topLeftLocation;
        Location bottomRight1 = topLeft1.clone().add(getStructureSizeX(this.type)-1, 0, getStructureSizeZ(this.type)-1);

        double a1 = location.getDirection().angle(new Vector(0, 0, -1)); // 270
        double a2 = location.getDirection().angle(new Vector(1, 0, 0)); // 180
        double a3 = location.getDirection().angle(new Vector(0, 0, 1)); // 90
        double a4 = location.getDirection().angle(new Vector(-1, 0, 0)); // 360

        double smallest = Math.min(a1, Math.min(a2, Math.min(a3, a4)));

        int r = getStructureSizeX(otherStructure);
        Location topLeft2 = null;
        if (smallest == a1) { // 90 degrees
            topLeft2 = location.clone().add(-1*r/2, 0, -1*(r)+1);
        }
        if (smallest == a2){ // 180 degrees
            topLeft2 = location.clone().add(0, 0, -1*(r/2));
        }
        if (smallest == a3){ // 270 degrees
            topLeft2 = location.clone().add(r/2 - r + 1, 0, 0);
        }
        if (smallest == a4){ //360 degrees
            topLeft2 = location.clone().add(-1*r+1, 0, r/2 - r + 1);
        }

        Location bottomRight2 = topLeft2.clone().add(r-1, 0, r-1);

        if (bottomRight2.getBlockX() < topLeft1.getBlockX() || bottomRight1.getBlockX() < topLeft2.getBlockX()){
            return false;
        }
        if (bottomRight2.getBlockZ() < topLeft1.getBlockZ() || bottomRight1.getBlockZ() < topLeft2.getBlockZ()){
            return false;
        }
        return true;
    }

    public int getWidthX(){
        return (int) Math.abs(pos1.getX() - pos2.getX());
    }

    public int getWidthZ(){
        return (int) Math.abs(pos1.getZ() - pos2.getZ());
    }

    public Location getLocation(){
        return this.location;
    }

    public int getLevel(){
        return this.level;
    }

    public void setLevel(int level){
        this.level = level;
    }

    public void destroy(Player player){
        int minX =(int) Math.min(pos1.getX(), pos2.getX());
        int minY =(int) Math.min(pos1.getY(), pos2.getY());
        int minZ =(int) Math.min(pos1.getZ(), pos2.getZ());

        int maxX =(int) Math.max(pos1.getX(), pos2.getX());
        int maxY =(int) Math.max(pos1.getY(), pos2.getY());
        int maxZ =(int) Math.max(pos1.getZ(), pos2.getZ());

        for (int i = minX; i<=maxX; i++){
            for (int j = minY; j<=maxY; j++){
                for (int k = minZ; k<=maxZ; k++){
                    player.getWorld().getBlockAt(i, j, k).setType(Material.AIR);
                    if (j == 49){
                        player.getWorld().getBlockAt(i, j, k).setType(Material.GRASS_BLOCK);
                    }
                }
            }
        }
    }

    private Clipboard loadSchematic(){
        if (Bukkit.getPluginManager().getPlugin("WorldEdit") != null){
            WorldEditPlugin worldEditPlugin = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
            File schematic = new File(worldEditPlugin.getDataFolder() + File.separator + "/schematics/" + name + Integer.toString(this.level) + ".schem");
            try{
                ClipboardFormat format = ClipboardFormats.findByFile(schematic);
                ClipboardReader reader = format.getReader(new FileInputStream(schematic));
                Clipboard clipboard = reader.read();
                return clipboard;
            }catch(IOException e){
                e.printStackTrace();
            }
        }
        else{
            Bukkit.broadcastMessage(ChatColor.RED + "WorldEdit is not installed on this server, couldn't load schematic");
        }
        return null;

    }

    public boolean containsLocation(Location location){
        Location topLeft1 = this.topLeftLocation;
        Location bottomRight1 = topLeft1.clone().add(getStructureSizeX(this.type)-1, 0, getStructureSizeZ(this.type)-1);
        if (location.getBlockX() < topLeft1.getBlockX() || bottomRight1.getBlockX() < location.getBlockX()){
            return false;
        }
        if (location.getBlockZ() < topLeft1.getBlockZ() || bottomRight1.getBlockZ() < location.getBlockZ()){
            return false;
        }
        return true;
    }

    public void build(Location buildLocation) {
        World world = new BukkitWorld(buildLocation.getWorld());
        try (EditSession editSession = WorldEdit.getInstance().newEditSession(world)) {
            Clipboard cb = loadSchematic();
            if (cb == null){
                Bukkit.broadcastMessage("Couldn't load schematic. Contact the admin of the server.");
                return;
            }
            ClipboardHolder cph = new ClipboardHolder(cb);
            Transform transform = new AffineTransform();

            double a1 = buildLocation.getDirection().angle(new Vector(0, 0, -1)); // 270
            double a2 = buildLocation.getDirection().angle(new Vector(1, 0, 0)); // 180
            double a3 = buildLocation.getDirection().angle(new Vector(0, 0, 1)); // 90
            double a4 = buildLocation.getDirection().angle(new Vector(-1, 0, 0)); // 360

            double smallest = Math.min(a1, Math.min(a2, Math.min(a3, a4)));

            if (smallest == a1) { // 90 degrees
                transform = new AffineTransform().rotateY(270);
            }
            if (smallest == a2){ // 180 degrees
                transform = new AffineTransform().rotateY(180);
            }
            if (smallest == a3){ // 270 degrees
                transform = new AffineTransform().rotateY(90);
            }

            cph.setTransform(transform);
            Operation operation = cph
                    .createPaste(editSession)
                    .to(BlockVector3.at(buildLocation.getX(), buildLocation.getY(), buildLocation.getZ()))
                    .ignoreAirBlocks(false)
                    .build();

            Region region = cph.getClipboard().getRegion().clone();
            BlockVector3 origin = cph.getClipboard().getOrigin();

            region.shift(BlockVector3.at(
                    buildLocation.getX() - origin.getX(),
                    buildLocation.getY() - origin.getY(),
                    buildLocation.getZ() - origin.getZ()));


            BlockVector3 b1 = region.getBoundingBox().getPos1();
            BlockVector3 b2 = region.getBoundingBox().getPos2();


            if (smallest == a1) { // 90 degrees
                b1 = rotatePointAround(region.getBoundingBox().getPos1(), buildLocation.getX(), buildLocation.getZ(), Math.PI * 0.5);
                b2 = rotatePointAround(region.getBoundingBox().getPos2(), buildLocation.getX(), buildLocation.getZ(), Math.PI * 0.5);
            }
            if (smallest == a2){ // 180 degrees
                b1 = rotatePointAround(region.getBoundingBox().getPos1(), buildLocation.getX(), buildLocation.getZ(), Math.PI * 1);
                b2 = rotatePointAround(region.getBoundingBox().getPos2(), buildLocation.getX(), buildLocation.getZ(), Math.PI * 1);
            }
            if (smallest == a3){ // 270 degrees
                b1 = rotatePointAround(region.getBoundingBox().getPos1(), buildLocation.getX(), buildLocation.getZ(), Math.PI * -0.5);
                b2 = rotatePointAround(region.getBoundingBox().getPos2(), buildLocation.getX(), buildLocation.getZ(), Math.PI * -0.5);
            }

            pos1 = new Location((org.bukkit.World) region.getWorld(), b1.getX(), b1.getY(), b1.getZ());
            pos2 = new Location((org.bukkit.World) region.getWorld(), b2.getX(), b2.getY(), b2.getZ());


            int minX =(int) Math.min(pos1.getX(), pos2.getX());
            int minZ =(int) Math.min(pos1.getZ(), pos2.getZ());

            this.topLeftLocation = new Location((org.bukkit.World) region.getWorld(),  minX, pos1.getY(), minZ);
            this.location = buildLocation;
            Operations.complete(operation);
        } catch (WorldEditException e) {
            e.printStackTrace();
        }
    }

    private BlockVector3 rotatePointAround(BlockVector3 block, double centerX, double centerZ, double angle) {
     //   double angle = -0.5 * Math.PI;

        double rotatedX = (Math.cos(angle) * (block.getX() - centerX) - Math.sin(angle) * (block.getZ() - centerZ) + centerX);
        double rotatedZ = (Math.sin(angle) * (block.getX() - centerX) + Math.cos(angle) * (block.getZ() - centerZ) + centerZ);
        block = block.add(BlockVector3.at(rotatedX - block.getX(), 0, rotatedZ - block.getZ()));
        return block;
    }

    public String getName(){
        return this.name;
    }

    @Override
    public String toString(){
        return pos1.toString() + " " + pos2.toString();
    }


    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("pos1", pos1);
        map.put("pos2", pos2);
        map.put("type", type.toString());
        map.put("location", location);
        map.put("topLeftLocation", topLeftLocation);
        map.put("level", level);
        return map;
    }

    public static Structure deserialize(Map<String, Object> map) {
        STRUCTURES type = STRUCTURES.valueOf((String)map.get("type"));
        return new Structure(
                (String)map.get("name"),
                (Location)map.get("pos1"),
                (Location)map.get("pos2"),
                type,
                (Location)map.get("location"),
                (Location)map.get("topLeftLocation"),
                (int)map.get("level"));
    }
}
