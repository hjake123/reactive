package dev.hyperlynx.reactive.alchemy.rxn;

import dev.hyperlynx.reactive.Registration;
import dev.hyperlynx.reactive.advancements.CriteriaTriggers;
import dev.hyperlynx.reactive.alchemy.Powers;
import dev.hyperlynx.reactive.alchemy.special.SpecialCaseMan;
import dev.hyperlynx.reactive.be.CrucibleBlockEntity;
import dev.hyperlynx.reactive.blocks.CrucibleBlock;
import dev.hyperlynx.reactive.blocks.NoduleBlock;
import dev.hyperlynx.reactive.blocks.ShulkerCrucibleBlock;
import dev.hyperlynx.reactive.client.particles.EnergyParticle;
import dev.hyperlynx.reactive.client.particles.ParticleScribe;
import dev.hyperlynx.reactive.entities.ReactorEntity;
import dev.hyperlynx.reactive.items.CrystalIronItem;
import dev.hyperlynx.reactive.ConfigMan;
import dev.hyperlynx.reactive.util.BeamHelper;
import dev.hyperlynx.reactive.util.BlockMoveChecker;
import dev.hyperlynx.reactive.util.WorldSpecificValue;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.LightningRodBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.*;

// Just a holder class for the various reaction effect methods. Only for use on the server side.
public class ReactionEffects {
    // Destroys the contents of the Crucible and some connected Symbols unless there is an Iron Symbol.
    public static void explosion(Reactor reactor) {
        BlockPos pos = reactor.blockPos();

        ParticleScribe.drawParticleZigZag(reactor.obtainLevel(), ParticleTypes.SMOKE, pos, reactor.getAreaMemory().fetch(reactor.obtainLevel(),
                Registration.GOLD_SYMBOL.get()), 20, 7, 0.8F);

        if(reactor.getAreaMemory().exists(reactor.obtainLevel(), Registration.IRON_SYMBOL.get())){
            ParticleScribe.drawParticleZigZag(reactor.obtainLevel(), ParticleTypes.SMOKE, reactor.getAreaMemory().fetch(reactor.obtainLevel(),
                    Registration.GOLD_SYMBOL.get()), reactor.getAreaMemory().fetch(reactor.obtainLevel(),
                    Registration.IRON_SYMBOL.get()), 20, 7, 0.8F);
        }else{
            reactor.expendPower();
            if(reactor instanceof CrucibleBlockEntity crucible){
                SpecialCaseMan.checkEmptySpecialCases(crucible);
                reactor.obtainLevel().setBlock(pos, reactor.obtainLevel().getBlockState(pos).setValue(CrucibleBlock.FULL, false), Block.UPDATE_CLIENTS);
            }
            reactor.obtainLevel().explode(null, pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, 1.0F, Level.ExplosionInteraction.NONE);

            if(reactor.getAreaMemory().exists(reactor.obtainLevel(), Registration.GOLD_SYMBOL.get()))
                reactor.obtainLevel().removeBlock(reactor.getAreaMemory().fetch(reactor.obtainLevel(), Registration.GOLD_SYMBOL.get()), true);
        }
    }

    // Changes the Gold Symbol into Active Gold Foam, which spreads outwards for a limited distance and leaves Gold Foam behind.
    public static void foaming(Reactor reactor) {
        BlockPos symbol_position = reactor.getAreaMemory().fetch(reactor.obtainLevel(), Registration.GOLD_SYMBOL.get());
        if(symbol_position == null)
            return;

        reactor.obtainLevel().setBlock(symbol_position, Registration.ACTIVE_GOLD_FOAM.get().defaultBlockState(), Block.UPDATE_CLIENTS);
        ParticleScribe.drawParticleZigZag(reactor.obtainLevel(), ParticleTypes.EFFECT,
                reactor.blockPos().getX() + 0.5F, reactor.blockPos().getY() + 0.5625F, reactor.blockPos().getZ() + 0.5F,
                symbol_position.getX()+0.5, symbol_position.getY()+0.5, symbol_position.getZ()+0.5, 12, 7,0.4);
    }

