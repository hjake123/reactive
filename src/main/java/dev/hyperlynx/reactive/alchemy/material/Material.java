package dev.hyperlynx.reactive.alchemy.material;

import dev.hyperlynx.reactive.ConfigMan;
import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.alchemy.Power;
import dev.hyperlynx.reactive.alchemy.material.formula.Formula;
import dev.hyperlynx.reactive.blocks.MaterialBlock;
import dev.hyperlynx.reactive.util.Color;
import dev.hyperlynx.reactive.util.NBTSerializer;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/// A particular Material that a [MaterialBlock] can have the properties of.
///
/// Each Material has a table of MaterialProperties that define its characteristics.
/// When a MaterialBlock queries its material, it can ask it for various block properties.
public class Material {
    private final Reference2ObjectMap<MaterialProperty<?>, Object> properties;
    private String custom_name;
    private final @Nullable Formula original_formula;
    private @Nullable Discoverer discoverer;
    private String notes = "";

    public static final NBTSerializer<Material> SERIALIZER = new NBTSerializer<>() {
        @Override
        public CompoundTag encode(Material data) {
            CompoundTag tag = new CompoundTag();
            tag.putString("name", data.custom_name);
            if(data.discoverer != null) {
                tag.put("discoverer", Discoverer.SERIALIZER.encode(data.discoverer));
            }
            tag.putString("notes", data.notes);
            if(data.original_formula != null) {
                tag.put("formula", Formula.SERIALIZER.encode(data.original_formula));
            }
            return tag;
        }

        @Override
        public @Nullable Material decode(Tag input) {
            if(!(input instanceof CompoundTag compound)) {
                return null;
            }
            return new Material(
                    null,
                    compound.getString("name"),
                    compound.contains("formula") ? Formula.SERIALIZER.decode(compound.getCompound("formula")) : null,
                    compound.contains("discoverer") ? Discoverer.SERIALIZER.decode(compound.getCompound("discoverer")) : null,
                    compound.getString("notes")
            );
        }
    };

    public Material(Map<MaterialProperty<?>, Object> properties, String custom_name, @Nullable Formula original_formula, @Nullable Discoverer discoverer, String notes) {
        this.properties = new Reference2ObjectArrayMap<>(properties);
        this.custom_name = custom_name;
        this.original_formula = original_formula;
        this.discoverer = discoverer;
        this.notes = notes;
    }

    public Material(Map<MaterialProperty<?>, Object> properties, String custom_name, @Nullable Formula original_formula) {
        this.properties = new Reference2ObjectArrayMap<>(properties);
        this.custom_name = custom_name;
        this.original_formula = original_formula;
    }

    public Material(Map<MaterialProperty<?>, Object> properties, String custom_name) {
        this.properties = new Reference2ObjectArrayMap<>(properties);
        this.custom_name = custom_name;
        this.original_formula = null;
    }

    public String getNotes() {
        return notes;
    }

    public static Material empty() {
        return new Material(Map.of(), "");
    }

    private String customNameRaw() {
        return custom_name;
    }

    public @Nullable Formula getOriginalFormula() {
        return original_formula;
    }

    private @Nullable Discoverer discoverer() { return discoverer; }

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

    public Map<MaterialProperty<?>, Object> properties() {
        return properties;
    }

    public String toString() {
        var result = SERIALIZER.encode(this);
        return result.getAsString();
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
        if(discoverer != null) {
            timestamp = discoverer.discovery_timestamp();
        } else {
            timestamp = System.currentTimeMillis();
        }
        discoverer = new Discoverer(player.getUUID(), player.getName().getString(), timestamp);
    }

    public boolean wasDiscovered() {
        return discoverer != null;
    }

    public Player getDiscoverer(Level level) {
        if(discoverer == null) {
            return null;
        }
        return level.getPlayerByUUID(discoverer.uuid);
    }

    public boolean playerDiscoveredThis(Player player) {
        return discoverer != null && this.discoverer.uuid.equals(player.getUUID());
    }

    /// Use this only as absolutely necessary.
    @SuppressWarnings("SameParameterValue")
    protected <T> void set(MaterialProperty<T> property, T value) {
        properties.put(property, value);
    }

