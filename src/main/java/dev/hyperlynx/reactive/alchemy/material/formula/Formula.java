package dev.hyperlynx.reactive.alchemy.material.formula;

import dev.hyperlynx.reactive.alchemy.Power;
import dev.hyperlynx.reactive.util.NBTExtras;
import dev.hyperlynx.reactive.util.NBTSerializer;
import net.minecraft.nbt.*;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public record Formula(Map<Power, Integer> powers, Item base_material) {
    public static final NBTSerializer<Formula> SERIALIZER = new NBTSerializer<>() {
        @Override
        public CompoundTag encode(Formula data) {
            CompoundTag tag = new CompoundTag();
            NBTExtras.encodeMap(
                    new NBTSerializer<>() {
                        @Override
                        public CompoundTag encode(Power data) {
                            CompoundTag tag = new CompoundTag();
                            tag.putString("power", data.getResourceLocation().toString());
                            return tag;
                        }

                        @Override
                        public @Nullable Power decode(Tag input) {
                            if(input instanceof CompoundTag c) {
                                Power.readPower(c, "power");
                            }
                            return null;
                        }
                    },
                    NBTExtras.INT,
                    data.powers);
            //noinspection deprecation
            tag.putString("base_material", data.base_material.builtInRegistryHolder().key().location().toString());
            return tag;
        }

        @Override
        public @Nullable Formula decode(Tag input) {
            return null;
        }
    };

    public Formula copy() {
        return new Formula(new HashMap<>(powers), base_material);
    }
}