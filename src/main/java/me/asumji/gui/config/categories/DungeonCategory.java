package me.asumji.gui.config.categories;

import com.google.gson.annotations.Expose;
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean;
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorButton;
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorText;
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption;
import me.asumji.gui.MoveGUI;
import net.minecraft.client.MinecraftClient;

public class DungeonCategory {
    @Expose
    @ConfigOption(name = "PF preview", desc = "Shows a quick preview of the player joining in dungeons.")
    @ConfigEditorBoolean
    public boolean DPU = false;

    @Expose
    @ConfigOption(name = "Relevant Items", desc = "List all items that should be listed in the preview from a players inventory. (e.g. item1,item2,item3)")
    @ConfigEditorText
    public String relevantItems = "";

    @Expose
    @ConfigOption(name = "Livid timer", desc = "Displays a timer until livid is damageable.")
    @ConfigEditorBoolean
    public boolean lividTimer = false;

    @Expose
    @ConfigOption(name = "Livid message", desc = "Message to send when time is up (leave empty to disable).")
    @ConfigEditorText
    public String lividTimerMessage = "[AU] Ice spray Livid now!";

    @Expose
    public int lividTimerHudX = 50;

    @Expose
    public int lividTimerHudY = 50;

    @Expose
    public float lividTimerHudScale = 2;

    @ConfigOption(name = "Move Livid HUD", desc = "Allows you to move the Timer HUD")
    @ConfigEditorButton(buttonText = "Click")
    public Runnable moveLividHud = () ->
        MinecraftClient.getInstance().send(() -> MinecraftClient.getInstance().setScreen(new MoveGUI("0.00s", "dungeonCategory","lividTimerHudX", "lividTimerHudY", "lividTimerHudScale")));
}
