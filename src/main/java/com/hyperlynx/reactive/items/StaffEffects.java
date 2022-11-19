package com.hyperlynx.reactive.items;

import net.minecraft.world.entity.player.Player;

// A container class for the various effects that the staff items can have when right-clicked.
// Similar in concept to ReactionEffects
public class StaffEffects {
    /*
    - Radiant: Fires beams of light that damage entities and severely damage the Undead.
    - Blazing: Fires a jet of flame that also light the ground on fire.
    - Warped: Fires a short-range zap that breaks blocks and does damage.
    - Soulful: Fires a targeted medium range zap.
    - Arcane: Fires a multiple zaps that home in on surrounding enemies
    - Living: Applies regen to things around it
     */
    public static Player radiance(Player user){
        return user;
    }
}
