package com.hyperlynx.reactive;

import com.hyperlynx.reactive.alchemy.Powers;
import com.hyperlynx.reactive.blocks.CrucibleBlock;
import com.hyperlynx.reactive.fx.CrucibleRenderer;
import com.hyperlynx.reactive.be.CrucibleBlockEntity;
import com.hyperlynx.reactive.recipes.DissolveRecipe;
import com.hyperlynx.reactive.recipes.DissolveRecipeSerializer;
import com.hyperlynx.reactive.recipes.TransmuteRecipe;
import com.hyperlynx.reactive.recipes.TransmuteRecipeSerializer;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.*;

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
    public static final RegistryObject<Item> CRUCIBLE_ITEM = fromBlock(CRUCIBLE, CreativeModeTab.TAB_MISC);

    public static final RegistryObject<BlockEntityType<CrucibleBlockEntity>> CRUCIBLE_BE_TYPE = TILES.register("crucible_be",
            () -> BlockEntityType.Builder.of(CrucibleBlockEntity::new, CRUCIBLE.get()).build(null));

    // Register items.
    public static final RegistryObject<Item> PURE_QUARTZ = ITEMS.register("quartz",
            () -> new Item(new Item.Properties().tab(CreativeModeTab.TAB_MISC)));

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


    // ----------------------- METHODS ------------------------

    // Helper method for BlockItem registration
    public static <B extends Block> RegistryObject<Item> fromBlock(RegistryObject<B> block, CreativeModeTab tab) {
        return ITEMS.register(block.getId().getPath(), () -> new BlockItem(block.get(), new Item.Properties().tab(tab)));
    }

    // Helper method for BlockItem registration without a tab
    public static <B extends Block> RegistryObject<Item> fromBlock(RegistryObject<B> block) {
        return ITEMS.register(block.getId().getPath(), () -> new BlockItem(block.get(), new Item.Properties()));
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
//    @SubscribeEvent
//    public static void registerParticles(ParticleFactoryRegisterEvent evt) { // Broken in new Forge
//    }

    @SubscribeEvent
    public static void registerBlockEntityRenderers(EntityRenderersEvent.RegisterRenderers evt) {
        evt.registerBlockEntityRenderer(CRUCIBLE_BE_TYPE.get(), CrucibleRenderer::new);
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void init(final FMLClientSetupEvent event) {
    }
}
