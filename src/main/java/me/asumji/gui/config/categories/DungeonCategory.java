package me.asumji.gui.config.categories;

import com.google.gson.annotations.Expose;
import io.github.notenoughupdates.moulconfig.ChromaColour;
import io.github.notenoughupdates.moulconfig.annotations.*;
import me.asumji.gui.MoveGUI;
import net.minecraft.client.MinecraftClient;
import org.apache.commons.lang3.ArrayUtils;

public class DungeonCategory {
    public DungeonCategory() {
        MoveGUI.GUIElements = ArrayUtils.add(MoveGUI.GUIElements, new String[]{"0.00s", "dungeonCategory", "lividTimerHudX", "lividTimerHudY", "lividTimerHudScale"});
        MoveGUI.GUIElements = ArrayUtils.add(MoveGUI.GUIElements, new String[]{"§eweeklies 3/4", "dungeonCategory","melodyHudX", "melodyHudY", "melodyHudScale"});
        MoveGUI.GUIElements = ArrayUtils.add(MoveGUI.GUIElements, new String[]{"§55.00", "dungeonCategory","purplePadTimerHudX", "purplePadTimerHudY", "purplePadTimerHudScale"});
    }

    @Expose
    @ConfigOption(name = "PF preview", desc = "Shows a quick preview of the player joining in dungeons.")
    @ConfigEditorBoolean
    public boolean DPU = false;

    @Expose
    @ConfigOption(name = "Relevant Items", desc = "List all items that should be listed in the preview from a players inventory. (e.g. item1,item2,item3)")
    @ConfigEditorText
    public String relevantItems = "scylla,hyperion,astraea,valkyrie,terminator,juju,axe of the shredded,livid dagger,spirit bow,last breath";

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

    @Expose
    @ConfigOption(name = "Wither Hitbox", desc = "Renders the wither's hitbox in F7.")
    @ConfigEditorBoolean
    public boolean witherHitbox = false;

    @Expose
    @ConfigOption(name = "Wither Hitbox Color", desc = "The color of the hitbox.")
    @ConfigEditorColour
    public String witherHitboxColor = ChromaColour.special(0,255,255,0,0);
}
