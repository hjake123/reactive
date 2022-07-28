package com.hyperlynx.reactive;

import com.hyperlynx.reactive.alchemy.Power;
import com.hyperlynx.reactive.blocks.CrucibleBlock;
import com.hyperlynx.reactive.fx.CrucibleRenderer;
import com.hyperlynx.reactive.be.CrucibleBlockEntity;
import com.hyperlynx.reactive.recipes.TransmuteRecipe;
import com.hyperlynx.reactive.recipes.TransmuteRecipeSerializer;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceLocation;
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

import java.util.function.Supplier;

public class Registration {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ReactiveMod.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ReactiveMod.MODID);
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, ReactiveMod.MODID);
    public static final DeferredRegister<BlockEntityType<?>> TILES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, ReactiveMod.MODID);

    // Handles registration of Powers.
    public static final DeferredRegister<Power> POWERS = DeferredRegister.create(new ResourceLocation(ReactiveMod.MODID, "power_registry"), ReactiveMod.MODID);
    public static final Supplier<IForgeRegistry<Power>> POWER_SUPPLIER = POWERS.makeRegistry(RegistryBuilder::new);

    // Handles registration of recipes.
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, ReactiveMod.MODID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, ReactiveMod.MODID);

    public static void init() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        BLOCKS.register(bus);
        ITEMS.register(bus);
        PARTICLES.register(bus);
        TILES.register(bus);
        POWERS.register(bus);
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

    // Registers the Alchemical Powers.
    public static final RegistryObject<Power> BLAZE_POWER = POWERS.register("blaze", () -> new Power("blaze",0xFFA300));
    public static final RegistryObject<Power> MIND_POWER = POWERS.register("mind", () -> new Power("mind",0x7A5BB5));
    public static final RegistryObject<Power> SOUL_POWER = POWERS.register("soul", () -> new Power("soul",0x60F5FA));
    public static final RegistryObject<Power> CURSE_POWER = POWERS.register("curse", () -> new Power("curse",0x2D231D));
    public static final RegistryObject<Power> LIGHT_POWER = POWERS.register("light", () -> new Power("light",0xF6DAB4));
    public static final RegistryObject<Power> WARP_POWER = POWERS.register("warp", () -> new Power("warp",0x118066));
    public static final RegistryObject<Power> VITAL_POWER = POWERS.register("vital", () -> new Power("vital",0xFF0606));
    public static final RegistryObject<Power> BODY_POWER = POWERS.register("body", () -> new Power("body",0xAF5220));
    public static final RegistryObject<Power> VERDANT_POWER = POWERS.register("verdant", () -> new Power("verdant",0x3ADB00));
    public static final RegistryObject<Power> ACID_POWER = POWERS.register("caustic", () -> new Power("caustic",0x9D1E2D));

    // These 'esoteric Powers' are formed from rare reactions and don't have an associated item tag.
    public static final RegistryObject<Power> X_POWER = POWERS.register("esoteric_x", () -> new Power("esoteric_x",0x0007C2));
    public static final RegistryObject<Power> Y_POWER = POWERS.register("esoteric_y", () -> new Power("esoteric_y",0xE5D059));
    public static final RegistryObject<Power> Z_POWER = POWERS.register("esoteric_z", () -> new Power("esoteric_z",0xDACCE8));

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
