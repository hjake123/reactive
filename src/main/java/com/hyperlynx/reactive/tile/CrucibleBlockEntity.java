package com.hyperlynx.reactive.tile;

import com.hyperlynx.reactive.Registration;
import com.hyperlynx.reactive.alchemy.AlchemyTags;
import com.hyperlynx.reactive.alchemy.IPowerBearer;
import com.hyperlynx.reactive.alchemy.Power;
import com.hyperlynx.reactive.blocks.CrucibleBlock;
import com.hyperlynx.reactive.util.Color;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.*;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.extensions.IForgeBlockEntity;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CrucibleBlockEntity extends BlockEntity implements IPowerBearer {
    private static final int CRUCIBLE_TICK_DELAY = 30; // The number of server ticks before the Crucible does its full calculations.
    public static final int CRUCIBLE_MAX_POWER = 640; // The maximum power the Crucible can hold.
    HashMap<Power, Integer> powers = new HashMap<>(); // A map of Powers to their amounts.
    private int total_power = 0; // The current total number of power units in the Crucible.
    private int tick_counter = 0; // Used for counting active ticks. See tick().
    private final Color mix_color = new Color(); // Used to cache mixture color between updates;
    private boolean color_changed = true; // This is set to true when the color needs to be updated next rendering tick.

    public CrucibleBlockEntity(BlockPos pos, BlockState state) {
        super(Registration.CRUCIBLE_BE_TYPE.get(), pos, state);
    }

    // ----- Tick and related worker methods -----
    public static void tick(Level level, BlockPos pos, BlockState state, CrucibleBlockEntity crucible) {
        crucible.tick_counter++;
        if(crucible.tick_counter >= CRUCIBLE_TICK_DELAY && !level.isClientSide()){
            crucible.tick_counter = 1;

            // Clear the crucible if it is empty.
            if(!state.getValue(CrucibleBlock.FULL)) crucible.expendPower();

            // Check for new items to dissolve into Power.
            List<ItemStack> items = getItemsInside(level, pos, state, crucible);
            if(!items.isEmpty()){
                boolean changed = false;
                for(ItemStack i : items){
                    changed = changed || crucible.addPower(Power.getSourcePower(i), i.getCount() * 10); // TODO: power levels??
                }
                if(changed){
                    crucible.setDirty(level, pos, state);
                    level.playSound(null, pos, SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 1F, 0.9F);
                }
            }

        }
    }

    private static List<ItemStack> getItemsInside(Level level, BlockPos pos, BlockState state, CrucibleBlockEntity crucible){
        ArrayList<ItemStack> items = new ArrayList<>();
        if(!state.getValue(CrucibleBlock.FULL)){
            return items;
        }
        for(Entity e : CrucibleBlock.getEntitesInside(pos, level)){
            if(e instanceof ItemEntity && crucible.canDissolve(((ItemEntity) e).getItem())){
                items.add(((ItemEntity) e).getItem());
                Power p = Power.getSourcePower(((ItemEntity) e).getItem());
                // Only remove items that have a power assigned to them.
                // TODO: Don't delete items that aren't dissolved.
                if(!(p == null)){
                    e.remove(Entity.RemovalReason.KILLED);
                }
            }
        }
        return items;
    }

    // Returns true if we have enough Caustic to dissolve the item.
    private boolean canDissolve(ItemStack i){
        return i.is(AlchemyTags.easyDissolve) || getPowerLevel(Registration.ACID_POWER.get()) >= 40;
    }

    // ----- Helper and power management methods -----
    private void setDirty(Level level, BlockPos pos, BlockState state){
        this.setChanged();
        level.sendBlockUpdated(pos, state, state, Block.UPDATE_CLIENTS);
    }

    public float getOpacity() {
        return 0.7F + (.3F * getTotalPowerLevel()/CRUCIBLE_MAX_POWER);
    }

    @Override
    public boolean addPower(Power p, int amount) {
        if(p == null){
            return false;
        }
        if(getTotalPowerLevel() + amount > CRUCIBLE_MAX_POWER) {
            amount = CRUCIBLE_MAX_POWER - getTotalPowerLevel();
        }

        int prev = powers.getOrDefault(p, 0);
        if(prev > 0)
            powers.replace(p, amount + prev);
        else
            powers.put(p, amount);

        total_power += amount;
        return true;
    }

    @Override
    public int getPowerLevel(Power t) {
        if(powers.isEmpty()){
            return 0;
        }
        return powers.get(t);
    }

    @Override
    public boolean expendPower(Power t, int amount) {
        if(powers.isEmpty()){
            return false;
        }
        int level = powers.get(t);
        if(level > amount){
            powers.put(t, level-amount);
            total_power -= amount;
            return true;
        }
        if (level == amount) {
            powers.remove(t);
            total_power -= amount;
            return true;
        }
        return false;
    }

    public void expendPower() {
        powers.clear();
        total_power = 0;
    }

    public int getTotalPowerLevel(){
        return total_power;
    }

    // These methods calculate and return the combined color of the cauldron's mixture
    @Override
    public Color getCombinedColor(int water_color_number) {
        Color water_color = new Color(water_color_number);
        if(powers == null || powers.isEmpty()){
            return water_color;
        }
        if(color_changed){
            updateColor(water_color);
        }
        return mix_color;
    }

    private void updateColor(Color water_color){
        if(powers == null || powers.isEmpty()){
            return;
        }
        // Iterate through each power and add its tint to the total, adjusted for its actual prevalence.
        mix_color.reset();
        for (Power p : powers.keySet()) {
            if(p == null){
                continue; // Skip any invalid values if they exist.
            }
            Color pow_color = p.getColor();
            float pow_weight = powers.get(p) / (float) getTotalPowerLevel();
            mix_color.red += pow_color.red * pow_weight;
            mix_color.green += pow_color.green * pow_weight;
            mix_color.blue += pow_color.blue * pow_weight;
        }

        // Adjust the tint to be proportional to the amount of the crucible's maximum currently in use.
        float tint_alpha = (float) getTotalPowerLevel()/ (float) CRUCIBLE_MAX_POWER;
        mix_color.red = (int) (water_color.red * (1 - tint_alpha) + mix_color.red * (tint_alpha));
        mix_color.green = (int) (water_color.green * (1 - tint_alpha) + mix_color.green * (tint_alpha));
        mix_color.blue = (int) (water_color.blue * (1 - tint_alpha) + mix_color.blue * (tint_alpha));
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
        // Mark that the color has changed and should be recalculated.
        color_changed = true;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag main_tag) {
        super.saveAdditional(main_tag);
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
    }


}
