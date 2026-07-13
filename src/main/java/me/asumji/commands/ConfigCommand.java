package me.asumji.commands;

import com.mojang.brigadier.CommandDispatcher;
import me.asumji.AsuAddons;
import me.asumji.gui.config.ConfigManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandBuildContext;

public class ConfigCommand {
    public static Boolean openGUI = false;

    public static void init() {
        ClientCommandRegistrationCallback.EVENT.register(ConfigCommand::configCommand);
        ClientTickEvents.END_CLIENT_TICK.register(ConfigCommand::openGUI);
    }

    private static void openGUI(Minecraft client) {
        if (openGUI) {
            client.execute(ConfigManager::openConfigScreen);
            openGUI = false;
        }
    }

    private static void configCommand(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext registryAccess) {
        dispatcher.register(ClientCommands.literal(AsuAddons.NAMESPACE).executes(context -> {
            openGUI = true;
            return 1;
        }));
    }
}
