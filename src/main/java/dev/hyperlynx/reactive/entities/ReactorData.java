package dev.hyperlynx.reactive.entities;

import dev.hyperlynx.reactive.alchemy.Power;
import dev.hyperlynx.reactive.alchemy.rxn.Reaction;
import dev.hyperlynx.reactive.alchemy.rxn.ReactionStatusEntry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record ReactorData(Map<Power, Integer> powers, List<ReactionStatusEntry> statuses) {
    private static final String POWERS_TAG = "Powers";
    private static final String STATUSES_TAG = "Statuses";

    public CompoundTag toTag() {
        ListTag power_list = Power.writePowerLevelMap(powers);

        ListTag status_list = new ListTag();
        for(ReactionStatusEntry status : statuses) {
            CompoundTag status_tag = new CompoundTag();
            status_tag.putString("s", status.getStatusAsString());
            status_tag.putString("r", status.reaction_alias());
            status_list.add(status_tag);
        }

        CompoundTag tag = new CompoundTag();
        tag.put(POWERS_TAG, power_list);
        tag.put(STATUSES_TAG, status_list);
        return tag;
    }

    public static ReactorData fromTag(CompoundTag tag){
        ListTag power_list = tag.getList(POWERS_TAG, Tag.TAG_COMPOUND);
        Map<Power, Integer> powers = Power.readPowerLevelMap(power_list);

        ListTag status_list = tag.getList(STATUSES_TAG, Tag.TAG_COMPOUND);
        List<ReactionStatusEntry> entries = new ArrayList<>();
        for(Tag status_t : status_list) {
            if(status_t instanceof CompoundTag status_tag) {
                entries.add(new ReactionStatusEntry(Reaction.Status.valueOf(status_tag.getString("s")), status_tag.getString("r")));
            }
        }
        return new ReactorData(powers, entries);
    }

    public ReactorData copy() {
        return new ReactorData(new HashMap<>(this.powers), new ArrayList<>(this.statuses));
    }

    public static class Serializer implements EntityDataSerializer<ReactorData> {
        @Override
        public void write(FriendlyByteBuf buf, ReactorData data) {
            buf.writeNbt(data.toTag());
        }

        @Override
        public ReactorData read(FriendlyByteBuf buf) {
            CompoundTag tag = buf.readNbt();
            return ReactorData.fromTag(tag);
        }

        @Override
        public ReactorData copy(ReactorData value) {
            return new ReactorData(value.powers(), value.statuses());
        }
    }
}
