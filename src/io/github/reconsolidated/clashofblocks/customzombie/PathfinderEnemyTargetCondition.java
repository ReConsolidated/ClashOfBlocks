package io.github.reconsolidated.clashofblocks.customzombie;

import net.minecraft.server.v1_16_R2.EntityLiving;
import net.minecraft.server.v1_16_R2.PathfinderTargetCondition;

import javax.annotation.Nullable;

public class PathfinderEnemyTargetCondition extends PathfinderTargetCondition {
    @Override
    public boolean a(@Nullable EntityLiving var0, EntityLiving var1) {
        if (var0 instanceof CustomZombie && var1 instanceof CustomZombie){
            CustomZombie z0 = (CustomZombie) var0;
            CustomZombie z1 = (CustomZombie) var1;
            if (!z0.isReadyToFight()){
                return false;
            }
            if (z0.getOwner().getName().equalsIgnoreCase(z1.getOwner().getName())){
                return false;
            }
        }
        return super.a(var0, var1);
    }


}
