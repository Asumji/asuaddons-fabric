package me.asumji.gui.config.categories;

import com.google.gson.annotations.Expose;
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean;
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorButton;
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorSlider;
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption;
import me.asumji.gui.MoveGUI;
import net.minecraft.client.MinecraftClient;

public class MiscCategory {
    @Expose
    @ConfigOption(name = "Autopet Notif", desc = "Displays a title when an Autopet rule is procced.")
    @ConfigEditorBoolean
    public boolean AutopetNotif = false;
    
    @Expose
    @ConfigOption(name = "Autopet Ticks", desc = "Defines the ticks how long the title should stay for")
    @ConfigEditorSlider(minValue = 1, maxValue = 20, minStep = 1)
    public int AutopetTicks = 8;

    @Expose
    public int clearTickTimerHudX = 50;

    @Expose
    public int clearTickTimerHudY = 50;

    @Expose
    public float clearTickTimerHudScale = 2;

    @Expose
    @ConfigOption(name = "Clear Tick Timer", desc = "Renders a Timer counting down from 20 during clear.")
    @ConfigEditorBoolean
    public boolean clearTickTimer = false;

    @ConfigOption(name = "Move Clear Tick Timer HUD", desc = "Allows you to move the Timer HUD")
    @ConfigEditorButton(buttonText = "Click")
    public Runnable moveClearTickTimerHud = () ->
            MinecraftClient.getInstance().send(() -> MinecraftClient.getInstance().setScreen(new MoveGUI("§a20", "miscCategory","clearTickTimerHudX", "clearTickTimerHudY", "clearTickTimerHudScale")));
}
