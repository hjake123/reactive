package com.hyperlynx.reactive.be;

import com.hyperlynx.reactive.Registration;
import com.hyperlynx.reactive.blocks.GravityBeamBlock;
import com.hyperlynx.reactive.fx.particles.ParticleScribe;
import com.hyperlynx.reactive.util.BeamHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class GravityBeamBlockEntity extends BlockEntity {
    public GravityBeamBlockEntity(BlockPos pos, BlockState state) {
        super(Registration.GRAVITY_BEAM_BE_TYPE.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state) {
        // Fires a beam that inflicts null gravity if the block is enabled.
        if(!state.getValue(GravityBeamBlock.ENABLED)){
            return;
        }
        BlockHitResult hit = BeamHelper.rayTrace(level, start_pos(pos, state.getValue(GravityBeamBlock.FACING)),
                xrot(state), yrot(state), ClipContext.Fluid.NONE, ClipContext.Block.VISUAL, 32, null);
        Vec3 particle_start = Vec3.atCenterOf(pos);
        particle_start.add(0, 0.1, 0);
        ParticleScribe.drawParticleLine(level, ParticleTypes.END_ROD, particle_start, Vec3.atCenterOf(hit.getBlockPos()), 1, 0.05);

        AABB effect_region = new AABB(Vec3.atCenterOf(pos).add(-0.1, -0.1, -0.1),
                Vec3.atCenterOf(hit.getBlockPos()).add(0.1, 0.1, 0.1));
        for(Entity target : level.getEntitiesOfClass(Entity.class, effect_region)){
            if(target instanceof LivingEntity victim){
                victim.addEffect(new MobEffectInstance(Registration.NULL_GRAVITY.get(), 5));
                victim.resetFallDistance();
            }else if(target instanceof ItemEntity item) {
                item.setNoGravity(true);
            }
        }
    }
    private static Vec3 start_pos(BlockPos center, Direction facing){
        return Vec3.atCenterOf(center).relative(facing, 1.01);
    }

    private static float xrot(BlockState state){
        Direction facing = state.getValue(DirectionalBlock.FACING);
        if(facing.equals(Direction.UP)){
            return 270;
        }
        if(facing.equals(Direction.DOWN)){
            return 90;
        }
        return 0;
    }

    private static float yrot(BlockState state){
        Direction facing = state.getValue(DirectionalBlock.FACING);
        return facing.toYRot();
    }
}
