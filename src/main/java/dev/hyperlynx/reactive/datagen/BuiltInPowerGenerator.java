package dev.hyperlynx.reactive.datagen;

import dev.hyperlynx.reactive.Registration;
import dev.hyperlynx.reactive.alchemy.Power;
import dev.hyperlynx.reactive.alchemy.Powers;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

import static dev.hyperlynx.reactive.alchemy.Powers.POWER_REGISTRY_KEY;

public class BuiltInPowerGenerator {
    public static final RegistrySetBuilder POWER_SET_BUILDER = new RegistrySetBuilder()
            .add(POWER_REGISTRY_KEY,
                    bootstrap -> {
                    bootstrap.register(
                            Powers.BLAZE_KEY,
                            new Power("blaze", 0xFFA300, Blocks.WATER, Registration.BLAZE_BOTTLE.get())
                    );

                    bootstrap.register(
                            Powers.MIND_KEY,
                            new Power("mind", 0x7A5BB5, Registration.DUMMY_MAGIC_WATER.get(), Registration.MIND_BOTTLE.get())
                    );

                    bootstrap.register(
                            Powers.SOUL_KEY,
                            new Power("soul", 0x60F5FA, Registration.DUMMY_FAST_WATER.get(), Registration.SOUL_BOTTLE.get())
                    );

                    bootstrap.register(
                            Powers.LIGHT_KEY,
                            new Power("light", 0xF6DAB4, Registration.DUMMY_MAGIC_WATER.get(), Registration.LIGHT_BOTTLE.get())
                    );

                    bootstrap.register(
                            Powers.WARP_KEY,
                            new Power("warp", 0x118066, Registration.DUMMY_NOISE_WATER.get(),Registration.WARP_BOTTLE.get())
                    );

                    bootstrap.register(
                            Powers.VITAL_KEY,
                            new Power("vital", 0xFF0606, Blocks.WATER, Registration.VITAL_BOTTLE.get())
                    );

                    bootstrap.register(
                            Powers.CURSE_KEY,
                            new Power("curse", 0x2D231D, Registration.DUMMY_NOISE_WATER.get(),null)
                    );

                    bootstrap.register(
                            Powers.VERDANT_KEY,
                            new Power("verdant", 0x3ADB00, Blocks.WATER, Registration.VERDANT_BOTTLE.get())
                    );

                    bootstrap.register(
                            Powers.BODY_KEY,
                            new Power("body", 0xAF5220, Blocks.WATER, Registration.BODY_BOTTLE.get())
                    );

                    bootstrap.register(
                            Powers.ACID_KEY,
                            new Power("caustic", 0x9D1E2D,  Blocks.WATER, Registration.ACID_BOTTLE.get())
                    );

                    bootstrap.register(
                            Powers.X_KEY,
                            new Power("esoteric_x", 0x9800FF, Registration.DUMMY_FAST_WATER.get(), null)
                    );

                    bootstrap.register(
                            Powers.Y_KEY,
                            new Power("esoteric_y", 0xADEA12, Registration.DUMMY_MAGIC_WATER.get(),null)
                    );

                    bootstrap.register(
                            Powers.Z_KEY,
                            new Power("esoteric_z", 0xDACCE8, Registration.DUMMY_NOISE_WATER.get(),null)
                    );

                    bootstrap.register(
                            Powers.FLOW_KEY,
                            new Power("flow", 0x7A82C4, Registration.DUMMY_FAST_WATER.get(),null)
                    );

                    bootstrap.register(
                            Powers.OMEN_KEY,
                            new Power("omen", 0x2A4455, Registration.DUMMY_SLOW_WATER.get(), Items.OMINOUS_BOTTLE)
                    );

                    bootstrap.register(
                            Powers.ASTRAL_KEY,
                            new Power("astral", 0xE9D7FA, Registration.DUMMY_MAGIC_WATER.get(),null)
                    );
            });

}