    public static void smoke(Reactor reactor) {
        if (reactor.obtainLevel().random.nextFloat() < 0.4) {
            AABB aoe = new AABB(reactor.blockPos());
            aoe = aoe.inflate(3); // Inflate the AOE to be 3x the size of the crucible.
            List<LivingEntity> nearby_ents = reactor.obtainLevel().getEntitiesOfClass(LivingEntity.class, aoe);
            for (LivingEntity e : nearby_ents) {
                if (CrystalIronItem.effectNotBlocked(e, 1)) {
                    e.addEffect(new MobEffectInstance(MobEffects.POISON, 200, 1));
                    e.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 200, 1));
                    e.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 1));
                }
            }
        }
    }

    public static void salt(Reactor reactor) {
        if(reactor.getTotalPowerLevel() < WorldSpecificValue.get("salt_overflow_threshold", 1000, 1300)){
            ItemEntity salt_drop = new ItemEntity(reactor.obtainLevel(), reactor.blockPos().getX() + 0.5,
                    reactor.blockPos().getY() + 0.5,
                    reactor.blockPos().getZ() + 0.6, Registration.SALT.get().getDefaultInstance());
            reactor.obtainLevel().addFreshEntity(salt_drop);
        }else{
            if(reactor instanceof CrucibleBlockEntity crucible){
                CrucibleBlockEntity.empty(reactor.obtainLevel(), reactor.blockPos(), reactor.blockState(), crucible);
                if(reactor.blockState().getBlock() instanceof ShulkerCrucibleBlock) {
                    ItemEntity shell_drop = new ItemEntity(reactor.obtainLevel(), reactor.blockPos().getX() + 0.5,
                            reactor.blockPos().getY() + 0.5,
                            reactor.blockPos().getZ() + 0.6, Items.SHULKER_SHELL.getDefaultInstance());
                    reactor.obtainLevel().addFreshEntity(shell_drop);

                }
                reactor.obtainLevel().setBlock(reactor.blockPos(), Registration.SALTY_CRUCIBLE.get().defaultBlockState(), Block.UPDATE_CLIENTS);
            } else if(reactor instanceof ReactorEntity entity){
                entity.obtainLevel().setBlock(entity.blockPos(), Registration.SALT_BLOCK.get().defaultBlockState(), Block.UPDATE_CLIENTS);
                entity.kill();
            }
            reactor.obtainLevel().playSound(null, reactor.blockPos(), SoundEvents.GLASS_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
    }

    public static void discharge(Reactor reactor) {
        Level level = reactor.obtainLevel();
        reactor.addElectricCharge(5);
        if (reactor.getElectricCharge() > 21) {
            BlockPos potential_rod = reactor.getAreaMemory().fetch(level, Blocks.LIGHTNING_ROD);
            if (potential_rod != null) {
                if (!reactor.obtainLevel().isClientSide) {
                    ((LightningRodBlock) Blocks.LIGHTNING_ROD).onLightningStrike(reactor.obtainLevel().getBlockState(potential_rod), level, potential_rod);
                    ParticleScribe.drawParticleZigZag(level, ParticleTypes.ELECTRIC_SPARK,
                            reactor.getPos().x, reactor.getPos().y, reactor.getPos().z,
                            potential_rod.getX()+0.5, potential_rod.getY()+0.5, potential_rod.getZ()+0.5, 8, 10,0.6);
                    reactor.obtainLevel().playSound(null, potential_rod, Registration.ZAP_SOUND.get(), SoundSource.BLOCKS, 0.5F, 1F);
                }
            } else {
                AABB aoe = new AABB(reactor.blockPos());
                aoe = aoe.inflate(ConfigMan.COMMON.crucibleRange.get()); // Inflate the AOE to be 5x the size of the crucible?
                List<LivingEntity> nearby_ents = reactor.obtainLevel().getEntitiesOfClass(LivingEntity.class, aoe);

                LivingEntity victim = null;
                for(LivingEntity e : nearby_ents){
                    if((victim == null || e.distanceToSqr(Vec3.atCenterOf(reactor.blockPos())) < victim.distanceToSqr(Vec3.atCenterOf(reactor.blockPos())))
                            && BeamHelper.hasLineOfSight(level, Vec3.atCenterOf(reactor.blockPos()), e.getEyePosition(0), ClipContext.Fluid.NONE, ClipContext.Block.COLLIDER, reactor.blockState().getBlock())){
                        victim = e;
                    }
                }

                if(victim == null){
                    return;
                }

                if (!level.isClientSide) {
                    if(CrystalIronItem.effectNotBlocked(victim, 2))
                        victim.hurt(reactor.obtainLevel().damageSources().magic(), 5);
                    ParticleScribe.drawParticleZigZag(reactor.obtainLevel(), ParticleTypes.ELECTRIC_SPARK,
                            reactor.getPos().x, reactor.getPos().y, reactor.getPos().z,
                            victim.getX(), victim.getEyeHeight() / 2 + victim.getY(), victim.getZ(), 8, 10, 0.3);
                    reactor.obtainLevel().playSound(null, victim.getX(), victim.getY(), victim.getZ(), Registration.ZAP_SOUND.get(), SoundSource.BLOCKS, 0.5F, 0.98F + reactor.obtainLevel().random.nextFloat()*0.05F);
                }
            }
            reactor.setElectricCharge(0);
        }
        reactor.setDirty();
    }

    // Apply levitation to nearby entities.
    public static void levitation(Reactor reactor) {
        AABB aoe = new AABB(reactor.blockPos());
        aoe = aoe.inflate(12); // Inflate the AOE to be 6x the size of the crucible.
        List<LivingEntity> nearby_ents = reactor.obtainLevel().getEntitiesOfClass(LivingEntity.class, aoe);

        for(LivingEntity victim : nearby_ents){
            if(CrystalIronItem.effectNotBlocked(victim, 1)) {
                victim.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 200, 1));
                if(victim instanceof ServerPlayer player){
                    CriteriaTriggers.BE_LEVITATED_TRIGGER.trigger(player);
                }
            }
            ParticleScribe.drawParticleZigZag(reactor.obtainLevel(), ParticleTypes.END_ROD,
                    reactor.getPos().x, reactor.getPos().y, reactor.getPos().z,
                    victim.getX(),victim.getEyeY()-0.2, victim.getZ(), 8, 7, 0.74);
            float pitch = 0.80F + reactor.obtainLevel().random.nextFloat()*0.1F;
            reactor.obtainLevel().playSound(null, victim.getX(), victim.getY(), victim.getZ(), SoundEvents.NOTE_BLOCK_CHIME.value(), SoundSource.BLOCKS, 0.3F, pitch);
            reactor.obtainLevel().playSound(null, victim.getX(), victim.getY(), victim.getZ(), SoundEvents.NOTE_BLOCK_CHIME.value(), SoundSource.BLOCKS, 0.3F, pitch/2);
        }
    }

    // Apply slow fall to nearby entities and, if possible, create Secret Scales.
    public static void slowfall(Reactor reactor) {
        AABB aoe = new AABB(reactor.blockPos());
        aoe = aoe.inflate(12); // Inflate the AOE to be 6x the size of the crucible.
        List<LivingEntity> nearby_ents = reactor.obtainLevel().getEntitiesOfClass(LivingEntity.class, aoe);

        for(LivingEntity victim : nearby_ents){
            if(CrystalIronItem.effectNotBlocked(victim, 1)) {
                victim.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 200, 1));
                if(victim instanceof ServerPlayer player){
                    CriteriaTriggers.BE_SLOWFALLED_TRIGGER.trigger(player);
                }
            }
            ParticleScribe.drawExactParticleRing(reactor.obtainLevel(), ParticleTypes.END_ROD, reactor.getPos(), 0, 0.6, 1);
            reactor.obtainLevel().playSound(null, victim.getX(), victim.getY(), victim.getZ(), SoundEvents.CONDUIT_AMBIENT_SHORT, SoundSource.BLOCKS, 0.1F, 1.2F);

        }

        // Craft the scales if levitation is also happening, and then empty the Crucible.
        if(reactor.getLinkedCrystal() != null && reactor.getPowerLevel(Powers.LIGHT_POWER.get()) > WorldSpecificValue.get("levitationcost", 10, 30)){
            craftSecretScale(reactor);
        }
    }

    private static void craftSecretScale(Reactor reactor) {
        for(Entity entity : CrucibleBlock.getEntitesInside(reactor.blockPos(), reactor.obtainLevel())){
            if(entity instanceof ItemEntity item_entity && item_entity.getItem().is(Registration.PHANTOM_RESIDUE.get())) {
                ParticleScribe.drawParticleZigZag(reactor.obtainLevel(), ParticleTypes.END_ROD,
                        reactor.blockPos().getX(), reactor.blockPos().getY(), reactor.blockPos().getZ(),
                        entity.getX(), entity.getY(), entity.getZ(), 25, 10, 0.9);
                reactor.obtainLevel().playSound(null, reactor.blockPos(), SoundEvents.RESPAWN_ANCHOR_CHARGE, SoundSource.BLOCKS,
                        0.8F, 0.8F);
                int count = item_entity.getItem().getCount();
                item_entity.kill();
                ItemStack drop_stack = Registration.SECRET_SCALE.get().getDefaultInstance();
                drop_stack.setCount(count);
                ItemEntity secret_scale = new ItemEntity(reactor.obtainLevel(), reactor.blockPos().getX() + 0.5, reactor.blockPos().getY()+0.6, reactor.blockPos().getZ() + 0.5, drop_stack);
                secret_scale.setPickUpDelay(20);
                reactor.obtainLevel().addFreshEntity(secret_scale);
                reactor.obtainLevel().setBlock(reactor.blockPos(), reactor.blockState().setValue(CrucibleBlock.FULL, false), Block.UPDATE_CLIENTS);
            }
        }
    }

    // Causes nearby bonemeal-ables to be fertilized occasionally.
    public static void growth(Reactor reactor) {
        Random random = new Random();
        BlockPos target = reactor.blockPos().offset(random.nextInt(-32, 32), random.nextInt(-1, 0), random.nextInt(-32, 32));
        if (Objects.requireNonNull(reactor.obtainLevel()).getBlockState(target).getBlock() instanceof BonemealableBlock) {
            ((BonemealableBlock) reactor.obtainLevel().getBlockState(target).getBlock()).performBonemeal((ServerLevel) reactor.obtainLevel(), reactor.obtainLevel().random, target, reactor.obtainLevel().getBlockState(target));
        }
    }

    // Shoot flames from the crucible!
    public static void flamethrower(Reactor reactor) {
        if(reactor.obtainLevel() == null) return;

        AABB blast_zone = new AABB(reactor.blockPos());
        blast_zone = blast_zone.inflate(2, 5, 2);

        List<LivingEntity> nearby_ents = reactor.obtainLevel().getEntitiesOfClass(LivingEntity.class, blast_zone);
        for(LivingEntity e : nearby_ents){
            if(!BeamHelper.hasLineOfSight(reactor.obtainLevel(), reactor.blockPos().getCenter(), e.getEyePosition(0), ClipContext.Fluid.NONE, ClipContext.Block.COLLIDER, reactor.blockState().getBlock())) {
                continue;
            }
            e.setRemainingFireTicks(140);
        }
    }

    // Causes nearby undead to catch fire.
    public static void sunlight(Reactor reactor) {
        int range = 12;
        AABB aoe = new AABB(reactor.blockPos());
        aoe = aoe.inflate(range);
        List<Monster> nearby_monsters = reactor.obtainLevel().getEntitiesOfClass(Monster.class, aoe);

        for(Monster m : nearby_monsters){
            if(m.isInvertedHealAndHarm() && m.getPosition(0).distanceTo(reactor.blockPos().getCenter()) < range){
                m.hurt(reactor.obtainLevel().damageSources().inFire(), 3);
                m.setRemainingFireTicks(100);
            }
        }

        ParticleScribe.drawExactParticleRing(reactor.obtainLevel(), ParticleTypes.END_ROD, reactor.getPos().add(0, 0.1, 0), 0,12F, 20);
    }

    // Cause blocks to fall down near the Symbol.
    public static void blockfall(Reactor reactor) {
        Level level = reactor.obtainLevel();
        RandomSource random = level.random;
        BlockPos symbol_pos = reactor.getAreaMemory().fetch(level, Registration.GOLD_SYMBOL.get());
        if(symbol_pos == null)
            return;

        for(int i = 0; i < 10; i++) {
            BlockPos target = symbol_pos.offset(random.nextInt(-4, 4), random.nextInt(0, 4), random.nextInt(-4, 4));
            if (target == reactor.blockPos() || target == symbol_pos) continue;
            BlockState target_state = level.getBlockState(target);
            if (!target_state.isAir() && BlockMoveChecker.canMakeBlockFall(reactor.obtainLevel(), target, target_state)) {
                FallingBlockEntity.fall(level, target, target_state);
                ParticleScribe.drawParticleZigZag(level, ParticleTypes.END_ROD, reactor.blockPos(), target, 8, 32, 0.7F);
                ItemEntity drop = new ItemEntity(level, reactor.blockPos().getX()+0.5, reactor.blockPos().getY()+0.6, reactor.blockPos().getZ()+0.5,
                        Registration.MOTION_SALT.get().getDefaultInstance());
                level.addFreshEntity(drop);
            }
        }
    }

    public static void immobilize(Reactor reactor) {
        Level level = reactor.obtainLevel();
        if(level == null)
            return;

        AABB aoe = new AABB(reactor.blockPos());
        aoe = aoe.inflate(2);
        List<LivingEntity> nearby = level.getEntitiesOfClass(LivingEntity.class, aoe);

        for(LivingEntity living : nearby){
            ParticleScribe.drawParticleReactionSurface(level, ParticleTypes.REVERSE_PORTAL, reactor);
            if(CrystalIronItem.effectNotBlocked(living, 1)) {
                if(living instanceof Player player && player.isShiftKeyDown()){
                    MobEffectInstance stop = new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 50);
                    player.addEffect(stop);
                }else {
                    MobEffectInstance stop = new MobEffectInstance(Registration.IMMOBILE.get(), 50, 0, true, false, true);
                    living.addEffect(stop);
                }
            }
        }
    }

    public static void creation(Reactor reactor){
        Level level = Objects.requireNonNull(reactor.obtainLevel());
        if(level.random.nextFloat() < 0.35){
            for(BlockPos creation_point : getCreationPoints(reactor.blockPos())){
                if(level.getBlockState(creation_point).isAir() && level.isLoaded(creation_point)){
                    if(level.random.nextFloat() < 0.34) {
                        level.setBlock(creation_point, Registration.CREATION_SALT_BLOCK.get().defaultBlockState(), Block.UPDATE_CLIENTS);
                        level.updateNeighborsAt(creation_point, Registration.CREATION_SALT_BLOCK.get());
                    } else {
                        level.setBlock(creation_point, Registration.UNFORMED_MATTER.get().defaultBlockState(), Block.UPDATE_CLIENTS);
                        level.updateNeighborsAt(creation_point, Registration.UNFORMED_MATTER.get());
                    }
                    ParticleScribe.drawParticleZigZag(level, Registration.STARDUST_PARTICLE, reactor.blockPos(), creation_point, 10, 5, 0.5F);
                    break;
                }
            }
        }
    }

    public static Set<BlockPos> getCreationPoints(BlockPos origin){
        Set<BlockPos> points = new HashSet<>();
        Random wsv_source = WorldSpecificValue.getSource("creation_points");
        while(points.size() < 3){
            points.add(origin.offset(
                    wsv_source.nextInt(0, 3) * 2 - 3,
                    wsv_source.nextInt(0, 2) * 2 + 2,
                    wsv_source.nextInt(0, 3) * 2 - 3));
        }
        return points;
    }

    public static void cryo(Reactor reactor) {
        Level level = reactor.obtainLevel();
        if(level == null)
            return;

        AABB aoe = new AABB(reactor.blockPos());
        aoe = aoe.inflate(5);
        List<LivingEntity> nearby = level.getEntitiesOfClass(LivingEntity.class, aoe);

        for(LivingEntity living : nearby) {
            if(CrystalIronItem.effectNotBlocked(living, 1)) {
                living.setTicksFrozen(200);
            }
        }

        BlockPos.betweenClosedStream(aoe).forEach((pos) -> {
            if(level.getBlockState(pos).is(Blocks.FROSTED_ICE)) {
                // Refresh the frosted ice
                level.setBlock(pos, Blocks.FROSTED_ICE.defaultBlockState(), Block.UPDATE_CLIENTS);
            }
            if(level.getBlockState(pos).is(Blocks.WATER) && level.getBlockState(pos.above()).isAir()) {
                level.setBlock(pos, Blocks.FROSTED_ICE.defaultBlockState(), Block.UPDATE_CLIENTS);
                reactor.addPower(Powers.BLAZE_POWER.get(), 3);
            }
        });
    }

    public static void noduleGrowth(Reactor reactor) {
        Level level = reactor.obtainLevel();
        if(level == null)
            return;

        int range = 4;
        BlockPos pos = reactor.blockPos().offset(level.random.nextInt(-range, range + 1), level.random.nextInt(-1, range), level.random.nextInt(-range, range + 1));
        if(!level.getBlockState(pos).isAir()) {
            if(level.getBlockState(pos).is(Registration.UNGROWN_NODULE.get())) {
                BlockState old_state = level.getBlockState(pos);
                Direction direction = old_state.getValue(NoduleBlock.FACING);
                level.setBlock(pos, Registration.NODULE.get().defaultBlockState().setValue(NoduleBlock.FACING, direction), Block.UPDATE_CLIENTS);
                runNodulePlaceEffects(pos, direction, level, reactor);
                level.playSound(null, pos, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.BLOCKS,0.2F, 0.7F + level.random.nextFloat() * 0.1F);
            }
            return;
        }
        for(Direction direction : Direction.allShuffled(level.random)) {
            BlockPos side_pos = pos.offset(new Vec3i(direction.getStepX(), direction.getStepY(), direction.getStepZ()));
            if(level.getBlockState(side_pos).isCollisionShapeFullBlock(level, side_pos)) {
                level.setBlock(pos, Registration.UNGROWN_NODULE.get().defaultBlockState().setValue(NoduleBlock.FACING, direction), Block.UPDATE_CLIENTS);
                runNodulePlaceEffects(pos, direction, level, reactor);
                return;
            }
        }
    }

    private static void runNodulePlaceEffects(BlockPos pos, Direction direction, Level level, Reactor reactor) {
        reactor.expendPower(Powers.WARP_POWER.get(), 4);
        level.playSound(null, pos, SoundEvents.TUFF_PLACE, SoundSource.BLOCKS,1.0F, 1.2F);
        Vec3 beam_target = pos.getCenter().add(new Vec3(direction.step()).scale(0.3));
        ParticleScribe.drawParticleLine(level, new EnergyParticle.Options(0.2F, Powers.Z_POWER.get().getColor(), beam_target, false), reactor.blockPos().getCenter(), beam_target, 50, 0.05F);
    }

    public static void omenSettling(Reactor reactor){
        if(reactor.obtainLevel().random.nextFloat() < 0.1F){
            reactor.addPower(Powers.CURSE_POWER.get(), 1);
        }
        reactor.addPower(Powers.SOUL_POWER.get(), 2);
    }

}
