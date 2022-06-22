package com.hyperlynx.reactive.tile;

import com.hyperlynx.reactive.Registration;
import com.hyperlynx.reactive.alchemy.Power;
import com.hyperlynx.reactive.alchemy.IPowerBearer;
import com.hyperlynx.reactive.blocks.CrucibleBlock;
import com.hyperlynx.reactive.util.Color;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Objects;

public class CrucibleBlockEntity extends BlockEntity implements IPowerBearer {
    private static final int CRUCIBLE_TICK_DELAY = 30;
    public static final int CRUCIBLE_MAX_POWER = 1000;
    private int counter = 1;
    HashMap<Power, Integer> powers = new HashMap<>();
    public CrucibleBlockEntity(BlockPos pos, BlockState state) {
        super(Registration.CRUCIBLE_BE_TYPE.get(), pos, state);
    }

    public float getOpacity() {
        return 0.7F + (.3F * getTotalPowerLevel()/CRUCIBLE_MAX_POWER);
    }

    @Override
    public void addPower(Power p, int amount) {
        if(getTotalPowerLevel() + amount <= CRUCIBLE_MAX_POWER)
            powers.put(p, amount);
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
            return true;
        }
        if (level == amount) {
            powers.remove(t);
            return true;
        }
        return false;
    }

    public int getTotalPowerLevel(){
        if(powers.isEmpty()){
            return 0;
        }
        int total = 0;
        for (Power p : powers.keySet()) {
            total += powers.get(p);
        }
        return total;
    }

    @Override
    public Color getCombinedColor(int water_color_number) {
        Color water_color = new Color(water_color_number);
        Color color = new Color();

        if(powers.isEmpty()){
            return water_color;
        }
        // Iterate through each power and add its tint to the total, adjusted for its actual prevalence.
        for (Power p : powers.keySet()) {
            Color pow_color = p.getColor();
            float pow_weight = powers.get(p) / (float) getTotalPowerLevel();
            color.red += pow_color.red * pow_weight;
            color.green += pow_color.green * pow_weight;
            color.blue += pow_color.blue * pow_weight;
        }

        // Adjust the tint to be proportional to the amount of the crucible's maximum currently in use.
        color.red = water_color.red * (1 - getTotalPowerLevel()/CRUCIBLE_MAX_POWER) + color.red * (getTotalPowerLevel()/CRUCIBLE_MAX_POWER);
        color.green = water_color.green * (1 - getTotalPowerLevel()/CRUCIBLE_MAX_POWER) + color.green * (getTotalPowerLevel()/CRUCIBLE_MAX_POWER);
        color.blue = water_color.blue * (1 - getTotalPowerLevel()/CRUCIBLE_MAX_POWER) + color.blue * (getTotalPowerLevel()/CRUCIBLE_MAX_POWER);

        return color;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, CrucibleBlockEntity crucible) {
        crucible.counter++;
        if(crucible.counter >= CRUCIBLE_TICK_DELAY){
            crucible.counter = 1;
            crucible.addPower(Registration.ACID_POWER.get(), 10);
            if(!state.getValue(CrucibleBlock.FULL)){
                crucible.powers.clear();
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag main_tag) {
        super.saveAdditional(main_tag);
        if(powers == null){
            return;
        }
        ListTag power_list_tag = new ListTag();
        for (Power p : powers.keySet()) {
            CompoundTag tag = new CompoundTag();
            tag.put(p.getName(), IntTag.valueOf(getPowerLevel(p)));
            power_list_tag.add(tag);
        }
        main_tag.put("powers", power_list_tag);
    }

    @Override
    public void load(CompoundTag main_tag) {
        super.load(main_tag);

        // Powers tag is guaranteed to be a list.
        ListTag power_list_tag = (ListTag) main_tag.get("powers");
        if(power_list_tag != null) {
            for (Tag power_tag : power_list_tag) {
                Power p = Power.readPower((CompoundTag) power_tag);
                powers.put(p, ((CompoundTag) power_tag).getInt(p.getName()));
            }
        }
    }


}
