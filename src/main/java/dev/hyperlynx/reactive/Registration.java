package dev.hyperlynx.reactive;

import dev.hyperlynx.reactive.advancements.EnterBlockInEndTrigger;
import dev.hyperlynx.reactive.advancements.FlagTrigger;
import dev.hyperlynx.reactive.advancements.StagedFlagTrigger;
import dev.hyperlynx.reactive.alchemy.Powers;
import dev.hyperlynx.reactive.alchemy.rxn.ReactionMan;
import dev.hyperlynx.reactive.alchemy.special.SpecialCaseMan;
//import com.hyperlynx.reactive.integration.create.ReactiveCreatePlugin;
import dev.hyperlynx.reactive.be.*;
import dev.hyperlynx.reactive.cmd.PowerArgumentInfo;
import dev.hyperlynx.reactive.cmd.PowerArgumentType;
import dev.hyperlynx.reactive.components.BoundEntity;
import dev.hyperlynx.reactive.components.LitmusMeasurement;
import dev.hyperlynx.reactive.components.WarpBottleTarget;
import dev.hyperlynx.reactive.integration.kubejs.events.EventTransceiver;
import dev.hyperlynx.reactive.util.HyperMobEffect;
import dev.hyperlynx.reactive.util.WorldSpecificValue;
import com.mojang.serialization.Codec;
import dev.hyperlynx.reactive.blocks.*;
import dev.hyperlynx.reactive.items.*;
import dev.hyperlynx.reactive.recipes.*;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Unit;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.ConditionalEffect;
import net.minecraft.world.item.enchantment.effects.EnchantmentValueEffect;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.event.RegisterConfigurationTasksEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

