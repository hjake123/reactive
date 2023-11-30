package com.hyperlynx.reactive.mixin;

import com.hyperlynx.reactive.Registration;
import com.hyperlynx.reactive.fx.particles.ParticleScribe;
import com.hyperlynx.reactive.util.ConfigMan;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.entity.vehicle.Boat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(Boat.class)
public abstract class BoatMixin {
    @Inject(method = "tick", at = @At("RETURN"))
    public void tick(CallbackInfo ci) {
        // Draw End Rod particles under no gravity boats.
        Boat self = ((Boat) (Object) this);
        if(self.level.isClientSide && self.isNoGravity()){
            ParticleScribe.drawParticle(self.level, ParticleTypes.END_ROD, self.getX(), self.getY()-0.02, self.getZ());
        }
    }

}
