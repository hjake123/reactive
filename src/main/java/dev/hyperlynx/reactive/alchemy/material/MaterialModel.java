package dev.hyperlynx.reactive.alchemy.material;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.SoundType;

public enum MaterialModel implements StringRepresentable {
    SALT("salt", 0.0F, SoundType.SAND),
    CRACKED("cracked", 1, SoundType.DRIPSTONE_BLOCK),
    BUMPY("bumpy", 2, SoundType.SLIME_BLOCK);

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
