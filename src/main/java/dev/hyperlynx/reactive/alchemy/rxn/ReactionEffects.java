package dev.hyperlynx.reactive.alchemy.rxn;

import dev.hyperlynx.reactive.ConfigMan;
import dev.hyperlynx.reactive.Registration;
import dev.hyperlynx.reactive.alchemy.Powers;
import dev.hyperlynx.reactive.alchemy.special.SpecialCaseMan;
import dev.hyperlynx.reactive.be.CrucibleBlockEntity;
import dev.hyperlynx.reactive.blocks.CrucibleBlock;
import dev.hyperlynx.reactive.blocks.ShulkerCrucibleBlock;
import dev.hyperlynx.reactive.client.particles.ParticleScribe;
import dev.hyperlynx.reactive.entites.ReactorEntity;
import dev.hyperlynx.reactive.items.CrystalIronItem;
import dev.hyperlynx.reactive.util.BeamHelper;
import dev.hyperlynx.reactive.util.BlockMoveChecker;
import dev.hyperlynx.reactive.util.WorldSpecificValue;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.EvokerFangs;
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
        BlockPos pos = reactor.getBlockPos();

        ParticleScribe.drawParticleZigZag(reactor.getLevel(), ParticleTypes.SMOKE, pos, reactor.getAreaMemory().fetch(reactor.getLevel(),
                Registration.GOLD_SYMBOL.get()), 20, 7, 0.8F);

        if(reactor.getAreaMemory().exists(reactor.getLevel(), Registration.IRON_SYMBOL.get())){
            ParticleScribe.drawParticleZigZag(reactor.getLevel(), ParticleTypes.SMOKE, reactor.getAreaMemory().fetch(reactor.getLevel(),
                    Registration.GOLD_SYMBOL.get()), reactor.getAreaMemory().fetch(reactor.getLevel(),
                    Registration.IRON_SYMBOL.get()), 20, 7, 0.8F);
        }else{
            if(reactor instanceof CrucibleBlockEntity crucible){
                SpecialCaseMan.checkEmptySpecialCases(crucible);
            }
            reactor.expendPower();
            reactor.getLevel().setBlock(pos, reactor.getLevel().getBlockState(pos).setValue(CrucibleBlock.FULL, false), Block.UPDATE_CLIENTS);
            reactor.getLevel().explode(null, pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, 1.0F, Level.ExplosionInteraction.NONE);

            if(reactor.getAreaMemory().exists(reactor.getLevel(), Registration.GOLD_SYMBOL.get()))
                reactor.getLevel().removeBlock(reactor.getAreaMemory().fetch(reactor.getLevel(), Registration.GOLD_SYMBOL.get()), true);
        }
    }

    // Changes the Gold Symbol into Active Gold Foam, which spreads outwards for a limited distance and leaves Gold Foam behind.
    public static void foaming(Reactor reactor) {
        BlockPos symbol_position = reactor.getAreaMemory().fetch(reactor.getLevel(), Registration.GOLD_SYMBOL.get());
        if(symbol_position == null)
            return;

        reactor.getLevel().setBlock(symbol_position, Registration.ACTIVE_GOLD_FOAM.get().defaultBlockState(), Block.UPDATE_CLIENTS);
        ParticleScribe.drawParticleZigZag(reactor.getLevel(), ParticleTypes.EFFECT,
                reactor.getBlockPos().getX() + 0.5F, reactor.getBlockPos().getY() + 0.5625F, reactor.getBlockPos().getZ() + 0.5F,
                symbol_position.getX()+0.5, symbol_position.getY()+0.5, symbol_position.getZ()+0.5, 12, 7,0.4);
    }

    public static void smoke(Reactor reactor) {
        if (reactor.getLevel().random.nextFloat() < 0.4) {
            AABB aoe = new AABB(reactor.getBlockPos());
            aoe = aoe.inflate(3); // Inflate the AOE to be 3x the size of the crucible.
            List<LivingEntity> nearby_ents = reactor.getLevel().getEntitiesOfClass(LivingEntity.class, aoe);
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
            ItemEntity salt_drop = new ItemEntity(reactor.getLevel(), reactor.getBlockPos().getX() + 0.5,
                    reactor.getBlockPos().getY() + 0.5,
                    reactor.getBlockPos().getZ() + 0.6, Registration.SALT.get().getDefaultInstance());
            reactor.getLevel().addFreshEntity(salt_drop);
        }else{
            if(reactor instanceof CrucibleBlockEntity crucible){
                CrucibleBlockEntity.empty(reactor.getLevel(), reactor.getBlockPos(), reactor.getBlockState(), crucible);
                if(reactor.getBlockState().getBlock() instanceof ShulkerCrucibleBlock) {
                    ItemEntity shell_drop = new ItemEntity(reactor.getLevel(), reactor.getBlockPos().getX() + 0.5,
                            reactor.getBlockPos().getY() + 0.5,
                            reactor.getBlockPos().getZ() + 0.6, Items.SHULKER_SHELL.getDefaultInstance());
                    reactor.getLevel().addFreshEntity(shell_drop);

                }
                reactor.getLevel().setBlock(reactor.getBlockPos(), Registration.SALTY_CRUCIBLE.get().defaultBlockState(), Block.UPDATE_CLIENTS);
            } else if(reactor instanceof ReactorEntity entity){
                entity.getLevel().setBlock(entity.getBlockPos(), Registration.SALT_BLOCK.get().defaultBlockState(), Block.UPDATE_CLIENTS);
                entity.kill();
            }
            reactor.getLevel().playSound(null, reactor.getBlockPos(), SoundEvents.GLASS_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
    }

    public static void discharge(Reactor reactor) {
        Level level = reactor.getLevel();
        reactor.addElectricCharge(5);
        if (reactor.getElectricCharge() > 21) {
            BlockPos potential_rod = reactor.getAreaMemory().fetch(level, Blocks.LIGHTNING_ROD);
            if (potential_rod != null) {
                if (!reactor.getLevel().isClientSide) {
                    ((LightningRodBlock) Blocks.LIGHTNING_ROD).onLightningStrike(reactor.getLevel().getBlockState(potential_rod), level, potential_rod);
                    ParticleScribe.drawParticleZigZag(level, ParticleTypes.ELECTRIC_SPARK,
                            reactor.getPos().x, reactor.getPos().y, reactor.getPos().z,
                            potential_rod.getX()+0.5, potential_rod.getY()+0.5, potential_rod.getZ()+0.5, 8, 10,0.6);
                    reactor.getLevel().playSound(null, potential_rod, Registration.ZAP_SOUND.get(), SoundSource.BLOCKS, 0.5F, 1F);
                }
            } else {
                AABB aoe = new AABB(reactor.getBlockPos());
                aoe = aoe.inflate(ConfigMan.COMMON.crucibleRange.get()); // Inflate the AOE to be 5x the size of the crucible?
                List<LivingEntity> nearby_ents = reactor.getLevel().getEntitiesOfClass(LivingEntity.class, aoe);

                LivingEntity victim = null;
                for(LivingEntity e : nearby_ents){
                    if((victim == null || e.distanceToSqr(Vec3.atCenterOf(reactor.getBlockPos())) < victim.distanceToSqr(Vec3.atCenterOf(reactor.getBlockPos())))
                    && BeamHelper.hasLineOfSight(level, Vec3.atCenterOf(reactor.getBlockPos()), e.getEyePosition(0), ClipContext.Fluid.NONE, ClipContext.Block.COLLIDER, reactor.getBlockState().getBlock())){
                        victim = e;
                    }
                }

                if(victim == null){
                    return;
                }

                if (!level.isClientSide) {
                    if(CrystalIronItem.effectNotBlocked(victim, 2))
                        victim.hurt(reactor.getLevel().damageSources().magic(), 5);
                    ParticleScribe.drawParticleZigZag(reactor.getLevel(), ParticleTypes.ELECTRIC_SPARK,
                            reactor.getPos().x, reactor.getPos().y, reactor.getPos().z,
                            victim.getX(), victim.getEyeHeight() / 2 + victim.getY(), victim.getZ(), 8, 10, 0.3);
                    reactor.getLevel().playSound(null, victim.getX(), victim.getY(), victim.getZ(), Registration.ZAP_SOUND.get(), SoundSource.BLOCKS, 0.5F, 0.98F + reactor.getLevel().random.nextFloat()*0.05F);
                }
            }
            reactor.setElectricCharge(0);
        }
        reactor.setDirty();
    }

    // Apply levitation to nearby entities.
    public static void levitation(Reactor reactor) {
        AABB aoe = new AABB(reactor.getBlockPos());
        aoe = aoe.inflate(12); // Inflate the AOE to be 6x the size of the crucible.
        List<LivingEntity> nearby_ents = reactor.getLevel().getEntitiesOfClass(LivingEntity.class, aoe);

        BlockPos origin_pos = reactor.getBlockPos();

        for(LivingEntity victim : nearby_ents){
            if(CrystalIronItem.effectNotBlocked(victim, 1)) {
                victim.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 200, 1));
                if(victim instanceof ServerPlayer player){
                    Registration.BE_LEVITATED_TRIGGER.get().trigger(player);
                }
            }
            ParticleScribe.drawParticleZigZag(reactor.getLevel(), ParticleTypes.END_ROD,
                    reactor.getPos().x, reactor.getPos().y, reactor.getPos().z,
                    victim.getX(),victim.getEyeY()-0.2, victim.getZ(), 8, 7, 0.74);
            float pitch = 0.80F + reactor.getLevel().random.nextFloat()*0.1F;
            reactor.getLevel().playSound(null, victim.getX(), victim.getY(), victim.getZ(), SoundEvents.NOTE_BLOCK_CHIME.value(), SoundSource.BLOCKS, 0.3F, pitch);
            reactor.getLevel().playSound(null, victim.getX(), victim.getY(), victim.getZ(), SoundEvents.NOTE_BLOCK_CHIME.value(), SoundSource.BLOCKS, 0.3F, pitch/2);
        }
    }

    // Apply slow fall to nearby entities and, if possible, create Secret Scales.
    public static void slowfall(Reactor reactor) {
        AABB aoe = new AABB(reactor.getBlockPos());
        aoe = aoe.inflate(12); // Inflate the AOE to be 6x the size of the crucible.
        List<LivingEntity> nearby_ents = reactor.getLevel().getEntitiesOfClass(LivingEntity.class, aoe);

        for(LivingEntity victim : nearby_ents){
            if(CrystalIronItem.effectNotBlocked(victim, 1)) {
                victim.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 200, 1));
                if(victim instanceof ServerPlayer player){
                    Registration.BE_SLOWFALLED_TRIGGER.get().trigger(player);
                }
            }
            ParticleScribe.drawExactParticleRing(reactor.getLevel(), ParticleTypes.END_ROD, reactor.getPos(), 0.6, 1);
            reactor.getLevel().playSound(null, victim.getX(), victim.getY(), victim.getZ(), SoundEvents.CONDUIT_AMBIENT_SHORT, SoundSource.BLOCKS, 0.1F, 1.2F);

        }

        // Craft the scales if levitation is also happening, and then empty the Crucible.
        if(reactor.getLinkedCrystal() != null && reactor.getPowerLevel(Powers.LIGHT_POWER.get()) > WorldSpecificValue.get("levitationcost", 10, 30)){
            craftSecretScale(reactor);
        }
    }

    private static void craftSecretScale(Reactor reactor) {
        for(Entity entity : CrucibleBlock.getEntitesInside(reactor.getBlockPos(), reactor.getLevel())){
            if(entity instanceof ItemEntity item_entity && item_entity.getItem().is(Registration.PHANTOM_RESIDUE.get())) {
                ParticleScribe.drawParticleZigZag(reactor.getLevel(), ParticleTypes.END_ROD,
                        reactor.getBlockPos().getX(), reactor.getBlockPos().getY(), reactor.getBlockPos().getZ(),
                        entity.getX(), entity.getY(), entity.getZ(), 25, 10, 0.9);
                reactor.getLevel().playSound(null, reactor.getBlockPos(), SoundEvents.RESPAWN_ANCHOR_CHARGE, SoundSource.BLOCKS,
                        0.8F, 0.8F);
                int count = item_entity.getItem().getCount();
                item_entity.kill();
                ItemStack drop_stack = Registration.SECRET_SCALE.get().getDefaultInstance();
                drop_stack.setCount(count);
                ItemEntity secret_scale = new ItemEntity(reactor.getLevel(), reactor.getBlockPos().getX() + 0.5, reactor.getBlockPos().getY()+0.6, reactor.getBlockPos().getZ() + 0.5, drop_stack);
                secret_scale.setPickUpDelay(20);
                reactor.getLevel().addFreshEntity(secret_scale);
                reactor.getLevel().setBlock(reactor.getBlockPos(), reactor.getBlockState().setValue(CrucibleBlock.FULL, false), Block.UPDATE_CLIENTS);
            }
        }
    }

    // Causes nearby bonemeal-ables to be fertilized occasionally.
    public static void growth(Reactor reactor) {
        Random random = new Random();
        BlockPos target = reactor.getBlockPos().offset(random.nextInt(-32, 32), random.nextInt(-1, 0), random.nextInt(-32, 32));
        if (Objects.requireNonNull(reactor.getLevel()).getBlockState(target).getBlock() instanceof BonemealableBlock) {
            ((BonemealableBlock) reactor.getLevel().getBlockState(target).getBlock()).performBonemeal((ServerLevel) reactor.getLevel(), reactor.getLevel().random, target, reactor.getLevel().getBlockState(target));
        }
    }

    // Shoot flames from the crucible!
    public static void flamethrower(Reactor reactor) {
        if(reactor.getLevel() == null) return;

        AABB blast_zone = new AABB(reactor.getBlockPos());
        blast_zone = blast_zone.inflate(2, 5, 2);

        List<LivingEntity> nearby_ents = reactor.getLevel().getEntitiesOfClass(LivingEntity.class, blast_zone);
        for(LivingEntity e : nearby_ents){
            if(!BeamHelper.hasLineOfSight(reactor.getLevel(), reactor.getBlockPos().getCenter(), e.getEyePosition(0), ClipContext.Fluid.NONE, ClipContext.Block.COLLIDER, reactor.getBlockState().getBlock())) {
                continue;
            }
            e.hurt(reactor.getLevel().damageSources().inFire(), 4);
            e.setRemainingFireTicks(140);
        }
    }

    // Causes nearby undead to catch fire.
    public static void sunlight(Reactor reactor) {
        int range = 12;
        AABB aoe = new AABB(reactor.getBlockPos());
        aoe = aoe.inflate(range);
        List<Monster> nearby_monsters = reactor.getLevel().getEntitiesOfClass(Monster.class, aoe);

        for(Monster m : nearby_monsters){
            if(m.isInvertedHealAndHarm() && m.getPosition(0).distanceTo(reactor.getBlockPos().getCenter()) < range){
                m.hurt(reactor.getLevel().damageSources().inFire(), 3);
                m.setRemainingFireTicks(100);
            }
        }

        ParticleScribe.drawExactParticleRing(reactor.getLevel(), ParticleTypes.END_ROD, reactor.getPos().add(0, 0.1, 0), 12F, 20);
    }

    // Cause blocks to fall down near the Symbol.
    public static void blockfall(Reactor reactor) {
        Level level = reactor.getLevel();
        RandomSource random = level.random;
        BlockPos symbol_pos = reactor.getAreaMemory().fetch(level, Registration.GOLD_SYMBOL.get());
        if(symbol_pos == null)
            return;

        for(int i = 0; i < 10; i++) {
            BlockPos target = symbol_pos.offset(random.nextInt(-4, 4), random.nextInt(0, 4), random.nextInt(-4, 4));
            if (target == reactor.getBlockPos() || target == symbol_pos) continue;
            BlockState target_state = level.getBlockState(target);
            if (!target_state.isAir() && BlockMoveChecker.canMakeBlockFall(reactor.getLevel(), target, target_state)) {
                FallingBlockEntity.fall(level, target, target_state);
                ParticleScribe.drawParticleZigZag(level, ParticleTypes.END_ROD, reactor.getBlockPos(), target, 8, 32, 0.7F);
                ItemEntity drop = new ItemEntity(level, reactor.getBlockPos().getX()+0.5, reactor.getBlockPos().getY()+0.6, reactor.getBlockPos().getZ()+0.5,
                        Registration.MOTION_SALT.get().getDefaultInstance());
                level.addFreshEntity(drop);
            }
        }
    }

    public static void immobilize(Reactor reactor) {
        Level level = reactor.getLevel();
        if(level == null)
            return;

        AABB aoe = new AABB(reactor.getBlockPos());
        aoe = aoe.inflate(2);
        List<LivingEntity> nearby = level.getEntitiesOfClass(LivingEntity.class, aoe);

        for(LivingEntity living : nearby){
            ParticleScribe.drawParticleReactionSurface(level, ParticleTypes.REVERSE_PORTAL, reactor);
            if(CrystalIronItem.effectNotBlocked(living, 1)) {
                if(living instanceof Player player && player.isShiftKeyDown()){
                    MobEffectInstance stop = new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 50);
                    player.addEffect(stop);
                }else {
                    MobEffectInstance stop = new MobEffectInstance(Registration.IMMOBILE, 50, 0, true, false, true);
                    living.addEffect(stop);
                }
            }
        }
    }

    public static void creation(Reactor reactor){
        Level level = Objects.requireNonNull(reactor.getLevel());
        if(level.random.nextFloat() < 0.2){
            for(BlockPos creation_point : getCreationPoints(reactor.getBlockPos())){
                if(level.getBlockState(creation_point).isAir() && level.isLoaded(creation_point)){
                    level.setBlock(creation_point, Registration.UNFORMED_MATTER.get().defaultBlockState(), Block.UPDATE_CLIENTS);
                    level.updateNeighborsAt(creation_point, Registration.UNFORMED_MATTER.get());
                    ParticleScribe.drawParticleZigZag(level, Registration.STARDUST_PARTICLE, reactor.getBlockPos(), creation_point, 10, 5, 0.5F);
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

    public static void flowTooStrong(Reactor reactor){
        int flow = reactor.getPowerLevel(Powers.FLOW_POWER.get());
        reactor.expendPower(Powers.FLOW_POWER.get(), flow);
        reactor.addPower(Powers.LIGHT_POWER.get(), flow / 3);
        reactor.setElectricCharge(reactor.getElectricCharge() + 20);
        SpecialCaseMan.windBomb(reactor.getLevel(), Vec3.atCenterOf(reactor.getBlockPos()));
    }

    public static void omenSettling(Reactor reactor){
        if(reactor.getLevel().random.nextFloat() < 0.1F){
            reactor.addPower(Powers.CURSE_POWER.get(), 1);
        }
        reactor.addPower(Powers.SOUL_POWER.get(), 2);
    }

    public static void chomp(Reactor reactor) {
        var level = reactor.getLevel();
        assert level != null;
        for(Entity entity : CrucibleBlock.getEntitesInside(reactor.getBlockPos(), level)){
            if(entity instanceof ItemEntity item && item.getItem().is(Items.IRON_INGOT)){
                item.kill();
            }
        }
        var pos = Vec3.atCenterOf(reactor.getBlockPos());
        EvokerFangs fangs = new EvokerFangs(level, pos.x, pos.y + 0.48, pos.z, 0F, 10, null);
        reactor.getLevel().addFreshEntity(fangs);

        AABB aoe = new AABB(reactor.getBlockPos());
        aoe = aoe.inflate(4);
        List<LivingEntity> nearby = level.getEntitiesOfClass(LivingEntity.class, aoe);
        for(LivingEntity living : nearby){
            EvokerFangs targeted_fangs = new EvokerFangs(level, living.position().x, living.position().y, living.position().z, 0F, 10, null);
            reactor.getLevel().addFreshEntity(targeted_fangs);
        }
    }

    public static void shrink(Reactor reactor) {
        resizeNearby(reactor, ConfigMan.SERVER.shrinkSmallSize.get(), ConfigMan.SERVER.shrinkSmallStep.get(), ResizeMode.REDUCE, Registration.ACID_BUBBLE_PARTICLE);
    }

    public static void grow(Reactor reactor) {
        resizeNearby(reactor, ConfigMan.SERVER.growLargeSize.get(), ConfigMan.SERVER.growLargeStep.get(), ResizeMode.ENLARGE, ParticleTypes.HAPPY_VILLAGER);
    }

    public static void revert_from_large(Reactor reactor) {
        resizeNearby(reactor, 1.0, 0.6, ResizeMode.REDUCE, ParticleTypes.ELECTRIC_SPARK);
    }

    public static void revert_from_small(Reactor reactor) {
        resizeNearby(reactor, 1.0, 0.6, ResizeMode.ENLARGE, ParticleTypes.ELECTRIC_SPARK);
    }

    private static void resizeNearby(Reactor reactor, double new_scale, double new_step_height, ResizeMode mode, ParticleOptions particle) {
        if (Objects.requireNonNull(reactor.getLevel()).random.nextFloat() < 0.4) {
            AABB aoe = new AABB(reactor.getBlockPos());
            aoe = aoe.inflate(3);
            List<LivingEntity> victims = reactor.getLevel().getEntitiesOfClass(LivingEntity.class, aoe);
            for (LivingEntity victim : victims) {
                if (CrystalIronItem.effectNotBlocked(victim, 2)) {
                    double current_scale = victim.getAttributeValue(Attributes.SCALE);
                    if(mode == ResizeMode.ENLARGE && current_scale < new_scale
                            || mode == ResizeMode.REDUCE && current_scale > new_scale){
                        Objects.requireNonNull(victim.getAttribute(Attributes.SCALE)).setBaseValue(new_scale);
                        Objects.requireNonNull(victim.getAttribute(Attributes.STEP_HEIGHT)).setBaseValue(new_step_height);
                        ParticleScribe.drawParticleZigZag(reactor.getLevel(), particle,
                                reactor.getPos().x, reactor.getPos().y, reactor.getPos().z,
                                victim.getEyePosition().x, victim.getEyePosition().y, victim.getEyePosition().z, 20, 5, 0.9);
                        reactor.getLevel().playSound(null, reactor.getBlockPos(), Registration.ZAP_SOUND.get(), SoundSource.BLOCKS);
                        reactor.getLevel().playSound(null, reactor.getBlockPos(), SoundEvents.RESPAWN_ANCHOR_CHARGE, SoundSource.BLOCKS, 0.5F, 1.3F + reactor.getLevel().random.nextFloat()*0.2F);
                        victim.hurt(reactor.getLevel().damageSources().magic(), 1);
                        if(victim instanceof ServerPlayer splayer){
                            if (new_scale == 1.0) {
                                Registration.SIZE_REVERTED_TRIGGER.get().trigger(splayer);
                            } else {
                                Registration.SIZE_CHANGED_TRIGGER.get().trigger(splayer);
                            }
                        }
                    }
                }
            }
        }
    }

    enum ResizeMode {
        ENLARGE,
        REDUCE
    }

    public static void lightning(Reactor reactor) {
        Level level = reactor.getLevel();
        assert level != null;
        LightningBolt bolt = new LightningBolt(EntityType.LIGHTNING_BOLT, level);
        if(reactor.getAreaMemory().exists(level, Blocks.LIGHTNING_ROD)){
            bolt.setPos(Vec3.atCenterOf(reactor.getAreaMemory().fetch(level, Blocks.LIGHTNING_ROD)));
        } else {
            bolt.setPos(Vec3.atCenterOf(reactor.getBlockPos()));
        }
        level.addFreshEntity(bolt);
        reactor.expendPower(Powers.LIGHT_POWER.get(), reactor.maxPower());
        reactor.setDirty();
    }
}
