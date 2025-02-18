package dev.hyperlynx.reactive.net;

import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.alchemy.Power;
import dev.hyperlynx.reactive.alchemy.rxn.CatalystEffectReaction;
import dev.hyperlynx.reactive.alchemy.rxn.CurseAssimilationReaction;
import dev.hyperlynx.reactive.alchemy.rxn.Reaction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import vazkii.patchouli.api.IVariable;

public class ReactionPageServer {
    public static void handlePageRequest(ReactionPageRequestPayload payload, IPayloadContext context) {
        Reaction reaction = ReactiveMod.REACTION_MAN.get(context.player().level(), payload.alias());
        StringBuilder formula = new StringBuilder();
        if(reaction != null) {
            formula.append(Component.translatable("docs.reactive.powers_label").getString());
            for (Power power : reaction.getReagents().keySet()) {
                formula.append(power.getName()).append("$(br)");
            }
            if (reaction instanceof CurseAssimilationReaction) {
                formula.append(Component.translatable("docs.reactive.curse_label").getString());
            }
            if (reaction.getReagents().keySet().isEmpty()) {
                formula.append(Component.translatable("docs.reactive.any_label").getString());
            }
            formula.append(Component.translatable("docs.reactive.stimulus_label").getString());
            switch (reaction.getStimulus()) {
                case NONE -> formula.append(Component.translatable("text.reactive.none").getString());
                case GOLD_SYMBOL -> formula.append(Component.translatable("block.reactive.gold_symbol").getString());
                case ELECTRIC -> formula.append(Component.translatable("text.reactive.electric_charge").getString());
                case SACRIFICE -> formula.append(Component.translatable("text.reactive.sacrificial").getString());
                case END_CRYSTAL -> formula.append(Component.translatable("item.minecraft.end_crystal").getString());
                case NO_ELECTRIC ->
                        formula.append(Component.translatable("text.reactive.lack_of").getString()).append(Component.translatable("text.reactive.electric_charge").getString());
                case NO_END_CRYSTAL ->
                        formula.append(Component.translatable("text.reactive.lack_of").getString()).append(Component.translatable("item.minecraft.end_crystal").getString());
            }
            if (reaction instanceof CatalystEffectReaction catre) {
                formula.append(Component.translatable("docs.reactive.catalyst_label").getString());
                formula.append(catre.getCatalyst().getDescription().getString());
            }
        }
        ReactiveMod.LOGGER.debug("Sending formula for {}", payload.alias());
        PacketDistributor.sendToAllPlayers(new ReactionPagePayload(payload.alias(), formula.toString()));
    }
}
