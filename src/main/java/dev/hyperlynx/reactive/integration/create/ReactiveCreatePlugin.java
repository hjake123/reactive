package dev.hyperlynx.reactive.integration.create;

import com.simibubi.create.api.behaviour.display.DisplaySource;
import com.simibubi.create.foundation.data.CreateRegistrate;
import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.Registration;
import net.minecraft.core.registries.Registries;
import net.minecraftforge.eventbus.api.IEventBus;

public class ReactiveCreatePlugin {
    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(ReactiveMod.MODID);

    public static void init(IEventBus bus){
        REGISTRATE.registerEventListeners(bus);
        REGISTRATE.displaySource("crucible_powers", CrucibleDisplaySource::new)
                .onRegisterAfter(Registries.BLOCK_ENTITY_TYPE,
                        source ->
                                DisplaySource.BY_BLOCK_ENTITY.add(Registration.CRUCIBLE_BE.get(), source)
                )
                .register();
        REGISTRATE.displaySource("crucible_integrity", CrucibleIntegrityDisplaySource::new)
                .onRegisterAfter(Registries.BLOCK_ENTITY_TYPE,
                        source ->
                                DisplaySource.BY_BLOCK_ENTITY.add(Registration.CRUCIBLE_BE.get(), source)
                )
                .register();
    }
}
