package dev.hyperlynx.reactive.alchemy.material.formula;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.hyperlynx.reactive.alchemy.Power;
import dev.hyperlynx.reactive.alchemy.Powers;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public class IntegerZeroToMaxFormulaOutcome extends FormulaOutcome implements IntegerFormulaOutcome {
    final ResourceLocation power;
    final int min_power;
    final int max_power;
    final int max_value;

    public static final MapCodec<IntegerZeroToMaxFormulaOutcome> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("power").forGetter(IntegerZeroToMaxFormulaOutcome::power),
            Codec.INT.fieldOf("min_power").forGetter(IntegerZeroToMaxFormulaOutcome::getMinPower),
            Codec.INT.fieldOf("max_power").forGetter(IntegerZeroToMaxFormulaOutcome::getMaxPower),
            Codec.INT.fieldOf("max_value").forGetter(IntegerZeroToMaxFormulaOutcome::getMaxValue)
            ).apply(instance, IntegerZeroToMaxFormulaOutcome::new));

    public IntegerZeroToMaxFormulaOutcome(ResourceLocation power, int threshold, int max_power, int max_value) {
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

    public int getMaxValue() {
        return max_value;
    }

    public ResourceLocation power() {
        return power;
    }

    @Override
    public MapCodec<? extends FormulaOutcome> type() {
        return FormulaOutcomeTypes.ZERO_TO_MAX_INT.get();
    }

    public int calculate(Map<Power, Integer> formula) {
        Power power = Powers.POWER_REGISTRY.get(power());
        if(formula.containsKey(power) && formula.get(power) > getMinPower()) {
            int provided_power = Math.min(formula.get(power) - getMinPower(), getMaxPower());
            double power_proportion = (double) provided_power / getMaxPower();
            return (int) (power_proportion * getMaxValue());
        }
        return 0;
    }
}
