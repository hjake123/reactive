package dev.hyperlynx.reactive.entites.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.hyperlynx.reactive.alchemy.Power;
import dev.hyperlynx.reactive.alchemy.Powers;
import dev.hyperlynx.reactive.alchemy.rxn.ReactionStatusEntry;
import net.minecraft.nbt.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.syncher.EntityDataSerializer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record ReactorData(Map<Power, Integer> powers, List<ReactionStatusEntry> statuses, List<String> render_aliases) {
    public static final StreamCodec<FriendlyByteBuf, ReactorData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(HashMap::new, Power.STREAM_CODEC, ByteBufCodecs.INT), ReactorData::powers,
            ReactionStatusEntry.STREAM_CODEC.apply(ByteBufCodecs.list()), ReactorData::statuses,
            ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()), ReactorData::render_aliases,
            ReactorData::new
    );

    public static final Codec<ReactorData> CODEC = RecordCodecBuilder.create((instance) ->
        instance.group(
                Codec.unboundedMap(Power.CODEC, Codec.INT).fieldOf("powers").forGetter(ReactorData::powers),
                Codec.list(ReactionStatusEntry.CODEC).fieldOf("statuses").forGetter(ReactorData::statuses),
                Codec.list(Codec.STRING).fieldOf("render_aliases").forGetter(ReactorData::render_aliases)
        ).apply(instance, ReactorData::new)
    );

    public Tag toTag() {
        return CODEC.encodeStart(NbtOps.INSTANCE, this).getOrThrow();
    }

    public static ReactorData fromTag(CompoundTag tag){
        return CODEC.decode(NbtOps.INSTANCE, tag).getOrThrow().getFirst();
    }

    public static class Serializer implements EntityDataSerializer<ReactorData> {
        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, ReactorData> codec() {
            return ReactorData.STREAM_CODEC;
        }

        @Override
        public ReactorData copy(ReactorData value) {
            return new ReactorData(value.powers(), value.statuses(), value.render_aliases());
        }
    }
}
