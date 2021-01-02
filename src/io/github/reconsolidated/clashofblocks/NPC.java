package io.github.reconsolidated.clashofblocks;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.util.UUID;


public class NPC {
    private Location location;
    private String name;
    private GameProfile gameProfile;
    private Object entityPlayer;
    private String texture;
    private String signature;

    public NPC(Location location, String name, String texture, String signature) //You can also directly use the nms world class but this is easier if you are spawning this entity.
    {
        this.location = location;
        this.name = name;
        this.texture = texture;
        this.signature = signature;
    }

    public void spawn() {
        try {
            Object minecraftServer = getCraftBukkitClass("CraftServer").getMethod("getServer").invoke(Bukkit.getServer());
            Object worldServer = getCraftBukkitClass("CraftWorld").getMethod("getHandle").invoke(location.getWorld());

            this.gameProfile = new GameProfile(UUID.randomUUID(), ChatColor.translateAlternateColorCodes('&', this.name));
            this.gameProfile.getProperties().put("textures", new Property("textures", texture, signature));

            Constructor<?> entityPlayerConstructor = getNMSClass("EntityPlayer").getDeclaredConstructors()[0];
            Constructor<?> interactManagerConstructor  = getNMSClass("PlayerInteractManager").getDeclaredConstructors()[0];

            this.entityPlayer = entityPlayerConstructor.newInstance(minecraftServer, worldServer, gameProfile, interactManagerConstructor.newInstance(worldServer));
            this.entityPlayer.getClass().getMethod("setLocation", double.class, double.class, double.class, float.class, float.class)
                    .invoke(entityPlayer, location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        }
        catch(Exception ex){
            ex.printStackTrace();
        }

    }

    public void show(Player player){

        try{
            Object addPlayerEnum = getNMSClass("PacketPlayOutPlayerInfo$EnumPlayerInfoAction").getField("ADD_PLAYER").get(null);
            Constructor<?> packetPlayOutPlayerInfoConstructor =
                    getNMSClass("PacketPlayOutPlayerInfo")
                            .getConstructor(
                                    getNMSClass("PacketPlayOutPlayerInfo$EnumPlayerInfoAction"),
                                    Class.forName("[Lnet.minecraft.server." + getVersion() + ".EntityPlayer;"));

            Object array = Array.newInstance(getNMSClass("EntityPlayer"), 1);
            Array.set(array, 0, this.entityPlayer);

            Object packetPlayOutPlayerInfo = packetPlayOutPlayerInfoConstructor.newInstance(addPlayerEnum, array);
            sendPacket(player, packetPlayOutPlayerInfo);

            Constructor<?> packetPlayOutNamedEntitySpawnConstructor = getNMSClass("PacketPlayOutNamedEntitySpawn").getConstructor(getNMSClass("EntityHuman"));
            Object packetPlayOutNamedEntitySpawn = packetPlayOutNamedEntitySpawnConstructor.newInstance(this.entityPlayer);
            sendPacket(player, packetPlayOutNamedEntitySpawn);

            Constructor<?> packetPlayOutEntityHeadRotationConstructor = getNMSClass("PacketPlayOutEntityHeadRotation").getConstructor(getNMSClass("Entity"), byte.class);
            float yaw = (float) this.entityPlayer.getClass().getField("yaw").get(this.entityPlayer);
            Object packetPlayOutEntityHeadRotation = packetPlayOutEntityHeadRotationConstructor.newInstance(this.entityPlayer, (byte) (yaw *256/360));
            sendPacket(player, packetPlayOutEntityHeadRotation);

        }catch(Exception ex){
            ex.printStackTrace();
        }


    }

    private void sendPacket(Player player, Object packet){
        try{
            Object handle = player.getClass().getMethod("getHandle").invoke(player);
            Object playerConnection = handle.getClass().getField("playerConnection").get(handle);

            playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private Class<?> getNMSClass(String name){
        try{
            return Class.forName("net.minecraft.server." + getVersion() + "." + name);
        }
        catch (ClassNotFoundException ex){
            ex.printStackTrace();
        }
        return null;
    }

    private Class<?> getCraftBukkitClass(String name){
        try {
            return Class.forName("org.bukkit.craftbukkit." + getVersion() + "." + name);
        } catch (ClassNotFoundException ex){
            ex.printStackTrace();
        }
        return null;
    }

    private String getVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    }

}