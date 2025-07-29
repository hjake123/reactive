package dev.hyperlynx.reactive.alchemy.material;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record YieldEntry(int yield, String default_model) {
    public static final Codec<YieldEntry> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
                Codec.INT.fieldOf("yield").forGetter(YieldEntry::yield),
                Codec.STRING.fieldOf("default_model").forGetter(YieldEntry::default_model)
        ).apply(instance, YieldEntry::new)
    );
}
