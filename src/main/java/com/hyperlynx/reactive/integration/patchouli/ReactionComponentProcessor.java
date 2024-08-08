package com.hyperlynx.reactive.integration.patchouli;

import com.hyperlynx.reactive.ReactiveMod;
import com.hyperlynx.reactive.alchemy.Power;
import com.hyperlynx.reactive.alchemy.rxn.CatalystEffectReaction;
import com.hyperlynx.reactive.alchemy.rxn.CurseAssimilationReaction;
import com.hyperlynx.reactive.alchemy.rxn.Reaction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;
import vazkii.patchouli.api.PatchouliAPI;

public class ReactionComponentProcessor implements IComponentProcessor {
    String reaction_alias = "error";
    @Override
    public void setup(Level level, IVariableProvider variables) {
        reaction_alias = variables.get("reaction").asString();
    }

    @Override
    public IVariable process(Level level, String key) {
        if(key.equals("formula")){
            for(Reaction reaction : ReactiveMod.REACTION_MAN.getReactions()){
                if(reaction.getAlias().equals(reaction_alias)){
                    StringBuilder formula = new StringBuilder();
                    formula.append(Component.translatable("docs.reactive.powers_label").getString());
                    for(Power power : reaction.getReagents().keySet()){
                        formula.append(power.getName()).append("$(br)");
                    }
                    if(reaction instanceof CurseAssimilationReaction){
                        formula.append(Component.translatable("docs.reactive.curse_label").getString());
                    }
                    if(reaction.getReagents().keySet().isEmpty()){
                        formula.append(Component.translatable("docs.reactive.any_label").getString());
                    }
                    formula.append(Component.translatable("docs.reactive.stimulus_label").getString());
                    switch(reaction.getStimulus()){
                        case NONE -> formula.append(Component.translatable("text.reactive.none").getString());
                        case GOLD_SYMBOL -> formula.append(Component.translatable("block.reactive.gold_symbol").getString());
                        case ELECTRIC -> formula.append(Component.translatable("text.reactive.electric_charge").getString());
                        case SACRIFICE -> formula.append(Component.translatable("text.reactive.sacrificial").getString());
                        case END_CRYSTAL -> formula.append(Component.translatable("item.minecraft.end_crystal").getString());
                        case NO_ELECTRIC -> formula.append(Component.translatable("text.reactive.lack_of").getString()).append(Component.translatable("text.reactive.electric_charge").getString());
                        case NO_END_CRYSTAL -> formula.append(Component.translatable("text.reactive.lack_of").getString()).append(Component.translatable("item.minecraft.end_crystal").getString());
                    }
                    if(reaction instanceof CatalystEffectReaction catre){
                        formula.append(Component.translatable("docs.reactive.catalyst_label").getString());
                        formula.append(catre.getCatalyst().getDescription().getString());
                    }
                    return IVariable.wrap(formula.toString());
                }
            }
            return IVariable.empty();
        }
        if(key.equals("lock")){
            for(Reaction reaction : ReactiveMod.REACTION_MAN.getReactions()) {
                if (reaction.getAlias().equals(reaction_alias)) {
                    return IVariable.wrap("reactive:reactions/" + reaction_alias + "_perfect");
                }
            }
            return IVariable.empty();
        }
        return IVariable.empty();
    }
}
