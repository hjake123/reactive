package com.hyperlynx.reactive.alchemy;

import com.hyperlynx.reactive.Registration;
import com.hyperlynx.reactive.advancements.CriteriaTriggers;
import com.hyperlynx.reactive.advancements.FlagCriterion;
import com.hyperlynx.reactive.be.CrucibleBlockEntity;
import com.hyperlynx.reactive.blocks.DisplacedBlock;
import com.hyperlynx.reactive.blocks.IncompleteStaffBlock;
import com.hyperlynx.reactive.fx.particles.ParticleScribe;
import com.hyperlynx.reactive.items.CrystalIronItem;
import com.hyperlynx.reactive.items.LitmusPaperItem;
import com.hyperlynx.reactive.items.PowerBottleItem;
import com.hyperlynx.reactive.items.WarpBottleItem;
import com.hyperlynx.reactive.util.ConfigMan;
import com.hyperlynx.reactive.util.HyperPortalShape;
import com.hyperlynx.reactive.util.WorldSpecificValue;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.StructureTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.EyeOfEnder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.*;

// It's SpecialCaseMan, to the rescue once again!
// Handles various circumstances that go beyond the normal logic of the mod.
public class SpecialCaseMan {
    public static void checkDissolveSpecialCases(CrucibleBlockEntity c, ItemEntity e){
        if(e.getItem().is(Registration.LITMUS_PAPER.get()))
            LitmusPaperItem.takeMeasurement(e.getItem(), c);
        else if(e.getItem().is(Tags.Items.ENDER_PEARLS))
            enderPearlDissolve(Objects.requireNonNull(c.getLevel()), c.getBlockPos(), e, c);
        else if(e.getItem().is(Tags.Items.GUNPOWDER) && c.getPowerLevel(Powers.BLAZE_POWER.get()) > 10)
            explodeGunpowderDueToBlaze(Objects.requireNonNull(c.getLevel()), c.getBlockPos(), e);
        else if(e.getItem().is(Items.CARVED_PUMPKIN))
            pumpkinMagic(Objects.requireNonNull(c.getLevel()), e, c);
        else if(e.getItem().is(Items.WRITABLE_BOOK))
            waterWriting(c, e);
        else if(e.getItem().is(Tags.Items.INGOTS_COPPER) && c.getPowerLevel(Powers.ACID_POWER.get()) > 10)
            copperCharging(c);
        else if(e.getItem().is(Items.ENDER_EYE) && c.getPowerLevel(Powers.CURSE_POWER.get()) < 10)
            enderEyeFlyAway(c, e);
        else if(e.getItem().is(Registration.PHANTOM_RESIDUE.get()) && c.getPowerLevel(Powers.VERDANT_POWER.get()) > 700)
            residualSlime(c, e);
        else if(e.getItem().is(Items.SCULK_CATALYST))
            sculkMagic(c, e);
        else if((e.getItem().is(Registration.MOTION_SALT_BLOCK_ITEM.get()) || e.getItem().is(Registration.FRAMED_MOTION_SALT_BLOCK_ITEM.get()))
                && c.electricCharge > 0)
            displaceNearby(c, e);

        PowerBottleItem.tryEmptyPowerBottle(e, c);
    }

    public static void checkEmptySpecialCases(CrucibleBlockEntity c){
        if(c.getLevel() == null) return;
        if(c.getPowerLevel(Powers.SOUL_POWER.get()) > WorldSpecificValue.get("soul_escape_threshold", 300, 600))
            soulEscape(c);
        if(c.getPowerLevel(Powers.CURSE_POWER.get()) > 665)
            curseEscape(c);
        if(c.getPowerLevel(Powers.BLAZE_POWER.get()) > WorldSpecificValue.get("blaze_escape_threshold", 20, 100))
            blazeEscape(c);
        if(c.getPowerLevel(Powers.VERDANT_POWER.get()) > WorldSpecificValue.get("verdant_escape_threshold", 1300, 1500))
            verdantEscape(c);
        if(c.getPowerLevel(Powers.LIGHT_POWER.get()) > WorldSpecificValue.get("light_escape_threshold", 800, 1100))
            lightEscape(c);
        if(c.areaMemory.exists(c.getLevel(), ConfigMan.COMMON.crucibleRange.get(), Registration.INCOMPLETE_STAFF.get()))
            staffCraftStep(c, c.areaMemory.fetch(c.getLevel(), ConfigMan.COMMON.crucibleRange.get(), Registration.INCOMPLETE_STAFF.get()));
    }

