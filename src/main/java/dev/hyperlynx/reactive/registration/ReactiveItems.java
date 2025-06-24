package dev.hyperlynx.reactive.registration;

import dev.hyperlynx.reactive.ConfigMan;
import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.items.*;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ReactiveItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ReactiveMod.MODID);

    public static final DeferredHolder<Item, BlockItem> CRUCIBLE = ITEMS.registerSimpleBlockItem(ReactiveBlocks.CRUCIBLE);

    public static final DeferredHolder<Item, BlockItem> SALTY_CRUCIBLE = ITEMS.registerSimpleBlockItem(ReactiveBlocks.SALTY_CRUCIBLE);

    public static final DeferredHolder<Item, BlockItem> SHULKER_CRUCIBLE = ITEMS.register(ReactiveBlocks.SHULKER_CRUCIBLE.getId().getPath(),
            () -> new BlockItem(ReactiveBlocks.SHULKER_CRUCIBLE.get(), new Item.Properties()));

    public static final DeferredHolder<Item, SymbolItem> COPPER_SYMBOL = SymbolItem.registerSimpleBlockItem(ReactiveBlocks.COPPER_SYMBOL);

    public static final DeferredHolder<Item, SymbolItem> IRON_SYMBOL = SymbolItem.registerSimpleBlockItem(ReactiveBlocks.IRON_SYMBOL);

    public static final DeferredHolder<Item, SymbolItem> GOLD_SYMBOL = SymbolItem.registerSimpleBlockItem(ReactiveBlocks.GOLD_SYMBOL);

    public static final DeferredHolder<Item, SymbolItem> OCCULT_SYMBOL = SymbolItem.registerSimpleBlockItem(ReactiveBlocks.OCCULT_SYMBOL);

    public static final DeferredHolder<Item, SymbolItem> DIVINE_SYMBOL = SymbolItem.registerSimpleBlockItem(ReactiveBlocks.DIVINE_SYMBOL);

    public static final DeferredHolder<Item, BlockItem> RUNESTONE = ITEMS.registerSimpleBlockItem(ReactiveBlocks.RUNESTONE);

    public static final DeferredHolder<Item, BlockItem> BLAZE_ROD = ITEMS.registerSimpleBlockItem(ReactiveBlocks.BLAZE_ROD);

    public static final DeferredHolder<Item, BlockItem> BREEZE_ROD = ITEMS.registerSimpleBlockItem(ReactiveBlocks.BREEZE_ROD);

    public static final DeferredHolder<Item, BlockItem> VOLT_CELL = ITEMS.registerSimpleBlockItem(ReactiveBlocks.VOLT_CELL);

    public static final DeferredHolder<Item, BlockItem> CURSE_CELL = ITEMS.registerSimpleBlockItem(ReactiveBlocks.CURSE_CELL);

    public static final DeferredHolder<Item, BlockItem> SALT_BLOCK = ITEMS.registerSimpleBlockItem(ReactiveBlocks.SALT_BLOCK);

    public static final DeferredHolder<Item, BlockItem> MOTION_SALT_BLOCK = ITEMS.registerSimpleBlockItem(ReactiveBlocks.MOTION_SALT_BLOCK);

    public static final DeferredHolder<Item, BlockItem> FRAMED_MOTION_SALT_BLOCK = ITEMS.registerSimpleBlockItem(ReactiveBlocks.FRAMED_MOTION_SALT_BLOCK);

    public static final DeferredHolder<Item, BlockItem> PURE_QUARTZ_BLOCK = ITEMS.registerSimpleBlockItem(ReactiveBlocks.PURE_QUARTZ_BLOCK);

    public static final DeferredHolder<Item, BlockItem> SOLID_PORTAL = ITEMS.registerSimpleBlockItem(ReactiveBlocks.SOLID_PORTAL);

    public static final DeferredHolder<Item, BlockItem> GOLD_FOAM = ITEMS.registerSimpleBlockItem(ReactiveBlocks.GOLD_FOAM);

    public static final DeferredHolder<Item, BlockItem> WARP_SPONGE = ITEMS.registerSimpleBlockItem(ReactiveBlocks.WARP_SPONGE);

    public static final DeferredHolder<Item, BlockItem> GRAVITY_BEAM = ITEMS.registerSimpleBlockItem(ReactiveBlocks.GRAVITY_BEAM);

    public static final DeferredHolder<Item, BlockItem> MNEMONIC_BULB = ITEMS.registerSimpleBlockItem(ReactiveBlocks.MNEMONIC_BULB);

    public static final DeferredHolder<Item, BlockItem> MIND_LICHEN = ITEMS.registerSimpleBlockItem(ReactiveBlocks.MIND_LICHEN);

    public static final DeferredHolder<Item, BlockItem> FLOWER_VINES = ITEMS.registerSimpleBlockItem(ReactiveBlocks.FLOWER_VINES);

    public static final DeferredHolder<Item, BlockItem> GRAVITY_CHANDELIER = ITEMS.registerSimpleBlockItem(ReactiveBlocks.GRAVITY_CHANDELIER);

    public static final DeferredHolder<Item, BlockItem> GATEWAY_PLINTH = ITEMS.registerSimpleBlockItem(ReactiveBlocks.GATEWAY_PLINTH);

    public static final DeferredHolder<Item, QuartzBottleItem> QUARTZ_BOTTLE = ITEMS.register("quartz_bottle",
            () -> new QuartzBottleItem(new Item.Properties()));

    public static final DeferredHolder<Item, PowerBottleItem> ACID_BOTTLE = ITEMS.register("acid_bottle",
            () -> new PowerBottleItem(new Item.Properties(), ReactiveBlocks.ACID_BOTTLE_BLOCK.get()));

    public static final DeferredHolder<Item, PowerBottleItem> VITAL_BOTTLE = ITEMS.register("vital_bottle",
            () -> new PowerBottleItem(new Item.Properties(), ReactiveBlocks.VITAL_BOTTLE_BLOCK.get()));

    public static final DeferredHolder<Item, BlazeBottleItem> BLAZE_BOTTLE = ITEMS.register("blaze_bottle",
            () -> new BlazeBottleItem(new Item.Properties(), ReactiveBlocks.BLAZE_BOTTLE_BLOCK.get()));

    public static final DeferredHolder<Item, PowerBottleItem> LIGHT_BOTTLE = ITEMS.register("light_bottle",
            () -> new PowerBottleItem(new Item.Properties(), ReactiveBlocks.LIGHT_BOTTLE_BLOCK.get()));

    public static final DeferredHolder<Item, PowerBottleItem> VERDANT_BOTTLE = ITEMS.register("verdant_bottle",
            () -> new PowerBottleItem(new Item.Properties(), ReactiveBlocks.VERDANT_BOTTLE_BLOCK.get()));

    public static final DeferredHolder<Item, WarpBottleItem> WARP_BOTTLE = ITEMS.register("warp_bottle",
            () -> new WarpBottleItem(new Item.Properties(), ReactiveBlocks.WARP_BOTTLE_BLOCK.get()));

    public static final DeferredHolder<Item, PowerBottleItem> SOUL_BOTTLE = ITEMS.register("soul_bottle",
            () -> new PowerBottleItem(new Item.Properties(), ReactiveBlocks.SOUL_BOTTLE_BLOCK.get()));

    public static final DeferredHolder<Item, PowerBottleItem> MIND_BOTTLE = ITEMS.register("mind_bottle",
            () -> new PowerBottleItem(new Item.Properties(), ReactiveBlocks.MIND_BOTTLE_BLOCK.get()));

    public static final DeferredHolder<Item, PowerBottleItem> BODY_BOTTLE = ITEMS.register("body_bottle",
            () -> new PowerBottleItem(new Item.Properties(), ReactiveBlocks.BODY_BOTTLE_BLOCK.get()));

    public static final DeferredHolder<Item, AcidBucketItem> ACID_BUCKET = ITEMS.register("acid_bucket",
            () -> new AcidBucketItem(ReactiveBlocks.ACID_BLOCK.get(), SoundEvents.BUCKET_FILL, new Item.Properties()));

    public static final DeferredHolder<Item, BlockItem> INCOMPLETE_STAFF = ITEMS.register(ReactiveBlocks.INCOMPLETE_STAFF.getId().getPath(),
            () -> new BlockItem(ReactiveBlocks.INCOMPLETE_STAFF.get(), new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, StaffItem> STAFF_OF_LIFE = ITEMS.register(ReactiveBlocks.STAFF_OF_LIFE.getId().getPath(),
            () -> new StaffItem(ReactiveBlocks.STAFF_OF_LIFE.get(), new Item.Properties().durability(600), StaffEffects::living, true,
                    () -> 10, VITAL_BOTTLE.get()));

    public static final DeferredHolder<Item, StaffItem> STAFF_OF_BLAZE = ITEMS.register(ReactiveBlocks.STAFF_OF_BLAZE.getId().getPath(),
            () -> new StaffItem(ReactiveBlocks.STAFF_OF_BLAZE.get(), new Item.Properties().durability(1200).fireResistant(), StaffEffects::blazing, false,
                    ConfigMan.COMMON.blazeStaffFrequency, BLAZE_BOTTLE.get()));

    public static final DeferredHolder<Item, LightStaffItem> STAFF_OF_LIGHT = ITEMS.register(ReactiveBlocks.STAFF_OF_LIGHT.getId().getPath(),
            () -> new LightStaffItem(ReactiveBlocks.STAFF_OF_LIGHT.get(), new Item.Properties().durability(1000), StaffEffects::radiance, true, LIGHT_BOTTLE.get()));

    public static final DeferredHolder<Item, WarpStaffItem> STAFF_OF_WARP = ITEMS.register(ReactiveBlocks.STAFF_OF_WARP.getId().getPath(),
            () -> new WarpStaffItem(ReactiveBlocks.STAFF_OF_WARP.get(), new Item.Properties().durability(500), WARP_BOTTLE.get()));

    public static final DeferredHolder<Item, StaffItem> STAFF_OF_SOUL = ITEMS.register(ReactiveBlocks.STAFF_OF_SOUL.getId().getPath(),
            () -> new StaffItem(ReactiveBlocks.STAFF_OF_SOUL.get(), new Item.Properties().durability(800), StaffEffects::spectral, false,
                    ConfigMan.COMMON.soulStaffFrequency, SOUL_BOTTLE.get()));

    public static final DeferredHolder<Item, StaffItem> STAFF_OF_MIND = ITEMS.register(ReactiveBlocks.STAFF_OF_MIND.getId().getPath(),
            () -> new StaffItem(ReactiveBlocks.STAFF_OF_MIND.get(), new Item.Properties().durability(1200), StaffEffects::missile, false,
                    ConfigMan.COMMON.mindStaffFrequency, MIND_BOTTLE.get()));

    public static final DeferredHolder<Item, SoupItem> SOUP = ITEMS.register("soup",
            () -> new SoupItem(new Item.Properties().stacksTo(64).food((new FoodProperties.Builder().nutrition(7).saturationModifier(0.5F)).build())));

    public static final DeferredHolder<Item, DisplacerItem> DISPLACER = ITEMS.register("displacer",
            () -> new DisplacerItem(new Item.Properties()
                    .durability(350)));

    public static final DeferredHolder<Item, VortexStoneItem> VORTEX_STONE = ITEMS.register("vortex_stone",
            () -> new VortexStoneItem(new Item.Properties()
                    .durability(640)));

    public static final DeferredHolder<Item, LitmusPaperItem> LITMUS_PAPER = ITEMS.register("litmus_paper",
            () -> new LitmusPaperItem(new Item.Properties()));

    public static final DeferredHolder<Item, AlchemyScrollItem> SCROLL = ITEMS.register("scroll",
            () -> new AlchemyScrollItem(new Item.Properties()
                    .stacksTo(1).rarity(Rarity.RARE)));

    public static final DeferredHolder<Item, Item> PURE_QUARTZ = ITEMS.register("quartz",
            () -> new Item(new Item.Properties()));

    public static final DeferredHolder<Item, StardustItem> STARDUST = ITEMS.register("stardust",
            () -> new StardustItem(new Item.Properties()));

    public static final DeferredHolder<Item, Item> SALT = ITEMS.register("salt",
            () -> new Item(new Item.Properties()));

    public static final DeferredHolder<Item, Item> MOTION_SALT = ITEMS.register("motion_salt",
            () -> new Item(new Item.Properties()));

    public static final DeferredHolder<Item, Item> PHANTOM_RESIDUE = ITEMS.register("phantom_residue",
            () -> new Item(new Item.Properties()));

    public static final DeferredHolder<Item, CrystalIronItem> CRYSTAL_IRON = ITEMS.register("crystal_iron",
            () -> new CrystalIronItem(new Item.Properties().durability(64)));

    public static final DeferredHolder<Item, SecretScaleItem> SECRET_SCALE = ITEMS.register("secret_scale",
            () -> new SecretScaleItem(new Item.Properties()));

    public static final DeferredHolder<Item, Item> ETERNAL_SPRIG = ITEMS.register("eternal_life_sprig",
            () -> new Item(new Item.Properties().food(new FoodProperties.Builder()
                    .nutrition(4)
                    .saturationModifier(1.4F)
                    .effect(() -> new MobEffectInstance(MobEffects.ABSORPTION, -1, 4, true, false), 1F)
                    .build())));

    public static final DeferredHolder<Item, Item> GOLD_THREAD = ITEMS.register("gold_thread",
            () -> new Item(new Item.Properties()));

    public static final DeferredHolder<Item, Item> INERT_CRYSTAL = ITEMS.register("inert_crystal",
            () -> new Item(new Item.Properties()));

    public static final DeferredHolder<Item, ReactionFlaskItem> REACTION_FLASK = ITEMS.register("reaction_flask",
            () -> new ReactionFlaskItem(new Item.Properties().stacksTo(16)));

    public static final DeferredHolder<Item, BlockItem> UNGROWN_NODULE = ITEMS.registerSimpleBlockItem(ReactiveBlocks.UNGROWN_NODULE);

    public static final DeferredHolder<Item, BlockItem> NODULE = ITEMS.registerSimpleBlockItem(ReactiveBlocks.NODULE);
}