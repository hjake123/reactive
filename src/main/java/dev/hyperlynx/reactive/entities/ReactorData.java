package dev.hyperlynx.reactive.entities;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.hyperlynx.reactive.alchemy.Power;
import dev.hyperlynx.reactive.alchemy.rxn.ReactionStatusEntry;
import dev.hyperlynx.reactive.util.ReactiveVanillaCodecs;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.syncher.EntityDataSerializer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record ReactorData(Map<Power, Integer> powers, List<ReactionStatusEntry> statuses) {
    public static final StreamCodec<FriendlyByteBuf, ReactorData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(HashMap::new, Power.STREAM_CODEC, ByteBufCodecs.INT), ReactorData::powers,
            ReactionStatusEntry.STREAM_CODEC.apply(ByteBufCodecs.list()), ReactorData::statuses,
            ReactorData::new
    );

    public static final Codec<ReactorData> CODEC = RecordCodecBuilder.create((instance) ->
        instance.group(
                Codec.unboundedMap(Power.CODEC, Codec.INT).xmap(ReactiveVanillaCodecs::makeMapMutable, ReactiveVanillaCodecs::doNothing)
                        .fieldOf("powers").forGetter(ReactorData::powers),
                Codec.list(ReactionStatusEntry.CODEC).xmap(ReactiveVanillaCodecs::makeListMutable, ReactiveVanillaCodecs::doNothing)
                        .fieldOf("statuses").forGetter(ReactorData::statuses)
        ).apply(instance, ReactorData::new)
    );

    public Tag toTag() {
        return CODEC.encodeStart(NbtOps.INSTANCE, this).getOrThrow();
    }

    public static ReactorData fromTag(CompoundTag tag){
        var result = CODEC.decode(NbtOps.INSTANCE, tag);
        if (result.isError()) {
            return new ReactorData(new HashMap<>(), new ArrayList<>());
        }
        return result.getOrThrow().getFirst();
    }

    public ReactorData copy() {
        return new ReactorData(new HashMap<>(this.powers), new ArrayList<>(this.statuses));
    }

    public static class Serializer implements EntityDataSerializer<ReactorData> {
        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, ReactorData> codec() {
            return ReactorData.STREAM_CODEC;
        }

        @Override
        public ReactorData copy(ReactorData value) {
            return new ReactorData(value.powers(), value.statuses());
        }
    }
}
