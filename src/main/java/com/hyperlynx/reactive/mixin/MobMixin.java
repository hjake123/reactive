package com.hyperlynx.reactive.mixin;

import com.hyperlynx.reactive.Registration;
import com.hyperlynx.reactive.util.ConfigMan;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Monster;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(Mob.class)
public abstract class MobMixin {
    Optional<BlockPos> symbol_maybe = Optional.empty();
    int symbol_cache_ticker = 0;
    @Inject(method = "tick", at = @At("RETURN"))
    public void hurtUndeadWithDivineSymbolOnTick(CallbackInfo ci) {
        if(((Mob)(Object)this).isInvertedHealAndHarm()){
            symbol_cache_ticker++;
            if(symbol_cache_ticker > ConfigMan.COMMON.crucibleTickDelay.get()*5){
                symbol_maybe = BlockPos.findClosestMatch(((Mob)(Object)this).blockPosition(), 6, 6,
                        blockPos -> ((Mob)(Object)this).level().getBlockState(blockPos).is(Registration.DIVINE_SYMBOL.get()));
                symbol_cache_ticker = 0;
            }

            if(symbol_maybe.isPresent()) {
                ((Mob)(Object) this).hurt(((Mob) (Object) this).level().damageSources().magic(), 1);
            }
        }
    }

}
