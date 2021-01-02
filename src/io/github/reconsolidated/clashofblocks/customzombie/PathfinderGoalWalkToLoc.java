package io.github.reconsolidated.clashofblocks.customzombie;

import net.minecraft.server.v1_16_R2.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftCreature;

public class PathfinderGoalWalkToLoc extends PathfinderGoal {
    @Override
    public boolean a() { //shouldStart()
        if (entity instanceof CustomZombie){
            CustomZombie myZombie = (CustomZombie) entity;
            loc = myZombie.getCurrentDestiny();
            if (loc == null){
                return false;
            }
        }
        return true; // <-- run c()
    }


    public void c() // onStart()
    {
        if (entity instanceof CustomZombie){
            CustomZombie myZombie = (CustomZombie) entity;
            loc = myZombie.getCurrentDestiny();
            if (loc != null)
                this.entity.getNavigation().a(loc.getX(), loc.getY(), loc.getZ(), (double) speed);
        }

    }

    public boolean b(){
        //runs after c()
        //run eve
        if (entity instanceof CustomZombie){
            CustomZombie myZombie = (CustomZombie) entity;
            loc = myZombie.getCurrentDestiny();
            if (loc == null){
                return false;
            }
        }
        return !this.entity.getNavigation().m();
    }

    public void d(){
        // runs if b() is false
        this.entity.getNavigation().a(entity.locX(), entity.locY(), entity.locZ(), (double) speed);
    }

    private double speed;

    private EntityInsentient entity;

    private Location loc;


    public PathfinderGoalWalkToLoc(EntityInsentient entity, Location loc, double speed)
    {
        this.entity = entity;
        this.loc = loc;
        this.speed = speed;
    }

}
