package com.hyperlynx.reactive.be;

import com.hyperlynx.reactive.ReactiveMod;
import com.hyperlynx.reactive.Registration;
import com.hyperlynx.reactive.advancements.CriteriaTriggers;
import com.hyperlynx.reactive.advancements.FlagCriterion;
import com.hyperlynx.reactive.alchemy.*;
import com.hyperlynx.reactive.alchemy.rxn.Reaction;
import com.hyperlynx.reactive.blocks.CrucibleBlock;
import com.hyperlynx.reactive.fx.ParticleScribe;
import com.hyperlynx.reactive.recipes.DissolveRecipe;
import com.hyperlynx.reactive.recipes.PrecipitateRecipe;
import com.hyperlynx.reactive.recipes.TransmuteRecipe;
import com.hyperlynx.reactive.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.*;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ConduitBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/*
    The heart of the whole mod, the Crucible's Block Entity.
    This is a complicated class, but each method should be pretty self-explanatory, or documented if not.
    Overall, the crucible does these things every (configurable, default 30) ticks:
        - Check the block state to see if it should empty itself.
        - Check to see if there are new item entities.
            - If there are, check if they need to have any recipes applied, and do that if there are.
            - Otherwise, just dissolve them and add their Power to the pool.
        - Use ReactionMan to run reactions.
        - Check for special cases along the way.
 */

@Mod.EventBusSubscriber
public class CrucibleBlockEntity extends BlockEntity implements PowerBearer {
    public static final int CRUCIBLE_MAX_POWER = 1600; // The maximum power the Crucible can hold.
    // Don't change this without updating the recipes.

    private final HashMap<Power, Integer> powers = new HashMap<>(); // A map of Powers to their amounts.
    public AreaMemory areaMemory; // Used to check for nearby blocks of interest.

    private int tick_counter = 0; // Used for counting active ticks. See tick().
    private int process_stage = 0; // Used for sequential processing. See tick().
    private int gather_stage = 0; // Used for sequential processing. See gatherPower().

    private final Color mix_color = new Color(); // Used to cache mixture color between updates;
    public boolean color_changed = true; // This is set to true when the color needs to be updated next rendering tick.
    private final Color next_mix_color = new Color(); // Used to smoothly change mix_color.
    private boolean color_initialized = false; // This is set to true when mix_color is

    public int electricCharge = 0; // Used for the ELECTRIC Reaction Stimulus. Set by nearby Volt Cells and lightning.
    public int sacrificeCount = 0; // Used for the SACRIFICE Reaction Stimulus.
    public int enderRiftStrength = 0; // Used for the Ender Pearl Dissolve feature.

    public EndCrystal linked_crystal = null; // Used for the END_CRYSTAL Reaction Stimulus.
    public boolean used_crystal_this_cycle = false; // True if the linked crystal powered a reaction this tick. If not, break the link.

    public CrucibleBlockEntity(BlockPos pos, BlockState state) {
        super(Registration.CRUCIBLE_BE_TYPE.get(), pos, state);
        MinecraftForge.EVENT_BUS.register(this);
        areaMemory = new AreaMemory(pos);
    }

