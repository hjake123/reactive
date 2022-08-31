package com.hyperlynx.reactive.util;

import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class Helper {
    // Taken from getPOVPlayerLook()
    public static BlockHitResult playerRayTrace(Level pLevel, Player pPlayer, ClipContext.Fluid pFluidMode, ClipContext.Block pBlockMode, double range) {
        float f = pPlayer.getXRot();
        float f1 = pPlayer.getYRot();
        Vec3 vec3 = pPlayer.getEyePosition();
        float f2 = Mth.cos(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
        float f3 = Mth.sin(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
        float f4 = -Mth.cos(-f * ((float)Math.PI / 180F));
        float f5 = Mth.sin(-f * ((float)Math.PI / 180F));
        float f6 = f3 * f4;
        float f7 = f2 * f4;
        Vec3 vec31 = vec3.add((double)f6 * range, (double)f5 * range, (double)f7 * range);
        return pLevel.clip(new ClipContext(vec3, vec31, pBlockMode, pFluidMode, pPlayer));
    }

    public static void drawParticleLine(Level level, ParticleOptions opt, double x1, double y1, double z1, double x2, double y2, double z2, int frequency){
        for(int i = 0; i < frequency; i++){
            double u = level.random.nextDouble();
            double x = (1-u) * x1 + u * x2;
            double y = (1-u) * y1 + u * y2;
            double z = (1-u) * z1 + u * z2;

            if(level.isClientSide()) {
                level.addParticle(opt, x, y, z, 0, 0, 0);
            }else {
                ((ServerLevel)level).sendParticles(opt, x, y, z, 1, 0, 0, 0, 0.0);
            }
        }
    }

    public static void drawParticleRing(Level level, ParticleOptions opt, BlockPos pos, double height, double radius, int frequency){
        double center_x = pos.getX() + 0.5;
        double center_z = pos.getZ() + 0.5;

        if(level.isClientSide()){
            for(int i = 0; i < frequency; i++){
                int deflection_angle = level.random.nextInt(1, 360);
                double x = Math.cos(Math.toRadians(deflection_angle)) * radius + center_x;
                double z = Math.sin(Math.toRadians(deflection_angle)) * radius + center_z;
                level.addParticle(opt, x, pos.getY() + height, z, 0, 0, 0);
            }
        }
    }

    public static void drawParticleCrucibleTop(Level level, ParticleOptions opt, BlockPos pos){
        drawParticleCrucibleTop(level, opt, pos, 1, 0, 0, 0);
    }

    public static void drawParticleCrucibleTop(Level level, ParticleOptions opt, BlockPos pos, float odds){
        drawParticleCrucibleTop(level, opt, pos, odds, 0, 0, 0);
    }

    public static void drawParticleCrucibleTop(Level level, ParticleOptions opt, BlockPos pos, float odds, double xspeed, double yspeed, double zspeed){
        if(level.isClientSide()){
            if(level.random.nextFloat() < odds){
                double x = pos.getX() + level.getRandom().nextFloat() * (10.0/16) + 3.0/16;
                double z = pos.getZ() + level.getRandom().nextFloat() * (10.0/16) + 3.0/16;
                level.addParticle(opt, x, pos.getY() + 0.6, z, xspeed, yspeed, zspeed);
            }
        }else{
            if(level.random.nextFloat() < odds){
                double x = pos.getX() + level.getRandom().nextFloat() * (10.0/16) + 3.0/16;
                double z = pos.getZ() + level.getRandom().nextFloat() * (10.0/16) + 3.0/16;
                ((ServerLevel) level).sendParticles(opt, x, pos.getY() + 0.6, z, 1, xspeed, yspeed, zspeed, 0.0);
            }
        }
    }
}
