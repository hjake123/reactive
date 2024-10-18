package dev.hyperlynx.reactive.mixin;

import dev.hyperlynx.reactive.Registration;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityGravityMixin {

    @Inject(method = "shouldDiscardFriction", at = @At("RETURN"), cancellable = true)
    public void onShouldDiscardFriction(CallbackInfoReturnable<Boolean> cir) {
        if (((LivingEntity) (Object) this).hasEffect(Registration.NULL_GRAVITY)) {
            if (!(((LivingEntity) (Object) this) instanceof Player player && player.isShiftKeyDown())) {
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(method = "getFrictionInfluencedSpeed", at = @At("RETURN"), cancellable = true)
    public void onGetFrictionInfluencedSpeed(CallbackInfoReturnable<Float> cir) {
        if (((LivingEntity) (Object) this).hasEffect(Registration.NULL_GRAVITY)) {
            cir.setReturnValue((((LivingEntity) (Object) this) instanceof Player player && (player.getDeltaMovement().length() > 10 || player.isShiftKeyDown())) ? 0.05f : 0f);
            ((LivingEntity) (Object) this).resetFallDistance();
        }
    }

    @Inject(method = "isVisuallySwimming", at = @At("RETURN"), cancellable = true)
    public void isVisuallySwimming(CallbackInfoReturnable<Boolean> cir) {
        if (((LivingEntity) (Object) this).hasEffect(Registration.NULL_GRAVITY)) {
            cir.setReturnValue(true);
        }
    }
}