    // ----- Tick and related worker methods -----
    public static void tick(Level level, BlockPos pos, BlockState state, CrucibleBlockEntity crucible) {
        crucible.tick_counter++;

        // Each tick, deal with ender rift if needed.
        if(!level.isClientSide && crucible.enderRiftStrength > 0){
            crucible.enderRiftStrength = SpecialCaseMan.tryTeleportNearbyEntity(crucible.getBlockPos(), crucible.getLevel(), crucible.getBlockPos(), true) ? 0 : crucible.enderRiftStrength-1;
            ((ServerLevel) level).sendParticles(ParticleTypes.PORTAL, pos.getX() + 0.5, pos.getY() + 0.5625 + level.random.nextDouble() * 2.0, pos.getZ()+ 0.5, 1, level.random.nextGaussian(), 0.0, level.random.nextGaussian(), 0.0);
        }

        // Perform the main Crucible Tick every (tick delay) ticks.
        if(crucible.tick_counter >= ConfigMan.COMMON.crucibleTickDelay.get()) {
            crucible.tick_counter = 1;

            // Become empty when there's no water.
            if (!state.getValue(CrucibleBlock.FULL)) {
                empty(level, pos, state, crucible);
            }

            switch (crucible.process_stage){
                case 0 -> {
                    // Deal with electricity.
                    if (level.getBlockState(pos.below()).is(Registration.VOLT_CELL.get()) && crucible.electricCharge < 15) {
                        crucible.electricCharge = 15;
                    } else if (crucible.electricCharge > 0) {
                        crucible.electricCharge--;
                    }

                    // Check for Effusive Sponges and fill if there is one.
                    if (!level.isClientSide() && !state.getValue(CrucibleBlock.FULL)) {
                        if (crucible.areaMemory.existsAbove(crucible.level, ConfigMan.COMMON.crucibleRange.get(), Registration.WARP_SPONGE.get())) {
                            crucible.getLevel().setBlock(crucible.getBlockPos(), level.getBlockState(crucible.getBlockPos()).setValue(CrucibleBlock.FULL, true), Block.UPDATE_CLIENTS);
                            level.playSound(null, pos, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 0.6F, 1F);
                        }
                    }
                }

                case 1 -> {
                    // Gather energy from the surroundings.
                    if(!level.isClientSide() && state.getValue(CrucibleBlock.FULL)){
                        gatherPower(level, crucible);
                    }
                }

                case 2 -> {
                    // Process items inside the Crucible
                    if(!level.isClientSide() && state.getValue(CrucibleBlock.FULL)){
                        if (processItemsInside(level, pos, state, crucible)) {
                            level.playSound(null, pos, SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 1F, 0.65F+(level.getRandom().nextFloat()/5));
                        }
                    }
                }

                case 3 -> {
                    // Perform applicable reactions.
                    if (!level.isClientSide() && state.getValue(CrucibleBlock.FULL)) {
                        react(level, crucible);
                    }
                }

                case 4 -> {
                    // Synchronize the client and server.
                    crucible.setDirty();
                    crucible.process_stage = -1;
                }

                default -> System.err.println("Crucible ran out of steps! This can't be!");
            }
            crucible.process_stage++;
        }
    }

