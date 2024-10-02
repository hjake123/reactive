package com.hyperlynx.reactive.alchemy.rxn;

import net.minecraft.network.chat.Component;

public record ReactionStatusEntry(Reaction.Status status, String reaction_alias) {
    public static ReactionStatusEntry of(String status, String reaction_alias){
        return new ReactionStatusEntry(Reaction.Status.valueOf(status), reaction_alias);
    }

    public String getStatusAsString(){
        return status.toString();
    }

    public Component getName() {
        return Component.translatable("reaction.reactive." + reaction_alias);
    }

    public static ReactionStatusEntry stable(){
        return new ReactionStatusEntry(Reaction.Status.STABLE, "");
    }
}
