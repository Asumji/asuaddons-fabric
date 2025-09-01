package me.asumji.gui.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.notenoughupdates.moulconfig.common.IMinecraft;
import io.github.notenoughupdates.moulconfig.gui.MoulConfigEditor;
import io.github.notenoughupdates.moulconfig.processor.BuiltinMoulConfigGuis;
import io.github.notenoughupdates.moulconfig.processor.ConfigProcessorDriver;
import io.github.notenoughupdates.moulconfig.processor.MoulConfigProcessor;
import me.asumji.AsuAddons;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

//Full credit to https://github.com/GiovanniClient/giovanni-client (hate cheater :angry:)
public class ConfigManager {
    private static final Gson GSON = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .setPrettyPrinting()
            .create();

    private static final File CONFIG_FILE =
            new File("config/asuaddons/config.json");

    private static ConfigGUI config;

    private static MoulConfigProcessor<ConfigGUI> processor;
    private static ConfigProcessorDriver driver;
    private static MoulConfigEditor<ConfigGUI> editor;

    private static final ScheduledExecutorService SCHEDULER =
            Executors.newSingleThreadScheduledExecutor();

    public static void init() {
        CONFIG_FILE.getParentFile().mkdirs();
        loadConfig();
        processor = new MoulConfigProcessor<>(config);
        BuiltinMoulConfigGuis.addProcessors(processor);

        driver = new ConfigProcessorDriver(processor);
        driver.processConfig(config);

        SCHEDULER.scheduleAtFixedRate(
                () -> saveConfig("auto-save"),
                60, 60, TimeUnit.SECONDS
        );
    }

    public static void loadConfig() {
        if (!CONFIG_FILE.exists()) {
            config = new ConfigGUI();
            saveConfig("initial");
            return;
        }
        try (FileReader fr = new FileReader(CONFIG_FILE)) {
            config = GSON.fromJson(fr, ConfigGUI.class);
            if (config == null) throw new IOException("Empty file");
        } catch (Exception e) {
            e.printStackTrace();
            try {
                File backup = new File(
                        CONFIG_FILE.getParentFile(),
                        "config-" + Instant.now().toEpochMilli() + ".bak.json"
                );
                Files.copy(CONFIG_FILE.toPath(), backup.toPath(),
                        StandardCopyOption.REPLACE_EXISTING);
                System.err.println("Backed up bad config to " + backup);
            } catch (IOException ioe) {
                AsuAddons.LOGGER.info(ioe.toString());
            }
            config = new ConfigGUI();
        }
    }

    public static void saveConfig(String reason) {
        try (FileWriter fw = new FileWriter(CONFIG_FILE)) {
            fw.write(GSON.toJson(config));
        } catch (IOException e) {
            AsuAddons.LOGGER.info(e.toString());
        }
    }

    public static void openConfigScreen() {
        if (editor == null) {
            editor = new MoulConfigEditor<>(processor);
        }
        IMinecraft.instance.openWrappedScreen(editor);
    }

    public static ConfigGUI getConfig() {
        return config;
    }
}