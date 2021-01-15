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

    private BlockVector3 dimensions;
    private Region region;
    private Location playerLocationWhenBuilding;

    public Structure(ClashOfBlocks plugin, String name, Player player){
        this.name = name;
        this.plugin = plugin;
        build(player);
    }

    public void destroy(Player player){
        Bukkit.broadcastMessage(dimensions.toString());
        Bukkit.broadcastMessage(region.toString());
        region.getBoundingBox().forEach(blockVector -> {
            Bukkit.broadcastMessage(blockVector.toString());
            player.getWorld().getBlockAt(blockVector.getX(), blockVector.getY(), blockVector.getZ()).setType(Material.AIR);
        });

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
            Bukkit.broadcastMessage(editSession.getMaximumPoint().toString());
            Clipboard cb = loadSchematic(player);
            if (cb == null){
                Bukkit.broadcastMessage("Couldn't load schematic");
                return;
            }
            ClipboardHolder cph = new ClipboardHolder(cb);
            Bukkit.broadcastMessage(cph.getClipboard().getDimensions().toString());
            Transform transform = new AffineTransform();

            this.region = cph.getClipboard().getRegion().clone();
            Bukkit.broadcastMessage("Before shift: ");
            Bukkit.broadcastMessage(region.getMinimumPoint().toString());
            Bukkit.broadcastMessage(region.getMaximumPoint().toString());


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

            this.dimensions = cph.getClipboard().getDimensions();


            Bukkit.broadcastMessage("Player location: ");
            Bukkit.broadcastMessage(player.getLocation().toString());
            Bukkit.broadcastMessage("Region center location: ");
            Bukkit.broadcastMessage(region.getCenter().toString());

            this.region.shift(BlockVector3.at(
                    player.getLocation().getX() - region.getCenter().getX(),
                    player.getLocation().getY() - region.getCenter().getY(),
                    player.getLocation().getZ() - region.getCenter().getZ()));

            Bukkit.broadcastMessage("After shift: ");
            Bukkit.broadcastMessage(region.getMinimumPoint().toString());
            Bukkit.broadcastMessage(region.getMaximumPoint().toString());
            Operations.complete(operation);
        } catch (WorldEditException e) {
            e.printStackTrace();
        }
    }

    public String getName(){
        return this.name;
    }
}
