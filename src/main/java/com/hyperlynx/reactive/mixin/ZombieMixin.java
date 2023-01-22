package com.hyperlynx.reactive.mixin;

import com.hyperlynx.reactive.util.OccultSymbolAttractGoal;
import net.minecraft.world.entity.monster.Zombie;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Zombie.class)
public abstract class ZombieMixin {

    @Inject(method = "addBehaviourGoals", at = @At("TAIL"))
    public void addBehaviourGoals(CallbackInfo ci) {
        ((Zombie)(Object) this).goalSelector.addGoal(5, new OccultSymbolAttractGoal(((Zombie)(Object) this), 1.0D, 3));
    }

}