    public static ItemStack checkBottleSpecialCases(CrucibleBlockEntity c, ItemStack bottle){
        if(c.enderRiftStrength > 0 && bottle.is(Registration.WARP_BOTTLE.get()))
            return makeRiftBottle(c, bottle);
        return bottle;
    }

    // Copper ingots in acid charge the Crucible.
    private static void copperCharging(CrucibleBlockEntity c) {
        if(c.electricCharge < 20)
            c.electricCharge += 2;
    }

    // Ender eyes that are thrown into the Crucible without Curse are launched as if used.
    private static void enderEyeFlyAway(CrucibleBlockEntity c, ItemEntity e) {
        ServerLevel serverlevel = (ServerLevel) c.getLevel();
        BlockPos blockpos = serverlevel.findNearestMapStructure(StructureTags.EYE_OF_ENDER_LOCATED, c.getBlockPos(), 100, false);
        if (blockpos != null) {
            EyeOfEnder eyeofender = new EyeOfEnder(c.getLevel(), c.getBlockPos().getX(), c.getBlockPos().getY(), c.getBlockPos().getZ());
            eyeofender.setItem(e.getItem());
            eyeofender.signalTo(blockpos);
            c.getLevel().addFreshEntity(eyeofender);
            c.getLevel().playSound(null, c.getBlockPos().getX()+0.5, c.getBlockPos().getY()+0.5, c.getBlockPos().getZ()+0.5, SoundEvents.ENDER_EYE_LAUNCH, SoundSource.NEUTRAL, 0.5F, 0.4F / (c.getLevel().getRandom().nextFloat() * 0.4F + 0.8F));
            e.getItem().shrink(1);
            if(e.getItem().getCount() < 1)
                e.kill();
        }
    }

    // Dissolving a carved pumpkin might have many effects.
    private static void pumpkinMagic(Level level, ItemEntity e, CrucibleBlockEntity c) {
        if (level.isClientSide || c.areaMemory.exists(level, ConfigMan.COMMON.crucibleRange.get(), Registration.IRON_SYMBOL.get()))
            return;

        BlockPos blazeRodPos = c.areaMemory.fetch(level, ConfigMan.COMMON.crucibleRange.get(), Registration.BLAZE_ROD.get());
        if(blazeRodPos != null){
            conjureBlaze(level, e, c, blazeRodPos);
            return;
        }

        if(c.getPowerLevel(Powers.SOUL_POWER.get()) == 0)
            return;

        int cause = WorldSpecificValues.GOLEM_CAUSE.get();
        BlockPos candlePos = c.areaMemory.fetch(level, ConfigMan.COMMON.crucibleRange.get(), BlockTags.CANDLES);

        if (candlePos != null && level.getBlockState(candlePos).getValue(CandleBlock.LIT)) {
            conjureSpirit(level, e, c, cause, candlePos);
        }
    }

    // Either spread Sculk or change Vital to Soul using a Catalyst.
    private static void sculkMagic(CrucibleBlockEntity c, ItemEntity e) {
        if(!(c.getLevel() instanceof ServerLevel serverlevel))
            return;

        int spread = WorldSpecificValue.get("sculk_spread_amount", 12, 20);

        if(c.getPowerLevel(Powers.SOUL_POWER.get()) > 800){
            c.sculkSpreader.addCursors(c.getBlockPos().north(), spread);
            c.sculkSpreader.addCursors(c.getBlockPos().south(), spread);
            c.sculkSpreader.addCursors(c.getBlockPos().east(), spread);
            c.sculkSpreader.addCursors(c.getBlockPos().west(), spread);
            c.expendPower(Powers.SOUL_POWER.get(), 500);
        }else{
            if(c.getPowerLevel(Powers.VITAL_POWER.get()) > 100){
                c.expendPower(Powers.VITAL_POWER.get(), 100);
                c.addPower(Powers.SOUL_POWER.get(), WorldSpecificValue.get("sculk_soul_return", 60, 100));
            }
        }
    }