    public static void empty(Level level, BlockPos pos, BlockState state, CrucibleBlockEntity crucible) {
        if(crucible.getTotalPowerLevel() > 0) {
            SpecialCaseMan.checkEmptySpecialCases(crucible);
            crucible.expendPower();
            crucible.resetColor();
            crucible.setDirty(level, pos, state);
            level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.6F, 1F);
        }
        if(crucible.linked_crystal != null) {
            crucible.unlinkCrystal(level, pos, state);
        }
    }

    // Only call this method when linked_crystal isn't null please and thank you.
    private void unlinkCrystal(Level level, BlockPos pos, BlockState state) {
        linked_crystal.setBeamTarget(null);
        linked_crystal = null;
        setDirty(level, pos, state);
    }

    private static void gatherPower(Level level, CrucibleBlockEntity crucible){
        // Only gather power if a Copper Symbol is nearby, but not an Iron one.
        if(crucible.areaMemory.exists(level, ConfigMan.COMMON.crucibleRange.get(), Registration.COPPER_SYMBOL.get()) && !crucible.areaMemory.exists(level, 3, Registration.IRON_SYMBOL.get())){
            switch(crucible.gather_stage){
                case 0 -> {
                    // Nether portals remove Powers, unless you surpass the concentration, in which case it solidifies the portal.
                    if(crucible.areaMemory.exists(level, ConfigMan.COMMON.crucibleRange.get(), Blocks.NETHER_PORTAL) && crucible.getTotalPowerLevel() > 400){
                        if (crucible.getPowerLevel(Powers.MIND_POWER.get()) > 1300) {
                            BlockPos portal_pos = crucible.areaMemory.fetch(crucible.level, ConfigMan.COMMON.crucibleRange.get(), Blocks.NETHER_PORTAL);
                            SpecialCaseMan.solidifyPortal(crucible.level, portal_pos, crucible.level.getBlockState(portal_pos).getValue(NetherPortalBlock.AXIS));
                            crucible.level.playSound(null, portal_pos, SoundEvents.ZOMBIE_VILLAGER_CURE, SoundSource.BLOCKS, 1.0F, 1.0F);
                        }

                        crucible.expendAnyPowerExcept(null, 400);
                        FlagCriterion.triggerForNearbyPlayers((ServerLevel) level, CriteriaTriggers.PORTAL_TRADE_TRIGGER, crucible.getBlockPos(), ConfigMan.COMMON.crucibleRange.get());
                    }
                }

                case 1 -> {
                    // Blaze Rods add blaze.
                    if(crucible.areaMemory.exists(level, ConfigMan.COMMON.crucibleRange.get(), Registration.BLAZE_ROD.get())){
                        crucible.addPower(Powers.BLAZE_POWER.get(), WorldSpecificValue.get("blaze_rod_power_amount", 20, 50));
                    }
                }

                case 2 -> {
                    // End Rods add light.
                    if(crucible.areaMemory.exists(level, ConfigMan.COMMON.crucibleRange.get(), Blocks.END_ROD)){
                        crucible.addPower(Powers.LIGHT_POWER.get(), WorldSpecificValue.get("end_rod_power_amount", 30, 100));
                    }
                }

                case 3 -> {
                    // Occult Symbols and Wither Skeleton Skulls add curse.
                    if(crucible.areaMemory.exists(level, ConfigMan.COMMON.crucibleRange.get(), Registration.OCCULT_SYMBOL.get()) || crucible.areaMemory.exists(level, ConfigMan.COMMON.crucibleRange.get(), Blocks.WITHER_SKELETON_SKULL) || crucible.areaMemory.exists(level, ConfigMan.COMMON.crucibleRange.get(), Blocks.WITHER_SKELETON_WALL_SKULL)){
                        crucible.addPower(Powers.CURSE_POWER.get(), WorldSpecificValue.get("wither_skull_power_amount", 50, 400));
                    }
                }

                case 4 -> {
                    // Active conduits give add either soul or warp, depending on the world.
                    if(crucible.areaMemory.exists(level, ConfigMan.COMMON.crucibleRange.get(), Blocks.CONDUIT)){
                        Optional<ConduitBlockEntity> maybe_conduit = level.getBlockEntity(crucible.areaMemory.fetch(level, ConfigMan.COMMON.crucibleRange.get(), Blocks.CONDUIT), BlockEntityType.CONDUIT);
                        if(maybe_conduit.isPresent() && maybe_conduit.get().isActive()){
                            if(WorldSpecificValues.CONDUIT_POWER.get() == 1){
                                crucible.addPower(Powers.SOUL_POWER.get(), WorldSpecificValue.get("conduit_power_amount", 120, 140));
                            }else{
                                crucible.addPower(Powers.WARP_POWER.get(), WorldSpecificValue.get("conduit_power_amount", 120, 140));
                            }
                        }
                    }
                }

                case 5 -> {
                    // The Rift effect slowly generates Warp.
                    if(crucible.enderRiftStrength > 0){
                        crucible.addPower(Powers.WARP_POWER.get(), 10);
                    }
                    crucible.gather_stage = -1;
                }

            }

            crucible.gather_stage++;
        }

        // Slowly dilute powers in the rain.
        if(level.canSeeSky(crucible.getBlockPos()) && level.isRainingAt(crucible.getBlockPos())){
            crucible.expendAnyPowerExcept(Powers.CURSE_POWER.get(), 80);
        }
    }

    // The method that performs reactions.
    private static void react(Level level, CrucibleBlockEntity crucible){
        crucible.used_crystal_this_cycle = false;
        for(Reaction r : ReactiveMod.REACTION_MAN.getReactions()){
            if (r.conditionsMet(crucible)) {
                r.run(crucible);
                crucible.setDirty();
            }

        }
        if(!crucible.used_crystal_this_cycle && crucible.linked_crystal != null)
            crucible.unlinkCrystal(level, crucible.getBlockPos(), crucible.getBlockState());
    }

    // Used to gather and operate on items sitting inside the crucible.
    private static boolean processItemsInside(Level level, BlockPos pos, BlockState state, CrucibleBlockEntity crucible){
        if(!state.getValue(CrucibleBlock.FULL)){
            return false;
        }
        boolean changed = false;
        for(Entity entity_inside : CrucibleBlock.getEntitesInside(pos, level)){
            if(entity_inside instanceof ItemEntity){
                SpecialCaseMan.checkDissolveSpecialCases(crucible, (ItemEntity) entity_inside);
                // The special case may have removed the item entity; continue to the next if it has died.
                if(!entity_inside.isAlive()) continue;

                changed = changed || tryTransmute(level, pos, state, crucible, ((ItemEntity) entity_inside));
                changed = changed || tryReduceToPower(((ItemEntity) entity_inside).getItem(), crucible);

                // Remove entities that were completely transmuted or dissolved.
                if(((ItemEntity) entity_inside).getItem().getCount() == 0){
                    entity_inside.remove(Entity.RemovalReason.KILLED);
                }
            }
        }
        changed = changed || tryPrecipitate(level, pos, state, crucible);

        return changed;
    }

    // Attempts to 'dissolve' the item into Power. If it does, the power is added to the Crucible, and it returns true.
    public static boolean tryReduceToPower(ItemStack stack, CrucibleBlockEntity crucible){
        List<Power> stack_power_list = Power.getSourcePower(stack);
        boolean changed = false;
        if(stack_power_list.isEmpty()){
            boolean dissolved = tryDissolveWithByproduct(Objects.requireNonNull(crucible.getLevel()), crucible.getBlockPos(), stack, stack.getCount());
            if(dissolved)
                stack.setCount(0);
            return false;
        }
        for (Power p : stack_power_list) {
            int dissolve_capacity = (CrucibleBlockEntity.CRUCIBLE_MAX_POWER - crucible.getPowerLevel(p)) / Power.getSourceLevel(stack);
            if(dissolve_capacity <= 0){
                continue;
            }
            changed = changed || crucible.addPower(p, stack.getCount() * Power.getSourceLevel(stack) / stack_power_list.size());
            tryDissolveWithByproduct(Objects.requireNonNull(crucible.getLevel()), crucible.getBlockPos(), stack, Math.min(stack.getCount(), dissolve_capacity));
            stack.setCount(Math.max(stack.getCount()-dissolve_capacity, 0));
        }
        return changed;
    }

    // Attempts to find a matching Dissolve recipe, and if it does, adds the output as a new item entity.
    private static boolean tryDissolveWithByproduct(Level level, BlockPos pos, ItemStack stack, int count){
        List<DissolveRecipe> purify_recipes = level.getRecipeManager().getAllRecipesFor(Registration.DISSOLVE_RECIPE_TYPE.get());
        for (DissolveRecipe r : purify_recipes) {
            if(r.matches(new FakeContainer(stack), level)){
                ItemStack reactant = stack.copy();
                reactant.setCount(count);
                level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5, pos.getY()+0.6, pos.getZ() + 0.5, r.assemble(new FakeContainer(reactant))));
                return true;
            }
        }
        return false;
    }

    // Attempts to find a transmutation recipe that matches, and if it does, adds the output as a new item entity and returns true.
    private static boolean tryTransmute(Level level, BlockPos pos, BlockState state, CrucibleBlockEntity crucible, ItemEntity itemEntity) {
        List<TransmuteRecipe> purify_recipes = level.getRecipeManager().getAllRecipesFor(Registration.TRANS_RECIPE_TYPE.get());
        for (TransmuteRecipe r : purify_recipes) {
            if (r.matches(new FakeContainer(itemEntity.getItem()), level)) {
                if (r.powerMet(crucible, level)) {
                    ItemStack result = r.apply(itemEntity.getItem(), crucible, level);
                    level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5, pos.getY()+0.6, pos.getZ() + 0.5, result));
                    crucible.setDirty(level, pos, state);
                    return true;
                }
            }
        }
        return false;
    }

    // Attempts to find a precipitation recipe that matches, and if it does, adds the output as a new item entity and returns true.
    private static boolean tryPrecipitate(Level level, BlockPos pos, BlockState state, CrucibleBlockEntity crucible) {
        List<PrecipitateRecipe> creation_recipes = level.getRecipeManager().getAllRecipesFor(Registration.PRECIPITATE_RECIPE_TYPE.get());
        for(PrecipitateRecipe r : creation_recipes){
            if(r.powerMet(crucible, level)){
                ItemStack result = r.apply(crucible, level);
                level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, result));
                crucible.setDirty(level, pos, state);
                return true;
            }
        }
        return false;
    }

        // ----- Helper and power management methods -----

    public void setDirty(){
        setDirty(Objects.requireNonNull(this.getLevel()), this.getBlockPos(), this.getBlockState());
    }

    public void beHitByLightning(){
        electricCharge = 50;
        setDirty();
    }

    // Deals with the sacrifice mechanic. Sacrifices add to the sacrifice counter and contribute Power.
    @SubscribeEvent
    public void onDeath(LivingDeathEvent event) {
        if(this.getLevel() == null || !this.getBlockState().getValue(CrucibleBlock.FULL) || event.getEntity().level.isClientSide || Objects.requireNonNull(this.getLevel()).isClientSide) {
            return;
        }

        double dist = BeamHelper.distance(event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ(), this.getBlockPos().getX(), this.getBlockPos().getY(), this.getBlockPos().getZ());
        if(dist > ConfigMan.COMMON.crucibleRange.get() || areaMemory.exists(event.getEntity().level, ConfigMan.COMMON.crucibleRange.get(), Registration.IRON_SYMBOL.get())) {
            return;
        }

        if(event.getEntity().getMobType().equals(MobType.UNDEAD)){
            addPower(Powers.CURSE_POWER.get(), WorldSpecificValue.get("undead_curse_strength", 30, 300));
            return;
        }

        sacrificeCount++;
        FlagCriterion.triggerForNearbyPlayers((ServerLevel) event.getEntity().level, CriteriaTriggers.SEE_SACRIFICE_TRIGGER, getBlockPos(), 8);

        double x = event.getEntity().getX();
        double y = event.getEntity().getY();
        double z = event.getEntity().getZ();

        // While Mind is being devoured by Curse, sacrifices spawn Phantoms.
        if(getPowerLevel(Powers.CURSE_POWER.get()) >= WorldSpecificValues.CURSE_RATE.get() && getPowerLevel(Powers.MIND_POWER.get()) > 0
        && !(event.getEntity() instanceof Phantom)
        && (event.getEntity().getLevel().isNight())){
            spawnPhantom(x, y, z);
        }else{
            ParticleScribe.drawParticleLine(level, ParticleTypes.CLOUD,
                    getBlockPos().getX() + 0.5, getBlockPos().getY() + 0.4, getBlockPos().getZ() + 0.5,
                    x, y, z, 15, 0.3);
        }

        // Add Vital due to sacrifices.
        int power;
        int best_sacrifice_type = WorldSpecificValues.BEST_SACRIFICE.get();
        if (best_sacrifice_type == 1 && event.getEntity() instanceof Animal) {
            power = WorldSpecificValue.get("strong_sacrifice", 300, 600);
        } else if (best_sacrifice_type == 2 && event.getEntity() instanceof AbstractVillager) {
            power = WorldSpecificValue.get("strong_sacrifice", 300, 600);
        } else if (best_sacrifice_type == 3 && (event.getEntity() instanceof AbstractPiglin || event.getEntity() instanceof Hoglin)) {
            power = WorldSpecificValue.get("strong_sacrifice", 300, 600);
        } else if (best_sacrifice_type == 4 && event.getEntity() instanceof Monster) {
            power = WorldSpecificValue.get("strong_sacrifice", 300, 600);
        } else {
            power = WorldSpecificValue.get("weak_sacrifice", 30, 60);
        }
        addPower(Powers.VITAL_POWER.get(), power);
        setDirty();
    }

    private void spawnPhantom(double x, double y, double z) {
        Phantom p = new Phantom(EntityType.PHANTOM, Objects.requireNonNull(getLevel()));
        p.setPos(new Vec3(x, y +2, z));
        p.setPhantomSize(this.getLevel().random.nextInt(1, 4));
        getLevel().addFreshEntity(p);
        ParticleScribe.drawParticleLine(level, ParticleTypes.SMOKE, x, y, z, x, y +2, z, 25, 0.1);
    }

    @Override
    public @NotNull Map<Power, Integer> getPowerMap() {
        return powers;
    }

    // Causes a block update, which should force the client to synchronize the block entity data with the server.
    public void setDirty(Level level, BlockPos pos, BlockState state){
        if(!level.isClientSide) {
            this.setChanged();
            level.sendBlockUpdated(pos, state, state, Block.UPDATE_CLIENTS);
        }
    }

    // These methods manage power in the Crucible. They might be extracted to an interface later.

    @Override
    public boolean addPower(Power p, int amount) {
        if(p == null){
            return false;
        }
        if(getPowerLevel(p) == CRUCIBLE_MAX_POWER){
            return false;
        }
        if(getTotalPowerLevel() + amount > CRUCIBLE_MAX_POWER) {
            int excess = getTotalPowerLevel() + amount - CRUCIBLE_MAX_POWER;
            expendAnyPowerExcept(p, excess); // Replace other powers if needed.
            excess = getTotalPowerLevel() + amount - CRUCIBLE_MAX_POWER;
            if(excess > 0) {
                amount -= excess;
            }
        }

        int prev = powers.getOrDefault(p, 0);
        if(prev > 0)
            powers.replace(p, amount + prev);
        else
            powers.put(p, amount);

//        if(this.getLevel() != null && !this.getLevel().isClientSide)
//            System.out.println("Tried to add " + amount + " " + p.getName() + ".");

        return true;
    }

    @Override
    public int getPowerLevel(Power t) {
        if(powers.isEmpty() || powers.get(t) == null){
            return 0;
        }
        return powers.get(t);
    }

    @Override
    public boolean expendPower(Power t, int amount) {
        if(powers.isEmpty() || !powers.containsKey(t)){
            return false;
        }
        int level = powers.get(t);
        if(level > amount){
            powers.put(t, level-amount);
            return true;
        }
        if (level == amount) {
            powers.put(t, 0);
            return true;
        }

        // This implies that all power t wasn't enough to meet amount.
        powers.put(t, 0);
        return false;
    }


    public void expendAnyPowerExcept(Power immune_power, int amount) {
        boolean expended = false;
        for(Power p : powers.keySet()){
            if(p != immune_power && p != Powers.CURSE_POWER.get()){
                expended = expendPower(p, amount);
            }
            if(expended) return;
        }
    }

    public void expendPower() {
        powers.clear();
        color_changed = true;
        mix_color.reset();
        next_mix_color.reset();
        color_initialized = false;
    }

    public int getTotalPowerLevel(){
        int totalpp = 0;
        if(powers == null) return 0;
        for (Power p : powers.keySet()) {
            totalpp += powers.get(p);
        }
        return totalpp;
    }

    // These methods calculate and return the combined color of the cauldron's mixture
    @Override
    public Color getCombinedColor(int water_color_number) {
        Color water_color = new Color(water_color_number);
        if(powers == null || powers.isEmpty() || getTotalPowerLevel() == 0){
            return water_color;
        }
        if(color_changed){
            updateColor(water_color);
        }
        if(!color_initialized){
            mix_color.red = water_color.red;
            mix_color.green = water_color.green;
            mix_color.blue = water_color.blue;
            color_initialized = true;
        }
        if(!mix_color.equals(next_mix_color)){ // Smoothly change the mix color to match the new color.
            int delta_red = Math.min(Math.abs(next_mix_color.red - mix_color.red), 2);
            int delta_green = Math.min(Math.abs(next_mix_color.green - mix_color.green), 2);
            int delta_blue = Math.min(Math.abs(next_mix_color.blue - mix_color.blue), 2);
            mix_color.red = next_mix_color.red > mix_color.red ? mix_color.red + delta_red : mix_color.red - delta_red;
            mix_color.green = next_mix_color.green > mix_color.green ? mix_color.green + delta_green : mix_color.green - delta_green;
            mix_color.blue = next_mix_color.blue > mix_color.blue ? mix_color.blue + delta_blue : mix_color.blue - delta_blue;
        }
        return mix_color;
    }

    private void updateColor(Color water_color){
        if(powers == null){
            return;
        }
        // Iterate through each power and add its tint to the total, adjusted for its actual prevalence.
        next_mix_color.reset();
        for (Power p : powers.keySet()) {
            if(p == null){
                continue; // Skip any invalid values if they exist.
            }
            Color pow_color = p.getColor();
            float pow_weight = getPowerLevel(p) / (float) getTotalPowerLevel();
            next_mix_color.red += pow_color.red * pow_weight;
            next_mix_color.green += pow_color.green * pow_weight;
            next_mix_color.blue += pow_color.blue * pow_weight;
        }

        // Adjust the tint to be proportional to the amount of the crucible's maximum currently in use.
        float tint_alpha = (float) getTotalPowerLevel()/ (float) CRUCIBLE_MAX_POWER;
        next_mix_color.red = (int) (water_color.red * (1 - tint_alpha) + next_mix_color.red * (tint_alpha));
        next_mix_color.green = (int) (water_color.green * (1 - tint_alpha) + next_mix_color.green * (tint_alpha));
        next_mix_color.blue = (int) (water_color.blue * (1 - tint_alpha) + next_mix_color.blue * (tint_alpha));
        color_changed = false;
    }

    private void resetColor() {
        color_changed = true;
        color_initialized = false;
        mix_color.reset();
        next_mix_color.reset();
    }

    public float getOpacity() {
        return 0.7F + (.3F * getTotalPowerLevel()/CRUCIBLE_MAX_POWER);
    }

    // ----- Data management methods -----

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag);
        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        super.onDataPacket(net, pkt);
        color_changed = true;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag main_tag) {
        super.saveAdditional(main_tag);
        main_tag.put("electric_charge", IntTag.valueOf(electricCharge));
        if(linked_crystal != null)
            main_tag.put("LinkedCrystal", IntTag.valueOf(linked_crystal.getId()));

        // Powers must be saved last.
        if(powers == null || powers.isEmpty()){
            return;
        }
        ListTag power_list_tag = new ListTag();
        for (Power p : powers.keySet()) {
            if(p == null) {
                System.err.println("Skipping null power in save process.");
                continue; // Purge bad nulls.
            }
            CompoundTag tag = new CompoundTag();
            tag.put("name", StringTag.valueOf(p.getId()));
            tag.put("level", IntTag.valueOf(getPowerLevel(p)));
            power_list_tag.add(tag);
        }
        main_tag.put("powers", power_list_tag);
    }

    @Override
    public void load(@NotNull CompoundTag main_tag) {
        super.load(main_tag);
        if(main_tag.contains("LinkedCrystal")){
            int crystal_id = main_tag.getInt("LinkedCrystal");
            if(level.getEntity(crystal_id) instanceof EndCrystal)
                linked_crystal = (EndCrystal) level.getEntity(crystal_id);
        }else{
            linked_crystal = null;
        }
        // Powers tag is guaranteed to be a list.
        ListTag power_list_tag = (ListTag) main_tag.get("powers");
        powers.clear();
        if(power_list_tag != null && !power_list_tag.isEmpty()) {
            for (Tag power_tag : power_list_tag) {
                Power p = Power.readPower((CompoundTag) power_tag);
                addPower(p, ((CompoundTag) power_tag).getInt("level"));
            }
        }
        electricCharge = main_tag.getInt("electric_charge");
    }

}
