package com.hyperlynx.reactive.alchemy.rxn.docs;

import com.hyperlynx.reactive.ReactiveMod;
import com.hyperlynx.reactive.alchemy.Power;
import com.hyperlynx.reactive.alchemy.rxn.CurseAssimilationReaction;
import com.hyperlynx.reactive.alchemy.rxn.Reaction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
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
                    formula.append("Powers: $(br)");
                    for(Power power : reaction.getReagents().keySet()){
                        formula.append(power.getName()).append("$(br)");
                    }
                    if(reaction instanceof CurseAssimilationReaction){
                        formula.append("Curse$(br)");
                    }
                    if(reaction.getReagents().keySet().isEmpty()){
                        formula.append("Any$(br)");
                    }
                    formula.append("$(br)Stimulus:$(br)");
                    switch(reaction.getStimulus()){
                        case NONE -> formula.append("None");
                        case GOLD_SYMBOL -> formula.append("Gold Symbol");
                        case ELECTRIC -> formula.append("Electric Charge");
                        case SACRIFICE -> formula.append("Sacrificial");
                        case END_CRYSTAL -> formula.append("End Crystal");
                        case NO_ELECTRIC -> formula.append("Lack of Electric Charge");
                        case NO_END_CRYSTAL -> formula.append("Lack of End Crystal");
                    }
                    return IVariable.wrap(formula.toString());
                }
            }
            return IVariable.wrap("There was no reaction matching the alias " + reaction_alias);
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
