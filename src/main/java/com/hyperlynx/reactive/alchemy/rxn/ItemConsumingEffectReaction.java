package com.hyperlynx.reactive.alchemy.rxn;

import com.hyperlynx.reactive.alchemy.Power;
import com.hyperlynx.reactive.be.CrucibleBlockEntity;
import com.hyperlynx.reactive.blocks.CrucibleBlock;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;

import java.util.function.Function;

public class ItemConsumingEffectReaction extends EffectReaction{
    Item reactant;

    public ItemConsumingEffectReaction(String alias, Function<CrucibleBlockEntity, CrucibleBlockEntity> function, Function<CrucibleBlockEntity, CrucibleBlockEntity> render, Power required_power, Item reactant) {
        super(alias, function, render, required_power);
        this.reactant = reactant;
    }

    @Override
    public boolean conditionsMet(CrucibleBlockEntity crucible) {
        for(Entity entity_inside : CrucibleBlock.getEntitesInside(crucible.getBlockPos(), crucible.getLevel())) {
            if (entity_inside instanceof ItemEntity item_ent && item_ent.getItem().is(reactant)) {
                item_ent.getItem().shrink(1);
                if(item_ent.getItem().getCount() == 0)
                    item_ent.kill();
                return super.conditionsMet(crucible);
            }
        }
        return false;
    }
}
