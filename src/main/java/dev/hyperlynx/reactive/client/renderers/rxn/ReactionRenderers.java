package dev.hyperlynx.reactive.client.renderers.rxn;

import dev.hyperlynx.reactive.Registration;
import dev.hyperlynx.reactive.alchemy.Powers;
import dev.hyperlynx.reactive.alchemy.rxn.Reaction;
import dev.hyperlynx.reactive.alchemy.rxn.ReactionEffects;
import dev.hyperlynx.reactive.alchemy.rxn.ReactionStatusEntry;
import dev.hyperlynx.reactive.alchemy.rxn.Reactor;
import dev.hyperlynx.reactive.client.particles.EnergyParticle;
import dev.hyperlynx.reactive.client.particles.ParticleScribe;
import dev.hyperlynx.reactive.integration.kubejs.ReactiveKubeJSPlugin;
import dev.hyperlynx.reactive.net.rxn.ReactionStatusMessage;
import dev.hyperlynx.reactive.particles.EnergyParticleOptions;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.ModList;

import java.util.*;

// This class mananges the renderers for reactions.
// This is important, since reaction rendering is no longer even known on the server side!
public class ReactionRenderers {
    public final Map<String, ReactionRenderer> RENDERERS = new HashMap<>();

    public ReactionRenderers(){
        RENDERERS.put("curse_assimilation", this::curseRing);
        RENDERERS.put("discharge_annihilation", this::annihilationSmoke);
        RENDERERS.put("smoke_annihilation", this::smoke);
        RENDERERS.put("growth", this::growth);
        RENDERERS.put("flames", this::flamethrower);
        RENDERERS.put("astral_curse_annihilation", this::creation);
        RENDERERS.put("cryo", this::snow);
        RENDERERS.put("nodule", this::warpEnergy);
        RENDERERS.put("astral", this::astralRing);
    }

    public static void handleReactionStatusMessage(ReactionStatusMessage message) {
        Level level = Minecraft.getInstance().level;
        Reactor reactor = message.target().getReactor(level);
        if(reactor == null){
            return;
        }

        reactor.clearRenderReactions();
        for(ReactionStatusEntry entry : message.statuses()){
            if(entry.status() == Reaction.Status.REACTING){
                reactor.addRenderReaction(entry.reaction_alias());
            }
        }
    }

    public Iterable<ReactionRenderer> getRenderers(Iterable<String> aliases){
        if(ModList.get().isLoaded("kubejs")){
            ReactiveKubeJSPlugin.REACTION_EFFECT_CACHE.requestRenderers();
        }

        List<ReactionRenderer> ret = new ArrayList<>();
        for(String alias : aliases){
            if(RENDERERS.containsKey(alias)){
                ret.add(RENDERERS.get(alias));
            }
        }
        return ret;
    }

    public void smoke(Reactor reactor) {
        ParticleScribe.drawParticleReactionSurface(reactor.obtainLevel(), ParticleTypes.LARGE_SMOKE, reactor, 0.3F);
    }

    public void annihilationSmoke(Reactor reactor) {
        ParticleScribe.drawParticleReactionSurface(reactor.obtainLevel(), ParticleTypes.SMOKE, reactor, 0.2F);
    }

    public void curseRing(Reactor reactor) {
        RandomSource random = reactor.obtainLevel().random;
        if(random.nextFloat() < 0.3) {
            Vec3 random_offset = new Vec3(random.nextFloat() * 0.4 - 0.2, random.nextFloat() * 0.4 - 0.4, random.nextFloat() * 0.4 - 0.2);
            ParticleScribe.drawExactParticleRing(reactor.obtainLevel(), new EnergyParticleOptions(0.05F, Powers.CURSE_POWER.get().getColor(), reactor.getPos(), false, true), reactor.getPos().add(random_offset), 0, 0.7, 1);
        }
    }

    public void astralRing(Reactor reactor) {
        RandomSource random = reactor.obtainLevel().random;
        if (random.nextFloat() < 0.3) {
            Vec3 random_offset = new Vec3(random.nextFloat() * 0.4 - 0.2, random.nextFloat() * 0.4 - 0.4, random.nextFloat() * 0.4 - 0.2);
            ParticleScribe.drawExactParticleRing(reactor.obtainLevel(), new EnergyParticleOptions(0.05F, Powers.ASTRAL_POWER.get().getColor(), reactor.getPos(), true, true), reactor.getPos().add(random_offset), 0, 0.7, 1);
        }
    }

    public void growth(Reactor reactor) {
        ParticleScribe.drawParticleReactionSurface(reactor.obtainLevel(), ParticleTypes.HAPPY_VILLAGER, reactor, 0.1F);
    }

    // Shoot flames from the crucible!
    public void flamethrower(Reactor reactor) {
        if(reactor.obtainLevel() == null) return;

        if(reactor.getPowerLevel(Powers.SOUL_POWER.get()) > 20){
            ParticleScribe.drawParticleReactionSurface(reactor.obtainLevel(), ParticleTypes.SOUL_FIRE_FLAME, reactor, 0.1F, 0, 0.1, 0);
        }else{
            ParticleScribe.drawParticleReactionSurface(reactor.obtainLevel(), ParticleTypes.FLAME, reactor, 0.1F, 0, 0.1, 0);
        }
    }

    public void creation(Reactor reactor){
        Set<BlockPos> points = ReactionEffects.getCreationPoints(reactor.blockPos());
        for(BlockPos pos : points){
            if(reactor.obtainLevel().getBlockState(pos).isAir())
                ParticleScribe.drawParticleSphere(Objects.requireNonNull(reactor.obtainLevel()), Registration.STARDUST_PARTICLE, pos, 0.5, 1.0, 1);
        }
    }

    public void snow(Reactor reactor) {
        AABB aoe = new AABB(reactor.blockPos());
        aoe = aoe.inflate(5);
        ParticleScribe.drawParticleBox(reactor.obtainLevel(), ParticleTypes.SNOWFLAKE, aoe, 1);
    }

    public void warpEnergy(Reactor reactor) {
        if(reactor.obtainLevel().random.nextFloat() < 0.2F) {
            ParticleScribe.drawParticleBox(reactor.obtainLevel(), new EnergyParticleOptions(0.1F, Powers.WARP_POWER.get().getColor(), reactor.getPos(), true), AABB.ofSize(reactor.getPos(), 1, 1, 1), 1);
        }
    }
}
