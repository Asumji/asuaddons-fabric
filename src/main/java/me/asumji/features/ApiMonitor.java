package me.asumji.features;

import com.google.gson.JsonObject;
import me.asumji.AsuAddons;
import me.asumji.gui.config.ConfigManager;
import me.asumji.util.HTTP;
import me.asumji.util.Shortcuts;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;

import java.nio.channels.ClosedChannelException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ApiMonitor {
    private static boolean failed = false;

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
    }
}
