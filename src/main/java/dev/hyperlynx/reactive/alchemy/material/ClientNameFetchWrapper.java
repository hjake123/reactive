package dev.hyperlynx.reactive.alchemy.material;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class ClientNameFetchWrapper {
    public static Component getName(@Nullable ResourceLocation id) {
        return ClientMaterialMan.getName(id);
    }
}
