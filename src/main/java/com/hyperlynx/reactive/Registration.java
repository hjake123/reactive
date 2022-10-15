package com.hyperlynx.reactive;

import com.hyperlynx.reactive.alchemy.Powers;
import com.hyperlynx.reactive.be.CrucibleBlockEntity;
import com.hyperlynx.reactive.be.SymbolBlockEntity;
import com.hyperlynx.reactive.blocks.*;
import com.hyperlynx.reactive.fx.CrucibleRenderer;
import com.hyperlynx.reactive.fx.StardustParticle;
import com.hyperlynx.reactive.fx.SymbolRenderer;
import com.hyperlynx.reactive.items.*;
import com.hyperlynx.reactive.util.FlagCriterion;
import com.hyperlynx.reactive.recipes.DissolveRecipe;
import com.hyperlynx.reactive.recipes.DissolveRecipeSerializer;
import com.hyperlynx.reactive.recipes.TransmuteRecipe;
import com.hyperlynx.reactive.recipes.TransmuteRecipeSerializer;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@SuppressWarnings("unused")
public class Registration {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ReactiveMod.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ReactiveMod.MODID);
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, ReactiveMod.MODID);
    public static final DeferredRegister<BlockEntityType<?>> TILES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, ReactiveMod.MODID);

    // Handles registration of recipes.
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, ReactiveMod.MODID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, ReactiveMod.MODID);

    public static void init() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        BLOCKS.register(bus);
        ITEMS.register(bus);
        PARTICLES.register(bus);
        TILES.register(bus);
        Powers.POWERS.register(bus);
        RECIPE_TYPES.register(bus);
        RECIPE_SERIALIZERS.register(bus);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> bus.register(Registration.class));
    }

    // ----------------------- REGISTRATION ------------------------
    // Register the all-important Crucible.
    public static final RegistryObject<Block> CRUCIBLE = BLOCKS.register("crucible",
            () -> new CrucibleBlock(BlockBehaviour.Properties.copy(Blocks.CAULDRON)));
    public static final RegistryObject<Item> CRUCIBLE_ITEM = fromBlock(CRUCIBLE, ReactiveMod.CREATIVE_TAB);

    public static final RegistryObject<BlockEntityType<CrucibleBlockEntity>> CRUCIBLE_BE_TYPE = TILES.register("crucible_be",
            () -> BlockEntityType.Builder.of(CrucibleBlockEntity::new, CRUCIBLE.get()).build(null));

    // Register the Symbol blocks, items, and the BE.
    public static final RegistryObject<Block> COPPER_SYMBOL = BLOCKS.register("copper_symbol",
            () -> new SymbolBlock(BlockBehaviour.Properties.copy(Blocks.TRIPWIRE_HOOK)));
    public static final RegistryObject<Item> COPPER_SYMBOL_ITEM = fromBlock(COPPER_SYMBOL, ReactiveMod.CREATIVE_TAB);

    public static final RegistryObject<Block> IRON_SYMBOL = BLOCKS.register("iron_symbol",
            () -> new SymbolBlock(BlockBehaviour.Properties.copy(Blocks.TRIPWIRE_HOOK)));
    public static final RegistryObject<Item> IRON_SYMBOL_ITEM = fromBlock(IRON_SYMBOL, ReactiveMod.CREATIVE_TAB);

    public static final RegistryObject<Block> GOLD_SYMBOL = BLOCKS.register("gold_symbol",
            () -> new SymbolBlock(BlockBehaviour.Properties.copy(Blocks.TRIPWIRE_HOOK)));
    public static final RegistryObject<Item> GOLD_SYMBOL_ITEM = fromBlock(GOLD_SYMBOL, ReactiveMod.CREATIVE_TAB);

    public static final RegistryObject<BlockEntityType<SymbolBlockEntity>> SYMBOL_BE_TYPE = TILES.register("symbol_be",
            () -> BlockEntityType.Builder.of(SymbolBlockEntity::new, COPPER_SYMBOL.get(), IRON_SYMBOL.get(), GOLD_SYMBOL.get()).build(null));

    // Additional blocks
    public static final RegistryObject<Block> BLAZE_ROD = BLOCKS.register("blaze_rod",
            () -> new BlazeRodBlock(BlockBehaviour.Properties.copy(Blocks.END_ROD)));
    public static final RegistryObject<Item> BLAZE_ROD_ITEM = fromBlock(BLAZE_ROD, ReactiveMod.CREATIVE_TAB);

    public static final RegistryObject<Block> STARDUST = BLOCKS.register("stardust",
            () -> new StardustBlock(BlockBehaviour.Properties.copy(Blocks.TORCH).lightLevel((BlockState s) -> 15).sound(SoundType.WOOL)));

    public static final RegistryObject<Block> PURE_QUARTZ_BLOCK = BLOCKS.register("pure_quartz_block",
            () -> new PureQuartzBlock(BlockBehaviour.Properties.copy(Blocks.QUARTZ_BLOCK)));
    public static final RegistryObject<Item> PURE_QUARTZ_BLOCK_ITEM = fromBlock(PURE_QUARTZ_BLOCK, ReactiveMod.CREATIVE_TAB);

    public static final RegistryObject<Block> VOLT_CELL = BLOCKS.register("volt_cell",
            () -> new VoltCellBlock(BlockBehaviour.Properties.copy(Blocks.COPPER_BLOCK)));
    public static final RegistryObject<Item> VOLT_CELL_ITEM = fromBlock(VOLT_CELL, ReactiveMod.CREATIVE_TAB);


    // Register items.
    public static final RegistryObject<Item> PURE_QUARTZ = ITEMS.register("quartz",
            () -> new Item(new Item.Properties().tab(ReactiveMod.CREATIVE_TAB)));
    public static final RegistryObject<Item> STARDUST_ITEM = ITEMS.register("stardust",
            () -> new StardustItem(new Item.Properties().tab(ReactiveMod.CREATIVE_TAB)));
    public static final RegistryObject<Item> SCROLL = ITEMS.register("scroll",
            () -> new AlchemyScroll(new Item.Properties().tab(ReactiveMod.CREATIVE_TAB)
                    .stacksTo(1).rarity(Rarity.RARE)));
    public static final RegistryObject<Item> QUARTZ_BOTTLE = ITEMS.register("quartz_bottle",
            () -> new Item(new Item.Properties().tab(ReactiveMod.CREATIVE_TAB)));
    public static final RegistryObject<Item> CRYSTAL_IRON = ITEMS.register("crystal_iron",
            () -> new CrystalIronItem(new Item.Properties().tab(ReactiveMod.CREATIVE_TAB).defaultDurability(16)));
    public static final RegistryObject<Item> SOUP = ITEMS.register("soup",
            () -> new SoupItem(new Item.Properties().tab(CreativeModeTab.TAB_FOOD).stacksTo(64).food((new FoodProperties.Builder().nutrition(8).saturationMod(0.6F)).build())));


    // Register Power bottles
    public static final RegistryObject<Item> ACID_BOTTLE = ITEMS.register("acid_bottle",
            () -> new PowerBottleItem(new Item.Properties().tab(ReactiveMod.CREATIVE_TAB)));
    public static final RegistryObject<Item> BLAZE_BOTTLE = ITEMS.register("blaze_bottle",
            () -> new BlazeBottleItem(new Item.Properties().tab(ReactiveMod.CREATIVE_TAB)));
    public static final RegistryObject<Item> MIND_BOTTLE = ITEMS.register("mind_bottle",
            () -> new PowerBottleItem(new Item.Properties().tab(ReactiveMod.CREATIVE_TAB)));
    public static final RegistryObject<Item> SOUL_BOTTLE = ITEMS.register("soul_bottle",
            () -> new PowerBottleItem(new Item.Properties().tab(ReactiveMod.CREATIVE_TAB)));
    public static final RegistryObject<Item> WARP_BOTTLE = ITEMS.register("warp_bottle",
            () -> new PowerBottleItem(new Item.Properties().tab(ReactiveMod.CREATIVE_TAB)));
    public static final RegistryObject<Item> VERDANT_BOTTLE = ITEMS.register("verdant_bottle",
            () -> new PowerBottleItem(new Item.Properties().tab(ReactiveMod.CREATIVE_TAB)));
    public static final RegistryObject<Item> BODY_BOTTLE = ITEMS.register("body_bottle",
            () -> new PowerBottleItem(new Item.Properties().tab(ReactiveMod.CREATIVE_TAB)));
    public static final RegistryObject<Item> LIGHT_BOTTLE = ITEMS.register("light_bottle",
            () -> new PowerBottleItem(new Item.Properties().tab(ReactiveMod.CREATIVE_TAB)));

    // Register particles
    public static final SimpleParticleType STARDUST_PARTICLE = new SimpleParticleType(false);
    public static final RegistryObject<ParticleType<SimpleParticleType>> STARDUST_PARTICLE_TYPE = PARTICLES.register("stardust",
            () -> STARDUST_PARTICLE);

    // Register dummy blocks for the weird water types.
    public static final RegistryObject<Block> DUMMY_MAGIC_WATER = BLOCKS.register("magic_water",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.WATER)));
    public static final RegistryObject<Block> DUMMY_NOISE_WATER = BLOCKS.register("noisy_water",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.WATER)));
    public static final RegistryObject<Block> DUMMY_FAST_WATER = BLOCKS.register("fast_water",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.WATER)));

    //Register the recipe types and serializers.
    public static final RegistryObject<RecipeType<TransmuteRecipe>> TRANS_RECIPE_TYPE = RECIPE_TYPES.register("transmutation", () -> getRecipeType("transmutation"));
    public static final RegistryObject<RecipeSerializer<TransmuteRecipe>> TRANS_SERIALIZER = RECIPE_SERIALIZERS.register("transmutation", TransmuteRecipeSerializer::new);

    public static final RegistryObject<RecipeType<DissolveRecipe>> DISSOLVE_RECIPE_TYPE = RECIPE_TYPES.register("dissolve", () -> getRecipeType("dissolve"));
    public static final RegistryObject<RecipeSerializer<DissolveRecipe>> DISSOLVE_SERIALIZER = RECIPE_SERIALIZERS.register("dissolve", DissolveRecipeSerializer::new);

    //Register advancement criteria for the book
    public static final FlagCriterion ENDER_PEARL_DISSOLVE_TRIGGER = new FlagCriterion(new ResourceLocation(ReactiveMod.MODID, "dissolve_tp_criterion"));
    public static final FlagCriterion SEE_SYNTHESIS_TRIGGER = new FlagCriterion(new ResourceLocation(ReactiveMod.MODID, "see_synthesis_criterion"));
    public static final FlagCriterion BE_CURSED_TRIGGER = new FlagCriterion(new ResourceLocation(ReactiveMod.MODID, "be_cursed_criterion"));
    public static final FlagCriterion TRY_NETHER_CRUCIBLE_TRIGGER = new FlagCriterion(new ResourceLocation(ReactiveMod.MODID, "try_nether_crucible_criterion"));
    public static final FlagCriterion TRY_LAVA_CRUCIBLE_TRIGGER = new FlagCriterion(new ResourceLocation(ReactiveMod.MODID, "try_lava_crucible_criterion"));
    public static final FlagCriterion SEE_SACRIFICE_TRIGGER = new FlagCriterion(new ResourceLocation(ReactiveMod.MODID, "see_sacrifice_criterion"));


    // ----------------------- METHODS ------------------------

    // Helper method for BlockItem registration
    public static <B extends Block> RegistryObject<Item> fromBlock(RegistryObject<B> block, CreativeModeTab tab) {
        return ITEMS.register(block.getId().getPath(), () -> new BlockItem(block.get(), new Item.Properties().tab(tab)));
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

    // Various event handlers to set up different items.
    @SubscribeEvent
    public static void registerParticles(RegisterParticleProvidersEvent evt) {
        Minecraft.getInstance().particleEngine.register(STARDUST_PARTICLE_TYPE.get(), StardustParticle.StardustParticleProvider::new);
    }

    @SubscribeEvent
    public static void commonSetupHandler(FMLCommonSetupEvent evt){
        ((SymbolBlock) COPPER_SYMBOL.get()).setSymbolItem(COPPER_SYMBOL_ITEM.get());
        ((SymbolBlock) IRON_SYMBOL.get()).setSymbolItem(IRON_SYMBOL_ITEM.get());
        ((SymbolBlock) GOLD_SYMBOL.get()).setSymbolItem(GOLD_SYMBOL_ITEM.get());
        evt.enqueueWork(() -> CriteriaTriggers.register(ENDER_PEARL_DISSOLVE_TRIGGER));
        evt.enqueueWork(() -> CriteriaTriggers.register(SEE_SYNTHESIS_TRIGGER));
        evt.enqueueWork(() -> CriteriaTriggers.register(BE_CURSED_TRIGGER));
        evt.enqueueWork(() -> CriteriaTriggers.register(TRY_NETHER_CRUCIBLE_TRIGGER));
        evt.enqueueWork(() -> CriteriaTriggers.register(TRY_LAVA_CRUCIBLE_TRIGGER));
        evt.enqueueWork(() -> CriteriaTriggers.register(SEE_SACRIFICE_TRIGGER));
    }

    @SubscribeEvent
    public static void registerBlockEntityRenderers(EntityRenderersEvent.RegisterRenderers evt) {
        evt.registerBlockEntityRenderer(CRUCIBLE_BE_TYPE.get(), CrucibleRenderer::new);
        evt.registerBlockEntityRenderer(SYMBOL_BE_TYPE.get(), SymbolRenderer::new);
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void init(final FMLClientSetupEvent event) {

    }
}
