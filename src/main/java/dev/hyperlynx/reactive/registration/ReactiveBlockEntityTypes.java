package dev.hyperlynx.reactive.registration;

import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.be.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ReactiveBlockEntityTypes {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, ReactiveMod.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GatewayBlockEntity>> GATEWAY =
            BLOCK_ENTITY_TYPES.register("gateway_be",
            () -> BlockEntityType.Builder.of(GatewayBlockEntity::new, ReactiveBlocks.GATEWAY_BLOCK.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DisplacedBlockEntity>> DISPLACED_BLOCK =
            BLOCK_ENTITY_TYPES.register("displaced_block_be",
            () -> BlockEntityType.Builder.of(DisplacedBlockEntity::new, ReactiveBlocks.DISPLACED_BLOCK.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ActiveFoamBlockEntity>> ACTIVE_GOLD_FOAM =
            BLOCK_ENTITY_TYPES.register("active_gold_foam_be",
            () -> BlockEntityType.Builder.of(ActiveFoamBlockEntity::new, ReactiveBlocks.ACTIVE_GOLD_FOAM.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<StaffBlockEntity>> STAFF =
            BLOCK_ENTITY_TYPES.register("staff_be",
            () -> BlockEntityType.Builder.of(StaffBlockEntity::new, ReactiveBlocks.STAFF_OF_LIGHT.get(), ReactiveBlocks.STAFF_OF_SOUL.get(), ReactiveBlocks.STAFF_OF_LIFE.get(), ReactiveBlocks.STAFF_OF_MIND.get(), ReactiveBlocks.STAFF_OF_BLAZE.get(), ReactiveBlocks.STAFF_OF_WARP.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GravityBeamBlockEntity>> GRAVITY_BEAM =
            BLOCK_ENTITY_TYPES.register("gravity_beam_be",
            () -> BlockEntityType.Builder.of(GravityBeamBlockEntity::new, ReactiveBlocks.GRAVITY_BEAM.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GravityChandelierBlockEntity>> GRAVITY_CHANDELIER =
            BLOCK_ENTITY_TYPES.register("gravity_chandelier_be",
            () -> BlockEntityType.Builder.of(GravityChandelierBlockEntity::new, ReactiveBlocks.GRAVITY_CHANDELIER.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SymbolBlockEntity>> SYMBOL =
            BLOCK_ENTITY_TYPES.register("symbol_be",
            () -> BlockEntityType.Builder.of(SymbolBlockEntity::new, ReactiveBlocks.COPPER_SYMBOL.get(), ReactiveBlocks.IRON_SYMBOL.get(), ReactiveBlocks.GOLD_SYMBOL.get(), ReactiveBlocks.OCCULT_SYMBOL.get(), ReactiveBlocks.DIVINE_SYMBOL.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CrucibleBlockEntity>> CRUCIBLE =
            BLOCK_ENTITY_TYPES.register("crucible_be",
            () -> BlockEntityType.Builder.of(CrucibleBlockEntity::new, ReactiveBlocks.CRUCIBLE.get(), ReactiveBlocks.SHULKER_CRUCIBLE.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MnemonicBlockEntity>> MNEMONIC_BULB =
            BLOCK_ENTITY_TYPES.register("mnemonic_bulb_be",
            () -> BlockEntityType.Builder.of(MnemonicBlockEntity::new, ReactiveBlocks.MNEMONIC_BULB.get()).build(null));
}
