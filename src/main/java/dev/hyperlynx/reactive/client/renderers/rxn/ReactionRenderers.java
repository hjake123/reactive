package dev.hyperlynx.reactive.client.renderers.rxn;

import dev.hyperlynx.reactive.Registration;
import dev.hyperlynx.reactive.alchemy.Powers;
import dev.hyperlynx.reactive.alchemy.rxn.ReactionEffects;
import dev.hyperlynx.reactive.alchemy.rxn.Reactor;
import dev.hyperlynx.reactive.client.particles.ParticleScribe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.Level;

import java.util.*;

// This class mananges the renderers for reactions.
// This is important, since reaction rendering is no longer even known on the server side!
public class ReactionRenderers {
    public Map<String, ReactionRenderer> RENDERERS = new HashMap<>();

    public ReactionRenderers(){
        RENDERERS.put("curse_assimilation", this::curseRing);
        RENDERERS.put("discharge_annihilation", this::annihilationSmoke);
        RENDERERS.put("smoke_annihilation", this::smoke);
        RENDERERS.put("growth", this::growth);
        RENDERERS.put("flames", this::flamethrower);
        RENDERERS.put("size_shrink_effect", this::acid_based);
        RENDERERS.put("size_grow_effect", this::verdant_based);
        RENDERERS.put("ominous_transformation", this::ominous);
        RENDERERS.put("astral_curse_annihilation", this::creation);
    }

    public Iterable<ReactionRenderer> getRenderers(Iterable<String> aliases){
        List<ReactionRenderer> ret = new ArrayList<>();
        for(String alias : aliases){
            ret.add(RENDERERS.get(alias));
        }
        return ret;
    }

    public void smoke(Reactor reactor) {
        ParticleScribe.drawParticleCrucibleTop(reactor.getLevel(), ParticleTypes.LARGE_SMOKE, reactor.getBlockPos(), 0.3F);
    }

    public void annihilationSmoke(Reactor reactor) {
        ParticleScribe.drawParticleCrucibleTop(reactor.getLevel(), ParticleTypes.SMOKE, reactor.getBlockPos(), 0.2F);
    }

    public void curseRing(Reactor reactor) {
        ParticleScribe.drawParticleRing(reactor.getLevel(), ParticleTypes.ASH, reactor.getBlockPos(), 0.45, 0.7, 1);
    }

    public void growth(Reactor reactor) {
        ParticleScribe.drawParticleCrucibleTop(reactor.getLevel(), ParticleTypes.HAPPY_VILLAGER, reactor.getBlockPos(), 0.1F);
    }

    // Shoot flames from the crucible!
    public void flamethrower(Reactor reactor) {
        if(reactor.getLevel() == null) return;

        if(reactor.getPowerLevel(Powers.SOUL_POWER.get()) > 20){
            ParticleScribe.drawParticleCrucibleTop(reactor.getLevel(), ParticleTypes.SOUL_FIRE_FLAME, reactor.getBlockPos(), 0.1F, 0, 0.1, 0);
        }else{
            ParticleScribe.drawParticleCrucibleTop(reactor.getLevel(), ParticleTypes.FLAME, reactor.getBlockPos(), 0.1F, 0, 0.1, 0);
        }
    }

    public void creation(Reactor reactor){
        Set<BlockPos> points = ReactionEffects.getCreationPoints(reactor.getBlockPos());
        for(BlockPos pos : points){
            if(reactor.getLevel().getBlockState(pos).isAir())
                ParticleScribe.drawParticleSphere(Objects.requireNonNull(reactor.getLevel()), Registration.STARDUST_PARTICLE, pos, 0.5, 1.0, 1);
        }
    }

    public void ominous(Reactor reactor) {
        ParticleScribe.drawParticleCrucibleTop(reactor.getLevel(), ParticleTypes.OMINOUS_SPAWNING, reactor.getBlockPos(), 0.005F);
    }

    public void acid_based(Reactor reactor) {
        Level level = reactor.getLevel();
        if(level.random.nextFloat() < 0.1F)
            ParticleScribe.drawParticleCrucibleTop(reactor.getLevel(), Registration.ACID_BUBBLE_PARTICLE.getType(), reactor.getBlockPos());
    }

    public void verdant_based(Reactor reactor) {
        Level level = reactor.getLevel();
        if(level.random.nextFloat() < 0.1F)
            ParticleScribe.drawParticleCrucibleTop(level, ParticleTypes.HAPPY_VILLAGER, reactor.getBlockPos());
    }

    public void astral(Reactor reactor) {
        if(reactor.getPowerLevel(Powers.ASTRAL_POWER.get()) < reactor.getTotalPowerLevel())
            ParticleScribe.drawParticleRing(reactor.getLevel(), Registration.STARDUST_PARTICLE.getType(), reactor.getBlockPos(), 0.45, 0.7, 1);
    }
}
