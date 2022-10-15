package com.hyperlynx.reactive.mixin;

import com.hyperlynx.reactive.Registration;
import com.hyperlynx.reactive.util.ConfigMan;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(Allay.class)
public abstract class AllayMixin {

    @Shadow public abstract SimpleContainer getInventory();

    Optional<BlockPos> symbol_maybe = Optional.empty();
    int symbol_cache_ticker = 0;

    @Inject(method = "tick", at = @At("RETURN"))
    public void tick(CallbackInfo ci) {
        symbol_cache_ticker++;
        if(symbol_cache_ticker > ConfigMan.COMMON.crucibleTickDelay.get()){
            symbol_maybe = BlockPos.findClosestMatch(((Allay)(Object)this).blockPosition(), 10, 10,
                    blockPos -> ((Allay)(Object)this).level.getBlockState(blockPos).is(Registration.IRON_SYMBOL.get()));
            symbol_cache_ticker = 0;
        }

        if(symbol_maybe.isPresent()) {
            ((Allay)(Object) this).hurt(DamageSource.MAGIC, 4);
        }

        if(((Allay)(Object) this).getItemInHand(InteractionHand.MAIN_HAND).is(Registration.CRYSTAL_IRON.get())){
            ((Allay)(Object) this).hurt(DamageSource.MAGIC, 10);
        }
    }

}
