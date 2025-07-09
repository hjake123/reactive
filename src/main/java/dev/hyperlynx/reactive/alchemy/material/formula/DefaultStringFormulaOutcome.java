package dev.hyperlynx.reactive.alchemy.material.formula;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.hyperlynx.reactive.alchemy.Power;

import java.util.Map;

public class DefaultStringFormulaOutcome extends FormulaOutcome implements StringFormulaOutcome{
    String value;

    public static final MapCodec<DefaultStringFormulaOutcome> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.fieldOf("value").forGetter(DefaultStringFormulaOutcome::value)
    ).apply(instance, DefaultStringFormulaOutcome::new));

    public DefaultStringFormulaOutcome(String value) {
        this.value = value;
    }

    private String value() { return value; }

    @Override
    public MapCodec<? extends FormulaOutcome> type() {
        return FormulaOutcomeTypes.DEFAULT_STRING.get();
    }

    @Override
    public String calculate(Map<Power, Integer> formula) {
        return value;
    }
}
