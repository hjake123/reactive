package dev.hyperlynx.reactive.alchemy.material;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Optional;

public record YieldEntry(int yield, String default_model, Float power_effect_multiplier) {
    public static final Codec<YieldEntry> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
                Codec.INT.fieldOf("yield").forGetter(YieldEntry::yield),
                Codec.STRING.fieldOf("default_model").forGetter(YieldEntry::default_model),
                Codec.FLOAT.fieldOf("power_effect_multiplier").forGetter(YieldEntry::power_effect_multiplier)
        ).apply(instance, YieldEntry::new)
    );
}