    private static void conjureBlaze(Level level, ItemEntity e, CrucibleBlockEntity c, BlockPos blazeRodPos) {
        c.addPower(Powers.BLAZE_POWER.get(), WorldSpecificValue.get("blaze_conjure_yield", 200, 400));
        EntityType.BLAZE.spawn((ServerLevel) level, (CompoundTag) null, null, blazeRodPos, MobSpawnType.MOB_SUMMONED, true, true);
        e.kill();
        ParticleScribe.drawParticleLine(level, ParticleTypes.FLAME,
                c.getBlockPos().getX() + 0.5, c.getBlockPos().getY() + 0.5125, c.getBlockPos().getZ() + 0.5,
                blazeRodPos.getX() + 0.5, blazeRodPos.getY() + 0.38, blazeRodPos.getZ() + 0.5, 20, 0.01);
        for(int i = 0; i < 10; i++) {
            ((ServerLevel) level).sendParticles(ParticleTypes.POOF,
                    blazeRodPos.getX() + 0.5, blazeRodPos.getY() + 0.38, blazeRodPos.getZ() + 0.5,
                    1, 0, 0, 0, 0.0);
        }
        level.playSound(null, blazeRodPos, SoundEvents.BLAZE_SHOOT, SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    private static void conjureSpirit(Level level, ItemEntity e, CrucibleBlockEntity c, int cause, BlockPos candlePos) {
        if (cause == 1) { // It's most likely that an Allay will spawn.
            if (level.random.nextFloat() > 0.07 && !(c.getPowerLevel(Powers.CURSE_POWER.get()) > 20)) {
                EntityType.ALLAY.spawn((ServerLevel) level, (CompoundTag) null, null, candlePos, MobSpawnType.MOB_SUMMONED, true, true);
                if(e.getOwner() instanceof ServerPlayer player)
                    CriteriaTriggers.SEE_ALLAY_SUMMON_TRIGGER.trigger(player);
            }
            else
                EntityType.VEX.spawn((ServerLevel) level, (CompoundTag) null, null, candlePos, MobSpawnType.MOB_SUMMONED, true, true);
        } else if (cause == 2) { // It's most likely that a Vex will spawn.
            if (level.random.nextFloat() > 0.07 && !(c.getPowerLevel(Powers.MIND_POWER.get()) > 20))
                EntityType.VEX.spawn((ServerLevel) level, (CompoundTag) null, null, candlePos, MobSpawnType.MOB_SUMMONED, true, true);
            else {
                EntityType.ALLAY.spawn((ServerLevel) level, (CompoundTag) null, null, candlePos, MobSpawnType.MOB_SUMMONED, true, true);
                if(e.getOwner()  instanceof ServerPlayer player)
                    CriteriaTriggers.SEE_ALLAY_SUMMON_TRIGGER.trigger(player);
            }
        }
        e.kill();
        ParticleScribe.drawParticleLine(level, ParticleTypes.ENCHANTED_HIT,
                c.getBlockPos().getX() + 0.5, c.getBlockPos().getY() + 0.5125, c.getBlockPos().getZ() + 0.5,
                candlePos.getX() + 0.5, candlePos.getY() + 0.38, candlePos.getZ() + 0.5, 20, 0.01);

        for(int i = 0; i < 10; i++) {
            ((ServerLevel) level).sendParticles(ParticleTypes.POOF,
                    candlePos.getX() + 0.5, candlePos.getY() + 0.38, candlePos.getZ() + 0.5,
                    1, 0, 0, 0, 0.0);
        }

        level.playSound(null, candlePos, SoundEvents.SOUL_ESCAPE, SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    // Dissolving an Ender Pearl teleports you onto the crucible if there's enough Warp.
    private static void enderPearlDissolve(Level l, BlockPos p, ItemEntity e, CrucibleBlockEntity c){
        float chance = ((float) c.getPowerLevel(Powers.WARP_POWER.get())) / CrucibleBlockEntity.CRUCIBLE_MAX_POWER;
        if(l.random.nextFloat() > chance){
            return;
        }

        for(int i = 0; i < 32; ++i) {
            ((ServerLevel) l).sendParticles(ParticleTypes.PORTAL, e.getX(), e.getY() + l.random.nextDouble() * 2.0, e.getZ(), 1, l.random.nextGaussian(), 0.0, l.random.nextGaussian(), 0.0);
        }

        boolean foundTarget = false;

        Entity thrower = e.getOwner();
        if(thrower != null) {
            Player player = l.getPlayerByUUID(thrower.getUUID());
            if(!l.isClientSide)
                CriteriaTriggers.ENDER_PEARL_DISSOLVE_TRIGGER.trigger((ServerPlayer) player);
            if(player != null && e.level().dimension().equals(player.level().dimension())){
                player.teleportTo(p.getX() + 0.5, p.getY() + 0.85, p.getZ() + 0.5);
                foundTarget = true;
            }
        }
        if(!foundTarget){
            FlagCriterion.triggerForNearbyPlayers((ServerLevel) l, CriteriaTriggers.MAKE_RIFT_TRIGGER, p, 20);
            c.enderRiftStrength = 2000;
        }
        e.kill();
    }

    // Attempts to teleport an entity with Crucible range of pos to the destination.
    public static boolean tryTeleportNearbyEntity(BlockPos pos, Level level, BlockPos destination, boolean can_teleport_players){
        AABB aoe = new AABB(pos);
        aoe = aoe.inflate(ConfigMan.COMMON.crucibleRange.get());
        List<LivingEntity> nearby_ents = Objects.requireNonNull(level).getEntitiesOfClass(LivingEntity.class, aoe);

        List<LivingEntity> to_be_excluded = new ArrayList<>();

        for(LivingEntity e : nearby_ents){
            if(ConfigMan.COMMON.doNotTeleport.get().contains(e.getEncodeId())){
                to_be_excluded.add(e);
            }
            if(e instanceof Player && !can_teleport_players){
                to_be_excluded.add(e);
            }
        }

        nearby_ents.removeAll(to_be_excluded);

        if(nearby_ents.isEmpty() || !CrystalIronItem.effectNotBlocked(nearby_ents.get(0), level.random.nextFloat() < 0.02 ? 1 : 0))
            return false;

        LivingEntity victim = nearby_ents.get(0);
        for(LivingEntity e : nearby_ents){
            if(victim == null || e.distanceToSqr(Vec3.atCenterOf(pos)) < victim.distanceToSqr(Vec3.atCenterOf(pos))){
                victim = e;
            }
        }

        victim.teleportTo(destination.getX() + 0.5, destination.getY() + 0.85, destination.getZ() + 0.5);
        return true;
    }

    // Make a Warp Bottle into a Rift Bottle.
    public static ItemStack makeRiftBottle(CrucibleBlockEntity c, ItemStack bottle){
        if(bottle.getTag() == null){
            bottle.setTag(new CompoundTag());
        }
        WarpBottleItem.addTeleportTags(Objects.requireNonNull(c.getLevel()).dimension(), c.getBlockPos(), bottle.getTag());
        c.enderRiftStrength = 0;
        return bottle;
    }

    // Explode gunpowder due to blaze.
    private static void explodeGunpowderDueToBlaze(Level l, BlockPos p, ItemEntity e){
        l.explode(e, p.getX()+0.5, p.getY()+0.5, p.getZ()+0.5, 1.0F, Level.ExplosionInteraction.NONE);
        e.kill();
    }

    // Putting a writable book in a crucible with Mind will change its contents.
    /*
    Specifically:
        - If Mind is low, it consumes words from the book to generate more Mind.
        - If Mind is medium, it transposes a few characters and does nothing.
        - If Mind is high, it fills the book with gibberish and consumes Mind.
     */
    private static void waterWriting(CrucibleBlockEntity c, ItemEntity e){
        int low = WorldSpecificValue.get("water_write_low_threshold", 200, 400);
        int high = WorldSpecificValue.get("water_write_high_threshold", 500, 800);
        if(c.getPowerLevel(Powers.MIND_POWER.get()) < low) {
            boolean harvested = lowWaterWriting(c, e, low);
            if(harvested){
                e.level().playSound(null, c.getBlockPos(), SoundEvents.BOOK_PAGE_TURN, SoundSource.BLOCKS, 1F, 1F);
                e.level().playSound(null, c.getBlockPos(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 0.6F, 0.6F);
            }
        }
        else if(c.getPowerLevel(Powers.MIND_POWER.get()) > high){
            highWaterWriting(c, e, high);
            e.level().playSound(null, c.getBlockPos(), SoundEvents.BOOK_PAGE_TURN, SoundSource.BLOCKS, 1F, 1F);
            e.level().playSound(null, c.getBlockPos(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 0.7F, 0.7F);
        }
    }
    private static void highWaterWriting(CrucibleBlockEntity c, ItemEntity e, int threshold) {
        String CHAR_LIST = "abcdefhijklmnopqstuvwxyz, -;6'";

        CompoundTag book_tag = e.getItem().getTag();
        if(book_tag == null) {
            e.getItem().addTagElement("pages", new ListTag());
            book_tag = e.getItem().getTag();
        }

        ListTag pages = book_tag.getList("pages", CompoundTag.TAG_STRING);
        for(int page_index = 0; page_index < pages.size(); page_index++){
            if(c.getPowerLevel(Powers.MIND_POWER.get()) < threshold)
                break;
            List<String> words = new ArrayList<>(List.of(pages.get(page_index).getAsString().split("\\s+")));
            if(words.size() == 0){
                if(e.getOwner() != null)
                    words.add(e.getOwner().getName().getString());
                else
                    words.add("turning");
            }
            if(e.level().random.nextFloat() < 0.3){
                // Add a chaos word.
                StringBuilder chaos = new StringBuilder();
                for(int i = 0; i < e.level().random.nextInt(3, 25); i++){
                    chaos.append(CHAR_LIST.charAt(e.level().random.nextInt(CHAR_LIST.length())));
                }
                words.add(e.level().random.nextInt(words.size()), chaos.toString());
            }else{
                // Copy an existing word.
                int index = e.level().random.nextInt(words.size());
                words.add(index, words.get(index));
            }

            String text = words.stream().reduce((s1, s2) -> s1 + " " + s2).get();
            if(text.length() > 250){
                text = text.substring(0, 250);
            }
            pages.set(page_index, StringTag.valueOf(text));
            c.expendPower(Powers.MIND_POWER.get(), WorldSpecificValue.get("water_write_cost", 10, 20));
        }
        c.setDirty();
    }

    private static void medWaterWriting(CrucibleBlockEntity c, ItemEntity e) {
        // Nothing at the moment.
    }

    private static boolean lowWaterWriting(CrucibleBlockEntity c, ItemEntity e, int threshold){
        CompoundTag book_tag = e.getItem().getTag();
        if(book_tag == null) {
            e.getItem().addTagElement("pages", new ListTag());
            book_tag = e.getItem().getTag();
        }
        boolean did_anything = false;
        ListTag pages = book_tag.getList("pages", CompoundTag.TAG_STRING);
        for(int page_index = 0; page_index < pages.size(); page_index++) {
            if(c.getPowerLevel(Powers.MIND_POWER.get()) > threshold)
                break;
            // Remove a random word from the page.
            List<String> words = new ArrayList<>(List.of(pages.get(page_index).getAsString().split("\\s+")));
            if(words.size() == 0)
                continue;
            did_anything = true;
            String victim = words.get(e.level().random.nextInt(words.size()));
            String blank = " ".repeat(victim.length());
            pages.set(page_index, StringTag.valueOf(pages.get(page_index).getAsString().replace(victim, blank)));
            c.addPower(Powers.MIND_POWER.get(), WorldSpecificValue.get("water_write_cost", 10, 20) - 1);
        }
        c.setDirty();
        return did_anything;
    }

    // Phantom residue + verdant = summon a slime.
    private static void residualSlime(CrucibleBlockEntity c, ItemEntity e) {
        c.expendPower(Powers.VERDANT_POWER.get(), 400);
        c.setDirty();
        if(e.getItem().getCount() == 1)
            e.kill();
        else
            e.getItem().shrink(1);
        Slime slime = new Slime(EntityType.SLIME, Objects.requireNonNull(c.getLevel()));
        slime.setPos(Vec3.atCenterOf(c.getBlockPos()).add(0, 0.1, 0));
        slime.setSize(1, true);
        c.getLevel().addFreshEntity(slime);
    }

    // Throwing a Motion Salt Block into an electrified crucible displaces a nearby block.
    private static void displaceNearby(CrucibleBlockEntity c, ItemEntity e) {
        e.kill();
        Optional<BlockPos> target = BlockPos.findClosestMatch(c.getBlockPos(), ConfigMan.COMMON.crucibleRange.get(), ConfigMan.COMMON.crucibleRange.get(),
                blockPos -> {
                    BlockState state = Objects.requireNonNull(c.getLevel()).getBlockState(blockPos);
                    return !blockPos.equals(c.getBlockPos()) && !state.isAir() && !state.is(Registration.VOLT_CELL.get());
                });
        if(target.isPresent()){
            DisplacedBlock.displace(c.getLevel().getBlockState(target.get()), target.get(), c.getLevel(), 200);
            for(int i = 0; i < 2; i++)
                ParticleScribe.drawParticleZigZag(c.getLevel(), ParticleTypes.ELECTRIC_SPARK, c.getBlockPos(), target.get(),
                        5, 8, 0.9F);
        }
    }

    private static void soulEscape(CrucibleBlockEntity c){
        if(c.getLevel() == null) return;
        if(c.getLevel().isClientSide()){
            c.getLevel().addParticle(ParticleTypes.SOUL, c.getBlockPos().getX() + 0.5, c.getBlockPos().getY() + 0.65,
                    c.getBlockPos().getZ() + 0.5, 0, 0, 0);
        }else{
            ((ServerLevel) c.getLevel()).sendParticles(ParticleTypes.SOUL, c.getBlockPos().getX() + 0.5, c.getBlockPos().getY() + 0.65,
                    c.getBlockPos().getZ() + 0.5, 1,0, 0, 0, 0.0);
        }
    }

    private static void curseEscape(CrucibleBlockEntity c){
        if(c.getLevel() == null) return;
        AABB aoe = new AABB(c.getBlockPos());
        aoe = aoe.inflate(5); // Inflate the AOE to be 5x the size of the crucible.
        if(!c.getLevel().isClientSide()){
            if(c.getPowerLevel(Powers.CURSE_POWER.get()) > 1400){
                Monster m;
                if(c.getLevel().getRandom().nextFloat() < 0.35){
                    m = new Skeleton(EntityType.SKELETON, c.getLevel());
                }else{
                   m = new Zombie(EntityType.ZOMBIE, c.getLevel());
                }
                m.setSilent(true);
                m.setPos(aoe.getCenter().add(WorldSpecificValue.get("monster_summon_x", -5, 5), 1, WorldSpecificValue.get("monster_summon_z", -5, 5)));
                c.getLevel().addFreshEntity(m);
            }
            List<LivingEntity> nearby_ents = c.getLevel().getEntitiesOfClass(LivingEntity.class, aoe);
            for(LivingEntity e : nearby_ents){
                if(e.getMobType().equals(MobType.UNDEAD))
                    continue;
                e.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 300, 0));
                e.addEffect(new MobEffectInstance(MobEffects.WITHER, 200, 0));
                e.hurt(e.level().damageSources().magic(), 10);
                if(e instanceof Player){
                    CriteriaTriggers.BE_CURSED_TRIGGER.trigger((ServerPlayer) e);
                }
            }
            c.getLevel().playSound(null, c.getBlockPos(), SoundEvents.AMBIENT_CAVE.get(), SoundSource.BLOCKS, 1, 1);
        }
    }

    private static void blazeEscape(CrucibleBlockEntity c){
        if(c.getLevel() == null) return;
        AABB blast_zone = new AABB(c.getBlockPos());
        blast_zone.inflate(1.5, 3, 1.5);
        if(!c.getLevel().isClientSide()){
            List<LivingEntity> nearby_ents = c.getLevel().getEntitiesOfClass(LivingEntity.class, blast_zone);
            for(LivingEntity e : nearby_ents){
                e.hurt(e.level().damageSources().inFire(), 12);
                e.setSecondsOnFire(3);
            }
            c.getLevel().playSound(null, c.getBlockPos(), SoundEvents.BLAZE_SHOOT, SoundSource.BLOCKS, 1.0F, 1.0F);
            for(int i = 0; i < 10; i++) {
                if(c.getPowerLevel(Powers.SOUL_POWER.get()) > 20){
                    ParticleScribe.drawParticleCrucibleTop(c.getLevel(), ParticleTypes.SOUL_FIRE_FLAME, c.getBlockPos(), 1, 0, 1, 0);
                }else{
                    ParticleScribe.drawParticleCrucibleTop(c.getLevel(), ParticleTypes.FLAME, c.getBlockPos(), 1, 0, 1, 0);
                }
            }
        }
    }

    private static void verdantEscape(CrucibleBlockEntity c) {
        if(c.getLevel() == null || c.getLevel().isClientSide || WorldSpecificValue.getBool("no_moss", 0.5F))
            return;
        ((MossBlock) Blocks.MOSS_BLOCK).performBonemeal((ServerLevel) c.getLevel(), c.getLevel().random, c.getBlockPos().below(), c.getBlockState());
    }

    private static void lightEscape(CrucibleBlockEntity c) {
        if(c.getLevel() == null || c.getLevel().isClientSide || !c.getLevel().getBlockState(c.getBlockPos().above()).isAir())
            return;
        c.getLevel().setBlock(c.getBlockPos().above(), Registration.GLOWING_AIR.get().defaultBlockState(), Block.UPDATE_CLIENTS);
    }

    public static void solidifyPortal(Level l, BlockPos p, Direction.Axis axis){
        HyperPortalShape portal = new HyperPortalShape(l, p, axis);
        if(portal.isComplete()){
            portal.createSolidPortalBlocks();
            if(!l.isClientSide)
                FlagCriterion.triggerForNearbyPlayers((ServerLevel) l, CriteriaTriggers.PORTAL_FREEZE_TRIGGER, p, 9);
        }
    }

    public static void staffCraftStep(CrucibleBlockEntity c, BlockPos staff_pos){
        for(Power p : c.getPowerMap().keySet()){
            if(c.getPowerLevel(p) > 800){
                IncompleteStaffBlock.tryMakeProgress(Objects.requireNonNull(c.getLevel()), c.getLevel().getBlockState(staff_pos), staff_pos, p);
            }
        }
    }

    // Prevent anything from jumping when Immobilized.
    @SubscribeEvent(priority= EventPriority.LOWEST)
    public static void onJump(LivingEvent.LivingJumpEvent event) {
        if(event.getEntity().hasEffect(Registration.IMMOBILE.get())){
            event.getEntity().setJumping(false);
            event.getEntity().setDeltaMovement(0, 0, 0);
        }
    }
}
