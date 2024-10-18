package dev.hyperlynx.reactive.be;

import dev.hyperlynx.reactive.Registration;
import dev.hyperlynx.reactive.client.particles.ParticleScribe;
import dev.hyperlynx.reactive.util.BeamHelper;
import dev.hyperlynx.reactive.ConfigMan;
import dev.hyperlynx.reactive.util.WorldSpecificValue;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

public class SymbolBlockEntity extends BlockEntity {

    public Direction facing = Direction.DOWN;
    public Item symbol_item = Items.BARRIER;

    public SymbolBlockEntity(BlockPos pos, BlockState state, Item item) {
        super(Registration.SYMBOL_BE_TYPE.get(), pos, state);
        NeoForge.EVENT_BUS.register(this);
        setItem(item);
    }

    public SymbolBlockEntity(BlockPos pos, BlockState state) {
        super(Registration.SYMBOL_BE_TYPE.get(), pos, state);
    }

    public void setFacing(Direction facing) {
        this.facing = facing;
    }
    public void setItem(Item item){ this.symbol_item = item; }

    // If you die near an Occult Symbol, it breaks, and you come back as an undead being.
    @SubscribeEvent
    public void onDeath(LivingDeathEvent event) {
        if(event.getEntity().level().isClientSide || !symbol_item.getDefaultInstance().is(Registration.OCCULT_SYMBOL_ITEM.get())
                ||  !(event.getEntity() instanceof Player)){
            return;
        }

        double dist = BeamHelper.distance(event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ(), this.getBlockPos().getX(), this.getBlockPos().getY(), this.getBlockPos().getZ());
        if(dist > ConfigMan.COMMON.crucibleRange.get()) {
            return;
        }

        Level level = event.getEntity().level();
        if(level.getBlockState(this.getBlockPos()).isAir())
            return; // Needed since the event sometimes fires more than once for the same death.

        level.setBlock(this.getBlockPos(), Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
        level.playSound(null, this.getBlockPos(), SoundEvents.BEACON_DEACTIVATE, SoundSource.BLOCKS, 0.7F, 0.90F + level.random.nextFloat()*0.1F);

        if(WorldSpecificValue.getBool("occult_death_spawn_zombie", 0.5F)){
            Zombie zombie = new Zombie(level);
            zombie.setPos(event.getEntity().getPosition(0f));
            zombie.setCustomName(event.getEntity().getDisplayName());
            zombie.setCustomNameVisible(true);
            level.addFreshEntity(zombie);
        }else{
            Skeleton skeleton = new Skeleton(EntityType.SKELETON, level);
            skeleton.setPos(event.getEntity().getPosition(0f));
            skeleton.setCustomName(event.getEntity().getDisplayName());
            skeleton.setCustomNameVisible(true);
            level.addFreshEntity(skeleton);
        }

        for(int i = 0; i < 4; i++)
            ParticleScribe.drawParticleZigZag(level, Registration.SMALL_BLACK_RUNE_PARTICLE, this.getBlockPos(), event.getEntity().blockPosition().above(), 5, 4, 0.7);
    }
}
