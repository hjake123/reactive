package com.hyperlynx.reactive.fx.particles;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;

import java.util.concurrent.ThreadLocalRandom;

public class ParticleScribe {
    public static void drawParticle(Level level, ParticleOptions opt, double x, double y, double z) {
        if (level.isClientSide()) {
            level.addParticle(opt, x, y, z, 0, 0, 0);
        } else {
            ((ServerLevel) level).sendParticles(opt, x, y, z, 1, 0, 0, 0, 0.0);
        }
    }

    public static void drawParticleBox(Level level, ParticleOptions opt, AABB aabb, int frequency) {
        double x, y, z;
        for(int i = 0; i < frequency; i++){
            x = level.random.nextDouble() * (aabb.maxX - aabb.minX) + aabb.minX;
            y = level.random.nextDouble() * (aabb.maxY - aabb.minY) + aabb.minY;
            z = level.random.nextDouble() * (aabb.maxZ - aabb.minZ) + aabb.minZ;
            drawParticle(level, opt, x, y, z);
        }
    }

    public static void drawParticleLine(Level level, ParticleOptions opt, BlockPos a, BlockPos b, int frequency, double noise){
        drawParticleLine(level, opt, a.getX()+0.5, a.getY()+0.5,a.getZ()+0.5,
                b.getX()+0.5, b.getY()+0.5, b.getZ()+0.5, frequency, noise);
    }

    public static void drawParticleLine(Level level, ParticleOptions opt, double x1, double y1, double z1, double x2, double y2, double z2, int frequency, double noise) {
        for (int i = 0; i < frequency; i++) {
            double u = level.random.nextDouble();
            double x = (1 - u) * x1 + u * x2;
            double y = (1 - u) * y1 + u * y2;
            double z = (1 - u) * z1 + u * z2;

            x += (level.random.nextFloat() - 0.5) * noise;
            y += (level.random.nextFloat() - 0.5) * noise;
            z += (level.random.nextFloat() - 0.5) * noise;

            drawParticle(level, opt, x, y, z);
        }
    }

    public static void drawParticleZigZag(Level level, ParticleOptions opt, BlockPos a, BlockPos b, int frequency, int segments, double noise){
        drawParticleZigZag(level, opt, a.getX()+0.5, a.getY()+0.5, a.getZ()+0.5, b.getX()+0.5, b.getY()+0.5, b.getZ()+0.5, frequency, segments, noise);
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
                double max_progress = segments==3 ? 1.0 : 1.0/(segments-3);
                double actual_progress = ThreadLocalRandom.current().nextDouble(min_progress, max_progress);

                double x_dist = Math.abs(x2 - prev_x) * actual_progress;
                double y_dist = Math.abs(y2 - prev_y) * actual_progress;
                double z_dist = Math.abs(z2 - prev_z) * actual_progress;

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
        drawParticleRing(level, opt, Vec3.atBottomCenterOf(pos), height, radius, frequency);
    }

    public static void drawParticleRing(Level level, ParticleOptions opt, Vec3 pos, double height, double radius, int frequency){
        double center_x = pos.x;
        double center_z = pos.z;

        for(int i = 0; i < frequency; i++){
            int deflection_angle = level.random.nextInt(1, 360);
            double x = Math.cos(Math.toRadians(deflection_angle)) * radius + center_x;
            double z = Math.sin(Math.toRadians(deflection_angle)) * radius + center_z;
            drawParticle(level, opt, x, pos.y + height, z);
        }
    }

    public static void drawParticleSphere(Level level, ParticleOptions opt, BlockPos pos, double height, double radius, int frequency){
        double center_x = pos.getX() + 0.5;
        double center_z = pos.getZ() + 0.5;

        if(level.isClientSide()){
            for(int i = 0; i < frequency; i++){
                double x = level.random.nextGaussian();
                double y = level.random.nextGaussian();
                double z = level.random.nextGaussian();
                double normalizer = 1 / Math.sqrt(x * x + y * y + z * z);

                x = x * normalizer * radius;
                y = y * normalizer * radius;
                z = z * normalizer * radius;

                level.addParticle(opt, center_x + x, pos.getY() + height + y, center_z + z, 0, 0, 0);
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

    public static void drawParticleStream(Level level, ParticleOptions opt, Vec3 start, Vec3 angle, int frequency){
        angle.normalize();
        angle.multiply(0.0003, 0.0003, 0.0003);
        for(int i = 0; i < frequency; i++){
            level.addParticle(opt, start.x, start.y, start.z, angle.x, angle.y, angle.z);
        }
    }
}
