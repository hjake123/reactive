package dev.hyperlynx.reactive.alchemy.material;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record YieldEntry(int max_input_items, int yield_per_input, String default_model, Float power_effect_multiplier) {
    public static final Codec<YieldEntry> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
                Codec.INT.fieldOf("max_input_items").forGetter(YieldEntry::max_input_items),
                Codec.INT.fieldOf("yield_per_input").forGetter(YieldEntry::yield_per_input),
                Codec.STRING.fieldOf("default_model").forGetter(YieldEntry::default_model),
                Codec.FLOAT.fieldOf("power_effect_multiplier").forGetter(YieldEntry::power_effect_multiplier)
        ).apply(instance, YieldEntry::new)
    );

    /**
     * @return Whether this base yields cosmetic Materials, which have no functional properties. This is true if the multiplier is 0.
     */
    public boolean cosmetic() {
        return power_effect_multiplier == 0.0F;
    }
}
