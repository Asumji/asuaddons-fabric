package me.asumji.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import me.asumji.AsuAddons;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.network.chat.Component;
import com.mojang.brigadier.CommandDispatcher;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;

public class SimulateCommand {
    public static void init() {
        ClientCommandRegistrationCallback.EVENT.register(SimulateCommand::onCommand);
    }

    private static void onCommand(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext registryAccess) {
        dispatcher.register(ClientCommands.literal(AsuAddons.NAMESPACE)
            .then(ClientCommands.literal("simulate")
            .then(ClientCommands.argument("message", StringArgumentType.greedyString())
            .executes(context -> {
                Minecraft.getInstance().getChatListener().handleSystemMessage(Component.literal(getString(context, "message").replaceAll("&(.)", "§$1")), false);
                return 1;
            }))));
    }
}
