package dev.hyperlynx.reactive.mixin;

import dev.hyperlynx.reactive.util.OccultSymbolAttractGoal;
import net.minecraft.world.entity.monster.Creeper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Creeper.class)
public abstract class CreeperMixin {

    @Inject(method = "registerGoals", at = @At("TAIL"))
    public void registerGoals(CallbackInfo ci) {
        ((Creeper)(Object) this).goalSelector.addGoal(5, new OccultSymbolAttractGoal(((Creeper)(Object) this), 1.0D, 3));
    }

}
