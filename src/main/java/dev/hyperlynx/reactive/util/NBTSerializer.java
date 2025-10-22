package dev.hyperlynx.reactive.util;

import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.Nullable;

public interface NBTSerializer<T> {
    Tag encode(T data);
    @Nullable T decode(Tag input);
}
