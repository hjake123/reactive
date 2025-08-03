package dev.hyperlynx.reactive.cmd;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import dev.hyperlynx.reactive.ConfigMan;
import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.alchemy.material.Material;
import dev.hyperlynx.reactive.alchemy.material.MaterialMan;
import dev.hyperlynx.reactive.alchemy.material.MaterialProperties;
import dev.hyperlynx.reactive.alchemy.material.MaterialProperty;
import dev.hyperlynx.reactive.net.MaterialRenameScreenPayload;
import dev.hyperlynx.reactive.registration.ReactiveCommandArguments;
import dev.hyperlynx.reactive.alchemy.Power;
import dev.hyperlynx.reactive.alchemy.PowerBearer;
import dev.hyperlynx.reactive.alchemy.Powers;
import dev.hyperlynx.reactive.alchemy.rxn.Reaction;
import dev.hyperlynx.reactive.items.WarpBottleItem;
import dev.hyperlynx.reactive.registration.ReactiveComponentTypes;
import dev.hyperlynx.reactive.registration.ReactiveItems;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.coordinates.WorldCoordinates;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static net.minecraft.commands.arguments.coordinates.BlockPosArgument.ERROR_NOT_LOADED;

@SuppressWarnings("SameReturnValue")
@EventBusSubscriber(modid= ReactiveMod.MODID)
public class ReactiveCommand {
    private static final SimpleCommandExceptionType ERROR_NO_PLAYER = new SimpleCommandExceptionType(Component.translatable("commands.reactive.no_player"));
    private static final SimpleCommandExceptionType ERROR_NO_CRUCIBLE = new SimpleCommandExceptionType(Component.translatable("commands.reactive.no_crucible"));
    private static final SimpleCommandExceptionType ERROR_FAKE_POWER = new SimpleCommandExceptionType(Component.translatable("commands.reactive.fake_power"));

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> command_builder = Commands.literal("reactive")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("give_warp_bottle").then(Commands.argument("target", BlockPosArgument.blockPos())
                        .executes((context) -> giveWarpBottle(context.getSource(), context.getArgument("target", WorldCoordinates.class)))))

                .then(Commands.literal("reaction")
                        .then(Commands.literal("list")
                            .executes((context) -> listReactions(context.getSource())))
                        .then(Commands.literal("reload")
                            .executes((context) -> reloadReactions())
                        ))

                .then(Commands.literal("power")
                        .then(Commands.literal("add")
                                .then(Commands.argument("crucible_location", BlockPosArgument.blockPos())
                                .then(Commands.argument("power_id", PowerArgumentType.power())
                                .then(Commands.argument("amount", IntegerArgumentType.integer(1, 10000))
                                .executes((context) -> modifyPower(context.getSource(),
                                        context.getArgument("crucible_location", WorldCoordinates.class),
                                        context.getArgument("power_id", ResourceLocation.class),
                                        context.getArgument("amount", Integer.class), false)
                                )))))
                        .then(Commands.literal("remove")
                                .then(Commands.argument("crucible_location", BlockPosArgument.blockPos())
                                .then(Commands.argument("power_id", PowerArgumentType.power())
                                .then(Commands.argument("amount", IntegerArgumentType.integer(1, 10000))
                                .executes((context) -> modifyPower(context.getSource(),
                                        context.getArgument("crucible_location", WorldCoordinates.class),
                                        context.getArgument("power_id", ResourceLocation.class),
                                        context.getArgument("amount", Integer.class), true)
                                ))))))

                .then(Commands.literal("material")
                        .then(Commands.literal("give")
                                .then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.argument("id", ResourceLocationArgument.id())
                                .then(Commands.argument("amount", IntegerArgumentType.integer())
                                .executes(context -> giveMaterialBlockItem(context,
                                        ResourceLocationArgument.getId(context, "id"),
                                        IntegerArgumentType.getInteger(context, "amount"),
                                        EntityArgument.getPlayer(context, "player")))))))
                        .then(Commands.literal("rename")
                                .then(Commands.argument("id", ResourceLocationArgument.id())
                                .executes(context -> openRenameScreen(context,
                                        ResourceLocationArgument.getId(context, "id")))))
                        .then(Commands.literal("list")
                                .executes(context -> printMaterials(context.getSource())))
                        .then(Commands.literal("reload")
                                .executes(context -> reloadMaterials(context.getSource())))
                        .then(Commands.literal("remove")
                                .then(Commands.argument("id", ResourceLocationArgument.id())
                                .then(Commands.literal("confirm-delete")
                                .executes(context ->
                                        removeMaterial(context, ResourceLocationArgument.getId(context, "id"))))))
                        .then(Commands.literal("info")
                                .then(Commands.argument("id", ResourceLocationArgument.id())
                                        .executes(context -> printMaterialInfo(context,
                                                ResourceLocationArgument.getId(context, "id")))))
                        .then(Commands.literal("remove-everything")
                                .then(Commands.literal("confirm-delete")
                                .executes(ReactiveCommand::removeAllMaterials))));

        dispatcher.register(command_builder);
    }

    private static int reloadReactions() {
        ReactiveMod.REACTION_MAN.reset();
        return 1;
    }

    private static int modifyPower(CommandSourceStack source, WorldCoordinates crucible_location, ResourceLocation power_location, Integer amount, boolean remove) throws CommandSyntaxException {
        BlockPos pos = crucible_location.getBlockPos(source);
        ServerLevel level = source.getLevel();

        if(!(level.isLoaded(pos))){
            throw ERROR_NOT_LOADED.create();
        }

        List<PowerBearer> bearers = new ArrayList<>();

        if(level.getBlockEntity(pos) instanceof PowerBearer be_bearer){
            bearers.add(be_bearer);
        }

        var bearer_entities = level.getEntities((Entity) null, new AABB(pos), (entity) -> entity instanceof PowerBearer);
        for(Entity entity : bearer_entities){
            bearers.add((PowerBearer) entity);
        }

        if(bearers.isEmpty()){
            throw ERROR_NO_CRUCIBLE.create();
        }

        for(PowerBearer bearer : bearers) {
            Power power = Powers.POWER_REGISTRY.get(power_location);
            if(power == null){
                throw ERROR_FAKE_POWER.create();
            }

            if(remove){
                bearer.expendPower(power, amount);
            }else{
                bearer.addPower(power, amount);
            }
        }

        return 1;
    }

    private static int giveWarpBottle(CommandSourceStack source, WorldCoordinates target) throws CommandSyntaxException {
        ServerPlayer commander = source.getPlayer();
        if(commander == null){
            throw ERROR_NO_PLAYER.create();
        }
        ItemStack bottle = ReactiveItems.WARP_BOTTLE.get().getDefaultInstance();
        WarpBottleItem.setTeleportTarget(bottle, GlobalPos.of(commander.level().dimension(), target.getBlockPos(source)));
        commander.addItem(bottle);
        return 1;
    }

    private static int listReactions(CommandSourceStack source) throws CommandSyntaxException {
        if(!source.isPlayer()){
            throw ERROR_NO_PLAYER.create();
        }
        List<String> aliases = ReactiveMod.REACTION_MAN.getReactionAliases(source.getLevel());
        aliases.stream().sorted().forEach((alias) -> {
            Reaction reaction = ReactiveMod.REACTION_MAN.get(source.getLevel(), alias);
            source.sendSuccess(() -> Component.literal(alias + " : " + reaction.getName().getString()), true);
        });
        return 1;
    }

    private static int printMaterials(CommandSourceStack source) {
        source.sendSuccess(() -> Component.translatable("commands.reactive.material_header"), true);
        List<Map.Entry<ResourceLocation, Material>> materials = MaterialMan.getAll(source.getLevel()).entrySet().stream()
                .sorted(Comparator.comparing(left -> left.getKey().toString()))
                .toList();
        for(Map.Entry<ResourceLocation, Material> material_entry : materials) {
            source.sendSuccess(() -> Component.literal(material_entry.getKey().toString() + " - " + material_entry.getValue().getNameComponent().getString()), true);
        }
        return 1;
    }

    private static int removeMaterial(CommandContext<CommandSourceStack> context, ResourceLocation id) {
        if(!ConfigMan.SERVER.allowMaterialDeletion.get()) {
            context.getSource().sendFailure(Component.translatable("message.reactive.material_removal_disabled"));
            return 0;
        }
        if(!MaterialMan.occupied(context.getSource().getLevel(), id)) {
            context.getSource().sendFailure(Component.translatable("message.reactive.material_not_found"));
            return 0;
        }
        context.getSource().sendSuccess(() -> Component.translatable("message.reactive.material_removed"), true);
        MaterialMan.remove(context.getSource().getLevel(), id);
        return 1;
    }

    private static int removeAllMaterials(CommandContext<CommandSourceStack> context) {
        if(!ConfigMan.SERVER.allowMaterialDeletion.get()) {
            context.getSource().sendFailure(Component.translatable("message.reactive.material_removal_disabled"));
            return 0;
        }
        context.getSource().sendSuccess(() -> Component.translatable("message.reactive.materials_reset"), true);
        MaterialMan.reset(context.getSource().getLevel());
        return 1;
    }

    private static int giveMaterialBlockItem(CommandContext<CommandSourceStack> context, ResourceLocation material_id, int amount, ServerPlayer player) {
        ServerLevel level = context.getSource().getLevel();
        if(!MaterialMan.occupied(level, material_id)) {
            context.getSource().sendFailure(Component.translatable("message.reactive.material_id_invalid"));
            return 0;
        }
        ItemStack stack = ReactiveItems.MATERIAL.get().getDefaultInstance();
        stack.set(ReactiveComponentTypes.MATERIAL_ID.get(), material_id);
        stack.setCount(amount);
        player.addItem(stack);
        return 1;
    }

    private static int printMaterialInfo(CommandContext<CommandSourceStack> context, ResourceLocation material_id) {
        ServerLevel level = context.getSource().getLevel();
        if(!MaterialMan.occupied(level, material_id)) {
            context.getSource().sendFailure(Component.translatable("message.reactive.material_id_invalid"));
            return 0;
        }
        Material material = MaterialMan.fetch(level, material_id);
        context.getSource().sendSuccess(material::getNameComponent, true);
        context.getSource().sendSuccess(material::formulaComponent, true);
        for(MaterialProperty<?> property : material.properties().keySet()) {
            context.getSource().sendSuccess(() -> Component.literal(String.valueOf(MaterialProperties.PROPERTY_REGISTRY.getKey(property))), true);
        }
        return 1;
    }

    private static int reloadMaterials(CommandSourceStack source) {
        MaterialMan.data(source.getLevel()).addBuiltIns(source.getLevel());
        MaterialMan.data(source.getLevel()).setDirty();
        source.sendSuccess(() -> Component.translatable("message.reactive.reloaded_materials"), true);
        return 1;
    }

    private static int openRenameScreen(CommandContext<CommandSourceStack> context, ResourceLocation material_id) {
        ServerLevel level = context.getSource().getLevel();
        if(!MaterialMan.occupied(level, material_id)) {
            context.getSource().sendFailure(Component.translatable("message.reactive.material_not_found"));
            return 0;
        }
        if(context.getSource().getPlayer() == null) {
            return 0;
        }
        PacketDistributor.sendToPlayer(context.getSource().getPlayer(), new MaterialRenameScreenPayload(material_id));
        return 1;
    }

    @SubscribeEvent
    public static void onCommandRegister(RegisterCommandsEvent event){
        if(ConfigMan.COMMON.registerCommand.get()){
            ArgumentTypeInfos.registerByClass(PowerArgumentType.class, ReactiveCommandArguments.POWER_ARGUMENT.value());
            ReactiveCommand.register(event.getDispatcher());
        }
    }
}
