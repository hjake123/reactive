package dev.hyperlynx.reactive.registration;

import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.blocks.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ReactiveBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(ReactiveMod.MODID);

    public static final DeferredHolder<Block, CrucibleBlock> CRUCIBLE = BLOCKS.register("crucible",
            () -> new CrucibleBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CAULDRON).forceSolidOn()));

    public static final DeferredHolder<Block, ShulkerCrucibleBlock> SHULKER_CRUCIBLE = BLOCKS.register("shulker_crucible",
            () -> new ShulkerCrucibleBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SHULKER_BOX)));

    public static final DeferredHolder<Block, SaltFilledCrucibleBlock> SALTY_CRUCIBLE = BLOCKS.register("salty_crucible",
            () -> new SaltFilledCrucibleBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CAULDRON).forceSolidOn().sound(SoundType.BASALT)));

    public static final DeferredHolder<Block, SymbolBlock> COPPER_SYMBOL = BLOCKS.register("copper_symbol",
            () -> new SymbolBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.TRIPWIRE_HOOK)));

    public static final DeferredHolder<Block, SymbolBlock> IRON_SYMBOL = BLOCKS.register("iron_symbol",
            () -> new SymbolBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.TRIPWIRE_HOOK)));

    public static final DeferredHolder<Block, SymbolBlock> GOLD_SYMBOL = BLOCKS.register("gold_symbol",
            () -> new SymbolBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.TRIPWIRE_HOOK)));

    public static final DeferredHolder<Block, SymbolBlock> OCCULT_SYMBOL = BLOCKS.register("occult_symbol",
            () -> new OccultSymbolBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.TRIPWIRE_HOOK)));

    public static final DeferredHolder<Block, SymbolBlock> DIVINE_SYMBOL = BLOCKS.register("divine_symbol",
            () -> new DivineSymbolBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.TRIPWIRE_HOOK)));

    public static final DeferredHolder<Block, BlazeRodBlock> BLAZE_ROD = BLOCKS.register("blaze_rod",
            () -> new BlazeRodBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.END_ROD)));

    public static final DeferredHolder<Block, BreezeRodBlock> BREEZE_ROD = BLOCKS.register("breeze_rod",
            () -> new BreezeRodBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.END_ROD)));

    public static final DeferredHolder<Block, StardustBlock> STARDUST = BLOCKS.register("stardust",
            () -> new StardustBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.TORCH).lightLevel((BlockState s) -> 15).sound(SoundType.WOOL)));

    public static final DeferredHolder<Block, PureQuartzBlock> PURE_QUARTZ_BLOCK = BLOCKS.register("pure_quartz_block",
            () -> new PureQuartzBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.QUARTZ_BLOCK)));

    public static final DeferredHolder<Block, VoltCellBlock> VOLT_CELL = BLOCKS.register("volt_cell",
            () -> new VoltCellBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK)));

    public static final DeferredHolder<Block, CellBlock> CURSE_CELL = BLOCKS.register("curse_cell",
            () -> new CellBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK)));

    public static final DeferredHolder<Block, WarpSpongeBlock> WARP_SPONGE = BLOCKS.register("warp_sponge",
            () -> new WarpSpongeBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WET_SPONGE)));
    public static final DeferredHolder<Block, GoldFoamBlock> GOLD_FOAM = BLOCKS.register("gold_foam",
            () -> new GoldFoamBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SLIME_BLOCK)
                    .jumpFactor(0.7F).sound(SoundType.WOOL).speedFactor(1.15F)));

    public static final DeferredHolder<Block, SolidPortalBlock> SOLID_PORTAL = BLOCKS.register("solid_portal",
            () -> new SolidPortalBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GLOWSTONE)));

    public static final DeferredHolder<Block, RunestoneBlock> RUNESTONE = BLOCKS.register("runestone",
            () -> new RunestoneBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SMOOTH_STONE)));

    public static final DeferredHolder<Block, Block> SALT_BLOCK = BLOCKS.register("salt_block",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.SAND)));

    public static final DeferredHolder<Block, MotionSaltBlock> MOTION_SALT_BLOCK = BLOCKS.register("motion_salt_block",
            () -> new MotionSaltBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.TUFF).sound(SoundType.CALCITE)));

    public static final DeferredHolder<Block, FramedMotionSaltBlock> FRAMED_MOTION_SALT_BLOCK = BLOCKS.register("framed_motion_salt_block",
            () -> new FramedMotionSaltBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK)));

    public static final DeferredHolder<Block, GravityBeamBlock> GRAVITY_BEAM = BLOCKS.register("gravity_beam",
            () -> new GravityBeamBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DISPENSER)));

    public static final DeferredHolder<Block, MnemonicBlock> MNEMONIC_BULB = BLOCKS.register("mnemonic_bulb",
            () -> new MnemonicBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.MUD)));

    public static final DeferredHolder<Block, MindLichenBlock> MIND_LICHEN = BLOCKS.register("mind_lichen",
            () -> new MindLichenBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GLOW_LICHEN)));

    public static final DeferredHolder<Block, FlowerVineBlock> FLOWER_VINES = BLOCKS.register("flower_vine",
            () -> new FlowerVineBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.VINE)));

    public static final DeferredHolder<Block, GravityChandelierBlock> GRAVITY_CHANDELIER = BLOCKS.register("gravity_chandelier",
            () -> new GravityChandelierBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.TORCH)));

    public static final DeferredHolder<Block, GatewayPlinthBlock> GATEWAY_PLINTH = BLOCKS.register("rending_plinth",
            () -> new GatewayPlinthBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.LODESTONE)));

    public static final DeferredHolder<Block, AcidBlock> ACID_BLOCK = BLOCKS.register("acid_block",
            () -> new AcidBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SLIME_BLOCK).speedFactor(0.65F).strength(1.4F)));

    public static final DeferredHolder<Block, NoduleBlock> UNGROWN_NODULE = BLOCKS.register("ungrown_nodule",
            () -> new NoduleBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.TUFF), true));

    public static final DeferredHolder<Block, PowerBottleBlock> ACID_BOTTLE_BLOCK = BLOCKS.register("acid_bottle",
            () -> new PowerBottleBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.FLOWER_POT).sound(SoundType.LANTERN)));

    public static final DeferredHolder<Block, PowerBottleBlock> BLAZE_BOTTLE_BLOCK = BLOCKS.register("blaze_bottle",
            () -> new PowerBottleBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.FLOWER_POT).sound(SoundType.LANTERN).lightLevel((BlockState bs) -> 7)));

    public static final DeferredHolder<Block, PowerBottleBlock> MIND_BOTTLE_BLOCK = BLOCKS.register("mind_bottle",
            () -> new PowerBottleBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.FLOWER_POT).sound(SoundType.LANTERN)));

    public static final DeferredHolder<Block, PowerBottleBlock> SOUL_BOTTLE_BLOCK = BLOCKS.register("soul_bottle",
            () -> new PowerBottleBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.FLOWER_POT).sound(SoundType.LANTERN)));

    public static final DeferredHolder<Block, PowerBottleBlock> WARP_BOTTLE_BLOCK = BLOCKS.register("warp_bottle",
            () -> new PowerBottleBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.FLOWER_POT).sound(SoundType.LANTERN)));

    public static final DeferredHolder<Block, PowerBottleBlock> VERDANT_BOTTLE_BLOCK = BLOCKS.register("verdant_bottle",
            () -> new PowerBottleBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.FLOWER_POT).sound(SoundType.LANTERN)));

    public static final DeferredHolder<Block, PowerBottleBlock> BODY_BOTTLE_BLOCK = BLOCKS.register("body_bottle",
            () -> new PowerBottleBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.FLOWER_POT).sound(SoundType.LANTERN)));

    public static final DeferredHolder<Block, PowerBottleBlock> LIGHT_BOTTLE_BLOCK = BLOCKS.register("light_bottle",
            () -> new PowerBottleBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.FLOWER_POT).sound(SoundType.LANTERN).lightLevel((BlockState bs) -> 15)));

    public static final DeferredHolder<Block, PowerBottleBlock> VITAL_BOTTLE_BLOCK = BLOCKS.register("vital_bottle",
            () -> new PowerBottleBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.FLOWER_POT).sound(SoundType.LANTERN).lightLevel((BlockState bs) -> 2)));

    public static final DeferredHolder<Block, IncompleteStaffBlock> INCOMPLETE_STAFF = BLOCKS.register("incomplete_staff",
            () -> new IncompleteStaffBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.END_ROD)));

    public static final DeferredHolder<Block, StaffBlock> STAFF_OF_LIGHT = BLOCKS.register("light_staff",
            () -> new StaffBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.END_ROD).lightLevel((BlockState) -> 15)));

    public static final DeferredHolder<Block, StaffBlock> STAFF_OF_WARP = BLOCKS.register("warp_staff",
            () -> new StaffBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.END_ROD).lightLevel((BlockState) -> 7)));

    public static final DeferredHolder<Block, StaffBlock> STAFF_OF_BLAZE = BLOCKS.register("blaze_staff",
            () -> new StaffBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.END_ROD).lightLevel((BlockState) -> 13)));

    public static final DeferredHolder<Block, StaffBlock> STAFF_OF_SOUL = BLOCKS.register("soul_staff",
            () -> new StaffBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.END_ROD).lightLevel((BlockState) -> 7)));

    public static final DeferredHolder<Block, StaffBlock> STAFF_OF_MIND = BLOCKS.register("mind_staff",
            () -> new StaffBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.END_ROD).lightLevel((BlockState) -> 7)));

    public static final DeferredHolder<Block, StaffBlock> STAFF_OF_LIFE = BLOCKS.register("vital_staff",
            () -> new StaffBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.END_ROD).lightLevel((BlockState) -> 7)));

    public static final DeferredHolder<Block, ActiveGoldFoamBlock> ACTIVE_GOLD_FOAM = BLOCKS.register("active_gold_foam",
            () -> new ActiveGoldFoamBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SLIME_BLOCK).jumpFactor(0.9F).sound(SoundType.WOOL)));

    public static final DeferredHolder<Block, DisplacedBlock> DISPLACED_BLOCK = BLOCKS.register("displaced_block",
            DisplacedBlock::new);

    public static final DeferredHolder<Block, AirLightBlock> GLOWING_AIR = BLOCKS.register("glowing_air",
            () -> new AirLightBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.AIR).lightLevel((state) -> 15)));

    public static final DeferredHolder<Block, UnformedMatterBlock> UNFORMED_MATTER = BLOCKS.register("unformed_matter",
            () -> new UnformedMatterBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OBSIDIAN)
                    .sound(SoundType.HONEY_BLOCK)
                    .lightLevel((state) -> 15)
                    .hasPostProcess((a, b, c) -> true)
                    .emissiveRendering((a, b, c) -> true)
                    .pushReaction(PushReaction.DESTROY)));

    public static final DeferredHolder<Block, GatewayBlock> GATEWAY_BLOCK = BLOCKS.register("gateway",
            () -> new GatewayBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.END_GATEWAY)));

    public static final DeferredHolder<Block, Block> DUMMY_SLOW_WATER = BLOCKS.register("slow_water",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.WATER)));

    public static final DeferredHolder<Block, Block> DUMMY_FAST_WATER = BLOCKS.register("fast_water",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.WATER)));

    public static final DeferredHolder<Block, Block> DUMMY_NOISE_WATER = BLOCKS.register("noisy_water",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.WATER)));

    public static final DeferredHolder<Block, Block> DUMMY_MAGIC_WATER = BLOCKS.register("magic_water",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.WATER)));

    public static final DeferredHolder<Block, NoduleBlock> NODULE = BLOCKS.register("nodule",
            () -> new NoduleBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.TUFF).lightLevel((state) -> 8), false));

    public static final DeferredHolder<Block, FlowerVinePlantBlock> FLOWER_VINES_BODY = BLOCKS.register("flower_vine_plant",
            () -> new FlowerVinePlantBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.VINE)));

    public static final DeferredHolder<Block, MaterialBlock> MATERIAL_BLOCK = BLOCKS.register("material",
            MaterialBlock::new);

    public static final DeferredHolder<Block, DeskBlock> DESK = BLOCKS.register("desk",
            () -> new DeskBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.JUNGLE_PLANKS)));

    public static final DeferredHolder<Block, Block> ADEPT_SALT_BLOCK = BLOCKS.register("adept_salt_block",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.QUARTZ_BLOCK)));

    public static final DeferredHolder<Block, Block> CREATION_SALT_BLOCK = BLOCKS.register("creation_salt_block",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.SAND).sound(SoundType.SOUL_SOIL).lightLevel(state -> 12)));
}
