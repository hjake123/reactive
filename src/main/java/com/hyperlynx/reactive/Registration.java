package com.hyperlynx.reactive;

import com.hyperlynx.reactive.advancements.CriteriaTriggers;
import com.hyperlynx.reactive.alchemy.Powers;
import com.hyperlynx.reactive.alchemy.rxn.ReactionMan;
import com.hyperlynx.reactive.alchemy.special.SpecialCaseMan;
import com.hyperlynx.reactive.be.*;
import com.hyperlynx.reactive.blocks.*;
//import com.hyperlynx.reactive.integration.create.ReactiveCreatePlugin;
//import com.hyperlynx.reactive.integration.pehkui.ReactivePehkuiPlugin;
import com.hyperlynx.reactive.components.ReactiveDataComponents;
import com.hyperlynx.reactive.items.*;
import com.hyperlynx.reactive.recipes.*;
import com.hyperlynx.reactive.util.HyperMobEffect;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.common.brewing.BrewingRecipeRegistry;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.world.item.alchemy.PotionContents;


@SuppressWarnings("unused")
@EventBusSubscriber(modid=ReactiveMod.MODID, bus=EventBusSubscriber.Bus.MOD)
public class Registration {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ReactiveMod.MODID);
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(ReactiveMod.MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ReactiveMod.MODID);
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, ReactiveMod.MODID);
    public static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(BuiltInRegistries.POTION, ReactiveMod.MODID);
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, ReactiveMod.MODID);
    public static final DeferredRegister<BlockEntityType<?>> TILES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, ReactiveMod.MODID);
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(BuiltInRegistries.RECIPE_TYPE, ReactiveMod.MODID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, ReactiveMod.MODID);
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, ReactiveMod.MODID);

    public static void init(IEventBus bus) {
        ReactiveDataComponents.COMPONENT_TYPES.register(bus);
        BLOCKS.register(bus);
        ITEMS.register(bus);
        CREATIVE_TABS.register(bus);
        MOB_EFFECTS.register(bus);
        POTIONS.register(bus);
        PARTICLES.register(bus);
        TILES.register(bus);
        Powers.POWERS.register(bus);
        CriteriaTriggers.TRIGGERS.register(bus);
        bus.addListener(ReactionMan.CRITERIA_BUILDER::register);
        RECIPE_TYPES.register(bus);
        RECIPE_SERIALIZERS.register(bus);
        SOUND_EVENTS.register(bus);
        bus.register(Registration.class);
    }

    // ----------------------- REGISTRATION ------------------------
    // Register the all-important Crucible.
    public static final DeferredHolder<Block, CrucibleBlock> CRUCIBLE = BLOCKS.register("crucible",
            () -> new CrucibleBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CAULDRON)));
    public static final DeferredHolder<Item, BlockItem> CRUCIBLE_ITEM = ITEMS.registerSimpleBlockItem(CRUCIBLE);

    // Register the Shulker Crucible
    public static final DeferredHolder<Block, ShulkerCrucibleBlock> SHULKER_CRUCIBLE = BLOCKS.register("shulker_crucible",
            () -> new ShulkerCrucibleBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SHULKER_BOX)));
    public static final DeferredHolder<Item, ShulkerCrucibleItem> SHULKER_CRUCIBLE_ITEM = ITEMS.register(SHULKER_CRUCIBLE.getId().getPath(),
            () -> new ShulkerCrucibleItem(new Item.Properties()));

    // Register the Crucible BE.
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CrucibleBlockEntity>> CRUCIBLE_BE_TYPE = TILES.register("crucible_be",
            () -> BlockEntityType.Builder.of(CrucibleBlockEntity::new, CRUCIBLE.get(), SHULKER_CRUCIBLE.get()).build(null));

    // Register the rest of the blocks
    public static final DeferredHolder<Block, SaltFilledCrucibleBlock> SALTY_CRUCIBLE = BLOCKS.register("salty_crucible",
            () -> new SaltFilledCrucibleBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CAULDRON).sound(SoundType.BASALT)));
    public static final DeferredHolder<Item, BlockItem> SALTY_CRUCIBLE_ITEM = ITEMS.registerSimpleBlockItem(SALTY_CRUCIBLE);

    public static final DeferredHolder<Block, SymbolBlock> COPPER_SYMBOL = BLOCKS.register("copper_symbol",
            () -> new SymbolBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.TRIPWIRE_HOOK)));
    public static final DeferredHolder<Item, SymbolItem> COPPER_SYMBOL_ITEM = SymbolItem.registerSimpleBlockItem(COPPER_SYMBOL);

    public static final DeferredHolder<Block, SymbolBlock> IRON_SYMBOL = BLOCKS.register("iron_symbol",
            () -> new SymbolBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.TRIPWIRE_HOOK)));
    public static final DeferredHolder<Item, SymbolItem> IRON_SYMBOL_ITEM = SymbolItem.registerSimpleBlockItem(IRON_SYMBOL);

    public static final DeferredHolder<Block, SymbolBlock> GOLD_SYMBOL = BLOCKS.register("gold_symbol",
            () -> new SymbolBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.TRIPWIRE_HOOK)));
    public static final DeferredHolder<Item, SymbolItem> GOLD_SYMBOL_ITEM = SymbolItem.registerSimpleBlockItem(GOLD_SYMBOL);

    public static final DeferredHolder<Block, SymbolBlock> OCCULT_SYMBOL = BLOCKS.register("occult_symbol",
            () -> new OccultSymbolBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.TRIPWIRE_HOOK)));
    public static final DeferredHolder<Item, SymbolItem> OCCULT_SYMBOL_ITEM = SymbolItem.registerSimpleBlockItem(OCCULT_SYMBOL);

    public static final DeferredHolder<Block, SymbolBlock> DIVINE_SYMBOL = BLOCKS.register("divine_symbol",
            () -> new DivineSymbolBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.TRIPWIRE_HOOK)));
    public static final DeferredHolder<Item, SymbolItem> DIVINE_SYMBOL_ITEM = SymbolItem.registerSimpleBlockItem(DIVINE_SYMBOL);

    // Register the Symbol BE
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SymbolBlockEntity>> SYMBOL_BE_TYPE = TILES.register("symbol_be",
            () -> BlockEntityType.Builder.of(SymbolBlockEntity::new, COPPER_SYMBOL.get(), IRON_SYMBOL.get(), GOLD_SYMBOL.get(), OCCULT_SYMBOL.get(), DIVINE_SYMBOL.get()).build(null));

    public static final DeferredHolder<Block, BlazeRodBlock> BLAZE_ROD = BLOCKS.register("blaze_rod",
            () -> new BlazeRodBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.END_ROD)));
    public static final DeferredHolder<Item, BlockItem> BLAZE_ROD_ITEM = ITEMS.registerSimpleBlockItem(BLAZE_ROD);

    public static final DeferredHolder<Block, StardustBlock> STARDUST = BLOCKS.register("stardust",
            () -> new StardustBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.TORCH).lightLevel((BlockState s) -> 15).sound(SoundType.WOOL)));

    public static final DeferredHolder<Block, PureQuartzBlock> PURE_QUARTZ_BLOCK = BLOCKS.register("pure_quartz_block",
            () -> new PureQuartzBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.QUARTZ_BLOCK)));
    public static final DeferredHolder<Item, BlockItem> PURE_QUARTZ_BLOCK_ITEM = ITEMS.registerSimpleBlockItem(PURE_QUARTZ_BLOCK);

    public static final DeferredHolder<Block, VoltCellBlock> VOLT_CELL = BLOCKS.register("volt_cell",
            () -> new VoltCellBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK)));
    public static final DeferredHolder<Item, BlockItem> VOLT_CELL_ITEM = ITEMS.registerSimpleBlockItem(VOLT_CELL);

    public static final DeferredHolder<Block, CellBlock> CURSE_CELL = BLOCKS.register("curse_cell",
            () -> new CellBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK)));
    public static final DeferredHolder<Item, BlockItem> CURSE_CELL_ITEM = ITEMS.registerSimpleBlockItem(CURSE_CELL);

    public static final DeferredHolder<Block, WarpSpongeBlock> WARP_SPONGE = BLOCKS.register("warp_sponge",
            () -> new WarpSpongeBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WET_SPONGE)));
    public static final DeferredHolder<Item, BlockItem> WARP_SPONGE_ITEM = ITEMS.registerSimpleBlockItem(WARP_SPONGE);

    public static final DeferredHolder<Block, GoldFoamBlock> GOLD_FOAM = BLOCKS.register("gold_foam",
            () -> new GoldFoamBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SLIME_BLOCK)
                    .jumpFactor(0.7F).sound(SoundType.WOOL).speedFactor(1.15F)));
    public static final DeferredHolder<Item, BlockItem> GOLD_FOAM_ITEM = ITEMS.registerSimpleBlockItem(GOLD_FOAM);

    public static final DeferredHolder<Block, SolidPortalBlock> SOLID_PORTAL = BLOCKS.register("solid_portal",
            () -> new SolidPortalBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GLOWSTONE)));
    public static final DeferredHolder<Item, BlockItem> SOLID_PORTAL_ITEM = ITEMS.registerSimpleBlockItem(SOLID_PORTAL);

    public static final DeferredHolder<Block, RunestoneBlock> RUNESTONE = BLOCKS.register("runestone",
            () -> new RunestoneBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SMOOTH_STONE)));
    public static final DeferredHolder<Item, BlockItem> RUNESTONE_ITEM = ITEMS.registerSimpleBlockItem(RUNESTONE);

    public static final DeferredHolder<Block, Block> SALT_BLOCK = BLOCKS.register("salt_block",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.SAND)));
    public static final DeferredHolder<Item, BlockItem> SALT_BLOCK_ITEM = ITEMS.registerSimpleBlockItem(SALT_BLOCK);

    public static final DeferredHolder<Block, MotionSaltBlock> MOTION_SALT_BLOCK = BLOCKS.register("motion_salt_block",
            () -> new MotionSaltBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.TUFF).sound(SoundType.CALCITE)));
    public static final DeferredHolder<Item, BlockItem> MOTION_SALT_BLOCK_ITEM = ITEMS.registerSimpleBlockItem(MOTION_SALT_BLOCK);

    public static final DeferredHolder<Block, FramedMotionSaltBlock> FRAMED_MOTION_SALT_BLOCK = BLOCKS.register("framed_motion_salt_block",
            () -> new FramedMotionSaltBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK)));
    public static final DeferredHolder<Item, BlockItem> FRAMED_MOTION_SALT_BLOCK_ITEM = ITEMS.registerSimpleBlockItem(FRAMED_MOTION_SALT_BLOCK);

    public static final DeferredHolder<Block, GravityBeamBlock> GRAVITY_BEAM = BLOCKS.register("gravity_beam",
            () -> new GravityBeamBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DISPENSER)));

    public static final DeferredHolder<Item, BlockItem> GRAVITY_BEAM_ITEM = ITEMS.registerSimpleBlockItem(GRAVITY_BEAM);

    public static final DeferredHolder<Block, FlowerVineBlock> FLOWER_VINES = BLOCKS.register("flower_vine",
            () -> new FlowerVineBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.VINE)));

    public static final DeferredHolder<Block, FlowerVinePlantBlock> FLOWER_VINES_BODY = BLOCKS.register("flower_vine_plant",
            () -> new FlowerVinePlantBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.VINE)));

    public static final DeferredHolder<Item, BlockItem> FLOWER_VINES_ITEM = ITEMS.registerSimpleBlockItem(FLOWER_VINES);

    public static final DeferredHolder<Block, MindLichenBlock> MIND_LICHEN = BLOCKS.register("mind_lichen",
            () -> new MindLichenBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GLOW_LICHEN)));

    public static final DeferredHolder<Item, BlockItem> MIND_LICHEN_ITEM = ITEMS.registerSimpleBlockItem(MIND_LICHEN);

    public static final DeferredHolder<Block, GravityChandelierBlock> GRAVITY_CHANDELIER = BLOCKS.register("gravity_chandelier",
            () -> new GravityChandelierBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.TORCH)));

    public static final DeferredHolder<Item, BlockItem> GRAVITY_CHANDELIER_ITEM = ITEMS.registerSimpleBlockItem(GRAVITY_CHANDELIER);

    public static final DeferredHolder<Block, AcidBlock> ACID_BLOCK = BLOCKS.register("acid_block",
            () -> new AcidBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SLIME_BLOCK).speedFactor(0.65F).strength(1.4F)));

    public static final DeferredHolder<Item, AcidBucketItem> ACID_BUCKET = ITEMS.register("acid_bucket",
            () -> new AcidBucketItem(ACID_BLOCK.get(), SoundEvents.BUCKET_FILL, new Item.Properties()));

    // Register the Gravity related BEs
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GravityChandelierBlockEntity>> GRAVITY_CHANDELIER_BE_TYPE =
            TILES.register("gravity_chandelier_be",
            () -> BlockEntityType.Builder.of(GravityChandelierBlockEntity::new, GRAVITY_CHANDELIER.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GravityBeamBlockEntity>> GRAVITY_BEAM_BE_TYPE =
            TILES.register("gravity_beam_be",
                    () -> BlockEntityType.Builder.of(GravityBeamBlockEntity::new, GRAVITY_BEAM.get()).build(null));

    // Register Power bottles
    public static final DeferredHolder<Block, PowerBottleBlock> ACID_BOTTLE_BLOCK = BLOCKS.register("acid_bottle",
            () -> new PowerBottleBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.FLOWER_POT).sound(SoundType.LANTERN)));
    public static final DeferredHolder<Item, PowerBottleItem> ACID_BOTTLE = ITEMS.register("acid_bottle",
            () -> new PowerBottleItem(new Item.Properties(), ACID_BOTTLE_BLOCK.get()));
    public static final DeferredHolder<Block, PowerBottleBlock> BLAZE_BOTTLE_BLOCK = BLOCKS.register("blaze_bottle",
            () -> new PowerBottleBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.FLOWER_POT).sound(SoundType.LANTERN).lightLevel((BlockState bs) -> 7)));
    public static final DeferredHolder<Item, BlazeBottleItem> BLAZE_BOTTLE = ITEMS.register("blaze_bottle",
            () -> new BlazeBottleItem(new Item.Properties(), BLAZE_BOTTLE_BLOCK.get()));
    public static final DeferredHolder<Block, PowerBottleBlock> MIND_BOTTLE_BLOCK = BLOCKS.register("mind_bottle",
            () -> new PowerBottleBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.FLOWER_POT).sound(SoundType.LANTERN)));
    public static final DeferredHolder<Item, PowerBottleItem> MIND_BOTTLE = ITEMS.register("mind_bottle",
            () -> new PowerBottleItem(new Item.Properties(), MIND_BOTTLE_BLOCK.get()));
    public static final DeferredHolder<Block, PowerBottleBlock> SOUL_BOTTLE_BLOCK = BLOCKS.register("soul_bottle",
            () -> new PowerBottleBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.FLOWER_POT).sound(SoundType.LANTERN)));
    public static final DeferredHolder<Item, PowerBottleItem> SOUL_BOTTLE = ITEMS.register("soul_bottle",
            () -> new PowerBottleItem(new Item.Properties(), SOUL_BOTTLE_BLOCK.get()));
    public static final DeferredHolder<Block, PowerBottleBlock> WARP_BOTTLE_BLOCK = BLOCKS.register("warp_bottle",
            () -> new PowerBottleBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.FLOWER_POT).sound(SoundType.LANTERN)));
    public static final DeferredHolder<Item, WarpBottleItem> WARP_BOTTLE = ITEMS.register("warp_bottle",
            () -> new WarpBottleItem(new Item.Properties(), WARP_BOTTLE_BLOCK.get()));
    public static final DeferredHolder<Block, PowerBottleBlock> VERDANT_BOTTLE_BLOCK = BLOCKS.register("verdant_bottle",
            () -> new PowerBottleBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.FLOWER_POT).sound(SoundType.LANTERN)));
    public static final DeferredHolder<Item, PowerBottleItem> VERDANT_BOTTLE = ITEMS.register("verdant_bottle",
            () -> new PowerBottleItem(new Item.Properties(), VERDANT_BOTTLE_BLOCK.get()));
    public static final DeferredHolder<Block, PowerBottleBlock> BODY_BOTTLE_BLOCK = BLOCKS.register("body_bottle",
            () -> new PowerBottleBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.FLOWER_POT).sound(SoundType.LANTERN)));
    public static final DeferredHolder<Item, PowerBottleItem> BODY_BOTTLE = ITEMS.register("body_bottle",
            () -> new PowerBottleItem(new Item.Properties(), BODY_BOTTLE_BLOCK.get()));
    public static final DeferredHolder<Block, PowerBottleBlock> LIGHT_BOTTLE_BLOCK = BLOCKS.register("light_bottle",
            () -> new PowerBottleBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.FLOWER_POT).sound(SoundType.LANTERN).lightLevel((BlockState bs) -> 15)));
    public static final DeferredHolder<Item, PowerBottleItem> LIGHT_BOTTLE = ITEMS.register("light_bottle",
            () -> new PowerBottleItem(new Item.Properties(), LIGHT_BOTTLE_BLOCK.get()));
    public static final DeferredHolder<Block, PowerBottleBlock> VITAL_BOTTLE_BLOCK = BLOCKS.register("vital_bottle",
            () -> new PowerBottleBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.FLOWER_POT).sound(SoundType.LANTERN).lightLevel((BlockState bs) -> 2)));
    public static final DeferredHolder<Item, PowerBottleItem> VITAL_BOTTLE = ITEMS.register("vital_bottle",
            () -> new PowerBottleItem(new Item.Properties(), VITAL_BOTTLE_BLOCK.get()));

    // Register staves
    public static final DeferredHolder<Block, IncompleteStaffBlock> INCOMPLETE_STAFF = BLOCKS.register("incomplete_staff",
            () -> new IncompleteStaffBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.END_ROD)));
    public static final DeferredHolder<Item, BlockItem> INCOMPLETE_STAFF_ITEM = ITEMS.register(INCOMPLETE_STAFF.getId().getPath(),
            () -> new BlockItem(INCOMPLETE_STAFF.get(), new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Block, StaffBlock> STAFF_OF_LIGHT = BLOCKS.register("light_staff",
            () -> new StaffBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.END_ROD).lightLevel((BlockState) -> 15)));
    public static final DeferredHolder<Item, LightStaffItem> STAFF_OF_LIGHT_ITEM = ITEMS.register(STAFF_OF_LIGHT.getId().getPath(),
            () -> new LightStaffItem(STAFF_OF_LIGHT.get(), new Item.Properties().durability(1000), StaffEffects::radiance, true, LIGHT_BOTTLE.get()));

    public static final DeferredHolder<Block, StaffBlock> STAFF_OF_WARP = BLOCKS.register("warp_staff",
            () -> new StaffBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.END_ROD).lightLevel((BlockState) -> 7)));
    public static final DeferredHolder<Item, WarpStaffItem> STAFF_OF_WARP_ITEM = ITEMS.register(STAFF_OF_WARP.getId().getPath(),
            () -> new WarpStaffItem(STAFF_OF_WARP.get(), new Item.Properties().durability(500), WARP_BOTTLE.get()));

    public static final DeferredHolder<Block, StaffBlock> STAFF_OF_BLAZE = BLOCKS.register("blaze_staff",
            () -> new StaffBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.END_ROD).lightLevel((BlockState) -> 13)));
    public static final DeferredHolder<Item, StaffItem> STAFF_OF_BLAZE_ITEM = ITEMS.register(STAFF_OF_BLAZE.getId().getPath(),
            () -> new StaffItem(STAFF_OF_BLAZE.get(), new Item.Properties().durability(1200).fireResistant(), StaffEffects::blazing, false, 10, BLAZE_BOTTLE.get()));

    public static final DeferredHolder<Block, StaffBlock> STAFF_OF_SOUL = BLOCKS.register("soul_staff",
            () -> new StaffBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.END_ROD).lightLevel((BlockState) -> 7)));
    public static final DeferredHolder<Item, StaffItem> STAFF_OF_SOUL_ITEM = ITEMS.register(STAFF_OF_SOUL.getId().getPath(),
            () -> new StaffItem(STAFF_OF_SOUL.get(), new Item.Properties().durability(800), StaffEffects::spectral, false, 14, SOUL_BOTTLE.get()));

    public static final DeferredHolder<Block, StaffBlock> STAFF_OF_MIND = BLOCKS.register("mind_staff",
            () -> new StaffBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.END_ROD).lightLevel((BlockState) -> 7)));
    public static final DeferredHolder<Item, StaffItem> STAFF_OF_MIND_ITEM = ITEMS.register(STAFF_OF_MIND.getId().getPath(),
            () -> new StaffItem(STAFF_OF_MIND.get(), new Item.Properties().durability(1200), StaffEffects::missile, false, 10, MIND_BOTTLE.get()));

    public static final DeferredHolder<Block, StaffBlock> STAFF_OF_LIFE = BLOCKS.register("vital_staff",
            () -> new StaffBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.END_ROD).lightLevel((BlockState) -> 7)));
    public static final DeferredHolder<Item, StaffItem> STAFF_OF_LIFE_ITEM = ITEMS.register(STAFF_OF_LIFE.getId().getPath(),
            () -> new StaffItem(STAFF_OF_LIFE.get(), new Item.Properties().durability(600), StaffEffects::living, true, 10, VITAL_BOTTLE.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<StaffBlockEntity>> STAFF_BE = TILES.register("staff_be",
            () -> BlockEntityType.Builder.of(StaffBlockEntity::new, STAFF_OF_LIGHT.get(), STAFF_OF_SOUL.get(), STAFF_OF_LIFE.get(), STAFF_OF_MIND.get(), STAFF_OF_BLAZE.get(), STAFF_OF_WARP.get()).build(null));


    // Register technical blocks.
    public static final DeferredHolder<Block, ActiveGoldFoamBlock> ACTIVE_GOLD_FOAM = BLOCKS.register("active_gold_foam",
            () -> new ActiveGoldFoamBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SLIME_BLOCK).jumpFactor(0.9F).sound(SoundType.WOOL)));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ActiveFoamBlockEntity>> ACTIVE_GOLD_FOAM_BE = TILES.register("active_gold_foam_be",
            () -> BlockEntityType.Builder.of(ActiveFoamBlockEntity::new, ACTIVE_GOLD_FOAM.get()).build(null));

    public static final DeferredHolder<Block, DisplacedBlock> DISPLACED_BLOCK = BLOCKS.register("displaced_block",
            DisplacedBlock::new);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DisplacedBlockEntity>> DISPLACED_BLOCK_BE = TILES.register("displaced_block_be",
            () -> BlockEntityType.Builder.of(DisplacedBlockEntity::new, DISPLACED_BLOCK.get()).build(null));

    public static final DeferredHolder<Block, AirLightBlock> GLOWING_AIR = BLOCKS.register("glowing_air",
            () -> new AirLightBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.AIR).lightLevel((state) -> 15)));

    public static final DeferredHolder<Block, UnformedMatterBlock> UNFORMED_MATTER = BLOCKS.register("unformed_matter",
            () -> new UnformedMatterBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OBSIDIAN)
                    .sound(SoundType.HONEY_BLOCK)
                    .lightLevel((state) -> 15)
                    .hasPostProcess((a, b, c) -> true)
                    .emissiveRendering((a, b, c) -> true)
                    .pushReaction(PushReaction.DESTROY)));

    // Register items.
    public static final DeferredHolder<Item, DisplacerItem> DISPLACER = ITEMS.register("displacer",
            () -> new DisplacerItem(new Item.Properties()
                    .durability(350)));

    public static final DeferredHolder<Item, Item> PURE_QUARTZ = ITEMS.register("quartz",
            () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, StardustItem> STARDUST_ITEM = ITEMS.register("stardust",
            () -> new StardustItem(new Item.Properties()));
    public static final DeferredHolder<Item, AlchemyScrollItem> SCROLL = ITEMS.register("scroll",
            () -> new AlchemyScrollItem(new Item.Properties()
                    .stacksTo(1).rarity(Rarity.RARE)));
    public static final DeferredHolder<Item, LitmusPaperItem> LITMUS_PAPER = ITEMS.register("litmus_paper",
            () -> new LitmusPaperItem(new Item.Properties()));
    public static final DeferredHolder<Item, QuartzBottleItem> QUARTZ_BOTTLE = ITEMS.register("quartz_bottle",
            () -> new QuartzBottleItem(new Item.Properties()));
    public static final DeferredHolder<Item, CrystalIronItem> CRYSTAL_IRON = ITEMS.register("crystal_iron",
            () -> new CrystalIronItem(new Item.Properties().durability(64)));
    public static final DeferredHolder<Item, Item> PHANTOM_RESIDUE = ITEMS.register("phantom_residue",
            () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, SoupItem> SOUP = ITEMS.register("soup",
            () -> new SoupItem(new Item.Properties().stacksTo(64).food((new FoodProperties.Builder().nutrition(7).saturationModifier(0.5F)).build())));
    public static final DeferredHolder<Item, Item> SALT = ITEMS.register("salt",
            () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> MOTION_SALT = ITEMS.register("motion_salt",
            () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, ForceRockItem> FORCE_ROCK = ITEMS.register("force_rock",
            () -> new ForceRockItem(new Item.Properties()));
    public static final DeferredHolder<Item, SecretScaleItem> SECRET_SCALE = ITEMS.register("secret_scale",
            () -> new SecretScaleItem(new Item.Properties()));
    public static final DeferredHolder<Item, Item> ETERNAL_SPRIG = ITEMS.register("eternal_life_sprig",
            () -> new Item(new Item.Properties().food(new FoodProperties.Builder()
                    .nutrition(4)
                    .saturationModifier(1.4F)
                    .effect(() -> new MobEffectInstance(MobEffects.ABSORPTION, -1, 4, true, false), 1F)
                    .build())));

    // Register mob effects
    public static final DeferredHolder<MobEffect, MobEffect> NULL_GRAVITY = MOB_EFFECTS.register("no_gravity",
            () -> new HyperMobEffect(MobEffectCategory.NEUTRAL, 0xC0BF77)
                    .addAttributeModifier(Attributes.GRAVITY, "fa350eb8-d5d3-4240-8342-dcc89c1693b9",
                            -1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final DeferredHolder<MobEffect, MobEffect> IMMOBILE = MOB_EFFECTS.register("immobility",
            () -> new HyperMobEffect(MobEffectCategory.NEUTRAL, 0x118066)
                    .addAttributeModifier(Attributes.MOVEMENT_SPEED, "31861490-4050-11ee-be56-0242ac120002",
                            -1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                    .addAttributeModifier(Attributes.FLYING_SPEED, "8712e51e-4050-11ee-be56-0242ac120002",
                            -1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final DeferredHolder<MobEffect, MobEffect> FAR_REACH = MOB_EFFECTS.register("far_reach",
            () -> new HyperMobEffect(MobEffectCategory.BENEFICIAL, 0x7A5BB5)
                    .addAttributeModifier(Attributes.BLOCK_INTERACTION_RANGE, "91a1b581-0e56-446d-853a-a3037f2e97c5",
                            2, AttributeModifier.Operation.ADD_VALUE)
                    .addAttributeModifier(Attributes.ENTITY_INTERACTION_RANGE, "91a1c581-0e56-446d-853a-a3037f2e97c5",
                            2, AttributeModifier.Operation.ADD_VALUE));
    public static final DeferredHolder<MobEffect, HyperMobEffect> FIRE_SHIELD = MOB_EFFECTS.register("fire_shield",
            () -> new HyperMobEffect(MobEffectCategory.BENEFICIAL, 0xFFA511));

    // Register potions
    public static final DeferredHolder<Potion, Potion> NULL_GRAVITY_POTION = POTIONS.register("no_gravity",
            () -> new Potion("no_gravity", new MobEffectInstance(NULL_GRAVITY, 3000)));
    public static final DeferredHolder<Potion, Potion> LONG_NULL_GRAVITY_POTION = POTIONS.register("no_gravity_long",
            () -> new Potion("no_gravity", new MobEffectInstance(NULL_GRAVITY, 8000)));

    // Register particles
    public static final SimpleParticleType STARDUST_PARTICLE = new SimpleParticleType(false);
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> STARDUST_PARTICLE_TYPE = PARTICLES.register("stardust",
            () -> STARDUST_PARTICLE);

    public static final SimpleParticleType RUNE_PARTICLE = new SimpleParticleType(false);
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> RUNE_PARTICLE_TYPE = PARTICLES.register("runes",
            () -> RUNE_PARTICLE);

    public static final SimpleParticleType SMALL_RUNE_PARTICLE = new SimpleParticleType(false);
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SMALL_RUNE_PARTICLE_TYPE = PARTICLES.register("small_runes",
            () -> SMALL_RUNE_PARTICLE);

    public static final SimpleParticleType SMALL_BLACK_RUNE_PARTICLE = new SimpleParticleType(false);
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SMALL_BLACK_RUNE_PARTICLE_TYPE = PARTICLES.register("small_black_runes",
            () -> SMALL_BLACK_RUNE_PARTICLE);

    public static final SimpleParticleType ACID_BUBBLE_PARTICLE = new SimpleParticleType(false);
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> ACID_BUBBLE_PARTICLE_TYPE = PARTICLES.register("acid_bubble",
            () -> ACID_BUBBLE_PARTICLE);

    // Register sound events.
    public static final DeferredHolder<SoundEvent, SoundEvent> ZAP_SOUND = SOUND_EVENTS.register("zap",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("reactive:zap")));

    public static final DeferredHolder<SoundEvent, SoundEvent> RUMBLE_SOUND = SOUND_EVENTS.register("rumble",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("reactive:rumble")));

    // Register dummy blocks for the weird water types and the symbol eye render.
    public static final DeferredHolder<Block, Block> DUMMY_MAGIC_WATER = BLOCKS.register("magic_water",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.WATER)));
    public static final DeferredHolder<Block, Block> DUMMY_NOISE_WATER = BLOCKS.register("noisy_water",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.WATER)));
    public static final DeferredHolder<Block, Block> DUMMY_FAST_WATER = BLOCKS.register("fast_water",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.WATER)));

    // Register the recipe types and serializers.
    public static final DeferredHolder<RecipeType<?>, RecipeType<TransmuteRecipe>> TRANS_RECIPE_TYPE = RECIPE_TYPES.register("transmutation", () -> getRecipeType("transmutation"));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<TransmuteRecipe>> TRANS_SERIALIZER = RECIPE_SERIALIZERS.register("transmutation", TransmuteRecipeSerializer::new);

    public static final DeferredHolder<RecipeType<?>, RecipeType<DissolveRecipe>> DISSOLVE_RECIPE_TYPE = RECIPE_TYPES.register("dissolve", () -> getRecipeType("dissolve"));
    public static final DeferredHolder<RecipeSerializer<?>,RecipeSerializer<DissolveRecipe>> DISSOLVE_SERIALIZER = RECIPE_SERIALIZERS.register("dissolve", DissolveRecipeSerializer::new);

    public static final DeferredHolder<RecipeType<?>, RecipeType<PrecipitateRecipe>> PRECIPITATE_RECIPE_TYPE = RECIPE_TYPES.register("precipitation", () -> getRecipeType("precipitation"));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<PrecipitateRecipe>> PRECIPITATE_SERIALIZER = RECIPE_SERIALIZERS.register("precipitation", PrecipitateRecipeSerializer::new);

    // Register the creative mode tab.
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> REACTIVE_TAB = CREATIVE_TABS.register("reactive_tab",
            () -> CreativeModeTab.builder()
                    .icon(() -> CRUCIBLE_ITEM.get().getDefaultInstance())
                    .title(Component.translatable("reactive.tab"))
                    .displayItems((params, output) -> {
                        for(DeferredHolder<Item, ? extends Item> item_reg : ITEMS.getEntries()){
                                output.accept(item_reg.get());
                        }
                    })
                    .build());

    // ----------------------- METHODS ------------------------

    @SubscribeEvent
    public static void commonSetupHandler(FMLCommonSetupEvent evt){
        SpecialCaseMan.bootstrap();
        ((SymbolBlock) COPPER_SYMBOL.get()).setSymbolItem(COPPER_SYMBOL_ITEM.get());
        ((SymbolBlock) IRON_SYMBOL.get()).setSymbolItem(IRON_SYMBOL_ITEM.get());
        ((SymbolBlock) GOLD_SYMBOL.get()).setSymbolItem(GOLD_SYMBOL_ITEM.get());
        ((SymbolBlock) OCCULT_SYMBOL.get()).setSymbolItem(OCCULT_SYMBOL_ITEM.get());
        ((SymbolBlock) DIVINE_SYMBOL.get()).setSymbolItem(DIVINE_SYMBOL_ITEM.get());
        ComposterBlock.COMPOSTABLES.put(Registration.VERDANT_BOTTLE.get(), 1.0F);
        ComposterBlock.COMPOSTABLES.put(Registration.FLOWER_VINES_ITEM.get(), 0.4F);
//        if(ModList.get().isLoaded("create")){
//            ReactiveCreatePlugin.init();
//        }
//        ReactivePehkuiPlugin.init(evt, ModList.get().isLoaded("pehkui"));
    }

    @SubscribeEvent
    public static void registerPotions(RegisterBrewingRecipesEvent event) {
        PotionBrewing.Builder builder = event.getBuilder();

        builder.addMix(
                Potions.THICK,
                SECRET_SCALE.get(),
                NULL_GRAVITY_POTION
        );

        builder.addMix(
                NULL_GRAVITY_POTION,
                Items.REDSTONE,
                LONG_NULL_GRAVITY_POTION
        );
    }

    @SubscribeEvent
    public static void buildContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == REACTIVE_TAB.getKey()) {
            for(DeferredHolder<Item, ? extends Item> item_holder : ITEMS.getEntries()){
                event.accept(item_holder.get());
            }
        }
    }

    // Helper method for Recipe Types.
    public static <T extends Recipe<?>> RecipeType<T> getRecipeType(final String id) {
        return new RecipeType<>()
        {
            public String toString() {
                return ReactiveMod.MODID + ":" + id;
            }
        };
    }
}
