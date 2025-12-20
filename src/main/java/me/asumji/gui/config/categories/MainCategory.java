package me.asumji.gui.config.categories;

import com.google.gson.annotations.Expose;
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean;
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorButton;
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption;
import me.asumji.gui.MoveGUI;
import net.minecraft.client.MinecraftClient;

public class MainCategory {
    @ConfigOption(name = "Move GUI Elements", desc = "Allows you to move the GUI Elements.")
    @ConfigEditorButton(buttonText = "Click")
    public Runnable moveHud = () ->
            MinecraftClient.getInstance().send(() -> MinecraftClient.getInstance().setScreen(new MoveGUI()));

    @Expose
    public boolean firstLaunch = true;

    @Expose
    public String webhookUrl = "";

    @Expose
    public String lastestAction = "";

    @Expose
    public String downloaded = "";

    @Expose
    @ConfigOption(name = "Auto Updater", desc = "Enables automatic updates whenever a new action run has been found.")
    @ConfigEditorBoolean
    public boolean autoUpdates = true;
}
