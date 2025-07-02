package dev.hyperlynx.reactive.alchemy.material.formula;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.alchemy.Power;
import dev.hyperlynx.reactive.alchemy.Powers;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Map;

public class StringFormulaOutcome extends FormulaOutcome {
    List<Option> options;
    String default_value;

    public static final MapCodec<StringFormulaOutcome> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Option.CODEC.listOf().fieldOf("options").forGetter(StringFormulaOutcome::getOptions),
            Codec.STRING.fieldOf("default").forGetter(StringFormulaOutcome::defaultValue)
    ).apply(instance, StringFormulaOutcome::new));

    public StringFormulaOutcome(List<Option> options, String default_value) {
        this.options = options;
        this.default_value = default_value;
    }

    private List<Option> getOptions() {
        return options;
    }

    private String defaultValue() {
        return default_value;
    }

    @Override
    public MapCodec<? extends FormulaOutcome> type() {
        return FormulaOutcomeTypes.STRING_OPTIONS.get();
    }

    public String calculate(Map<Power, Integer> formula) {
        for(Option option : options) {
            Power power = Powers.POWER_REGISTRY.get(option.power);
            if(power == null) {
                ReactiveMod.LOGGER.error("Invalid power {} in StringFormulaOutcome", option.power);
                return "";
            }
            if(formula.containsKey(power) && formula.get(power) >= option.threshold && formula.get(power) <= option.max_power) {
                return option.option;
            }
        }
        return default_value;
    }

    public record Option(String option, ResourceLocation power, int threshold, int max_power) {
        public static final Codec<Option> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.fieldOf("option").forGetter(Option::option),
                ResourceLocation.CODEC.fieldOf("power").forGetter(Option::power),
                Codec.INT.fieldOf("threshold").forGetter(Option::threshold),
                Codec.INT.fieldOf("max_power").forGetter(Option::max_power)
        ).apply(instance, Option::new));
    }
}
