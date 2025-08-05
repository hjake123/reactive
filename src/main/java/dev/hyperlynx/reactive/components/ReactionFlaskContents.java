package dev.hyperlynx.reactive.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.hyperlynx.reactive.alchemy.Power;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.HashMap;
import java.util.Map;

public record ReactionFlaskContents(Map<Power, Integer> powers, boolean electric_charge) {
    public static final Codec<ReactionFlaskContents> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
                Codec.unboundedMap(Power.CODEC, Codec.INT).fieldOf("powers").forGetter(dev.hyperlynx.reactive.components.ReactionFlaskContents::powers),
                Codec.BOOL.fieldOf("charged").forGetter(dev.hyperlynx.reactive.components.ReactionFlaskContents::electric_charge)
        ).apply(instance, ReactionFlaskContents::new)
    );

    public static final StreamCodec<FriendlyByteBuf, ReactionFlaskContents> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.map(HashMap::new, Power.STREAM_CODEC, ByteBufCodecs.INT), dev.hyperlynx.reactive.components.ReactionFlaskContents::powers,
        ByteBufCodecs.BOOL, dev.hyperlynx.reactive.components.ReactionFlaskContents::electric_charge,
        ReactionFlaskContents::new
    );
}
