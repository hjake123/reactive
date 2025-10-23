package dev.hyperlynx.reactive.util;

import dev.hyperlynx.reactive.ReactiveMod;
import net.minecraft.nbt.*;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class NBTExtras {
    public static <T, U> ListTag encodeMap(NBTSerializer<T> t_serializer, NBTSerializer<U> u_serializer, Map<T, U> map) {
        ListTag entries = new ListTag();
        for(T key : map.keySet()) {
            CompoundTag tag = new CompoundTag();
            tag.put("key", t_serializer.encode(key));
            tag.put("value", u_serializer.encode(map.get(key)));
            entries.add(tag);
        }
        return entries;
    }

    public static <T, U> Map<T, U> decodeMap(NBTSerializer<T> t_serializer, NBTSerializer<U> u_serializer, ListTag input) {
        Map<T, U> map = new HashMap<>();
        for(Tag tag : input) {
            if(!(tag instanceof CompoundTag entry) || !entry.contains("key") || !entry.contains("value")) {
                ReactiveMod.LOGGER.error("Skipping bad entry: {}", tag.toString());
                continue;
            }
            map.put(t_serializer.decode(entry.get("key")), u_serializer.decode(entry.get("value")));
        }
        return map;
    }

    public static final NBTSerializer<Integer> INT = new NBTSerializer<>() {
        @Override
        public Tag encode(Integer data) {
            return IntTag.valueOf(data);
        }

        @Override
        public @Nullable Integer decode(Tag input) {
            if (!(input instanceof IntTag i)) {
                return null;
            }
            return i.getAsInt();
        }
    };
}
