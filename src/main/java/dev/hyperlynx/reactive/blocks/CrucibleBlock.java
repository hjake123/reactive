package dev.hyperlynx.reactive.blocks;

import dev.hyperlynx.reactive.Registration;
import dev.hyperlynx.reactive.alchemy.Power;
import dev.hyperlynx.reactive.alchemy.PowerBottleInsertContext;
import dev.hyperlynx.reactive.alchemy.Powers;
import dev.hyperlynx.reactive.alchemy.special.SpecialCaseMan;
import dev.hyperlynx.reactive.alchemy.WorldSpecificValues;
import dev.hyperlynx.reactive.be.CrucibleBlockEntity;
import dev.hyperlynx.reactive.client.particles.ParticleScribe;
import dev.hyperlynx.reactive.items.PowerBottleItem;
import dev.hyperlynx.reactive.util.WorldSpecificValue;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CrucibleBlock extends CrucibleShapedBlock implements EntityBlock, WorldlyContainerHolder {
    public static final BooleanProperty FULL = BooleanProperty.create("full");

    public CrucibleBlock(Properties p) {
        super(p);
        registerDefaultState(stateDefinition.any().setValue(FULL, false));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FULL);
        super.createBlockStateDefinition(builder);
    }

    protected static final VoxelShape INSIDE = Block.box(3, 3, 3, 13, 9, 13);

    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new CrucibleBlockEntity(pos, state);
    }

    public static List<Entity> getEntitesInside(BlockPos pos, Level level){
        return level.getEntities(null, INSIDE.bounds().move(pos));
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos my_pos, Block block, BlockPos neighbor_pos, boolean unknown_flag) {
        int signal = level.getDirectSignalTo(my_pos);
        if(signal > WorldSpecificValue.get("redstone_void_threshold", 11, 14)){
            if(level.getBlockEntity(my_pos) instanceof CrucibleBlockEntity crucible){
                switch(WorldSpecificValue.get("redstone_behavior", 0, 2)){
                    case 0 -> {
                        CrucibleBlockEntity.empty(level, my_pos, state, crucible);
                        crucible.setDirty();
                    }
                    case 1 -> {
                        if(crucible.getTotalPowerLevel() > 1500)
                            CrucibleBlockEntity.empty(level, my_pos, state, crucible);
                        else
                            crucible.expendAnyPowerExcept(Powers.CURSE_POWER.get(), 1500);
                        crucible.setDirty();
                    }
                    case 2 -> state.setValue(FULL, false);
                }
                CrucibleBlockEntity.empty(level, my_pos, state, crucible);
                level.playSound(null, my_pos, SoundEvents.BEACON_DEACTIVATE, SoundSource.BLOCKS, 0.7F, 0.90F + level.random.nextFloat()*0.1F);
            }
        }
    }

    private boolean checkFluidInStack(IFluidHandlerItem container, Fluid criterion){
        if(container == null)
            return false;
        for(int i = 0; i < container.getTanks(); i++){
            if(container.getFluidInTank(i).getFluid().isSame(criterion) && container.getFluidInTank(i).getAmount() >= 1000)
                return true;
        }
        return false;
    }

    @Override
    protected @NotNull InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if(level.isClientSide()){
            // Workaround to make sure that acid bucket addition instantly updates the mix color.
            if (stack.is(Registration.ACID_BUCKET.get())) {
                BlockEntity crucible = level.getBlockEntity(pos);
                if(!(crucible instanceof CrucibleBlockEntity c)){
                    return InteractionResult.TRY_WITH_EMPTY_HAND;
                }
                c.setStartingColor(Powers.ACID_POWER.get().getColor());
            }

            // If it wasn't an acid bucket, just pass on the client side.
            return InteractionResult.SUCCESS;
        }

        if(stack.is(Registration.LITMUS_PAPER.get())) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }

        if(stack.is(Items.OMINOUS_BOTTLE)){
            if(!(level.getBlockEntity(pos) instanceof CrucibleBlockEntity crucible))
                return InteractionResult.FAIL;
            CrucibleBlockEntity.insertPowerBottle(crucible, new PowerBottleInsertContext(new UseOnContext(level, player, hand, stack, hit)));
            return InteractionResult.SUCCESS;
        }

        if (!state.getValue(FULL)) {
            IFluidHandlerItem fluidHandler = FluidUtil.getFluidHandler(player.getItemInHand(hand).copy()).orElse(null);

            if (checkFluidInStack(fluidHandler, Fluids.WATER)) {
                becomeFull(state, level, pos, (ServerPlayer) player);
                if (((ServerPlayer) player).gameMode.isSurvival()) {
                    fluidHandler.drain(new FluidStack(Fluids.WATER, 1000), IFluidHandler.FluidAction.EXECUTE);
                    player.setItemInHand(hand, fluidHandler.getContainer());
                }
                return InteractionResult.CONSUME;
            }
            if (checkFluidInStack(fluidHandler, Fluids.LAVA)) {
                lavaCrucibleFill(level, pos, (ServerPlayer) player);
                if (((ServerPlayer) player).gameMode.isSurvival()) {
                    fluidHandler.drain(new FluidStack(Fluids.LAVA, 1000), IFluidHandler.FluidAction.EXECUTE);
                    player.setItemInHand(hand, fluidHandler.getContainer());
                }
                return InteractionResult.CONSUME;
            }
            if (player.getItemInHand(hand).is(Registration.ACID_BUCKET.get())) {
                BlockEntity crucible = level.getBlockEntity(pos);
                if (!(crucible instanceof CrucibleBlockEntity c)) {
                    return InteractionResult.TRY_WITH_EMPTY_HAND;
                }
                becomeFull(state, level, pos, (ServerPlayer) player);
                c.addPower(Powers.ACID_POWER.get(), WorldSpecificValues.BOTTLE_RETURN.get()*3);
                c.setDirty();
                if (((ServerPlayer) player).gameMode.isSurvival()) {
                    player.setItemInHand(hand, Items.BUCKET.getDefaultInstance());
                }
                return InteractionResult.CONSUME;
            }
        }

        if(state.getValue(FULL)){
            if(player.isShiftKeyDown()){
                level.playSound(null, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 0.6F, 0.8F);
                level.setBlock(pos, state.setValue(FULL, false), Block.UPDATE_CLIENTS);
                return InteractionResult.SUCCESS;
            }

            BlockEntity crucible = level.getBlockEntity(pos);
            if(!(crucible instanceof CrucibleBlockEntity c)){
                return InteractionResult.TRY_WITH_EMPTY_HAND;
            }

            if(player.getItemInHand(hand).is(Items.GLASS_BOTTLE)){
                extractGlassBottle(state, level, pos, player, hand, c);
            }
            // Collect bottles of mundane Powers.
            if(player.getItemInHand(hand).is(Registration.QUARTZ_BOTTLE.get())){
                extractQuartzBottle(level, pos, player, hand, c);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    private static void becomeFull(BlockState state, Level level, BlockPos pos, ServerPlayer player) {
        if(level.dimensionType().ultraWarm()){
            netherCrucibleFill(level, pos, player);
        }else{
            level.setBlock(pos, state.setValue(FULL, true), Block.UPDATE_CLIENTS);
            level.playSound(null, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 0.4F, 1F);
        }
    }

    private static void extractQuartzBottle(Level level, BlockPos pos, Player player, InteractionHand hand, CrucibleBlockEntity c) {
        for(Power p : c.getPowerMap().keySet()){
            if(!p.hasBottle())
                continue;
            if(c.getPowerLevel(p) > PowerBottleItem.BOTTLE_COST){
                c.expendPower(p, PowerBottleItem.BOTTLE_COST);
                player.addItem(SpecialCaseMan.checkBottleSpecialCases(c, p.getBottle()));
                player.getItemInHand(hand).shrink(1);
                level.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 0.8F, 1F);
            }
        }
        c.setDirty();
    }

    private static void extractGlassBottle(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, CrucibleBlockEntity c) {
        int amount = player.getItemInHand(hand).getCount();
        amount = Math.min(amount, 3);
        for(int i = 0; i < amount; i++) {
            if (c.getTotalPowerLevel() == 0) {
                player.addItem(Items.POTION.getDefaultInstance());
            } else {
                ItemStack potion;
                if (c.getPowerLevel(Powers.BODY_POWER.get()) > 10) {
                    potion = PotionContents.createItemStack(Items.POTION, Potions.THICK);
                } else if (c.getPowerLevel(Powers.ACID_POWER.get()) > 50) {
                    potion = PotionContents.createItemStack(Items.POTION, Potions.AWKWARD);
                } else {
                    potion = PotionContents.createItemStack(Items.POTION, Potions.MUNDANE);
                }
                player.addItem(potion);
            }
        }
        player.getItemInHand(hand).shrink(amount);
        level.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1F, 1F);
        level.setBlock(pos, state.setValue(FULL, false), Block.UPDATE_CLIENTS);
    }

    private static void lavaCrucibleFill(Level level, BlockPos pos, ServerPlayer player) {
        level.explode(null, pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, 1.0F, Level.ExplosionInteraction.NONE);
        level.playSound(null, pos, SoundEvents.GENERIC_BURN, SoundSource.BLOCKS, 0.5F, 1.0F);
        for(int i = 0; i < 5; i++)
            ParticleScribe.drawParticleCrucibleTop(level, ParticleTypes.LARGE_SMOKE, pos);
        ParticleScribe.drawParticleRing(level, Registration.RUNE_PARTICLE, pos, 0.7, 0.9, 7);
        level.setBlock(pos, Blocks.LAVA_CAULDRON.defaultBlockState(), Block.UPDATE_CLIENTS);
        Registration.TRY_LAVA_CRUCIBLE_TRIGGER.get().trigger(player);
    }

    private static void netherCrucibleFill(Level level, BlockPos pos, ServerPlayer player) {
        level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 2.6F + (level.random.nextFloat() - level.random.nextFloat()) * 0.8F);
        for(int i = 0; i < 5; i++)
            ParticleScribe.drawParticleCrucibleTop(level, ParticleTypes.LARGE_SMOKE, pos);
        Registration.TRY_NETHER_CRUCIBLE_TRIGGER.get().trigger(player);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState new_state, boolean p_60519_) {
        if(level.getBlockEntity(pos) instanceof CrucibleBlockEntity crucible && !level.isClientSide){
            CrucibleBlockEntity.empty(level, pos, state, crucible);
            if(crucible.integrity < 10){
                CrucibleBlockEntity.integrityFail(level, pos, state);
            }
        }

        super.onRemove(state, level, pos, new_state, p_60519_);
    }

    // Shunt method to let Shulker Crucibles not empty when broken.
    public void onRemoveWithoutEmpty(BlockState state, Level level, BlockPos pos, BlockState new_state, boolean p_60519_) {
        if(level.getBlockEntity(pos) instanceof CrucibleBlockEntity crucible && crucible.linked_crystal != null) {
            crucible.unlinkCrystal(level, pos, state);
        }
        super.onRemove(state, level, pos, new_state, p_60519_);
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return state.getValue(FULL);
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        if(!(level.getBlockEntity(pos) instanceof CrucibleBlockEntity crucible))
            return 0;

        if(crucible.integrity < 85){
            return 0;
        }

        if(crucible.used_crystal_this_cycle){
            return 15;
        }

        int signal = state.getValue(FULL) ? 1 : 0;
        signal += crucible.getTotalPowerLevel() / WorldSpecificValue.get("signal_pl_divisor", CrucibleBlockEntity.CRUCIBLE_MAX_POWER/12, CrucibleBlockEntity.CRUCIBLE_MAX_POWER/4);

        if (crucible.electricCharge > 5)
            signal += WorldSpecificValue.get("electric_signal_value", 1, 7);

        if (crucible.enderRiftStrength > 1)
            signal += level.random.nextInt(1, 7);

        if(signal > 15)
            return 15;

        return signal;
    }

    @Nullable
    @Override
    public <CrucibleBlockEntity extends BlockEntity> BlockEntityTicker<CrucibleBlockEntity> getTicker(Level level, BlockState state, BlockEntityType<CrucibleBlockEntity> type) {
       if(type == Registration.CRUCIBLE_BE_TYPE.get()){
           return (l, p, s, c) -> dev.hyperlynx.reactive.be.CrucibleBlockEntity.tick(l, p, s, (dev.hyperlynx.reactive.be.CrucibleBlockEntity) c);
       }
       return null;
    }

    // The Crucible acts as a container for the ItemEntities in its block space.
    @Override
    public WorldlyContainer getContainer(BlockState state, LevelAccessor accessor, BlockPos pos) {
        return new EntityInsideContainer(state, accessor, pos);
    }

    static class EntityInsideContainer extends SimpleContainer implements WorldlyContainer {
        private final BlockState state;
        private final LevelAccessor level;
        private final BlockPos pos;

        public EntityInsideContainer(BlockState state, LevelAccessor level, BlockPos pos) {
            super(0);
            this.state = state;
            this.level = level;
            this.pos = pos;
        }

        private List<ItemStack> getEntitySlots(){
            List<ItemStack> slots = new ArrayList<>();
            for(Entity entity : getEntitesInside(pos, (Level) level)){
                if(entity instanceof ItemEntity){
                    slots.add(((ItemEntity) entity).getItem());
                }
            }
            return slots;
        }

        @Override
        public int getContainerSize() {
            return getEntitySlots().size()+1;
        }

        @Override
        public int @NotNull [] getSlotsForFace(Direction face) {
            int size = getContainerSize();
            int[] slots_array = new int[size];
            for(int i = 0; i < size; i++){
                slots_array[i] = i;
            }
            return slots_array;
        }

        @Override
        public boolean canPlaceItemThroughFace(int unknown, ItemStack stack, @Nullable Direction face) {
            return state.getValue(FULL);
        }

        @Override
        public boolean canTakeItemThroughFace(int unknown, ItemStack stack, Direction face) {
            return false;
        }

        @Override
        public ItemStack getItem(int slot) {
            if(slot >= getContainerSize() - 1){
                return ItemStack.EMPTY;
            }
            return getEntitySlots().get(slot);
        }

        @Override
        public void setItem(int slot, ItemStack stack) {
            if(slot == getEntitySlots().size()) {
                ItemEntity ingredient_drop = new ItemEntity((Level) level, pos.getX() + 0.5, pos.getY() + 0.5265, pos.getZ() + 0.5, stack);
                ingredient_drop.setPickUpDelay(50);
                ingredient_drop.setDeltaMovement(0, 0, 0);
                level.addFreshEntity(ingredient_drop);
            }else{
                getEntitySlots().set(slot, stack);
            }
        }
    }
}
