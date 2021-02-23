package io.github.reconsolidated.clashofblocks.customzombie;

import net.minecraft.server.v1_16_R2.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R2.CraftWorld;
import org.bukkit.entity.Player;

import java.util.Map;

import static io.github.reconsolidated.clashofblocks.Utils.Utils.getPrivateField;

public class CustomZombie extends EntityZombie implements MovableByPlayer
{

    private org.bukkit.World world2;
    private Location currentDestiny;
    private Player owner;
    private boolean isReadyToFight = false;

    public CustomZombie(org.bukkit.World world, Player player)
    {
        super(((CraftWorld)world).getHandle());
        setOwner(player);
        this.world2 = world;
        this.m();


    }

    public void setCurrentDestiny(Location location){
        this.currentDestiny = location;
    }

    public void setIsReadyToFight(boolean isReady){
        this.isReadyToFight = isReady;
    }

    public boolean isReadyToFight(){
        return this.isReadyToFight;
    }

    public Location getCurrentDestiny(){
        return currentDestiny;
    }

    public void setOwner(Player player){
        this.owner = player;
    }

    public Player getOwner(){
        return this.owner;
    }

    @Override
    protected void initPathfinder() {
        this.goalSelector.a(8, new PathfinderGoalRandomLookaround(this));

    }

    @Override
    protected void m() {
        //PathfinderGoalNearestEnemyTarget is for choosing a target from enemy team
        this.targetSelector.a(1, new PathfinderGoalNearestEnemyTarget(this, CustomZombie.class, true, this.owner.getName()));
        //PathfinderGoalZombieAttack enables zombies to walk towards their target
        this.goalSelector.a(2, new PathfinderGoalZombieAttack(this, 1.0D, false));
  //      this.targetSelector.a(3, new PathfinderGoalNearestAttackableTarget(this, EntityIronGolem.class, true));
  //      this.goalSelector.a(6, new PathfinderGoalMoveThroughVillage(this, 1.0D, true, 4, this::eU));
        this.goalSelector.a(7, new PathfinderGoalWalkToLoc(this, currentDestiny, 2));
  //      this.goalSelector.a(7, new PathfinderGoalRandomStrollLand(this, 1.0D));
  //      this.targetSelector.a(4, (new PathfinderGoalHurtByTarget(this, new Class[0])).a(new Class[]{EntityPigZombie.class}));
//        if (this.world.spigotConfig.zombieAggressiveTowardsVillager) {
//            this.targetSelector.a(3, new PathfinderGoalNearestAttackableTarget(this, EntityVillagerAbstract.class, false));
//        }


//
    }

    @Override
    public void setDestination(Location location) {
        currentDestiny = location;
    }

    public enum EntityTypes
    {
        //NAME("Entity name", Entity ID, yourcustomclass.class);
        CUSTOM_ZOMBIE("Zombie", 54, CustomZombie.class); //You can add as many as you want.

        private EntityTypes(String name, int id, Class<? extends Entity> custom)
        {
            addToMaps(custom, name, id);
        }

        public static void spawnEntity(Entity entity, Location loc)
        {
            entity.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
            ((CraftWorld)loc.getWorld()).getHandle().addEntity(entity);
        }

        private static void addToMaps(Class clazz, String name, int id)
        {
            //getPrivateField is the method from above.
            //Remove the lines with // in front of them if you want to override default entities (You'd have to remove the default entity from the map first though).
            ((Map)getPrivateField("c", net.minecraft.server.v1_16_R2.EntityTypes.class, null)).put(name, clazz);
            ((Map)getPrivateField("d", net.minecraft.server.v1_16_R2.EntityTypes.class, null)).put(clazz, name);
            //((Map)getPrivateField("e", net.minecraft.server.v1_7_R4.EntityTypes.class, null)).put(Integer.valueOf(id), clazz);
            ((Map)getPrivateField("f", net.minecraft.server.v1_16_R2.EntityTypes.class, null)).put(clazz, Integer.valueOf(id));
            //((Map)getPrivateField("g", net.minecraft.server.v1_7_R4.EntityTypes.class, null)).put(name, Integer.valueOf(id));
        }
    }
}

