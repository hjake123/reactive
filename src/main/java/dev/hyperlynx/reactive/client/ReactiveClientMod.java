package dev.hyperlynx.reactive.client;

import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.client.renderers.rxn.ReactionRenderers;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

import java.util.Objects;

@Mod(value = ReactiveMod.MODID, dist = Dist.CLIENT)
public class ReactiveClientMod {
    public static final ReactionRenderers REACTION_RENDERERS = new ReactionRenderers();

    public ReactiveClientMod(ModContainer container) {
        ClientRegistration.init(Objects.requireNonNull(container.getEventBus()));
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }
}
