package me.asumji;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.asumji.gui.config.ConfigManager;
import me.asumji.features.*;
import me.asumji.util.Shortcuts;
import me.asumji.util.Variables;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientWorldEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;


public class AsuAddons implements ModInitializer {
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create();
	public static final String MOD_ID = "asuaddons";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final String MOD_VERSION = "1.6.0";
    public static final String MINECRAFT_VERSION = "1.21.10";
    public static final String NAMESPACE = "au";
    public static final String API_PROXY = "http://asumji.duckdns.org/";
    public static final String MOD_PREFIX = "§6AU > §r";
    public static final ScheduledExecutorService SCHEDULER =
            Executors.newSingleThreadScheduledExecutor();

	@Override
	public void onInitialize() {
        LOGGER.info("AsuAddons initialized!");
        ConfigManager.init();
        DPU.init();
        AUCommand.init();
        SimulateCommand.init();
        Variables.init();
        Autopet.init();
        LividTimer.init();
        BloodGiant.init();
        MelodyDisplay.init();
        PurplePadTimer.init();
        WitherHitbox.init();
        ApiMonitor.init();
        Shortcuts.init();
        AutoUpdater.init();
        LividSolver.init();
        RNGMeterTooltip.init();
        Bridge.init();
        ShadowAssassinHighlight.init();
        SellValue.init();

        ClientWorldEvents.AFTER_CLIENT_WORLD_CHANGE.register(AsuAddons::joinWorld);
    }

    private static void joinWorld(MinecraftClient minecraftClient, ClientWorld clientWorld) {
        if (ConfigManager.getConfig().mainCategory.firstLaunch) {
            LOGGER.info("AsuAddons First Launch!");
            ConfigManager.getConfig().mainCategory.firstLaunch = false;
            Shortcuts.queueClientMessage(Text.literal("§a" + Shortcuts.getChatBreak("=") +
            "\n§9§lAsuAddons " + MOD_VERSION +
            "\n§bThis seems to be your first time loading the module" +
            "\n§bUse §6\"/au\"§b to open the config menu" +
            "\n§cIf any issues arise contact asumji on discord" +
            "\n§a" + Shortcuts.getChatBreak("=")));
        }
    }
}