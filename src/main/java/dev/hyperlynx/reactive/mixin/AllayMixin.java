package dev.hyperlynx.reactive.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.hyperlynx.reactive.ConfigMan;
import dev.hyperlynx.reactive.registration.ReactiveBlocks;
import dev.hyperlynx.reactive.registration.ReactiveCriterionTriggers;
import dev.hyperlynx.reactive.registration.ReactiveItems;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@SuppressWarnings("ALL")
@Mixin(Allay.class)
public abstract class AllayMixin {
    @Shadow
    @Final
    private SimpleContainer inventory;
    Optional<BlockPos> symbol_maybe = Optional.empty();
    private static final EntityDataAccessor<Boolean> DATA_CAN_DONATE = SynchedEntityData.defineId(Allay.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_JUST_DONATED = SynchedEntityData.defineId(Allay.class, EntityDataSerializers.BOOLEAN);
    int symbol_cache_ticker = 0;

    @Inject(method = "defineSynchedData", at = @At("RETURN"))
    public void defineSynchedData(CallbackInfo ci, @Local SynchedEntityData.Builder builder) {
        builder.define(DATA_CAN_DONATE, false);
        builder.define(DATA_JUST_DONATED, false);
    }

    private Allay self() {
        return ((Allay)(Object) this);
    }

    private SynchedEntityData data() {
        return self().getEntityData();
    }

    @Inject(method = "duplicateAllay", at = @At("RETURN"))
    public void duplicateAllay(CallbackInfo ci) {
        data().set(DATA_CAN_DONATE, true);
        data().set(DATA_JUST_DONATED, false);
    }

    @Inject(method = "tick", at = @At("RETURN"))
    public void reactiveTick(CallbackInfo ci) {
        symbol_cache_ticker++;
        if(symbol_cache_ticker > ConfigMan.COMMON.crucibleTickDelay.get()){
            symbol_maybe = BlockPos.findClosestMatch(self().blockPosition(), 10, 10,
                    blockPos -> ((Allay)(Object)this).level().getBlockState(blockPos).is(ReactiveBlocks.IRON_SYMBOL.get()));
            symbol_cache_ticker = 0;
        }

        if(symbol_maybe.isPresent()) {
            self().hurt(self().level().damageSources().magic(), 4);
        }

        if(self().getItemInHand(InteractionHand.MAIN_HAND).is(ReactiveItems.CRYSTAL_IRON.get())){
            self().hurt(self().level().damageSources().magic(), 10);
        }

        if(self().getItemInHand(InteractionHand.MAIN_HAND).is(ReactiveItems.QUARTZ_BOTTLE.get())){
            if(data().get(DATA_CAN_DONATE)){
                self().level().playSound(null, self().blockPosition(), SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 0.8F, 1.1F);
                self().setItemInHand(InteractionHand.MAIN_HAND, ReactiveItems.SOUL_BOTTLE.get().getDefaultInstance());
                data().set(DATA_CAN_DONATE, false);
                data().set(DATA_JUST_DONATED, true);
            }
        }
    }

    @Inject(method = "mobInteract", at = @At("HEAD"))
    public void reactiveInteractTestTakeSoul(Player player, InteractionHand hand, CallbackInfoReturnable cir) {
        ItemStack held_item = self().getItemInHand(InteractionHand.MAIN_HAND);
        if(held_item.is(ReactiveItems.SOUL_BOTTLE) && data().get(DATA_JUST_DONATED) && player instanceof ServerPlayer splayer) {
            ReactiveCriterionTriggers.GET_SOUL_FROM_ALLAY.get().trigger(splayer);
            data().set(DATA_JUST_DONATED, false);
        }
    }

}
