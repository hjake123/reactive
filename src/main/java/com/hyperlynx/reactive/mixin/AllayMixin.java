package com.hyperlynx.reactive.mixin;

import com.hyperlynx.reactive.Registration;
import com.hyperlynx.reactive.ConfigMan;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.animal.allay.Allay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(Allay.class)
public abstract class AllayMixin {
    Optional<BlockPos> symbol_maybe = Optional.empty();
    private static final EntityDataAccessor<Boolean> DATA_CAN_DONATE = SynchedEntityData.defineId(Allay.class, EntityDataSerializers.BOOLEAN);
    int symbol_cache_ticker = 0;
    boolean unredeemed_duplication = false;

    @Inject(method = "defineSynchedData", at = @At("RETURN"))
    public void defineSynchedData(CallbackInfo ci) {
        ((Allay) (Object) this).getEntityData().define(DATA_CAN_DONATE, false);
    }

    @Inject(method = "duplicateAllay", at = @At("RETURN"))
    public void duplicateAllay(CallbackInfo ci) {
        unredeemed_duplication = true;
    }

    @Inject(method = "tick", at = @At("RETURN"))
    public void tick(CallbackInfo ci) {
        symbol_cache_ticker++;
        if(symbol_cache_ticker > ConfigMan.COMMON.crucibleTickDelay.get()){
            symbol_maybe = BlockPos.findClosestMatch(((Allay)(Object)this).blockPosition(), 10, 10,
                    blockPos -> ((Allay)(Object)this).level().getBlockState(blockPos).is(Registration.IRON_SYMBOL.get()));
            symbol_cache_ticker = 0;
        }

        if(symbol_maybe.isPresent()) {
            ((Allay)(Object) this).hurt(((Allay) (Object) this).level().damageSources().magic(), 4);
        }

        if(((Allay)(Object) this).getItemInHand(InteractionHand.MAIN_HAND).is(Registration.CRYSTAL_IRON.get())){
            ((Allay)(Object) this).hurt(((Allay) (Object) this).level().damageSources().magic(), 10);
        }

        if(((Allay)(Object) this).getItemInHand(InteractionHand.MAIN_HAND).is(Registration.QUARTZ_BOTTLE.get())){
            if(unredeemed_duplication){
                ((Allay)(Object) this).level().playSound(null, ((Allay)(Object) this).blockPosition(), SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 0.8F, 1.1F);
                ((Allay)(Object) this).setItemInHand(InteractionHand.MAIN_HAND, Registration.SOUL_BOTTLE.get().getDefaultInstance());
                ((Allay) (Object) this).getEntityData().set(DATA_CAN_DONATE, false);
                unredeemed_duplication = false;
            }
        }
    }

}
