package dev.hyperlynx.reactive.alchemy.material;

import net.minecraft.util.StringRepresentable;

public enum MaterialModel implements StringRepresentable {
    SALT("salt", 0.0F),
    CRACKED("cracked", 1),
    BUMPY("bumpy", 2);

    private final String name;
    private final float index;

    MaterialModel(String name, float index) {
        this.name = name;
        this.index = index;
    }

    @Override
    public String getSerializedName() {
        return name;
    }

    public float getModelIndex() {
        return index;
    }
}
