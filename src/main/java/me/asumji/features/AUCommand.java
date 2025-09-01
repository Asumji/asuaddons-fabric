package me.asumji.features;

import com.mojang.brigadier.CommandDispatcher;
import me.asumji.AsuAddons;
import me.asumji.gui.config.ConfigManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandRegistryAccess;

public class AUCommand {
    public static Boolean openGUI = false;

    public static void init() {
        ClientCommandRegistrationCallback.EVENT.register(AUCommand::onCommand);
        ClientTickEvents.END_CLIENT_TICK.register(AUCommand::openGUI);
    }

    private static void openGUI(MinecraftClient client) {
        if (openGUI) {
            client.execute(ConfigManager::openConfigScreen);
            openGUI = false;
        }
    }

    private static void onCommand(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        dispatcher.register(ClientCommandManager.literal(AsuAddons.NAMESPACE).executes(context -> {
            openGUI = true;
            return 1;
        }));
    }
}
