package com.hyperlynx.reactive.integration.create;

import com.hyperlynx.reactive.ReactiveMod;
import com.hyperlynx.reactive.Registration;
import com.hyperlynx.reactive.be.CrucibleBlockEntity;
import com.simibubi.create.content.redstone.displayLink.AllDisplayBehaviours;
import com.simibubi.create.foundation.ponder.PonderRegistry;
import com.simibubi.create.infrastructure.ponder.AllPonderTags;
import net.minecraft.resources.ResourceLocation;

public class ReactiveCreatePlugin {
    static CrucibleDisplaySource CRUCIBLE_DISPLAY_SOURCE = new CrucibleDisplaySource();
    static CrucibleIntegrityDisplaySource CRUCIBLE_INTEGRITY_DISPLAY_SOURCE = new CrucibleIntegrityDisplaySource();

    public static void init(){
        AllDisplayBehaviours.register(new ResourceLocation(ReactiveMod.MODID, "crucible_powers"), CRUCIBLE_DISPLAY_SOURCE);
        AllDisplayBehaviours.assignBlockEntity(CRUCIBLE_DISPLAY_SOURCE, Registration.CRUCIBLE_BE_TYPE.get());
        AllDisplayBehaviours.register(new ResourceLocation(ReactiveMod.MODID, "crucible_integrity"), CRUCIBLE_INTEGRITY_DISPLAY_SOURCE);
        AllDisplayBehaviours.assignBlockEntity(CRUCIBLE_INTEGRITY_DISPLAY_SOURCE, Registration.CRUCIBLE_BE_TYPE.get());
    }

    public static void initClient(){
        PonderRegistry.TAGS.forTag(AllPonderTags.DISPLAY_SOURCES).add(Registration.CRUCIBLE_ITEM.get());
    }
}
