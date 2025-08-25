package dev.hyperlynx.reactive.alchemy.material.formula;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.alchemy.Power;
import dev.hyperlynx.reactive.alchemy.Powers;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.Optional;

public class RangeStringFormulaOutcome extends FormulaOutcome implements StringFormulaOutcome {
    final String option;
    final ResourceLocation power_id;
    final int threshold;
    final Optional<Integer> max_power;

    public static final MapCodec<RangeStringFormulaOutcome> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.fieldOf("option").forGetter(RangeStringFormulaOutcome::option),
            ResourceLocation.CODEC.fieldOf("power").forGetter(RangeStringFormulaOutcome::power),
            Codec.INT.fieldOf("threshold").forGetter(RangeStringFormulaOutcome::threshold),
            Codec.INT.optionalFieldOf("max_power").forGetter(RangeStringFormulaOutcome::max_power)
    ).apply(instance, RangeStringFormulaOutcome::new));

    public RangeStringFormulaOutcome(String option, ResourceLocation power_id, int threshold, Optional<Integer> max_power) {
        this.power_id = power_id;
        this.option = option;
        this.threshold = threshold;
        this.max_power = max_power;
    }

    private String option() { return option; }
    public ResourceLocation power() { return power_id; }
    private int threshold() { return threshold; }
    private Optional<Integer> max_power() { return max_power; }

    @Override
    public MapCodec<? extends FormulaOutcome> type() {
        return FormulaOutcomeTypes.STRING_OPTIONS.get();
    }

    public String calculate(Map<Power, Integer> formula) {
        Power power = Powers.POWER_SUPPLIER.get().getValue(power_id);
        if(power == null) {
            ReactiveMod.LOGGER.error("Invalid power {} in StringFormulaOutcome", power_id);
            return "";
        }
        if(formula.containsKey(power) && formula.get(power) >= threshold) {
            if(max_power.isPresent() && formula.get(power) > max_power.get()) {
                return "";
            }
            return option;
        }
        return "";
    }
}
