package dev.hyperlynx.reactive.alchemy.material.formula;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.hyperlynx.reactive.alchemy.Power;
import dev.hyperlynx.reactive.alchemy.Powers;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public class FloatOneToValueFormulaOutcome extends FormulaOutcome implements FloatFormulaOutcome {
    final ResourceLocation power;
    final int min_power;
    final int max_power;
    final float value;

    public static final MapCodec<FloatOneToValueFormulaOutcome> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("power").forGetter(FloatOneToValueFormulaOutcome::power),
            Codec.INT.fieldOf("min_power").forGetter(FloatOneToValueFormulaOutcome::getMinPower),
            Codec.INT.fieldOf("max_power").forGetter(FloatOneToValueFormulaOutcome::getMaxPower),
            Codec.FLOAT.fieldOf("value").forGetter(FloatOneToValueFormulaOutcome::getValue)
            ).apply(instance, FloatOneToValueFormulaOutcome::new));

    public FloatOneToValueFormulaOutcome(ResourceLocation power, int threshold, int max_power, float value) {
        this.power = power;
        this.min_power = threshold;
        this.max_power = max_power;
        this.value = value;
    }

    public int getMinPower() {
        return min_power;
    }

    public int getMaxPower() {
        return max_power;
    }

    public float getValue() {
        return value;
    }

    public ResourceLocation power() {
        return power;
    }

    @Override
    public MapCodec<? extends FormulaOutcome> type() {
        return FormulaOutcomeTypes.ONE_TO_VALUE_FLOAT.get();
    }

    @Override
    public float calculate(Map<Power, Integer> formula) {
        Power power = Powers.POWER_REGISTRY.get(power());
        if(formula.containsKey(power) && formula.get(power) > getMinPower()) {
            int provided_power = Math.min((formula.get(power) - min_power), (max_power - min_power));
            float power_proportion = (float) provided_power / (max_power - min_power);
            return (getValue() - 1) * power_proportion + 1;
        }
        return 1.0F;
    }
}
