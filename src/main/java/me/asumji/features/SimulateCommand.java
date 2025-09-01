package me.asumji.features;

import com.mojang.brigadier.arguments.StringArgumentType;
import me.asumji.AsuAddons;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Text;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;

public class SimulateCommand {
    public static void init() {
        ClientCommandRegistrationCallback.EVENT.register(SimulateCommand::onCommand);
    }

    private static void onCommand(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        dispatcher.register(ClientCommandManager.literal(AsuAddons.NAMESPACE)
            .then(ClientCommandManager.literal("simulate")
            .then(ClientCommandManager.argument("message", StringArgumentType.greedyString())
            .executes(context -> {
                MinecraftClient.getInstance().getMessageHandler().onGameMessage(Text.of(getString(context, "message").replaceAll("&(.)", "§$1")), false);
                return 1;
            }))));
    }
}