    private static final int POWER_SAME_THRESHOLD = 100;

    /// Determines whether the formula given matches this Material, and therefore if it should be considered to be the output of the creation process.
    public boolean formulaMatches(@NotNull Formula formula) {
        if(original_formula == null) {
            return false;
        }
        if(!original_formula.base_material().is(formula.base_material().unwrap().orThrow())) {
            return false;
        }
        for(Power power : formula.powers().keySet()) {
            if(!original_formula.powers().containsKey(power)) {
                return false;
            }
        }
        for(Power power : original_formula.powers().keySet()) {
            if(!(formula.powers().containsKey(power))) {
                return false;
            }
            if(Math.abs(formula.powers().get(power) - original_formula.powers().get(power)) > POWER_SAME_THRESHOLD) {
                return false;
            }
        }
        return true;
    }

    public void setNotes(String notes) {
        if(notes.isEmpty()) {
            this.notes = "";
            return;
        }
        this.notes = notes;
    }

    public Component getDiscovererName(Level level) {
        if(!wasDiscovered()) {
            return Component.empty();
        }
        Player player = getDiscoverer(level);
        if(player == null) {
            assert discoverer != null;
            return Component.translatable("text.reactive.discovered_by").withStyle(ChatFormatting.LIGHT_PURPLE).append(discoverer.name);
        }
        setDiscoverer(player); // Resets the name of the player, so that if the player's username changes it will be up to date
        return Component.translatable("text.reactive.discovered_by").withStyle(ChatFormatting.LIGHT_PURPLE).append(player.getName());
    }

    public long getDiscoveryTime() {
        if(discoverer == null) {
            return 0L;
        }
        return discoverer.discovery_timestamp;
    }

    public record Discoverer(UUID uuid, String name, long discovery_timestamp) {
        public static final NBTSerializer<Discoverer> SERIALIZER = new NBTSerializer<>() {
            @Override
            public Tag encode(Discoverer data) {
                var tag = new CompoundTag();
                tag.putUUID("uuid", data.uuid);
                tag.putString("name", data.name);
                tag.putLong("timestamp", data.discovery_timestamp);
                return tag;
            }

            @Override
            public @Nullable Discoverer decode(Tag tag) {
                if(!(tag instanceof CompoundTag input)) {
                    return null;
                }
                try {
                    return new Discoverer(input.getUUID("uuid"), input.getString("name"), input.getLong("timestamp"));
                } catch (Exception e) {
                    ReactiveMod.LOGGER.error("Failed to load discoverer object: {}", e.toString());
                    return null;
                }
            }
        };
    }

    public MutableComponent formulaComponent() {
        var original_formula = this.getOriginalFormula();
        if(original_formula == null) {
            return Component.translatable("ui.reactive.no_formula");
        }
        Map<Power, Integer> original_powers = original_formula.powers();
        if(original_powers.isEmpty()) {
            return Component.translatable("ui.reactive.no_formula");
        }
        List<Component> formula_lines = new ArrayList<>();
        formula_lines.add(original_formula.base_material().value().getName(original_formula.base_material().value().getDefaultInstance()));
        for(Power power : original_powers.keySet().stream().sorted(Comparator.comparing(original_powers::get)).toList().reversed()) {
            formula_lines.add(Component.literal(power.getName() + ": " + Math.round(original_powers.get(power) / 16.0) + "%")
                    .withColor(shouldColorizeAgainstBlack(power.getColor()) ? power.getColor().hex : 0xFFFFFF));
        }
        MutableComponent readout_message = Component.empty();
        for(int i = 0; i < formula_lines.size(); i++) {
            readout_message.append(formula_lines.get(i));
            if(i < formula_lines.size() - 1) {
                readout_message.append("\n");
            }
        }
        return readout_message;
    }

    private static boolean shouldColorizeAgainstBlack(Color color) {
        int threshold = 90;
        if(FMLEnvironment.dist.isDedicatedServer()) {
            return false;
        }
        return ConfigMan.CLIENT.colorizeLitmusOutput.get() && (color.red > threshold || color.green > threshold || color.blue > threshold);
    }
}
