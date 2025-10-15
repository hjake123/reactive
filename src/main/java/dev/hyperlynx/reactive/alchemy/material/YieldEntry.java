package dev.hyperlynx.reactive.alchemy.material;

public record YieldEntry(int max_input_items, int yield_per_input, String default_model, Float power_effect_multiplier) {
    /**
     * @return Whether this base yields cosmetic Materials, which have no functional properties. This is true if the multiplier is 0.
     */
    public boolean wool() {
        return power_effect_multiplier == 0.0F;
    }
}
