package dev.hyperlynx.reactive.mixin;

import dev.hyperlynx.reactive.Registration;
import dev.hyperlynx.reactive.ConfigMan;
import dev.hyperlynx.reactive.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(Allay.class)
public abstract class AllayMixin {
    @Unique
    Optional<BlockPos> symbol_maybe = Optional.empty();

    @Unique
    private static final EntityDataAccessor<Boolean> DATA_CAN_DONATE = SynchedEntityData.defineId(Allay.class, EntityDataSerializers.BOOLEAN);

    @Unique
    private static final EntityDataAccessor<Boolean> DATA_JUST_DONATED = SynchedEntityData.defineId(Allay.class, EntityDataSerializers.BOOLEAN);

    @Unique
    int symbol_cache_ticker = 0;

    private Allay self() {
        return ((Allay)(Object) this);
    }

    @Inject(method = "defineSynchedData", at = @At("RETURN"))
    public void defineSynchedData(CallbackInfo ci) {
        self().getEntityData().define(DATA_CAN_DONATE, false);
        self().getEntityData().define(DATA_JUST_DONATED, false);
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
                    blockPos -> self().level().getBlockState(blockPos).is(Registration.IRON_SYMBOL.get()));
            symbol_cache_ticker = 0;
        }

        if(symbol_maybe.isPresent()) {
            self().hurt(self().level().damageSources().magic(), 4);
        }

        if(self().getItemInHand(InteractionHand.MAIN_HAND).is(Registration.CRYSTAL_IRON.get())){
            self().hurt(self().level().damageSources().magic(), 10);
        }

        if(self().getItemInHand(InteractionHand.MAIN_HAND).is(Registration.QUARTZ_BOTTLE.get())){
            if(data().get(DATA_CAN_DONATE)){
                self().level().playSound(null, self().blockPosition(), SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 0.8F, 1.1F);
                self().setItemInHand(InteractionHand.MAIN_HAND, Registration.SOUL_BOTTLE.get().getDefaultInstance());
                data().set(DATA_CAN_DONATE, false);
                data().set(DATA_JUST_DONATED, true);
            }
        }
    }

    @Inject(method = "mobInteract", at = @At("HEAD"))
    public void reactiveInteractTestTakeSoul(Player player, InteractionHand hand, CallbackInfoReturnable cir) {
        ItemStack held_item = self().getItemInHand(InteractionHand.MAIN_HAND);
        if(held_item.is(Registration.SOUL_BOTTLE.get()) && data().get(DATA_JUST_DONATED) && player instanceof ServerPlayer splayer) {
            CriteriaTriggers.GET_SOUL_FROM_ALLAY.trigger(splayer);
            data().set(DATA_JUST_DONATED, false);
        }
    }

}
