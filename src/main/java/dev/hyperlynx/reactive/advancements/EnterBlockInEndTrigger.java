package dev.hyperlynx.reactive.advancements;

import com.mojang.serialization.Codec;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.critereon.CriterionValidator;
import net.minecraft.advancements.critereon.EnterBlockTrigger;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class EnterBlockInEndTrigger extends EnterBlockTrigger {
    public void trigger(ServerPlayer player, BlockState state) {
        if(Level.END.equals(player.level().dimension())){
            this.trigger(player, (instance) -> instance.matches(state));
        }
    }
}
