package dev.hyperlynx.reactive.alchemy.material;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

/// A particular Material that MaterialBlocks can have the properties of.
///
/// Each Material has a table of MaterialProperties that define its characteristics.
/// When a MaterialBlock queries its material, it can ask it for various block properties.
public class Material {
    Reference2ObjectMap<MaterialProperty<?>, Object> properties;
    String custom_name = "";

    private static final Codec<Map<MaterialProperty<?>, Object>> PROPERTIES_CODEC =
            Codec.dispatchedMap(MaterialProperties.PROPERTY_REGISTRY.byNameCodec(), MaterialProperty::codec);

    public static final Codec<Material> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    PROPERTIES_CODEC.fieldOf("properties").forGetter(Material::properties),
                    Codec.STRING.fieldOf("name").forGetter(Material::customNameRaw)
            ).apply(instance, Material::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, Material> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(CODEC);

    public Material(Map<MaterialProperty<?>, Object> properties, String custom_name) {
        this.properties = new Reference2ObjectArrayMap<>(properties);
        this.custom_name = custom_name;
    }
    public static Material empty() {
        return new Material(Map.of(), "");
    }

    private String customNameRaw() {
        return custom_name;
    }

    public boolean has(MaterialProperty<?> type) {
        return properties.containsKey(type);
    }

    @SuppressWarnings("unchecked")
    // Requires suspicious cast to get around generic weirdness.
    // In practice only objects of T will associate with ResourceKeys involving T.
    public <T> T get(MaterialProperty<T> type) {
        return (T) properties.get(type);
    }

    public <T> T getOrDefault(MaterialProperty<T> key, T default_value) {
        if(has(key)) {
            return get(key);
        }
        return default_value;
    }

    private Map<MaterialProperty<?>, Object> properties() {
        return properties;
    }

    public String toString() {
        var result = CODEC.encode(this, NbtOps.INSTANCE, null);
        return result.getOrThrow().getAsString();
    }

    public Component getNameComponent(ResourceLocation id) {
        if(custom_name.isEmpty()) {
            return Component.literal(id.toString());
        }
        return Component.literal(custom_name);
    }

    public void setName(String name) {
        this.custom_name = name;
    }

    /// Use this only as absolutely necessary.
    protected <T> void set(MaterialProperty<T> property, T value) {
        properties.put(property, value);
    }
}
