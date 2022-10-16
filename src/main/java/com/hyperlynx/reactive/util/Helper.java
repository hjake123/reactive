package com.hyperlynx.reactive.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Helper {
    // Taken from getPOVPlayerLook()
//    public static BlockHitResult playerRayTrace(Level pLevel, Player pPlayer, ClipContext.Fluid pFluidMode, ClipContext.Block pBlockMode, double range) {
//        float f = pPlayer.getXRot();
//        float f1 = pPlayer.getYRot();
//        Vec3 vec3 = pPlayer.getEyePosition();
//        float f2 = Mth.cos(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
//        float f3 = Mth.sin(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
//        float f4 = -Mth.cos(-f * ((float)Math.PI / 180F));
//        float f5 = Mth.sin(-f * ((float)Math.PI / 180F));
//        float f6 = f3 * f4;
//        float f7 = f2 * f4;
//        Vec3 vec31 = vec3.add((double)f6 * range, (double)f5 * range, (double)f7 * range);
//        return pLevel.clip(new ClipContext(vec3, vec31, pBlockMode, pFluidMode, pPlayer));
//    }

    public static double distance(double x1, double y1, double z1, double x2, double y2, double z2){
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2) + Math.pow(z2 - z1, 2));
    }

    public static void drawParticleLine(Level level, ParticleOptions opt, double x1, double y1, double z1, double x2, double y2, double z2, int frequency, double noise){
        for(int i = 0; i < frequency; i++){
            double u = level.random.nextDouble();
            double x = (1-u) * x1 + u * x2;
            double y = (1-u) * y1 + u * y2;
            double z = (1-u) * z1 + u * z2;

            x += (level.random.nextFloat()-0.5) * noise;
            y += (level.random.nextFloat()-0.5) * noise;
            z += (level.random.nextFloat()-0.5) * noise;

            if(level.isClientSide()) {
                level.addParticle(opt, x, y, z, 0, 0, 0);
            }else {
                ((ServerLevel)level).sendParticles(opt, x, y, z, 1, 0, 0, 0, 0.0);
            }
        }
    }

    public static void drawParticleZigZag(Level level, ParticleOptions opt, double x1, double y1, double z1, double x2, double y2, double z2, int frequency, int segments, double noise){
        double prev_x = x1;
        double prev_y = y1;
        double prev_z = z1;

        // For each line segment:
        // - Find a minimum and maximum length ('progress')
        // - Choose an actual length with these as bounds
        // - Adjust x y and z by the chosen progress
        // - Deflect x y and z by a random amount
        for(int i = 0; i < segments; i++){
            double next_x;
            double next_y;
            double next_z;

            if(i == segments-1){
                next_x = x2;
                next_y = y2;
                next_z = z2;
            }else{
                double min_progress = 1.0/(segments+3);
                double max_progress = 1.0/(segments-3);
                double actual_progress = ThreadLocalRandom.current().nextDouble(min_progress, max_progress);

                double x_dist = Math.abs(x2 - prev_x) * actual_progress;
                double y_dist = Math.abs(y2 - prev_y) * actual_progress;
                double z_dist = Math.abs(z2 - prev_z) * actual_progress;

                // These need to be EXACTLY LIKE THIS for the zigzag to not move away from the target. Don't know why.
                if (x2 > 0)
                    next_x = x2 > prev_x ? prev_x + x_dist : prev_x - x_dist;
                else
                    next_x = x2 < prev_x ? prev_x - x_dist : prev_x + x_dist;

                if (y2 > 0)
                    next_y = y2 > prev_y ? prev_y + y_dist : prev_y - y_dist;
                else
                    next_y = y2 < prev_y ? prev_y - y_dist : prev_y + y_dist;

                if (z2 > 0)
                    next_z = z2 > prev_z ? prev_z + z_dist : prev_z - z_dist;
                else
                    next_z = z2 < prev_z ? prev_z - z_dist : prev_z + z_dist;

                next_x += (level.random.nextFloat()-0.5) * noise;
                next_y += (level.random.nextFloat()-0.5) * noise;
                next_z += (level.random.nextFloat()-0.5) * noise;
            }
            drawParticleLine(level, opt, prev_x, prev_y, prev_z, next_x, next_y, next_z, frequency, 0);

            prev_x = next_x;
            prev_y = next_y;
            prev_z = next_z;
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

    public static void triggerForNearbyPlayers(ServerLevel l, FlagCriterion crit, BlockPos center, int range){
        List<Player> nearby_players = l.getEntitiesOfClass(Player.class, AABB.ofSize(Vec3.atCenterOf(center), range, range, range));
        for(Player p : nearby_players) {
            crit.trigger((ServerPlayer) p);
        }
    }

}
