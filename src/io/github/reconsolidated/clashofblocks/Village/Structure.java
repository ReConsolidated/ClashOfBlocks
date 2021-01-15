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
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.math.transform.Transform;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.session.PasteBuilder;
import com.sk89q.worldedit.world.World;
import io.github.reconsolidated.clashofblocks.ClashOfBlocks;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Structure {

    private String name;

    private ClashOfBlocks plugin;

    private Region region;

    public Structure(ClashOfBlocks plugin, String name, Player player){
        this.name = name;
        this.plugin = plugin;
        build(player);

    }

    public void destroy(Player player){
        int minX = Math.min(region.getBoundingBox().getPos1().getX(), region.getBoundingBox().getPos2().getX());
        int minY = Math.min(region.getBoundingBox().getPos1().getY(), region.getBoundingBox().getPos2().getY());
        int minZ = Math.min(region.getBoundingBox().getPos1().getZ(), region.getBoundingBox().getPos2().getZ());

        int maxX = Math.max(region.getBoundingBox().getPos1().getX(), region.getBoundingBox().getPos2().getX());
        int maxY = Math.max(region.getBoundingBox().getPos1().getY(), region.getBoundingBox().getPos2().getY());
        int maxZ = Math.max(region.getBoundingBox().getPos1().getZ(), region.getBoundingBox().getPos2().getZ());

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

    private Clipboard loadSchematic(Player player){
        Location location = player.getLocation();
        if (Bukkit.getPluginManager().getPlugin("WorldEdit") != null){
            WorldEditPlugin worldEditPlugin = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
            File schematic = new File(worldEditPlugin.getDataFolder() + File.separator + "/schematics/" + name + ".schem");
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
            player.sendMessage(ChatColor.RED + "WorldEdit is not installed on this server, couldn't load schematic");
        }
        return null;

    }

    public void build(Player player) {

        World world = new BukkitWorld(player.getWorld());
        try (EditSession editSession = WorldEdit.getInstance().newEditSession(world)) {
            Clipboard cb = loadSchematic(player);
            if (cb == null){
                Bukkit.broadcastMessage("Couldn't load schematic. Contact the admin of the server.");
                return;
            }
            ClipboardHolder cph = new ClipboardHolder(cb);
            Transform transform = new AffineTransform();

            double a1 = player.getLocation().getDirection().angle(new Vector(0, 0, -1)); // 270
            double a2 = player.getLocation().getDirection().angle(new Vector(1, 0, 0)); // 180
            double a3 = player.getLocation().getDirection().angle(new Vector(0, 0, 1)); // 90
            double a4 = player.getLocation().getDirection().angle(new Vector(-1, 0, 0)); // 360

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
                    .to(BlockVector3.at(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ()))
                    .ignoreAirBlocks(false)
                    .build();

            this.region = cph.getClipboard().getRegion().clone();
            BlockVector3 origin = cph.getClipboard().getOrigin();

            this.region.shift(BlockVector3.at(
                    player.getLocation().getX() - origin.getX(),
                    player.getLocation().getY() - origin.getY(),
                    player.getLocation().getZ() - origin.getZ()));

            BlockVector3 b1 = this.region.getBoundingBox().getPos1();
            BlockVector3 b2 = this.region.getBoundingBox().getPos2();

            if (smallest == a1) { // 90 degrees
                b1 = rotatePointAround(this.region.getBoundingBox().getPos1(), region.getCenter().getX(), region.getCenter().getZ(), Math.PI * -0.5);
                b2 = rotatePointAround(this.region.getBoundingBox().getPos2(), region.getCenter().getX(), region.getCenter().getZ(), Math.PI * -0.5);
            }
            if (smallest == a2){ // 180 degrees
                b1 = rotatePointAround(this.region.getBoundingBox().getPos1(), region.getCenter().getX(), region.getCenter().getZ(), Math.PI * 1);
                b2 = rotatePointAround(this.region.getBoundingBox().getPos2(), region.getCenter().getX(), region.getCenter().getZ(), Math.PI * 1);
            }
            if (smallest == a3){ // 270 degrees
                b1 = rotatePointAround(this.region.getBoundingBox().getPos1(), region.getCenter().getX(), region.getCenter().getZ(), Math.PI * 0.5);
                b2 = rotatePointAround(this.region.getBoundingBox().getPos2(), region.getCenter().getX(), region.getCenter().getZ(), Math.PI * 0.5);
            }

            this.region.getBoundingBox().setPos1(b1);
            this.region.getBoundingBox().setPos2(b2);

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
}
