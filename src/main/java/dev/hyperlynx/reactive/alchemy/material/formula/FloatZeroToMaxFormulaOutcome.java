package dev.hyperlynx.reactive.alchemy.material.formula;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.hyperlynx.reactive.alchemy.Power;
import dev.hyperlynx.reactive.alchemy.Powers;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public class FloatZeroToMaxFormulaOutcome extends FormulaOutcome implements FloatFormulaOutcome {
    ResourceLocation power;
    int min_power;
    int max_power;
    float max_value;

    public static final MapCodec<FloatZeroToMaxFormulaOutcome> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("power").forGetter(FloatZeroToMaxFormulaOutcome::power),
            Codec.INT.fieldOf("min_power").forGetter(FloatZeroToMaxFormulaOutcome::getMinPower),
            Codec.INT.fieldOf("max_power").forGetter(FloatZeroToMaxFormulaOutcome::getMaxPower),
            Codec.FLOAT.fieldOf("max_value").forGetter(FloatZeroToMaxFormulaOutcome::getMaxValue)
            ).apply(instance, FloatZeroToMaxFormulaOutcome::new));

    public FloatZeroToMaxFormulaOutcome(ResourceLocation power, int threshold, int max_power, float max_value) {
        this.power = power;
        this.min_power = threshold;
        this.max_power = max_power;
        this.max_value = max_value;
    }

    public int getMinPower() {
        return min_power;
    }

    public int getMaxPower() {
        return max_power;
    }

    public float getMaxValue() {
        return max_value;
    }

    public ResourceLocation power() {
        return power;
    }

    @Override
    public MapCodec<? extends FormulaOutcome> type() {
        return FormulaOutcomeTypes.ZERO_TO_MAX_FLOAT.get();
    }

    @Override
    public float calculate(Map<Power, Integer> formula) {
        Power power = Powers.POWER_REGISTRY.get(power());
        if(formula.containsKey(power) && formula.get(power) > getMinPower()) {
            int provided_power = Math.min(formula.get(power) - getMinPower(), getMaxPower());
            float power_proportion = (float) provided_power / (getMaxPower() - getMinPower());
            return power_proportion * getMaxValue();
        }
        return 0.0F;
    }
}
