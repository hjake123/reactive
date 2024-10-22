package dev.hyperlynx.reactive.cmd;

import dev.hyperlynx.reactive.ConfigMan;
import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.Registration;
import dev.hyperlynx.reactive.alchemy.Power;
import dev.hyperlynx.reactive.alchemy.Powers;
import dev.hyperlynx.reactive.alchemy.rxn.Reaction;
import dev.hyperlynx.reactive.be.CrucibleBlockEntity;
import dev.hyperlynx.reactive.items.WarpBottleItem;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.coordinates.WorldCoordinates;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.util.List;

import static net.minecraft.commands.arguments.coordinates.BlockPosArgument.ERROR_NOT_LOADED;

@EventBusSubscriber(modid= ReactiveMod.MODID, bus=EventBusSubscriber.Bus.GAME)
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
                            .executes((context) -> reloadReactions())))

                .then(Commands.literal("power")
                        .then(Commands.literal("add")
                                .then(Commands.argument("crucible_location", BlockPosArgument.blockPos())
                                .then(Commands.argument("power_id", PowerArgumentType.power())
                                .then(Commands.argument("amount", IntegerArgumentType.integer(1, 1600))
                                .executes((context) -> modifyPower(context.getSource(),
                                        context.getArgument("crucible_location", WorldCoordinates.class),
                                        context.getArgument("power_id", ResourceLocation.class),
                                        context.getArgument("amount", Integer.class), false)
                                )))))
                        .then(Commands.literal("remove")
                                .then(Commands.argument("crucible_location", BlockPosArgument.blockPos())
                                .then(Commands.argument("power_id", PowerArgumentType.power())
                                .then(Commands.argument("amount", IntegerArgumentType.integer(1, 1600))
                                .executes((context) -> modifyPower(context.getSource(),
                                        context.getArgument("crucible_location", WorldCoordinates.class),
                                        context.getArgument("power_id", ResourceLocation.class),
                                        context.getArgument("amount", Integer.class), true)
                                )))))
                );

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

        if(!(level.getBlockEntity(pos) instanceof CrucibleBlockEntity crucible)){
            throw ERROR_NO_CRUCIBLE.create();
        }

        Power power = Powers.POWER_REGISTRY.getValue(power_location);
        if(power == null){
            throw ERROR_FAKE_POWER.create();
        }

        if(remove){
            crucible.expendPower(power, amount);
        }else{
            crucible.addPower(power, amount);
        }

        crucible.setDirty();
        return 1;
    }

    private static int giveWarpBottle(CommandSourceStack source, WorldCoordinates target) throws CommandSyntaxException {
        ServerPlayer commander = source.getPlayer();
        if(commander == null){
            throw ERROR_NO_PLAYER.create();
        }
        ItemStack bottle = Registration.WARP_BOTTLE.get().getDefaultInstance();
        WarpBottleItem.setTeleportTarget(bottle, GlobalPos.of(commander.level().dimension(), target.getBlockPos(source)));
        commander.addItem(bottle);
        return 1;
    }

    private static int listReactions(CommandSourceStack source) throws CommandSyntaxException {
        if(!source.isPlayer()){
            throw ERROR_NO_PLAYER.create();
        }
        List<String> aliases = ReactiveMod.REACTION_MAN.getReactionAliases();
        aliases.stream().sorted().forEach((alias) -> {
            Reaction reaction = ReactiveMod.REACTION_MAN.get(alias);
            source.sendSuccess(() -> Component.literal(alias + " : " + reaction.getName().getString()), true);
        });
        return 1;
    }

    @SubscribeEvent
    public static void onCommandRegister(RegisterCommandsEvent event){
        if(ConfigMan.COMMON.registerCommand.get()){
            ArgumentTypeInfos.registerByClass(PowerArgumentType.class, Registration.POWER_ARGUMENT.value());
            ReactiveCommand.register(event.getDispatcher());
        }
    }
}
