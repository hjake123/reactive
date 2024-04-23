package com.hyperlynx.reactive.mixin;

import com.hyperlynx.reactive.Registration;
import com.hyperlynx.reactive.ConfigMan;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.monster.Phantom;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(Phantom.class)
public abstract class PhantomMixin {

    Optional<BlockPos> symbol_maybe = Optional.empty();
    int symbol_cache_ticker = 0;

    @Inject(method = "tick", at = @At("RETURN"))
    public void tick(CallbackInfo ci) {
        symbol_cache_ticker++;
        if(symbol_cache_ticker > ConfigMan.COMMON.crucibleTickDelay.get()){
            symbol_maybe = BlockPos.findClosestMatch(((Phantom)(Object)this).blockPosition(), 10, 10,
                    blockPos -> ((Phantom)(Object)this).level().getBlockState(blockPos).is(Registration.IRON_SYMBOL.get()));
            symbol_cache_ticker = 0;
        }

        if(symbol_maybe.isPresent()) {
            ((Phantom)(Object) this).hurt(((Phantom) (Object) this).level().damageSources().magic(), 4);
        }
    }
}
