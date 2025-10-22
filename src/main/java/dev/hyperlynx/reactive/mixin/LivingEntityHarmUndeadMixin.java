package dev.hyperlynx.reactive.mixin;

import dev.hyperlynx.reactive.Registration;
import dev.hyperlynx.reactive.ConfigMan;
import dev.hyperlynx.reactive.advancements.CriteriaTriggers;
import dev.hyperlynx.reactive.items.CrystalIronItem;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(LivingEntity.class)
public abstract class LivingEntityHarmUndeadMixin {
    @Unique
    Optional<BlockPos> symbol_maybe = Optional.empty();

    @Unique
    int symbol_cache_ticker = 0;

    @Inject(method = "tick", at = @At("RETURN"))
    public void hurtUndeadWithDivineSymbolOnTick(CallbackInfo ci) {
        if(((LivingEntity)(Object)this).isInvertedHealAndHarm()){
            symbol_cache_ticker++;
            if(symbol_cache_ticker > ConfigMan.COMMON.crucibleTickDelay.get()*5){
                symbol_maybe = BlockPos.findClosestMatch(((LivingEntity)(Object)this).blockPosition(), 6, 6,
                        blockPos -> ((LivingEntity)(Object)this).level().getBlockState(blockPos).is(Registration.DIVINE_SYMBOL.get()));
                symbol_cache_ticker = 0;
            }

            if(symbol_maybe.isPresent() && CrystalIronItem.effectNotBlocked(((LivingEntity) (Object) this), 1)) {
                ((LivingEntity)(Object) this).hurt(((LivingEntity) (Object) this).level().damageSources().magic(), 1);
                if(((LivingEntity)(Object) this) instanceof ServerPlayer player) {
                    player.displayClientMessage(Component.translatable("message.reactive.undead_player_divine_hurt"), true);
                    CriteriaTriggers.UNDEAD_PLAYER_DIVINE_HURT.trigger(player);
                }
            } else if (symbol_maybe.isPresent()) {
                symbol_maybe = Optional.empty();
            }
        }
    }

}
