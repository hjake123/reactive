package com.hyperlynx.reactive.alchemy.rxn;

import com.hyperlynx.reactive.Registration;
import com.hyperlynx.reactive.advancements.CriteriaTriggers;
import com.hyperlynx.reactive.alchemy.Powers;
import com.hyperlynx.reactive.alchemy.special.SpecialCaseMan;
import com.hyperlynx.reactive.be.CrucibleBlockEntity;
import com.hyperlynx.reactive.blocks.CrucibleBlock;
import com.hyperlynx.reactive.blocks.ShulkerCrucibleBlock;
import com.hyperlynx.reactive.client.particles.ParticleScribe;
import com.hyperlynx.reactive.items.CrystalIronItem;
import com.hyperlynx.reactive.ConfigMan;
import com.hyperlynx.reactive.util.BlockMoveChecker;
import com.hyperlynx.reactive.util.WorldSpecificValue;
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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.EvokerFangs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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
    public static CrucibleBlockEntity explosion(CrucibleBlockEntity c) {
        BlockPos pos = c.getBlockPos();

        ParticleScribe.drawParticleZigZag(c.getLevel(), ParticleTypes.SMOKE, pos, c.areaMemory.fetch(c.getLevel(),
                Registration.GOLD_SYMBOL.get()), 20, 7, 0.8F);

        if(c.areaMemory.exists(c.getLevel(), Registration.IRON_SYMBOL.get())){
            ParticleScribe.drawParticleZigZag(c.getLevel(), ParticleTypes.SMOKE, c.areaMemory.fetch(c.getLevel(),
                    Registration.GOLD_SYMBOL.get()), c.areaMemory.fetch(c.getLevel(),
                    Registration.IRON_SYMBOL.get()), 20, 7, 0.8F);
        }else{
            SpecialCaseMan.checkEmptySpecialCases(c);
            c.expendPower();
            c.getLevel().setBlock(pos, c.getLevel().getBlockState(pos).setValue(CrucibleBlock.FULL, false), Block.UPDATE_CLIENTS);
            c.getLevel().explode(null, pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, 1.0F, Level.ExplosionInteraction.NONE);

            if(c.areaMemory.exists(c.getLevel(), Registration.GOLD_SYMBOL.get()))
                c.getLevel().removeBlock(c.areaMemory.fetch(c.getLevel(), Registration.GOLD_SYMBOL.get()), true);
        }
        return c;
    }

    // Changes the Gold Symbol into Active Gold Foam, which spreads outwards for a limited distance and leaves Gold Foam behind.
    public static CrucibleBlockEntity foaming(CrucibleBlockEntity c) {
        BlockPos symbol_position = c.areaMemory.fetch(c.getLevel(), Registration.GOLD_SYMBOL.get());
        if(symbol_position == null)
            return c;

        c.getLevel().setBlock(symbol_position, Registration.ACTIVE_GOLD_FOAM.get().defaultBlockState(), Block.UPDATE_CLIENTS);
        ParticleScribe.drawParticleZigZag(c.getLevel(), ParticleTypes.EFFECT,
                c.getBlockPos().getX() + 0.5F, c.getBlockPos().getY() + 0.5625F, c.getBlockPos().getZ() + 0.5F,
                symbol_position.getX()+0.5, symbol_position.getY()+0.5, symbol_position.getZ()+0.5, 12, 7,0.4);
        return c;
    }

    public static CrucibleBlockEntity smoke(CrucibleBlockEntity c) {
        if (c.getLevel().random.nextFloat() < 0.4) {
            AABB aoe = new AABB(c.getBlockPos());
            aoe = aoe.inflate(3); // Inflate the AOE to be 3x the size of the crucible.
            List<LivingEntity> nearby_ents = c.getLevel().getEntitiesOfClass(LivingEntity.class, aoe);
            for (LivingEntity e : nearby_ents) {
                if (CrystalIronItem.effectNotBlocked(e, 1)) {
                    e.addEffect(new MobEffectInstance(MobEffects.POISON, 200, 1));
                    e.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 200, 1));
                    e.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 1));
                }
            }
        }
        return c;
    }

    public static CrucibleBlockEntity salt(CrucibleBlockEntity c) {
        if(c.getTotalPowerLevel() < WorldSpecificValue.get("salt_overflow_threshold", 1000, 1300)){
            ItemEntity salt_drop = new ItemEntity(c.getLevel(), c.getBlockPos().getX() + 0.5,
                    c.getBlockPos().getY() + 0.5,
                    c.getBlockPos().getZ() + 0.6, Registration.SALT.get().getDefaultInstance());
            c.getLevel().addFreshEntity(salt_drop);
        }else{
            CrucibleBlockEntity.empty(c.getLevel(), c.getBlockPos(), c.getBlockState(), c);
            if(c.getBlockState().getBlock() instanceof ShulkerCrucibleBlock) {
                ItemEntity shell_drop = new ItemEntity(c.getLevel(), c.getBlockPos().getX() + 0.5,
                        c.getBlockPos().getY() + 0.5,
                        c.getBlockPos().getZ() + 0.6, Items.SHULKER_SHELL.getDefaultInstance());
                c.getLevel().addFreshEntity(shell_drop);
            }
            c.getLevel().setBlock(c.getBlockPos(), Registration.SALTY_CRUCIBLE.get().defaultBlockState(), Block.UPDATE_CLIENTS);
            c.getLevel().playSound(null, c.getBlockPos(), SoundEvents.GLASS_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
        return c;
    }

    public static CrucibleBlockEntity discharge(CrucibleBlockEntity c) {
        c.electricCharge += 5;
        if (c.electricCharge > 21) {
            BlockPos potential_rod = c.areaMemory.fetch(c.getLevel(), Blocks.LIGHTNING_ROD);
            if (potential_rod != null) {
                if (!c.getLevel().isClientSide) {
                    ((LightningRodBlock) Blocks.LIGHTNING_ROD).onLightningStrike(c.getLevel().getBlockState(potential_rod), c.getLevel(), potential_rod);
                    ParticleScribe.drawParticleZigZag(c.getLevel(), ParticleTypes.ELECTRIC_SPARK,
                            c.getBlockPos().getX() + 0.5F, c.getBlockPos().getY() + 0.5625F, c.getBlockPos().getZ() + 0.5F,
                            potential_rod.getX()+0.5, potential_rod.getY()+0.5, potential_rod.getZ()+0.5, 8, 10,0.6);
                    c.getLevel().playSound(null, potential_rod, Registration.ZAP_SOUND.get(), SoundSource.BLOCKS, 0.5F, 1F);
                }
            } else {
                AABB aoe = new AABB(c.getBlockPos());
                aoe = aoe.inflate(ConfigMan.COMMON.crucibleRange.get()); // Inflate the AOE to be 5x the size of the crucible?
                List<LivingEntity> nearby_ents = c.getLevel().getEntitiesOfClass(LivingEntity.class, aoe);
                if (nearby_ents.isEmpty()) {
                    return c;
                }
                LivingEntity victim = nearby_ents.get(0);

                if (!c.getLevel().isClientSide) {
                    if(CrystalIronItem.effectNotBlocked(victim, 2))
                        victim.hurt(c.getLevel().damageSources().magic(), 12);
                    ParticleScribe.drawParticleZigZag(c.getLevel(), ParticleTypes.ELECTRIC_SPARK,
                            c.getBlockPos().getX() + 0.5F, c.getBlockPos().getY() + 0.5625F, c.getBlockPos().getZ() + 0.5F,
                            victim.getX(), victim.getEyeHeight() / 2 + victim.getY(), victim.getZ(), 8, 10, 0.3);
                    c.getLevel().playSound(null, victim.getX(), victim.getY(), victim.getZ(), Registration.ZAP_SOUND.get(), SoundSource.BLOCKS, 0.5F, 0.98F + c.getLevel().random.nextFloat()*0.05F);
                }
            }
            c.electricCharge = 0;
        }
        c.setChanged();
        return c;
    }

    // Apply levitation to nearby entities.
    public static CrucibleBlockEntity levitation(CrucibleBlockEntity c) {
        AABB aoe = new AABB(c.getBlockPos());
        aoe = aoe.inflate(12); // Inflate the AOE to be 6x the size of the crucible.
        List<LivingEntity> nearby_ents = c.getLevel().getEntitiesOfClass(LivingEntity.class, aoe);

        BlockPos origin_pos = c.getBlockPos();

        for(LivingEntity victim : nearby_ents){
            if(CrystalIronItem.effectNotBlocked(victim, 1)) {
                victim.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 200, 1));
                if(victim instanceof ServerPlayer player){
                    CriteriaTriggers.BE_LEVITATED_TRIGGER.get().trigger(player);
                }
            }
            ParticleScribe.drawParticleZigZag(c.getLevel(), ParticleTypes.END_ROD,
                    origin_pos.getX() + 0.5, origin_pos.getY() + 0.5625, origin_pos.getZ() + 0.5,
                    victim.getX(),victim.getEyeY()-0.2, victim.getZ(), 8, 7, 0.74);
            float pitch = 0.80F + c.getLevel().random.nextFloat()*0.1F;
            c.getLevel().playSound(null, victim.getX(), victim.getY(), victim.getZ(), SoundEvents.NOTE_BLOCK_CHIME.value(), SoundSource.BLOCKS, 0.3F, pitch);
            c.getLevel().playSound(null, victim.getX(), victim.getY(), victim.getZ(), SoundEvents.NOTE_BLOCK_CHIME.value(), SoundSource.BLOCKS, 0.3F, pitch/2);
        }
        return c;
    }

    // Apply slow fall to nearby entities and, if possible, create Secret Scales.
    public static CrucibleBlockEntity slowfall(CrucibleBlockEntity crucible) {
        AABB aoe = new AABB(crucible.getBlockPos());
        aoe = aoe.inflate(12); // Inflate the AOE to be 6x the size of the crucible.
        List<LivingEntity> nearby_ents = crucible.getLevel().getEntitiesOfClass(LivingEntity.class, aoe);

        for(LivingEntity victim : nearby_ents){
            if(CrystalIronItem.effectNotBlocked(victim, 1)) {
                victim.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 200, 1));
                if(victim instanceof ServerPlayer player){
                    CriteriaTriggers.BE_SLOWFALLED_TRIGGER.get().trigger(player);
                }
            }
            ParticleScribe.drawParticleRing(crucible.getLevel(), ParticleTypes.END_ROD, crucible.getBlockPos(), 0.5, 0.6, 1);
            crucible.getLevel().playSound(null, victim.getX(), victim.getY(), victim.getZ(), SoundEvents.CONDUIT_AMBIENT_SHORT, SoundSource.BLOCKS, 0.1F, 1.2F);

        }

        // Craft the scales if levitation is also happening, and then empty the Crucible.
        if(crucible.linked_crystal != null && crucible.getPowerLevel(Powers.LIGHT_POWER.get()) > WorldSpecificValue.get("levitationcost", 10, 30)){
            craftSecretScale(crucible);
        }
        return crucible;
    }

    private static void craftSecretScale(CrucibleBlockEntity crucible) {
        for(Entity entity : CrucibleBlock.getEntitesInside(crucible.getBlockPos(), crucible.getLevel())){
            if(entity instanceof ItemEntity item_entity && item_entity.getItem().is(Registration.PHANTOM_RESIDUE.get())) {
                ParticleScribe.drawParticleZigZag(crucible.getLevel(), ParticleTypes.END_ROD,
                        crucible.getBlockPos().getX(), crucible.getBlockPos().getY(), crucible.getBlockPos().getZ(),
                        entity.getX(), entity.getY(), entity.getZ(), 25, 10, 0.9);
                crucible.getLevel().playSound(null, crucible.getBlockPos(), SoundEvents.RESPAWN_ANCHOR_CHARGE, SoundSource.BLOCKS,
                        0.8F, 0.8F);
                int count = item_entity.getItem().getCount();
                item_entity.kill();
                ItemStack drop_stack = Registration.SECRET_SCALE.get().getDefaultInstance();
                drop_stack.setCount(count);
                ItemEntity secret_scale = new ItemEntity(crucible.getLevel(), crucible.getBlockPos().getX() + 0.5, crucible.getBlockPos().getY()+0.6, crucible.getBlockPos().getZ() + 0.5, drop_stack);
                secret_scale.setPickUpDelay(20);
                crucible.getLevel().addFreshEntity(secret_scale);
                crucible.getLevel().setBlock(crucible.getBlockPos(), crucible.getBlockState().setValue(CrucibleBlock.FULL, false), Block.UPDATE_CLIENTS);
            }
        }
    }

    // Causes nearby bonemeal-ables to be fertilized occasionally.
    public static CrucibleBlockEntity growth(CrucibleBlockEntity c) {
        Random random = new Random();
        BlockPos target = c.getBlockPos().offset(random.nextInt(-32, 32), random.nextInt(-1, 0), random.nextInt(-32, 32));
        if (Objects.requireNonNull(c.getLevel()).getBlockState(target).getBlock() instanceof BonemealableBlock) {
            ((BonemealableBlock) c.getLevel().getBlockState(target).getBlock()).performBonemeal((ServerLevel) c.getLevel(), c.getLevel().random, target, c.getLevel().getBlockState(target));
        }
        return c;
    }

    // Shoot flames from the crucible!
    public static CrucibleBlockEntity flamethrower(CrucibleBlockEntity c) {
        if(c.getLevel() == null) return c;

        AABB blast_zone = new AABB(c.getBlockPos());
        blast_zone = blast_zone.inflate(2, 5, 2);

        List<LivingEntity> nearby_ents = c.getLevel().getEntitiesOfClass(LivingEntity.class, blast_zone);
        for(LivingEntity e : nearby_ents){
            e.hurt(c.getLevel().damageSources().inFire(), 7);
            e.setRemainingFireTicks(140);
        }
        return c;
    }

    // Causes nearby undead to catch fire.
    public static CrucibleBlockEntity sunlight(CrucibleBlockEntity c) {
        int range = 12;
        AABB aoe = new AABB(c.getBlockPos());
        aoe = aoe.inflate(range);
        List<Monster> nearby_monsters = c.getLevel().getEntitiesOfClass(Monster.class, aoe);

        for(Monster m : nearby_monsters){
            if(m.isInvertedHealAndHarm() && m.getPosition(0).distanceTo(c.getBlockPos().getCenter()) < range){
                m.hurt(c.getLevel().damageSources().inFire(), 3);
                m.setRemainingFireTicks(100);
            }
        }

        ParticleScribe.drawParticleRing(c.getLevel(), ParticleTypes.END_ROD, c.getBlockPos(), 0.6F, 12F, 20);
        return c;
    }

    // Cause blocks to fall down near the Symbol.
    public static CrucibleBlockEntity blockfall(CrucibleBlockEntity c) {
        Level level = c.getLevel();
        RandomSource random = level.random;
        BlockPos symbol_pos = c.areaMemory.fetch(level, Registration.GOLD_SYMBOL.get());
        if(symbol_pos == null)
            return c;

        for(int i = 0; i < 10; i++) {
            BlockPos target = symbol_pos.offset(random.nextInt(-4, 4), random.nextInt(0, 4), random.nextInt(-4, 4));
            if (target == c.getBlockPos() || target == symbol_pos) continue;
            BlockState target_state = level.getBlockState(target);
            if (!target_state.isAir() && BlockMoveChecker.canMakeBlockFall(c.getLevel(), target, target_state)) {
                FallingBlockEntity.fall(level, target, target_state);
                ParticleScribe.drawParticleZigZag(level, ParticleTypes.END_ROD, c.getBlockPos(), target, 8, 32, 0.7F);
                ItemEntity drop = new ItemEntity(level, c.getBlockPos().getX()+0.5, c.getBlockPos().getY()+0.6, c.getBlockPos().getZ()+0.5,
                        Registration.MOTION_SALT.get().getDefaultInstance());
                level.addFreshEntity(drop);
            }
        }
        return c;
    }

    public static CrucibleBlockEntity immobilize(CrucibleBlockEntity crucible) {
        Level level = crucible.getLevel();
        if(level == null)
            return crucible;

        AABB aoe = new AABB(crucible.getBlockPos());
        aoe = aoe.inflate(2);
        List<LivingEntity> nearby = level.getEntitiesOfClass(LivingEntity.class, aoe);

        for(LivingEntity living : nearby){
            ParticleScribe.drawParticleCrucibleTop(level, ParticleTypes.REVERSE_PORTAL, crucible.getBlockPos());
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
        return crucible;
    }

    public static CrucibleBlockEntity creation(CrucibleBlockEntity crucible){
        Level level = Objects.requireNonNull(crucible.getLevel());
        if(level.random.nextFloat() < 0.2){
            crucible.expendPower(Powers.CURSE_POWER.get(), 3);
            for(BlockPos creation_point : getCreationPoints(crucible.getBlockPos())){
                if(level.getBlockState(creation_point).isAir() && level.isLoaded(creation_point)){
                    level.setBlock(creation_point, Registration.UNFORMED_MATTER.get().defaultBlockState(), Block.UPDATE_CLIENTS);
                    level.updateNeighborsAt(creation_point, Registration.UNFORMED_MATTER.get());
                    ParticleScribe.drawParticleZigZag(level, Registration.STARDUST_PARTICLE, crucible.getBlockPos(), creation_point, 10, 5, 0.5F);
                    break;
                }
            }
        }
        return crucible;
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

    public static CrucibleBlockEntity flowTooStrong(CrucibleBlockEntity crucible){
        int flow = crucible.getPowerLevel(Powers.FLOW_POWER.get());
        crucible.expendPower(Powers.FLOW_POWER.get(), flow);
        crucible.addPower(Powers.LIGHT_POWER.get(), flow / 3);
        crucible.electricCharge += 20;
        SpecialCaseMan.windBomb(crucible.getLevel(), Vec3.atCenterOf(crucible.getBlockPos()));
        return crucible;
    }

    public static CrucibleBlockEntity omenSettling(CrucibleBlockEntity crucible){
        if(crucible.getLevel().random.nextFloat() < 0.1F){
            crucible.addPower(Powers.CURSE_POWER.get(), 1);
        }
        crucible.addPower(Powers.SOUL_POWER.get(), 2);
        return crucible;
    }

    public static CrucibleBlockEntity chomp(CrucibleBlockEntity crucible) {
        var level = crucible.getLevel();
        assert level != null;
        for(Entity entity : CrucibleBlock.getEntitesInside(crucible.getBlockPos(), level)){
            if(entity instanceof ItemEntity item && item.getItem().is(Items.IRON_INGOT)){
                item.kill();
            }
        }
        var pos = Vec3.atCenterOf(crucible.getBlockPos());
        EvokerFangs fangs = new EvokerFangs(level, pos.x, pos.y + 0.48, pos.z, 0F, 10, null);
        crucible.getLevel().addFreshEntity(fangs);

        AABB aoe = new AABB(crucible.getBlockPos());
        aoe = aoe.inflate(4);
        List<LivingEntity> nearby = level.getEntitiesOfClass(LivingEntity.class, aoe);
        for(LivingEntity living : nearby){
            EvokerFangs targeted_fangs = new EvokerFangs(level, living.position().x, living.position().y, living.position().z, 0F, 10, null);
            crucible.getLevel().addFreshEntity(targeted_fangs);
        }
        return crucible;
    }

    public static CrucibleBlockEntity shrink(CrucibleBlockEntity crucible) {
        resizeNearby(crucible, ConfigMan.SERVER.shrinkSmallSize.get(), ConfigMan.SERVER.shrinkSmallStep.get(), ResizeMode.REDUCE, Registration.ACID_BUBBLE_PARTICLE);
        return crucible;
    }

    public static CrucibleBlockEntity grow(CrucibleBlockEntity crucible) {
        resizeNearby(crucible, ConfigMan.SERVER.growLargeSize.get(), ConfigMan.SERVER.growLargeStep.get(), ResizeMode.ENLARGE, ParticleTypes.HAPPY_VILLAGER);
        return crucible;
    }

    public static CrucibleBlockEntity revert_from_large(CrucibleBlockEntity crucible) {
        resizeNearby(crucible, 1.0, 0.6, ResizeMode.REDUCE, ParticleTypes.ELECTRIC_SPARK);
        return crucible;
    }

    public static CrucibleBlockEntity revert_from_small(CrucibleBlockEntity crucible) {
        resizeNearby(crucible, 1.0, 0.6, ResizeMode.ENLARGE, ParticleTypes.ELECTRIC_SPARK);
        return crucible;
    }

    private static void resizeNearby(CrucibleBlockEntity crucible, double new_scale, double new_step_height, ResizeMode mode, ParticleOptions particle) {
        if (Objects.requireNonNull(crucible.getLevel()).random.nextFloat() < 0.4) {
            AABB aoe = new AABB(crucible.getBlockPos());
            aoe = aoe.inflate(3);
            List<LivingEntity> victims = crucible.getLevel().getEntitiesOfClass(LivingEntity.class, aoe);
            for (LivingEntity victim : victims) {
                if (CrystalIronItem.effectNotBlocked(victim, 2)) {
                    double current_scale = victim.getAttributeValue(Attributes.SCALE);
                    if(mode == ResizeMode.ENLARGE && current_scale < new_scale
                            || mode == ResizeMode.REDUCE && current_scale > new_scale){
                        Objects.requireNonNull(victim.getAttribute(Attributes.SCALE)).setBaseValue(new_scale);
                        Objects.requireNonNull(victim.getAttribute(Attributes.STEP_HEIGHT)).setBaseValue(new_step_height);
                        ParticleScribe.drawParticleZigZag(crucible.getLevel(), particle,
                                crucible.getBlockPos().getX()+0.5, crucible.getBlockPos().getY()+0.6, crucible.getBlockPos().getZ()+0.5,
                                victim.getEyePosition().x, victim.getEyePosition().y, victim.getEyePosition().z, 20, 5, 0.9);
                        crucible.getLevel().playSound(null, crucible.getBlockPos(), Registration.ZAP_SOUND.get(), SoundSource.BLOCKS);
                        crucible.getLevel().playSound(null, crucible.getBlockPos(), SoundEvents.RESPAWN_ANCHOR_CHARGE, SoundSource.BLOCKS, 0.5F, 1.3F + crucible.getLevel().random.nextFloat()*0.2F);
                        victim.hurt(crucible.getLevel().damageSources().magic(), 1);
                        if(victim instanceof ServerPlayer splayer){
                            if (new_scale == 1.0) {
                                CriteriaTriggers.SIZE_REVERTED_TRIGGER.get().trigger(splayer);
                            } else {
                                CriteriaTriggers.SIZE_CHANGED_TRIGGER.get().trigger(splayer);
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
}
