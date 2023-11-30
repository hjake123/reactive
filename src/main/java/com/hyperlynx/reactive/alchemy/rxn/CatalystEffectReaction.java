package com.hyperlynx.reactive.alchemy.rxn;

import com.hyperlynx.reactive.alchemy.Power;
import com.hyperlynx.reactive.be.CrucibleBlockEntity;
import com.hyperlynx.reactive.blocks.CrucibleBlock;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;

import java.util.function.Function;

public class CatalystEffectReaction extends EffectReaction{
    Item reactant;

    public CatalystEffectReaction(String alias, Function<CrucibleBlockEntity, CrucibleBlockEntity> function, Function<CrucibleBlockEntity, CrucibleBlockEntity> render, Power required_power, Item reactant) {
        super(alias, function, render, required_power);
        this.reactant = reactant;
    }

    public Item getCatalyst(){
        return reactant;
    }

    @Override
    public Status conditionsMet(CrucibleBlockEntity crucible) {
        Status reaction_status = super.conditionsMet(crucible);
        for(Entity entity_inside : CrucibleBlock.getEntitesInside(crucible.getBlockPos(), crucible.getLevel())) {
            if (entity_inside instanceof ItemEntity item_ent && item_ent.getItem().is(reactant)) {
                // The catalyst condition is met; return the check without catalyst consideration.
                return reaction_status;
            }
        }
        // The catalyst is missing.
        // Return the super status, unless it is meant to react, in which case switch it to a missing condition.
        return reaction_status == Status.REACTING ? Status.MISSING_CATALYST : reaction_status;
    }
}
