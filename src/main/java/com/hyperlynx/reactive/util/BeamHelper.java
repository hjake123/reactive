package com.hyperlynx.reactive.util;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class BeamHelper {

    // Taken from getPOVPlayerLook()
    public static BlockHitResult playerRayTrace(Level pLevel, Player pPlayer, ClipContext.Fluid pFluidMode, ClipContext.Block pBlockMode, double range) {
        return rayTrace(pLevel, pPlayer.getEyePosition(), pPlayer.getXRot(), pPlayer.getYRot(), pFluidMode, pBlockMode, range, pPlayer);
    }

    // Taken from getPOVPlayerLook()
    public static BlockHitResult rayTrace(Level level, Vec3 start, float xrot, float yrot, ClipContext.Fluid fluid_mode, ClipContext.Block block_mode, double range, @Nullable Player player){
        float f2 = Mth.cos(-yrot * ((float)Math.PI / 180F) - (float)Math.PI);
        float f3 = Mth.sin(-yrot * ((float)Math.PI / 180F) - (float)Math.PI);
        float f4 = -Mth.cos(-xrot * ((float)Math.PI / 180F));
        float f5 = Mth.sin(-xrot * ((float)Math.PI / 180F));
        float f6 = f3 * f4;
        float f7 = f2 * f4;
        Vec3 vec31 = start.add((double)f6 * range, (double)f5 * range, (double)f7 * range);
        return level.clip(new ClipContext(start, vec31, block_mode, fluid_mode, player));
    }

    public static double distance(double x1, double y1, double z1, double x2, double y2, double z2) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        double dz = z2 - z1;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

}
