package com.hyperlynx.reactive.be;

import com.hyperlynx.reactive.Registration;
import com.hyperlynx.reactive.blocks.GravityChandelierBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GravityChandelierBlockEntity extends BlockEntity {
    public static final double RANGE = 10.0;
    public GravityChandelierBlockEntity(BlockPos pos, BlockState state) {
        super(Registration.GRAVITY_CHANDELIER_BE_TYPE.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos) {
        if(!level.getBlockState(pos).is(Registration.GRAVITY_CHANDELIER.get())){
            return;
        }
        BlockPos effect_center;
        if(level.getBlockState(pos).getValue(GravityChandelierBlock.OFFSET)){
            effect_center = pos.above((int) RANGE);
        }else{
            effect_center = pos;
        }
        List<Entity> targets = level.getEntitiesOfClass(Entity.class, AABB.ofSize(Vec3.atCenterOf(effect_center),
                        RANGE*2, RANGE*2, RANGE*2),
                (Entity ent) -> ent.getPosition(0).distanceToSqr(Vec3.atCenterOf(effect_center)) < RANGE*RANGE);

        for(Entity target : targets){
            if(target instanceof ItemEntity){
                target.setNoGravity(true);
            }else if(target instanceof LivingEntity victim){
                victim.addEffect(new MobEffectInstance(Registration.NULL_GRAVITY.get(), 10));
                victim.resetFallDistance();
            }
        }
    }
}
