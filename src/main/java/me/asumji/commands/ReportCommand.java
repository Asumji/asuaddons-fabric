package me.asumji.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import me.asumji.AsuAddons;
import me.asumji.gui.config.ConfigManager;
import me.asumji.util.HTTP;
import me.asumji.util.Shortcuts;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Text;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;

public class ReportCommand {
    private static boolean used = false;

    public static void init() {
        ClientCommandRegistrationCallback.EVENT.register(ReportCommand::reportCommand);
    }

    private static void reportCommand(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        dispatcher.register(ClientCommandManager.literal(AsuAddons.NAMESPACE)
            .then(ClientCommandManager.literal("report")
            .then(ClientCommandManager.argument("error", StringArgumentType.greedyString())
            .executes(context -> {
                if (used) return 1;
                used = true;
                HTTP.sendWebhookMessage("{\"username\":\"AsuAddons API Reports\", \"content\": \"Someone has reported an API outage.\", \"embeds\": [{\"title\": \"New API Outage Report\", \"color\": 16711680, \"description\": \"" + MinecraftClient.getInstance().getSession().getUsername() + " has reported that the API is down.\\nError: " + getString(context, "error") + "\", \"footer\":{\"text\":\"This message was sent through the /au report command.\"}, \"thumbnail\":{\"url\":\"https://mc-heads.net/player/" + MinecraftClient.getInstance().getSession().getUsername() + "\"}}]}", ConfigManager.getConfig().mainCategory.webhookUrl);
                Shortcuts.queueClientMessage(Text.literal(AsuAddons.MOD_PREFIX + "§aThe report has successfully been sent!"));
                return 1;
            }))));
    }
}
