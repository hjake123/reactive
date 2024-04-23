package com.hyperlynx.reactive.mixin;

import com.hyperlynx.reactive.fx.particles.ParticleScribe;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.vehicle.Boat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Boat.class)
public abstract class BoatMixin {
    @Inject(method = "tick", at = @At("RETURN"))
    public void tick(CallbackInfo ci) {
        // Draw End Rod particles under no gravity boats.
        Boat self = ((Boat) (Object) this);
        if(self.level().isClientSide && self.isNoGravity()){
            ParticleScribe.drawParticle(self.level(), ParticleTypes.END_ROD, self.getX(), self.getY()-0.02, self.getZ());
        }
    }

}
