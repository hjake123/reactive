package com.hyperlynx.reactive.be;

import com.hyperlynx.reactive.ReactiveMod;
import com.hyperlynx.reactive.Registration;
import com.hyperlynx.reactive.alchemy.*;
import com.hyperlynx.reactive.alchemy.rxn.Reaction;
import com.hyperlynx.reactive.blocks.CrucibleBlock;
import com.hyperlynx.reactive.recipes.DissolveRecipe;
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

    private final HashMap<Power, Integer> powers = new HashMap<>(); // A map of Powers to their amounts.
    public AreaMemory areaMemory; // Used to check for nearby blocks of interest.

    private int tick_counter = 0; // Used for counting active ticks. See tick().
    private final Color mix_color = new Color(); // Used to cache mixture color between updates;
    public boolean color_changed = true; // This is set to true when the color needs to be updated next rendering tick.
    private final Color next_mix_color = new Color(); // Used to smoothly change mix_color.
    private boolean color_initialized = false; // This is set to true when mix_color is


    public int electricCharge = 0; // Used for the ELECTRIC Reaction Stimulus. Set by nearby Volt Cells and lightning.
    public int sacrificeCount = 0; // Used for the SACRIFICE Reaction Stimulus.

    public CrucibleBlockEntity(BlockPos pos, BlockState state) {
        super(Registration.CRUCIBLE_BE_TYPE.get(), pos, state);
        MinecraftForge.EVENT_BUS.register(this);
        areaMemory = new AreaMemory(pos);
    }

    // ----- Tick and related worker methods -----
    public static void tick(Level level, BlockPos pos, BlockState state, CrucibleBlockEntity crucible) {
        crucible.tick_counter++;
        if(crucible.tick_counter >= ConfigMan.COMMON.crucibleTickDelay.get()) {
            crucible.tick_counter = 1;
            System.out.println(crucible.powers);

            // Deal with electricity.
            if(level.getBlockState(pos.below()).is(Registration.VOLT_CELL.get()) && crucible.electricCharge < 15){
                crucible.electricCharge = 15;
            }else if(crucible.electricCharge > 0){
                crucible.electricCharge--;
            }

            if (!level.isClientSide()){
                // Become empty when there's no water.
                if (!state.getValue(CrucibleBlock.FULL) && crucible.getTotalPowerLevel() > 0) {
                    SpecialCaseMan.checkEmptySpecialCases(crucible);
                    crucible.expendPower();
                    crucible.sacrificeCount = 0;
                    crucible.setDirty(level, pos, state);
                    level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.6F, 1F);
                }else{
                    // Gather Power from the surroundings.
                    if(state.getValue(CrucibleBlock.FULL)){
                        gatherPower(level, crucible);

                        // Check for new items to dissolve into Power or transmute.
                        if (processItemsInside(level, pos, state, crucible)) {
                            crucible.setDirty(level, pos, state);
                            level.playSound(null, pos, SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 1F, 0.65F+(level.getRandom().nextFloat()/5));
                        }
                    }
                }
            }
            else {
                if (!state.getValue(CrucibleBlock.FULL)  && crucible.getTotalPowerLevel() > 0) {
                    // Repeated to make sure that the client side updates quickly.
                    crucible.expendPower();
                    crucible.setDirty(level, pos, state);
                }
            }

            // Do reactions, if you can. This intentionally happens on both logical sides.
            if(state.getValue(CrucibleBlock.FULL)) react(level, crucible);

        }
    }

    private static void gatherPower(Level level, CrucibleBlockEntity crucible){
        // Only gather power if a Copper Symbol is nearby, but not an Iron one.
        if(crucible.areaMemory.exists(level, ConfigMan.COMMON.crucibleRange.get(), Registration.COPPER_SYMBOL.get())){
            if(!crucible.areaMemory.exists(level, ConfigMan.COMMON.crucibleRange.get(), Registration.IRON_SYMBOL.get())){

                // Blaze Rods add blaze.
                if(crucible.areaMemory.exists(level, ConfigMan.COMMON.crucibleRange.get(), Registration.BLAZE_ROD.get())){
                    crucible.addPower(Powers.BLAZE_POWER.get(), WorldSpecificValue.get(level, "blaze_rod_power_amount", 20, 50));
                    crucible.setDirty(level, crucible.getBlockPos(), crucible.getBlockState());
                }

                // End Rods add light.
                if(crucible.areaMemory.exists(level, ConfigMan.COMMON.crucibleRange.get(), Blocks.END_ROD)){
                    crucible.addPower(Powers.LIGHT_POWER.get(), WorldSpecificValue.get(level, "end_rod_power_amount", 30, 100));
                    crucible.setDirty(level, crucible.getBlockPos(), crucible.getBlockState());
                }

                // Wither Skeleton Skulls add curse.
                if(crucible.areaMemory.exists(level, ConfigMan.COMMON.crucibleRange.get(), Blocks.WITHER_SKELETON_SKULL) || crucible.areaMemory.exists(level, ConfigMan.COMMON.crucibleRange.get(), Blocks.WITHER_SKELETON_WALL_SKULL)){
                    crucible.addPower(Powers.CURSE_POWER.get(), WorldSpecificValue.get(level, "wither_skull_power_amount", 50, 400));
                    crucible.setDirty(level, crucible.getBlockPos(), crucible.getBlockState());
                }

                // Active conduits give add either soul or warp, depending on the world.
                if(crucible.areaMemory.exists(level, ConfigMan.COMMON.crucibleRange.get(), Blocks.CONDUIT)){
                    Optional<ConduitBlockEntity> maybe_conduit = level.getBlockEntity(crucible.areaMemory.fetch(level, ConfigMan.COMMON.crucibleRange.get(), Blocks.CONDUIT), BlockEntityType.CONDUIT);
                    if(maybe_conduit.isPresent() && maybe_conduit.get().isActive()){
                        if(WorldSpecificValues.CONDUIT_POWER.get(level) == 1){
                            crucible.addPower(Powers.SOUL_POWER.get(), WorldSpecificValue.get(level, "conduit_power_amount", 120, 140));
                            crucible.setDirty(level, crucible.getBlockPos(), crucible.getBlockState());
                        }else{
                            crucible.addPower(Powers.WARP_POWER.get(), WorldSpecificValue.get(level, "conduit_power_amount", 120, 140));
                            crucible.setDirty(level, crucible.getBlockPos(), crucible.getBlockState());
                        }
                    }
                }
            }
        }
        // Slowly dilute powers in the rain.
        if(level.canSeeSky(crucible.getBlockPos()) && level.isRainingAt(crucible.getBlockPos())){
                crucible.expendAnyPowerExcept(Powers.CURSE_POWER.get(), 80);
        }
    }

    // The method that actually performs reactions.
    private static void react(Level level, CrucibleBlockEntity crucible){
        for(Reaction r : ReactiveMod.REACTION_MAN.getReactions(level)){
            if(level.getRandom().nextFloat() > crucible.getPowerLevel(Powers.BODY_POWER.get()) * 0.7F/CRUCIBLE_MAX_POWER) { // Body suppresses reactions.
                if (r.conditionsMet(crucible)) {
                    r.run(crucible);
                    crucible.setDirty();
                }
            }
        }
    }

    // Used to gather and operate on items sitting inside the crucible.
    private static boolean processItemsInside(Level level, BlockPos pos, BlockState state, CrucibleBlockEntity crucible){
        if(!state.getValue(CrucibleBlock.FULL)){
            return false;
        }
        boolean changed = false;
        for(Entity e : CrucibleBlock.getEntitesInside(pos, level)){
            if(e instanceof ItemEntity){
                SpecialCaseMan.checkDissolveSpecialCases(crucible, (ItemEntity) e);
                if(!e.isAlive()) continue; // The special case may have removed the item entity.
                changed = changed || tryTransmute(level, pos, state, crucible, ((ItemEntity) e));
                changed = changed || tryReduceToPower(((ItemEntity) e).getItem(), crucible);

                // Remove entities that were completely transmuted or dissolved.
                if(((ItemEntity) e).getItem().getCount() == 0){
                    e.remove(Entity.RemovalReason.KILLED);
                }
            }
        }
        return changed;
    }

    // Attempts to 'dissolve' the item into Power. If it does, the power is added to the Crucible, and it returns true.
    public static boolean tryReduceToPower(ItemStack i, CrucibleBlockEntity crucible){
        List<Power> stack_power_list = Power.getSourcePower(i);
        boolean changed = false;
        for (Power p : stack_power_list) {
            int dissolve_capacity = (CrucibleBlockEntity.CRUCIBLE_MAX_POWER - crucible.getPowerLevel(p)) / Power.getSourceLevel(i, crucible.getLevel());
            if(dissolve_capacity <= 0){
                continue;
            }
            changed = changed || crucible.addPower(p, i.getCount() * Power.getSourceLevel(i, crucible.getLevel()) / stack_power_list.size());
            tryDissolveWithByproduct(Objects.requireNonNull(crucible.getLevel()), crucible.getBlockPos(), i, Math.min(i.getCount(), dissolve_capacity));
            i.setCount(Math.max(i.getCount()-dissolve_capacity, 0));
        }
        return changed;
    }

    // Attempts to find a matching Dissolve recipe, and if it does, adds the output as a new item entity.
    private static void tryDissolveWithByproduct(Level level, BlockPos pos, ItemStack item, int count){
        List<DissolveRecipe> purify_recipes = level.getRecipeManager().getAllRecipesFor(Registration.DISSOLVE_RECIPE_TYPE.get());
        for (DissolveRecipe r : purify_recipes) {
            if(r.matches(new FakeContainer(item), level)){
                ItemStack reactant = item.copy();
                reactant.setCount(count);
                level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, r.assemble(new FakeContainer(reactant))));
                return;
            }
        }
    }

    // Attempts to find a transmutation recipe that matches, and if it does, adds the output as a new item entity and returns true.
    private static boolean tryTransmute(Level level, BlockPos pos, BlockState state, CrucibleBlockEntity crucible, ItemEntity itemEntity) {
        List<TransmuteRecipe> purify_recipes = level.getRecipeManager().getAllRecipesFor(Registration.TRANS_RECIPE_TYPE.get());
        for (TransmuteRecipe r : purify_recipes) {
            if (r.matches(new FakeContainer(itemEntity.getItem()), level)) {
                System.err.println("Checking power levels for " + r);
                if (r.powerMet(crucible, level)) {
                    ItemStack result = r.apply(itemEntity.getItem(), crucible, level);
                    level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, result));
                    crucible.setDirty(level, pos, state);
                    return true;
                }
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
        if(this.getBlockState().getValue(CrucibleBlock.FULL) && !event.getEntity().level.isClientSide && !Objects.requireNonNull(this.getLevel()).isClientSide){
            double dist = Helper.distance(event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ(), this.getBlockPos().getX(), this.getBlockPos().getY(), this.getBlockPos().getZ());
            if(dist < ConfigMan.COMMON.crucibleRange.get()
                    && !areaMemory.exists(event.getEntity().level, ConfigMan.COMMON.crucibleRange.get(), Registration.IRON_SYMBOL.get())){

                if(event.getEntity().getMobType().equals(MobType.UNDEAD)){
                    addPower(Powers.CURSE_POWER.get(), WorldSpecificValue.get(event.getEntity().level, "undead_curse_strength", 30, 300));
                    return;
                }
                sacrificeCount++;
                Helper.triggerForNearbyPlayers((ServerLevel) event.getEntity().level, Registration.SEE_SACRIFICE_TRIGGER, getBlockPos(), 8);

                double x = event.getEntity().getX();
                double y = event.getEntity().getY();
                double z = event.getEntity().getZ();

                // While Mind is being devoured by Curse, sacrifices spawn Phantoms.
                if(getPowerLevel(Powers.CURSE_POWER.get()) >= WorldSpecificValues.CURSE_RATE.get(getLevel()) && getPowerLevel(Powers.MIND_POWER.get()) > 0
                && !(event.getEntity() instanceof Phantom)){
                    Phantom p = new Phantom(EntityType.PHANTOM, Objects.requireNonNull(getLevel()));
                    p.setPos(new Vec3(x, y+2, z));
                    getLevel().addFreshEntity(p);
                    Helper.drawParticleLine(level, ParticleTypes.SMOKE, x, y, z, x, y+2, z, 20, 0.1);
                }else{
                    Helper.drawParticleLine(level, ParticleTypes.CLOUD,
                            getBlockPos().getX() + 0.5, getBlockPos().getY() + 0.4, getBlockPos().getZ() + 0.5,
                            x, y, z, 15, 0.4);
                }

                // Add Vital due to sacrifices.
                int power;
                int best_sacrifice_type = WorldSpecificValues.BEST_SACRIFICE.get(event.getEntity().level);
                if (best_sacrifice_type == 1 && event.getEntity() instanceof Animal) {
                    power = WorldSpecificValue.get(event.getEntity().level, "strong_sacrifice", 300, 600);
                } else if (best_sacrifice_type == 2 && event.getEntity() instanceof AbstractVillager) {
                    power = WorldSpecificValue.get(event.getEntity().level, "strong_sacrifice", 300, 600);
                } else if (best_sacrifice_type == 3 && (event.getEntity() instanceof AbstractPiglin || event.getEntity() instanceof Hoglin)) {
                    power = WorldSpecificValue.get(event.getEntity().level, "strong_sacrifice", 300, 600);
                } else if (best_sacrifice_type == 4 && event.getEntity() instanceof Monster) {
                    power = WorldSpecificValue.get(event.getEntity().level, "strong_sacrifice", 300, 600);
                } else {
                    power = WorldSpecificValue.get(event.getEntity().level, "weak_sacrifice", 30, 60);
                }
                addPower(Powers.VITAL_POWER.get(), power);
                setDirty();
            }
        }
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

    public float getOpacity() {
        return 0.7F + (.3F * getTotalPowerLevel()/CRUCIBLE_MAX_POWER);
    }

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
            tag.put("name", StringTag.valueOf(p.getName()));
            tag.put("level", IntTag.valueOf(getPowerLevel(p)));
            power_list_tag.add(tag);
        }
        main_tag.put("powers", power_list_tag);
    }

    @Override
    public void load(@NotNull CompoundTag main_tag) {
        super.load(main_tag);
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
