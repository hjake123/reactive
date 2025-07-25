package dev.hyperlynx.reactive.alchemy.material;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.hyperlynx.reactive.ConfigMan;
import dev.hyperlynx.reactive.alchemy.Power;
import dev.hyperlynx.reactive.blocks.MaterialBlock;
import dev.hyperlynx.reactive.util.Color;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import net.minecraft.ChatFormatting;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/// A particular Material that a [MaterialBlock] can have the properties of.
///
/// Each Material has a table of MaterialProperties that define its characteristics.
/// When a MaterialBlock queries its material, it can ask it for various block properties.
public class Material {
    private final Reference2ObjectMap<MaterialProperty<?>, Object> properties;
    private String custom_name;
    private final Optional<Map<Power, Integer>> original_formula;
    private Optional<Discoverer> discoverer = Optional.empty();
    private Optional<String> notes = Optional.empty();
    private final int yield;

    private static final Codec<Map<MaterialProperty<?>, Object>> PROPERTIES_CODEC =
            Codec.dispatchedMap(MaterialProperties.PROPERTY_REGISTRY.byNameCodec(), MaterialProperty::codec);

    public static final Codec<Material> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    PROPERTIES_CODEC.fieldOf("properties").forGetter(Material::properties),
                    Codec.STRING.fieldOf("name").forGetter(Material::customNameRaw),
                    Codec.unboundedMap(Power.CODEC, Codec.INT).optionalFieldOf("original_formula").forGetter(Material::getOriginalFormula),
                    Discoverer.CODEC.optionalFieldOf("discoverer").forGetter(Material::discoverer),
                    Codec.STRING.optionalFieldOf("notes").forGetter(Material::getNotes),
                    Codec.INT.fieldOf("yield").forGetter(Material::yield)
            ).apply(instance, Material::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, Material> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(CODEC);

    public Material(Map<MaterialProperty<?>, Object> properties, String custom_name, Optional<Map<Power, Integer>> original_formula, Optional<Discoverer> discoverer, Optional<String> notes, int yield) {
        this.properties = new Reference2ObjectArrayMap<>(properties);
        this.custom_name = custom_name;
        this.original_formula = original_formula;
        this.discoverer = discoverer;
        this.notes = notes;
        this.yield = yield;
    }

    public Material(Map<MaterialProperty<?>, Object> properties, String custom_name, Optional<Map<Power, Integer>> original_formula, int yield) {
        this.properties = new Reference2ObjectArrayMap<>(properties);
        this.custom_name = custom_name;
        this.original_formula = original_formula;
        this.yield = yield;
    }

    public Material(Map<MaterialProperty<?>, Object> properties, String custom_name, int yield) {
        this.properties = new Reference2ObjectArrayMap<>(properties);
        this.custom_name = custom_name;
        this.original_formula = Optional.empty();
        this.yield = yield;
    }

    public Optional<String> getNotes() {
        return notes;
    }

    public static Material empty() {
        return new Material(Map.of(), "", 0);
    }

    private String customNameRaw() {
        return custom_name;
    }

    public Optional<Map<Power, Integer>> getOriginalFormula() {
        return original_formula;
    }

    private Optional<Discoverer> discoverer() { return discoverer; }

    public int yield() { return yield; }

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

    public Component getNameComponent() {
        if(custom_name.isEmpty()) {
            return Component.translatable("block.reactive.undiscovered_material");
        }
        return Component.literal(custom_name);
    }

    public void setName(String name) {
        this.custom_name = name;
    }

    public void setDiscoverer(Player player) {
        long timestamp;
        if(this.discoverer.isPresent()) {
            timestamp = discoverer.get().discovery_timestamp();
        } else {
            timestamp = System.currentTimeMillis();
        }
        this.discoverer = Optional.of(new Discoverer(player.getUUID(), player.getName().getString(), timestamp));
    }

    public boolean wasDiscovered() {
        return this.discoverer.isPresent();
    }

    public Player getDiscoverer(Level level) {
        return this.discoverer.map(d -> level.getPlayerByUUID(d.uuid)).orElse(null);
    }

    public boolean playerDiscoveredThis(Player player) {
        return this.discoverer.isPresent() && this.discoverer.get().uuid.equals(player.getUUID());
    }

    /// Use this only as absolutely necessary.
    @SuppressWarnings("SameParameterValue")
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

    public Component getDiscovererName(Level level) {
        if(!wasDiscovered()) {
            return Component.empty();
        }
        Player player = getDiscoverer(level);
        if(player == null) {
            // The discoverer is known to exist (that's what we checked with wasDiscovered()), so...
            //noinspection OptionalGetWithoutIsPresent
            return Component.translatable("text.reactive.discovered_by").withStyle(ChatFormatting.LIGHT_PURPLE).append(discoverer().get().name);
        }
        setDiscoverer(player); // Resets the name of the player, so that if the player's username changes it will be up to date
        return Component.translatable("text.reactive.discovered_by").withStyle(ChatFormatting.LIGHT_PURPLE).append(player.getName());
    }

    public long getDiscoveryTime() {
        return discoverer.map(Discoverer::discovery_timestamp).orElse(0L);
    }

    public record Discoverer(UUID uuid, String name, long discovery_timestamp) {
        public static final Codec<Discoverer> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                UUIDUtil.CODEC.fieldOf("uuid").forGetter(Discoverer::uuid),
                Codec.STRING.fieldOf("name").forGetter(Discoverer::name),
                Codec.LONG.optionalFieldOf("discovery_timestamp",0L).forGetter(Discoverer::discovery_timestamp)
        ).apply(instance, Discoverer::new));
    }

    public MutableComponent formulaComponent() {
        MutableComponent readout_message = Component.empty();
        Map<Power, Integer> original_formula = this.getOriginalFormula().orElse(Map.of());
        if(original_formula.isEmpty()) {
            return Component.translatable("ui.reactive.no_formula");
        }
        List<Component> power_lines = new ArrayList<>();
        for(Power power : original_formula.keySet().stream().sorted(Comparator.comparing(original_formula::get)).toList().reversed()) {
            power_lines.add(Component.literal(power.getName() + ": " + Math.round(original_formula.get(power) / 16.0) + "%")
                    .withColor(shouldColorizeAgainstBlack(power.getColor()) ? power.getColor().hex() : 0xFFFFFF));
        }
        for(int i = 0; i < power_lines.size(); i++) {
            readout_message.append(power_lines.get(i));
            if(i < power_lines.size() - 1) {
                readout_message.append("\n");
            }
        }
        return readout_message;
    }

    private static boolean shouldColorizeAgainstBlack(Color color) {
        int threshold = 90;
        return ConfigMan.CLIENT.colorizeLitmusOutput.get() && (color.red > threshold || color.green > threshold || color.blue > threshold);
    }
}
