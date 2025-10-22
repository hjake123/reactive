package dev.hyperlynx.reactive.integration.patchouli;

import dev.hyperlynx.reactive.net.rxn.ReactionPageFetcher;
import net.minecraft.world.level.Level;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;

public class ReactionComponentProcessor implements IComponentProcessor {
    String reaction_alias = "error";
    @Override
    public void setup(Level level, IVariableProvider variables) {
        reaction_alias = variables.get("reaction").asString();
    }

    @Override
    public IVariable process(Level level, String key) {
        if(key.equals("formula")){
            try {
                return IVariable.wrap(ReactionPageFetcher.requestFormulaFor(reaction_alias));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        if(key.equals("lock")){
            return IVariable.wrap("reactive:reactions/" + reaction_alias + "_perfect");
        }
        return IVariable.empty();
    }
}
