package com.hyperlynx.reactive.alchemy.rxn;

import com.hyperlynx.reactive.be.CrucibleBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.Tags;

import java.util.UUID;

// Handles various circumstances that go beyond the normal logic of the mod.
public class SpecialCaseMan {

    public static void checkDissolveSpecialCases(CrucibleBlockEntity c, ItemEntity e){
        if(e.getItem().is(Tags.Items.ENDER_PEARLS)) enderPearlDissolve(c.getLevel(), c.getBlockPos(), e);
    }

    // Dissolving an Ender Pearl teleports you onto the crucible.
    public static void enderPearlDissolve(Level l, BlockPos p, ItemEntity e){
        for(int i = 0; i < 32; ++i) {
            ((ServerLevel) l).sendParticles(ParticleTypes.PORTAL, e.getX(), e.getY() + l.random.nextDouble() * 2.0, e.getZ(), 1, l.random.nextGaussian(), 0.0, l.random.nextGaussian(), 0.0);
        }

        UUID thrower = e.getThrower();
        if(thrower != null) {
            Player player = l.getPlayerByUUID(thrower);
            if(player != null){
                player.teleportTo(p.getX()+0.5, p.getY() + 0.85, p.getZ() + 0.5);
            }
        }

    }
}