@SuppressWarnings("unused")
@EventBusSubscriber(modid=ReactiveMod.MODID, bus=EventBusSubscriber.Bus.MOD)
public class Registration {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ReactiveMod.MODID);
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(ReactiveMod.MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ReactiveMod.MODID);
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, ReactiveMod.MODID);
    public static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(BuiltInRegistries.POTION, ReactiveMod.MODID);
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, ReactiveMod.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, ReactiveMod.MODID);
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(BuiltInRegistries.RECIPE_TYPE, ReactiveMod.MODID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, ReactiveMod.MODID);
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, ReactiveMod.MODID);
    public static final DeferredRegister<DataComponentType<?>> COMPONENT_TYPES = DeferredRegister.create(BuiltInRegistries.DATA_COMPONENT_TYPE, ReactiveMod.MODID);
    public static final DeferredRegister<DataComponentType<?>> ENCHANTMENT_COMPONENT_TYPES = DeferredRegister.create(BuiltInRegistries.ENCHANTMENT_EFFECT_COMPONENT_TYPE, ReactiveMod.MODID);
    public static final DeferredRegister<CriterionTrigger<?>> CRITERIA_TRIGGERS = DeferredRegister.create(BuiltInRegistries.TRIGGER_TYPES, ReactiveMod.MODID);
    public static final DeferredRegister<ArgumentTypeInfo<?, ?>> COMMAND_ARGUMENTS = DeferredRegister.create(BuiltInRegistries.COMMAND_ARGUMENT_TYPE, ReactiveMod.MODID);

    public static void init(IEventBus bus) {
        BLOCKS.register(bus);
        ITEMS.register(bus);
        CREATIVE_TABS.register(bus);
        MOB_EFFECTS.register(bus);
        POTIONS.register(bus);
        PARTICLES.register(bus);
        BLOCK_ENTITY_TYPES.register(bus);
        Powers.POWERS.register(bus);
        COMPONENT_TYPES.register(bus);
        ENCHANTMENT_COMPONENT_TYPES.register(bus);
        CRITERIA_TRIGGERS.register(bus);
        bus.addListener(ReactionMan.CRITERIA_BUILDER::register);
        RECIPE_TYPES.register(bus);
        RECIPE_SERIALIZERS.register(bus);
        SOUND_EVENTS.register(bus);
        COMMAND_ARGUMENTS.register(bus);
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
    public static final DeferredHolder<Item, BlockItem> SHULKER_CRUCIBLE_ITEM = ITEMS.register(SHULKER_CRUCIBLE.getId().getPath(),
            () -> new BlockItem(SHULKER_CRUCIBLE.get(), new Item.Properties()));

    // Register the Crucible BE.
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CrucibleBlockEntity>> CRUCIBLE_BE_TYPE = BLOCK_ENTITY_TYPES.register("crucible_be",
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
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SymbolBlockEntity>> SYMBOL_BE_TYPE = BLOCK_ENTITY_TYPES.register("symbol_be",
            () -> BlockEntityType.Builder.of(SymbolBlockEntity::new, COPPER_SYMBOL.get(), IRON_SYMBOL.get(), GOLD_SYMBOL.get(), OCCULT_SYMBOL.get(), DIVINE_SYMBOL.get()).build(null));

    public static final DeferredHolder<Block, BlazeRodBlock> BLAZE_ROD = BLOCKS.register("blaze_rod",
            () -> new BlazeRodBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.END_ROD)));
    public static final DeferredHolder<Item, BlockItem> BLAZE_ROD_ITEM = ITEMS.registerSimpleBlockItem(BLAZE_ROD);

    public static final DeferredHolder<Block, BreezeRodBlock> BREEZE_ROD = BLOCKS.register("breeze_rod",
            () -> new BreezeRodBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.END_ROD)));
    public static final DeferredHolder<Item, BlockItem> BREEZE_ROD_ITEM = ITEMS.registerSimpleBlockItem(BREEZE_ROD);

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

    public static final DeferredHolder<Block, MnemonicBlock> MNEMONIC_BULB = BLOCKS.register("mnemonic_bulb",
            () -> new MnemonicBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.MUD)));

    public static final DeferredHolder<Item, BlockItem> MNEMONIC_BULB_ITEM = ITEMS.registerSimpleBlockItem(MNEMONIC_BULB);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MnemonicBlockEntity>> MNEMONIC_BULB_BE_TYPE =
            BLOCK_ENTITY_TYPES.register("mnemonic_bulb_be",
                    () -> BlockEntityType.Builder.of(MnemonicBlockEntity::new, MNEMONIC_BULB.get()).build(null));

    public static final DeferredHolder<Block, MindLichenBlock> MIND_LICHEN = BLOCKS.register("mind_lichen",
            () -> new MindLichenBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GLOW_LICHEN)));

    public static final DeferredHolder<Item, BlockItem> MIND_LICHEN_ITEM = ITEMS.registerSimpleBlockItem(MIND_LICHEN);

    public static final DeferredHolder<Block, FlowerVineBlock> FLOWER_VINES = BLOCKS.register("flower_vine",
            () -> new FlowerVineBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.VINE)));

    public static final DeferredHolder<Block, FlowerVinePlantBlock> FLOWER_VINES_BODY = BLOCKS.register("flower_vine_plant",
            () -> new FlowerVinePlantBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.VINE)));

    public static final DeferredHolder<Item, BlockItem> FLOWER_VINES_ITEM = ITEMS.registerSimpleBlockItem(FLOWER_VINES);

    public static final DeferredHolder<Block, GravityChandelierBlock> GRAVITY_CHANDELIER = BLOCKS.register("gravity_chandelier",
            () -> new GravityChandelierBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.TORCH)));

    public static final DeferredHolder<Item, BlockItem> GRAVITY_CHANDELIER_ITEM = ITEMS.registerSimpleBlockItem(GRAVITY_CHANDELIER);

    public static final DeferredHolder<Block, GatewayPlinthBlock> GATEWAY_PLINTH = BLOCKS.register("rending_plinth",
            () -> new GatewayPlinthBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.LODESTONE)));

    public static final DeferredHolder<Item, BlockItem> GATEWAY_PLINTH_ITEM = ITEMS.registerSimpleBlockItem(GATEWAY_PLINTH);

    public static final DeferredHolder<Block, AcidBlock> ACID_BLOCK = BLOCKS.register("acid_block",
            () -> new AcidBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SLIME_BLOCK).speedFactor(0.65F).strength(1.4F)));

    public static final DeferredHolder<Item, AcidBucketItem> ACID_BUCKET = ITEMS.register("acid_bucket",
            () -> new AcidBucketItem(ACID_BLOCK.get(), SoundEvents.BUCKET_FILL, new Item.Properties()));

    // Register the Gravity related BEs
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GravityChandelierBlockEntity>> GRAVITY_CHANDELIER_BE_TYPE =
            BLOCK_ENTITY_TYPES.register("gravity_chandelier_be",
                    () -> BlockEntityType.Builder.of(GravityChandelierBlockEntity::new, GRAVITY_CHANDELIER.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GravityBeamBlockEntity>> GRAVITY_BEAM_BE_TYPE =
            BLOCK_ENTITY_TYPES.register("gravity_beam_be",
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

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<StaffBlockEntity>> STAFF_BE = BLOCK_ENTITY_TYPES.register("staff_be",
            () -> BlockEntityType.Builder.of(StaffBlockEntity::new, STAFF_OF_LIGHT.get(), STAFF_OF_SOUL.get(), STAFF_OF_LIFE.get(), STAFF_OF_MIND.get(), STAFF_OF_BLAZE.get(), STAFF_OF_WARP.get()).build(null));

    // Register technical blocks.
    public static final DeferredHolder<Block, ActiveGoldFoamBlock> ACTIVE_GOLD_FOAM = BLOCKS.register("active_gold_foam",
            () -> new ActiveGoldFoamBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SLIME_BLOCK).jumpFactor(0.9F).sound(SoundType.WOOL)));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ActiveFoamBlockEntity>> ACTIVE_GOLD_FOAM_BE = BLOCK_ENTITY_TYPES.register("active_gold_foam_be",
            () -> BlockEntityType.Builder.of(ActiveFoamBlockEntity::new, ACTIVE_GOLD_FOAM.get()).build(null));

    public static final DeferredHolder<Block, DisplacedBlock> DISPLACED_BLOCK = BLOCKS.register("displaced_block",
            DisplacedBlock::new);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DisplacedBlockEntity>> DISPLACED_BLOCK_BE = BLOCK_ENTITY_TYPES.register("displaced_block_be",
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
    public static final DeferredHolder<Item, VortexStoneItem> VORTEX_STONE = ITEMS.register("vortex_stone",
            () -> new VortexStoneItem(new Item.Properties()
                    .durability(640)));

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
                    .addAttributeModifier(Attributes.GRAVITY, ReactiveMod.location("no_gravity_effect"),
                            -1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));

    public static final DeferredHolder<MobEffect, MobEffect> IMMOBILE = MOB_EFFECTS.register("immobility",
            () -> new HyperMobEffect(MobEffectCategory.NEUTRAL, 0x118066)
                    .addAttributeModifier(Attributes.MOVEMENT_SPEED, ReactiveMod.location("immobility_slowness_effect"),
                            -1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                    .addAttributeModifier(Attributes.FLYING_SPEED, ReactiveMod.location("immobility_flying_slowness_effect"),
                            -1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));

    public static final DeferredHolder<MobEffect, MobEffect> FAR_REACH = MOB_EFFECTS.register("far_reach",
            () -> new HyperMobEffect(MobEffectCategory.BENEFICIAL, 0x7A5BB5)
                    .addAttributeModifier(Attributes.BLOCK_INTERACTION_RANGE, ReactiveMod.location("far_block_reach_effect"),
                            2, AttributeModifier.Operation.ADD_VALUE)
                    .addAttributeModifier(Attributes.ENTITY_INTERACTION_RANGE, ReactiveMod.location("far_entity_reach_effect"),
                            2, AttributeModifier.Operation.ADD_VALUE));

    public static final DeferredHolder<MobEffect, HyperMobEffect> FIRE_SHIELD = MOB_EFFECTS.register("fire_shield",
            () -> new HyperMobEffect(MobEffectCategory.BENEFICIAL, 0xFFA511));

    public static final DeferredHolder<MobEffect, MobEffect> HIGH_STEP = MOB_EFFECTS.register("high_step",
            () -> new HyperMobEffect(MobEffectCategory.BENEFICIAL, 0x18AD88)
                    .addAttributeModifier(Attributes.STEP_HEIGHT, ReactiveMod.location("high_step_effect"),
                            1, AttributeModifier.Operation.ADD_VALUE));

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
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.parse("reactive:zap")));

    public static final DeferredHolder<SoundEvent, SoundEvent> RUMBLE_SOUND = SOUND_EVENTS.register("rumble",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.parse("reactive:rumble")));

    // Register dummy blocks for the weird water types and the symbol eye render.
    public static final DeferredHolder<Block, Block> DUMMY_MAGIC_WATER = BLOCKS.register("magic_water",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.WATER)));
    public static final DeferredHolder<Block, Block> DUMMY_NOISE_WATER = BLOCKS.register("noisy_water",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.WATER)));
    public static final DeferredHolder<Block, Block> DUMMY_FAST_WATER = BLOCKS.register("fast_water",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.WATER)));
    public static final DeferredHolder<Block, Block> DUMMY_SLOW_WATER = BLOCKS.register("slow_water",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.WATER)));

    // Register the recipe types and serializers.
    public static final DeferredHolder<RecipeType<?>, RecipeType<TransmuteRecipe>> TRANS_RECIPE_TYPE = RECIPE_TYPES.register("transmutation", () -> getRecipeType("transmutation"));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<TransmuteRecipe>> TRANS_SERIALIZER = RECIPE_SERIALIZERS.register("transmutation", TransmuteRecipeSerializer::new);

    public static final DeferredHolder<RecipeType<?>, RecipeType<DissolveRecipe>> DISSOLVE_RECIPE_TYPE = RECIPE_TYPES.register("dissolve", () -> getRecipeType("dissolve"));
    public static final DeferredHolder<RecipeSerializer<?>,RecipeSerializer<DissolveRecipe>> DISSOLVE_SERIALIZER = RECIPE_SERIALIZERS.register("dissolve", DissolveRecipeSerializer::new);

    public static final DeferredHolder<RecipeType<?>, RecipeType<PrecipitateRecipe>> PRECIPITATE_RECIPE_TYPE = RECIPE_TYPES.register("precipitation", () -> getRecipeType("precipitation"));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<PrecipitateRecipe>> PRECIPITATE_SERIALIZER = RECIPE_SERIALIZERS.register("precipitation", PrecipitateRecipeSerializer::new);

    // Register the data components.
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Unit>> TUTORIAL_DONE =
            COMPONENT_TYPES.register("tutorial",
                    () -> DataComponentType.<Unit>builder()
                            .persistent(Codec.unit(Unit.INSTANCE))
                            .networkSynchronized(StreamCodec.unit(Unit.INSTANCE))
                            .build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<BoundEntity>> BOUND_ENTITY =
            COMPONENT_TYPES.register("bound_entity",
                    () -> DataComponentType.<BoundEntity>builder()
                            .persistent(BoundEntity.CODEC)
                            .networkSynchronized(BoundEntity.STREAM_CODEC)
                            .build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<LitmusMeasurement>> LITMUS_MEASUREMENT =
            COMPONENT_TYPES.register("litmus_measurement",
                    () -> DataComponentType.<LitmusMeasurement>builder()
                            .persistent(LitmusMeasurement.CODEC)
                            .networkSynchronized(LitmusMeasurement.STREAM_CODEC)
                            .build()
            );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<WarpBottleTarget>> WARP_BOTTLE_TARGET =
            COMPONENT_TYPES.register("warp_bottle_target",
                    () -> DataComponentType.<WarpBottleTarget>builder()
                            .persistent(WarpBottleTarget.CODEC)
                            .networkSynchronized(WarpBottleTarget.STREAM_CODEC)
                            .build()
            );

    // Register the enchantment components.
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>>> STAFF_DAMAGE =
            ENCHANTMENT_COMPONENT_TYPES.register("staff_damage",
                    () -> DataComponentType.<List<ConditionalEffect<EnchantmentValueEffect>>>builder()
                            .persistent(ConditionalEffect.codec(EnchantmentValueEffect.CODEC, LootContextParamSets.ENCHANTED_DAMAGE).listOf())
                            .build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>>> STAFF_RATE =
            ENCHANTMENT_COMPONENT_TYPES.register("staff_rate",
                    () -> DataComponentType.<List<ConditionalEffect<EnchantmentValueEffect>>>builder()
                            .persistent(ConditionalEffect.codec(EnchantmentValueEffect.CODEC, LootContextParamSets.ENCHANTED_DAMAGE).listOf())
                            .build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Unit>> WIDE_RANGE =
            ENCHANTMENT_COMPONENT_TYPES.register("wide_range",
                    () -> DataComponentType.<Unit>builder()
                            .persistent(Unit.CODEC)
                            .build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Unit>> WORLD_PIERCER =
            ENCHANTMENT_COMPONENT_TYPES.register("world_piercer",
                    () -> DataComponentType.<Unit>builder()
                            .persistent(Unit.CODEC)
                            .build());

    //Register advancement criteria for the book
    public static final DeferredHolder<CriterionTrigger<?>, FlagTrigger> MAKE_CRUCIBLE_TRIGGER = CRITERIA_TRIGGERS.register("make_crucible_criterion",
            () -> new FlagTrigger(ReactiveMod.location("make_crucible_criterion")));
    
    public static final DeferredHolder<CriterionTrigger<?>, FlagTrigger> ISOLATE_OMEN_TRIGGER = CRITERIA_TRIGGERS.register("isolate_omen_criterion",
            () -> new FlagTrigger(ReactiveMod.location("isolate_omen_criterion")));

    public static final DeferredHolder<CriterionTrigger<?>, FlagTrigger> SIZE_CHANGED_TRIGGER = CRITERIA_TRIGGERS.register("size_change_criterion",
            () -> new FlagTrigger(ReactiveMod.location("size_change_criterion")));

    public static final DeferredHolder<CriterionTrigger<?>, FlagTrigger> SIZE_REVERTED_TRIGGER = CRITERIA_TRIGGERS.register("size_revert_criterion",
            () -> new FlagTrigger(ReactiveMod.location("size_revert_criterion")));

    public static final DeferredHolder<CriterionTrigger<?>, FlagTrigger> SEE_BLAZE_GATHER_TRIGGER = CRITERIA_TRIGGERS.register("see_blaze_gather_criterion",
            () -> new FlagTrigger(ReactiveMod.location("see_blaze_gather_criterion")));

    public static final DeferredHolder<CriterionTrigger<?>, FlagTrigger> BE_TELEPORTED_TRIGGER = CRITERIA_TRIGGERS.register("be_teleported_criterion",
            () -> new FlagTrigger(ReactiveMod.location("be_teleported_criterion")));

    public static final DeferredHolder<CriterionTrigger<?>, FlagTrigger> SEE_CRUCIBLE_FAIL_TRIGGER = CRITERIA_TRIGGERS.register("see_crucible_fail_criterion",
            () -> new FlagTrigger(ReactiveMod.location("see_crucible_fail_criterion")));

    public static final DeferredHolder<CriterionTrigger<?>, FlagTrigger> SEE_ALLAY_SUMMON_TRIGGER = CRITERIA_TRIGGERS.register("see_allay_summon_criterion",
            () -> new FlagTrigger(ReactiveMod.location("see_allay_summon_criterion")));

    public static final DeferredHolder<CriterionTrigger<?>, FlagTrigger> BE_LEVITATED_TRIGGER = CRITERIA_TRIGGERS.register("be_levitated_criterion",
            () -> new FlagTrigger(ReactiveMod.location("be_levitated_criterion")));

    public static final DeferredHolder<CriterionTrigger<?>, FlagTrigger> BE_SLOWFALLED_TRIGGER = CRITERIA_TRIGGERS.register("be_slowfalled_criterion",
            () -> new FlagTrigger(ReactiveMod.location("be_slowfalled_criterion")));

    public static final DeferredHolder<CriterionTrigger<?>, FlagTrigger> SEE_DISPLACEMENT_TRIGGER = CRITERIA_TRIGGERS.register("see_displacement_criterion",
            () -> new StagedFlagTrigger(ReactiveMod.location("see_displacement_criterion"), ReactiveMod.location("get_motion_salts")));

    public static final DeferredHolder<CriterionTrigger<?>, FlagTrigger> HARVEST_TRIGGER = CRITERIA_TRIGGERS.register("harvest_criterion",
            () -> new StagedFlagTrigger(ReactiveMod.location("harvest_criterion"), ReactiveMod.location("see_synthesis")));

    public static final DeferredHolder<CriterionTrigger<?>, FlagTrigger> OCCULT_AWAKENING_TRIGGER = CRITERIA_TRIGGERS.register("activate_eye_criterion",
            () -> new StagedFlagTrigger(ReactiveMod.location("activate_eye_criterion"), ReactiveMod.location("place_eye")));

    public static final DeferredHolder<CriterionTrigger<?>, FlagTrigger> PLACE_OCCULT_TRIGGER = CRITERIA_TRIGGERS.register("place_eye_criterion",
            () -> new FlagTrigger(ReactiveMod.location("place_eye_criterion")));

    public static final DeferredHolder<CriterionTrigger<?>, FlagTrigger> PORTAL_FREEZE_TRIGGER = CRITERIA_TRIGGERS.register("portal_freeze_criterion",
            () -> new FlagTrigger(ReactiveMod.location("portal_freeze_criterion")));

    public static final DeferredHolder<CriterionTrigger<?>, FlagTrigger> PORTAL_TRADE_TRIGGER = CRITERIA_TRIGGERS.register("portal_trade_criterion",
            () -> new FlagTrigger(ReactiveMod.location("portal_trade_criterion")));

    public static final DeferredHolder<CriterionTrigger<?>, FlagTrigger> MAKE_RIFT_TRIGGER = CRITERIA_TRIGGERS.register("make_rift_criterion",
            () -> new StagedFlagTrigger(ReactiveMod.location("make_rift_criterion"), ReactiveMod.location("dissolve_tp")));

    public static final DeferredHolder<CriterionTrigger<?>, FlagTrigger> SEE_SACRIFICE_TRIGGER = CRITERIA_TRIGGERS.register("see_sacrifice_criterion",
            () -> new FlagTrigger(ReactiveMod.location("see_sacrifice_criterion")));

    public static final DeferredHolder<CriterionTrigger<?>, FlagTrigger> TRY_LAVA_CRUCIBLE_TRIGGER = CRITERIA_TRIGGERS.register("try_lava_crucible_criterion",
            () -> new StagedFlagTrigger(ReactiveMod.location("try_lava_crucible_criterion"), ReactiveMod.location("try_nether_crucible")));

    public static final DeferredHolder<CriterionTrigger<?>, FlagTrigger> TRY_NETHER_CRUCIBLE_TRIGGER = CRITERIA_TRIGGERS.register("try_nether_crucible_criterion",
            () -> new FlagTrigger(ReactiveMod.location("try_nether_crucible_criterion")));

    public static final DeferredHolder<CriterionTrigger<?>, FlagTrigger> BE_CURSED_TRIGGER = CRITERIA_TRIGGERS.register("be_cursed_criterion",
            () -> new FlagTrigger(ReactiveMod.location("be_cursed_criterion")));

    public static final DeferredHolder<CriterionTrigger<?>, FlagTrigger> SEE_SYNTHESIS_TRIGGER = CRITERIA_TRIGGERS.register("see_synthesis_criterion",
            () -> new FlagTrigger(ReactiveMod.location("see_synthesis_criterion")));

    public static final DeferredHolder<CriterionTrigger<?>, FlagTrigger> ENDER_PEARL_DISSOLVE_TRIGGER = CRITERIA_TRIGGERS.register("dissolve_tp_criterion",
            () -> new FlagTrigger(ReactiveMod.location("dissolve_tp_criterion")));

    public static final DeferredHolder<CriterionTrigger<?>, FlagTrigger> SEE_FAILED_FLOW_CONTAINMENT = CRITERIA_TRIGGERS.register("fail_flow_containment_criterion",
            () -> new FlagTrigger(ReactiveMod.location("fail_flow_containment_criterion")));

    public static final DeferredHolder<CriterionTrigger<?>, FlagTrigger> SEE_FLOW_CONTAINMENT = CRITERIA_TRIGGERS.register("see_flow_containment_criterion",
            () -> new FlagTrigger(ReactiveMod.location("see_flow_containment_criterion")));

    public static final DeferredHolder<CriterionTrigger<?>, EnterBlockInEndTrigger> TRAVERSE_END_GATEWAY_IN_END = CRITERIA_TRIGGERS.register("enter_block_in_end",
            EnterBlockInEndTrigger::new);

    public static final DeferredHolder<ArgumentTypeInfo<?, ?>, ArgumentTypeInfo<PowerArgumentType, PowerArgumentInfo.Template>> POWER_ARGUMENT =
            COMMAND_ARGUMENTS.register("power_argument", PowerArgumentInfo::new);

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
        COPPER_SYMBOL.get().setSymbolItem(COPPER_SYMBOL_ITEM.get());
        IRON_SYMBOL.get().setSymbolItem(IRON_SYMBOL_ITEM.get());
        GOLD_SYMBOL.get().setSymbolItem(GOLD_SYMBOL_ITEM.get());
        OCCULT_SYMBOL.get().setSymbolItem(OCCULT_SYMBOL_ITEM.get());
        DIVINE_SYMBOL.get().setSymbolItem(DIVINE_SYMBOL_ITEM.get());
//        if(ModList.get().isLoaded("create")){
//            ReactiveCreatePlugin.init();
//        }
        if(ModList.get().isLoaded("kubejs")){
            NeoForge.EVENT_BUS.register(EventTransceiver.class);
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

    // Register networking stuff for WSV.
    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        // Sets the current network version
        final PayloadRegistrar registrar = event.registrar("1");
        registrar.configurationToClient(
                WorldSpecificValue.AlchemySeedData.TYPE,
                WorldSpecificValue.AlchemySeedData.STREAM_CODEC,
                new WorldSpecificValue.AlchemySeedPayloadHandler()
        );
    }

    @SubscribeEvent
    public static void register(final RegisterConfigurationTasksEvent event) {
        event.register(new WorldSpecificValue.AlchemySeedConfigurationTask(event.getListener()));
    }
}