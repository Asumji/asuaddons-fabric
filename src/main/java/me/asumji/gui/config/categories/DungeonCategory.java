package me.asumji.gui.config.categories;

import com.google.gson.annotations.Expose;
import io.github.notenoughupdates.moulconfig.ChromaColour;
import io.github.notenoughupdates.moulconfig.annotations.*;
import io.github.notenoughupdates.moulconfig.observer.Property;
import me.asumji.gui.MoveGUI;
import net.fabricmc.loader.impl.lib.sat4j.core.Vec;
import net.minecraft.client.MinecraftClient;
import org.joml.Vector4f;

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

    @Expose
    @ConfigOption(name = "Diamante Alert", desc = "Chat Alert when a Diamante giant is detected.")
    @ConfigEditorBoolean
    public boolean diamanteAlert = false;

    @Expose
    @ConfigOption(name = "Melody Display", desc = "Displays melody progress on hud.")
    @ConfigEditorBoolean
    public boolean melodyDisplay = false;

    @Expose
    public int melodyHudX = 50;

    @Expose
    public int melodyHudY = 50;

    @Expose
    public float melodyHudScale = 2;

    @ConfigOption(name = "Move Melody HUD", desc = "Allows you to move the Melody HUD")
    @ConfigEditorButton(buttonText = "Click")
    public Runnable moveMelodyHud = () ->
            MinecraftClient.getInstance().send(() -> MinecraftClient.getInstance().setScreen(new MoveGUI("§eweeklies 3/4", "dungeonCategory","melodyHudX", "melodyHudY", "melodyHudScale")));

    @Expose
    @ConfigOption(name = "Purple Pad Timer", desc = "Displays timer when to crush purple.")
    @ConfigEditorBoolean
    public boolean purplePadTimer = false;

    @Expose
    public int purplePadTimerHudX = 50;

    @Expose
    public int purplePadTimerHudY = 50;

    @Expose
    public float purplePadTimerHudScale = 2;

    @ConfigOption(name = "Move Purple Pad Timer HUD", desc = "Allows you to move the Purple Pad Timer HUD")
    @ConfigEditorButton(buttonText = "Click")
    public Runnable movePurplePadTimerHud = () ->
            MinecraftClient.getInstance().send(() -> MinecraftClient.getInstance().setScreen(new MoveGUI("§55.00", "dungeonCategory","purplePadTimerHudX", "purplePadTimerHudY", "purplePadTimerHudScale")));

    @Expose
    @ConfigOption(name = "Wither Hitbox", desc = "Renders the wither's hitbox in f7.")
    @ConfigEditorBoolean
    public boolean witherHitbox = false;

    @Expose
    @ConfigOption(name = "Wither Hitbox Color", desc = "The color of the hitbox.")
    @ConfigEditorColour
    public String witherHitboxColor = ChromaColour.special(0,255,255,0,0);
}
