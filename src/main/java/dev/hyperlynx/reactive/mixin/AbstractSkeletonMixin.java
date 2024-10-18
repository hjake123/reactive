package dev.hyperlynx.reactive.mixin;

import dev.hyperlynx.reactive.util.OccultSymbolAttractGoal;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractSkeleton.class)
public abstract class AbstractSkeletonMixin {

    @Inject(method = "registerGoals", at = @At("TAIL"))
    public void registerGoals(CallbackInfo ci) {
        ((AbstractSkeleton)(Object) this).goalSelector.addGoal(5, new OccultSymbolAttractGoal(((AbstractSkeleton)(Object) this), 1.0D, 3));
    }

}
