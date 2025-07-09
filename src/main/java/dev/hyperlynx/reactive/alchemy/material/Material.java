package dev.hyperlynx.reactive.alchemy.material;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.hyperlynx.reactive.alchemy.Power;
import dev.hyperlynx.reactive.blocks.MaterialBlock;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/// A particular Material that a [MaterialBlock] can have the properties of.
///
/// Each Material has a table of MaterialProperties that define its characteristics.
/// When a MaterialBlock queries its material, it can ask it for various block properties.
public class Material {
    private final Reference2ObjectMap<MaterialProperty<?>, Object> properties;
    private String custom_name = "";
    private final Optional<Map<Power, Integer>> original_formula;
    private Optional<UUID> discoverer = Optional.empty();
    private Optional<String> notes = Optional.empty();

    private static final Codec<Map<MaterialProperty<?>, Object>> PROPERTIES_CODEC =
            Codec.dispatchedMap(MaterialProperties.PROPERTY_REGISTRY.byNameCodec(), MaterialProperty::codec);

    public static final Codec<Material> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    PROPERTIES_CODEC.fieldOf("properties").forGetter(Material::properties),
                    Codec.STRING.fieldOf("name").forGetter(Material::customNameRaw),
                    Codec.unboundedMap(Power.CODEC, Codec.INT).optionalFieldOf("original_formula").forGetter(Material::getOriginalFormula),
                    UUIDUtil.CODEC.optionalFieldOf("discoverer").forGetter(Material::discovererUUID),
                    Codec.STRING.optionalFieldOf("notes").forGetter(Material::getNotes)
            ).apply(instance, Material::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, Material> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(CODEC);

    public Material(Map<MaterialProperty<?>, Object> properties, String custom_name, Optional<Map<Power, Integer>> original_formula, Optional<UUID> discoverer, Optional<String> notes) {
        this.properties = new Reference2ObjectArrayMap<>(properties);
        this.custom_name = custom_name;
        this.original_formula = original_formula;
        this.discoverer = discoverer;
        this.notes = notes;
    }

    public Material(Map<MaterialProperty<?>, Object> properties, String custom_name, Optional<Map<Power, Integer>> original_formula) {
        this.properties = new Reference2ObjectArrayMap<>(properties);
        this.custom_name = custom_name;
        this.original_formula = original_formula;
    }

    public Material(Map<MaterialProperty<?>, Object> properties, String custom_name) {
        this.properties = new Reference2ObjectArrayMap<>(properties);
        this.custom_name = custom_name;
        this.original_formula = Optional.empty();
    }

    public Optional<String> getNotes() {
        return notes;
    }

    public static Material empty() {
        return new Material(Map.of(), "");
    }

    private String customNameRaw() {
        return custom_name;
    }

    private Optional<Map<Power, Integer>> getOriginalFormula() {
        return original_formula;
    }

    private Optional<UUID> discovererUUID() { return discoverer; }

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
            return Component.translatable("block.reactive.undiscovered_material");
        }
        return Component.literal(custom_name);
    }

    public void setName(String name) {
        this.custom_name = name;
    }

    public void setDiscoverer(Player player) {
        this.discoverer = Optional.of(player.getUUID());
    }

    public boolean wasDiscovered() {
        return this.discoverer.isPresent();
    }

    public Player getDiscoverer(Level level) {
        return this.discoverer.map(level::getPlayerByUUID).orElse(null);
    }

    public boolean playerDiscoveredThis(Player player) {
        return this.discoverer.isPresent() && this.discoverer.get().equals(player.getUUID());
    }

    /// Use this only as absolutely necessary.
    protected <T> void set(MaterialProperty<T> property, T value) {
        properties.put(property, value);
    }

    private static final int POWER_SAME_THRESHOLD = 100;

    /// Determines whether the formula given matches this Material, and therefore if it should be considered to be the output of the creation process.
    public boolean formulaMatches(@NotNull Map<Power, Integer> formula) {
        if(original_formula.isEmpty()) {
            return false;
        }
        for(Power power : formula.keySet()) {
            if(!original_formula.get().containsKey(power)) {
                return false;
            }
        }
        for(Power power : original_formula.get().keySet()) {
            if(!(formula.containsKey(power))) {
                return false;
            }
            if(Math.abs(formula.get(power) - original_formula.get().get(power)) > POWER_SAME_THRESHOLD) {
                return false;
            }
        }
        return true;
    }

    public void setNotes(String notes) {
        if(notes.isEmpty()) {
            this.notes = Optional.empty();
            return;
        }
        this.notes = Optional.of(notes);
    }
}
