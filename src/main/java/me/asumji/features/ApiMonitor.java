package me.asumji.features;

import com.google.gson.JsonObject;
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
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;

import java.nio.channels.ClosedChannelException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;

public class ApiMonitor {
    static boolean failed = false;
    static boolean used = false;

    public static void init() {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            if (failed) return;
            HTTP.GetRequest(AsuAddons.API_PROXY+"test").thenAcceptAsync(res -> {
                ConfigManager.getConfig().mainCategory.webhookUrl = AsuAddons.GSON.fromJson(res.body(), JsonObject.class).get("webhookUrl").getAsString();
                AsuAddons.LOGGER.info("AU > API Check succeeded.");
            }).exceptionally(e -> {
                if (!(e.getCause().getCause() instanceof ClosedChannelException)) return null;
                failed = true;
                AsuAddons.LOGGER.info("AU > API Check failed.\n"+e.getMessage()+"\n"+e.getCause());
                Shortcuts.queueClientMessage(Text.literal(
                        AsuAddons.MOD_PREFIX + "§cThe API cannot be reached! Some features might not work.\n")
                        .append(Text.literal("§a§l[CLICK HERE]§r§a to send a report to the dev.").styled(style -> style.withClickEvent(new ClickEvent.RunCommand("/au report "+e.getCause().getCause()))))
                );
                return null;
            });
        }, 15, 600, TimeUnit.SECONDS);
        ClientCommandRegistrationCallback.EVENT.register(ApiMonitor::onCommand);
    }

    private static void onCommand(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        dispatcher.register(ClientCommandManager.literal(AsuAddons.NAMESPACE)
            .then(ClientCommandManager.literal("report")
            .then(ClientCommandManager.argument("error", StringArgumentType.greedyString())
                .executes(context -> {
                    if (used) return 1;
                    used = true;
                    HTTP.sendWebhookMessage("{\"username\":\"AsuAddons API Reports\", \"content\": \"Someone has reported an API outage.\", \"embeds\": [{\"title\": \"New API Outage Report\", \"color\": 16711680, \"description\": \"" + MinecraftClient.getInstance().getSession().getUsername() + " has reported that the API is down.\\nError: " + getString(context, "error") + "\", \"footer\":{\"text\":\"This message was sent through the /au report command.\"}, \"thumbnail\":{\"url\":\"https://mc-heads.net/player/" + MinecraftClient.getInstance().getSession().getUsername() + "\"}}]}", ConfigManager.getConfig().mainCategory.webhookUrl);
                    return 1;
                }))));
    }
}
