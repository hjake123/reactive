package dev.hyperlynx.reactive.integration.ponder;

import com.simibubi.create.infrastructure.ponder.AllCreatePonderTags;
import dev.hyperlynx.reactive.Registration;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.createmod.ponder.foundation.PonderIndex;
import net.createmod.ponder.foundation.content.BasePonderPlugin;
import net.minecraft.resources.ResourceLocation;

public class ReactivePonderPlugin extends BasePonderPlugin {
    public static void clientInit() {
        PonderIndex.addPlugin(new ReactivePonderPlugin());
    }

    @Override
    public void registerTags(PonderTagRegistrationHelper<ResourceLocation> helper) {
        helper.addToTag(AllCreatePonderTags.DISPLAY_SOURCES).add(Registration.CRUCIBLE_ITEM.getId());
    }
}
