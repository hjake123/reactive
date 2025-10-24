package dev.hyperlynx.reactive.alchemy.material;

import dev.hyperlynx.reactive.alchemy.Power;
import dev.hyperlynx.reactive.util.Color;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;

import java.util.Map;

public class ColorMaterialProperty extends MaterialProperty<Color> {
    @Override
    public boolean requirementsMet(Map<Power, Integer> formula) {
        // All materials have a color.
        return true;
    }

    @Override
    public Color instance(Map<Power, Integer> formula) {
        Color mix_color = Color.black();
        int totalpp = 0;
        for (Power p : formula.keySet()) {
            if(!p.invisible)
                totalpp += formula.get(p);
        }
        mix_color.setMixColor(Color.white(), formula, totalpp, Math.max(1600, totalpp));
        return mix_color;
    }

    @Override
    public void save(CompoundTag tag, Object value) {
        if(value instanceof Color color) {
            tag.put(getTagName(), IntTag.valueOf(color.hex()));
        }
    }

    @Override
    public Color fromTag(CompoundTag tag) {
        return new Color(tag.getInt(getTagName()));
    }
}
