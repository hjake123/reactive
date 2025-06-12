package dev.hyperlynx.reactive.alchemy.material;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.SoundType;

/// A model and sound type that a material can have. Basically its "form factor" -- powder, solid, gel, etc.
/// To add a new material model, add:
/// - an entry to this enum
/// - a new block model (see `models/block/material`)
/// - a new entry in `models/item/material` with an override for the proper float id
/// - a new entry in `blockstates/material` with the proper string representation
public enum MaterialModel implements StringRepresentable {
    SALT("salt", 0.0F, SoundType.SAND),
    CRACKED("cracked", 1.0F, SoundType.DRIPSTONE_BLOCK),
    BUMPY("bumpy", 2.0F, SoundType.SLIME_BLOCK),
    STREAKED("streaked", 3.0F, SoundType.DEEPSLATE),
    SMOOTH("smooth", 4.0F, SoundType.WOOL);

    private final String name;
    private final float index;
    private final SoundType sound_type;

    MaterialModel(String name, float index, SoundType sound_type) {
        this.name = name;
        this.index = index;
        this.sound_type = sound_type;
    }

    @Override
    public String getSerializedName() {
        return name;
    }

    public float getModelIndex() {
        return index;
    }

    public SoundType getSoundType() {
        return sound_type;
    }

    public static MaterialModel fromName(String name) {
        return MaterialModel.valueOf(name.toUpperCase());
    }
}
