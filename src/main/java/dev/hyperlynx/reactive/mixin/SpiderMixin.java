package dev.hyperlynx.reactive.mixin;

import dev.hyperlynx.reactive.util.OccultSymbolAttractGoal;
import net.minecraft.world.entity.monster.Spider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Spider.class)
public abstract class SpiderMixin {

    @Inject(method = "registerGoals", at = @At("TAIL"))
    public void registerGoals(CallbackInfo ci) {
        ((Spider)(Object) this).goalSelector.addGoal(5, new OccultSymbolAttractGoal(((Spider)(Object) this), 1.0D, 3));
    }

}
