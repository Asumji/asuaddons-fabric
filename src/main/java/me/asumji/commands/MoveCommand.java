package me.asumji.commands;

import com.mojang.brigadier.CommandDispatcher;
import me.asumji.AsuAddons;
import me.asumji.gui.move.MoveGUI;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandBuildContext;

public class MoveCommand {
    public static Boolean openGUI = false;

    public static void init() {
        ClientCommandRegistrationCallback.EVENT.register(MoveCommand::moveCommand);
    }

    private static void moveCommand(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext registryAccess) {
        dispatcher.register(ClientCommandManager.literal(AsuAddons.NAMESPACE)
            .then(ClientCommandManager.literal("move")
            .executes(context -> {
                Minecraft.getInstance().schedule(() -> Minecraft.getInstance().setScreen(new MoveGUI()));
                return 1;
            })));
    }
}
