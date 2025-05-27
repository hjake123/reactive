package dev.hyperlynx.reactive.alchemy.material;

/// It's MaterialMan's time to shine!
/// Manages the world's Materials.
/// Materials are stored in a Level capability and created by crafting a Material Block.
/// Each Material has an associated integer ID, which is its position in the list.
public class MaterialMan {
    public static Material fetch(int materialId) {
        throw new RuntimeException("Not yet implemented.");
        // TODO -- Some kind of list capability on the Overworld.
    }
}
