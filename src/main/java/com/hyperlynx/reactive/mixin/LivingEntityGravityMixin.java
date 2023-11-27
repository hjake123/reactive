package com.hyperlynx.reactive.mixin;

import com.hyperlynx.reactive.Registration;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityGravityMixin {

    @Inject(method = "shouldDiscardFriction", at = @At("RETURN"), cancellable = true)
    public void onShouldDiscardFriction(CallbackInfoReturnable<Boolean> cir) {
        if(((LivingEntity) (Object) this).hasEffect(Registration.NULL_GRAVITY.get())){
            if(!(((LivingEntity) (Object) this) instanceof Player player && player.isShiftKeyDown())){
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(method = "getFrictionInfluencedSpeed", at = @At("RETURN"), cancellable = true)
    public void onGetFrictionInfluencedSpeed(CallbackInfoReturnable<Float> cir) {
        if(((LivingEntity) (Object) this).hasEffect(Registration.NULL_GRAVITY.get())){
            cir.setReturnValue((((LivingEntity) (Object) this) instanceof Player player && player.isShiftKeyDown()) ? 0.05f : 0f);
        }
    }
    @Inject(method = "handleRelativeFrictionAndCalculateMovement", at = @At("RETURN"), cancellable = true)
    private void enforceSpeedLimit(CallbackInfoReturnable<Vec3> cir) {
        final double max_speed = 1.3;
        if(((LivingEntity) (Object) this).hasEffect(Registration.NULL_GRAVITY.get())) {
            if(cir.getReturnValue().length() > max_speed){
                cir.setReturnValue(cir.getReturnValue().normalize().scale(max_speed));
            }
            ((LivingEntity) (Object) this).resetFallDistance();
        }
    }
}
