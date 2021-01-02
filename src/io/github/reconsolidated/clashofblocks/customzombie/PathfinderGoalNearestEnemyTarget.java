package io.github.reconsolidated.clashofblocks.customzombie;

import net.minecraft.server.v1_16_R2.*;
import org.bukkit.Bukkit;

import javax.annotation.Nullable;
import java.util.function.Predicate;

public class PathfinderGoalNearestEnemyTarget extends PathfinderGoalNearestAttackableTarget {
    private String ownerName;
    public PathfinderGoalNearestEnemyTarget(EntityInsentient entityinsentient, Class oclass, boolean flag, String ownerName) {
        super(entityinsentient, oclass, flag);
        this.ownerName = ownerName;
        this.d = (new PathfinderEnemyTargetCondition()).a(this.k()).a(null);

    }

    public PathfinderGoalNearestEnemyTarget(EntityInsentient entityinsentient, Class oclass, boolean flag, boolean flag1) {
        super(entityinsentient, oclass, flag, flag1);
    }

    public PathfinderGoalNearestEnemyTarget(EntityInsentient entityinsentient, Class oclass, int i, boolean flag, boolean flag1, @Nullable Predicate predicate) {
        super(entityinsentient, oclass, i, flag, flag1, predicate);
    }

    @Override
    public void c(){
        super.c();
    }

    @Override
    protected void g() {
        if (this.a != EntityHuman.class && this.a != EntityPlayer.class) {
            this.c = this.e.world.b(this.a, this.d, this.e, this.e.locX(), this.e.getHeadY(), this.e.locZ(), this.a(this.k()));

            if (this.a == CustomZombie.class && this.c != null){
                if (this.c instanceof CustomZombie){

                    CustomZombie targetZombie = (CustomZombie) this.c;
                    if (targetZombie.getOwner().getName().equalsIgnoreCase(ownerName)){
                        Bukkit.broadcastMessage(this.c.toString());
                        this.c = null;
                    }
                }
            }

        } else {
            this.c = this.e.world.a(this.d, this.e, this.e.locX(), this.e.getHeadY(), this.e.locZ());
        }

    }
}
