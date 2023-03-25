package com.hyperlynx.reactive.mixin;

import com.hyperlynx.reactive.Registration;
import com.hyperlynx.reactive.be.CrucibleBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

/*
Makes it so that lightning bolts will power the Crucible.
 */
@Mixin(LightningBolt.class)
public class LightningBoltMixin{

    @Shadow @Final private static double DETECTION_RADIUS;

    @Inject(method = "powerLightningRod", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void powerLightningRod(CallbackInfo ci, BlockPos blockpos, BlockState blockstate) {
        BlockPos cruciblePos = BlockPos.findClosestMatch(blockpos, (int) DETECTION_RADIUS, (int) DETECTION_RADIUS,
                pos -> ((LightningBolt)(Object)this).level.getBlockState(pos).is(Registration.CRUCIBLE.get())).orElse(null);
        if(cruciblePos != null){
            BlockEntity be = ((LightningBolt)(Object)this).level.getBlockEntity(cruciblePos);
            if(be instanceof CrucibleBlockEntity) { // Redundant check, but why not be extra careful in a mixin?
                ((CrucibleBlockEntity) be).beHitByLightning();
            }
        }
    }
}
