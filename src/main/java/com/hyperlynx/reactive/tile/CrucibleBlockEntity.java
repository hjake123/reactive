package com.hyperlynx.reactive.tile;

import com.hyperlynx.reactive.Registration;
import com.hyperlynx.reactive.alchemy.Power;
import com.hyperlynx.reactive.alchemy.IPowerBearer;
import com.hyperlynx.reactive.blocks.CrucibleBlock;
import com.hyperlynx.reactive.util.Color;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.*;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.extensions.IForgeBlockEntity;
import org.jetbrains.annotations.NotNull;

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
        if(getTotalPowerLevel() + amount <= CRUCIBLE_MAX_POWER) {
            int prev = powers.getOrDefault(p, 0);
            if(prev > 0)
                powers.replace(p, amount + prev);
            else
                powers.put(p, amount);
        }
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

        if(powers == null || powers.isEmpty()){
            return water_color;
        }
        // Iterate through each power and add its tint to the total, adjusted for its actual prevalence.
        for (Power p : powers.keySet()) {
            if(p == null){
                continue; // Skip any invalid values if they exist.
            }
            Color pow_color = p.getColor();
            float pow_weight = powers.get(p) / (float) getTotalPowerLevel();
            color.red += pow_color.red * pow_weight;
            color.green += pow_color.green * pow_weight;
            color.blue += pow_color.blue * pow_weight;
        }

        // Adjust the tint to be proportional to the amount of the crucible's maximum currently in use.
        float tint_alpha = (float) getTotalPowerLevel()/ (float) CRUCIBLE_MAX_POWER;
        color.red = (int) (water_color.red * (1 - tint_alpha) + color.red * (tint_alpha));
        color.green = (int) (water_color.green * (1 - tint_alpha) + color.green * (tint_alpha));
        color.blue = (int) (water_color.blue * (1 - tint_alpha) + color.blue * (tint_alpha));

        return color;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, CrucibleBlockEntity crucible) {
        crucible.counter++;
        if(crucible.counter >= CRUCIBLE_TICK_DELAY){
            crucible.counter = 1;
            crucible.addPower(Registration.ACID_POWER.get(), 50);
            crucible.expendPower(Registration.ACID_POWER.get(), 900);
            setChanged(level, pos, state);
        }
    }
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
                powers.put(p, ((CompoundTag) power_tag).getInt("level"));
            }
        }
    }


}
