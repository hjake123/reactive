package dev.hyperlynx.reactive.alchemy.rxn;

import dev.hyperlynx.reactive.alchemy.Power;
import dev.hyperlynx.reactive.be.CrucibleBlockEntity;
import dev.hyperlynx.reactive.util.WorldSpecificValue;
import net.minecraft.world.level.Level;

public class AssimilationReaction extends Reaction{
    Power consumedPower;
    Power producedPower;
    int rate;

    public AssimilationReaction(String alias, Power producedPower, Power consumedPower){
        super(alias, 0);
        reagents.put(producedPower, WorldSpecificValue.get(alias+"r"+1, 1, 100));
        this.producedPower = producedPower;
        this.consumedPower = consumedPower;
        rate = WorldSpecificValue.get(alias+"rate", 10, 20);
        reagents.put(consumedPower, rate);
    }

    @Override
    public void run(CrucibleBlockEntity crucible) {
        super.run(crucible);
        crucible.expendPower(consumedPower, rate);
        crucible.addPower(producedPower, rate);
    }

    @Override
    public void render(final Level l, final CrucibleBlockEntity crucible) {

    }

    @Override
    public boolean isPerfect(CrucibleBlockEntity crucible) {
        return true;
    }

    @Override
    public String toString() {
        return super.toString() + " - assimilation reaction";
    }
}
