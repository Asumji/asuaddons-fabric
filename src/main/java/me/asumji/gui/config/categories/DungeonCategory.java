package me.asumji.gui.config.categories;

import com.google.gson.annotations.Expose;
import io.github.notenoughupdates.moulconfig.ChromaColour;
import io.github.notenoughupdates.moulconfig.annotations.*;
import me.asumji.gui.move.HudElement;
import me.asumji.gui.move.MoveGUI;
import net.minecraft.client.MinecraftClient;

public class DungeonCategory {
    public DungeonCategory() {
        MoveGUI.HudElements.add(
                new HudElement(
                        "dungeonCategory", "f5Accordion", "lividTimerHudX", "lividTimerHudY", "lividTimerHudScale", "lividTimer",
                        (context, x, y, width, height) ->
                            context.drawCenteredTextWithShadow(MinecraftClient.getInstance().textRenderer, "0.00s", x, y, 0xFFFFFFFF),
                        26, 9
                )
        );
        MoveGUI.HudElements.add(
                new HudElement(
                        "dungeonCategory", "f7Accordion", "melodyHudX", "melodyHudY", "melodyHudScale", "melodyDisplay",
                        (context, x, y, width, height) ->
                            context.drawCenteredTextWithShadow(MinecraftClient.getInstance().textRenderer, "§bweeklies (M) §e3/4", x, y, 0xFFFFFFFF),
                        82, 9
                )
        );
        MoveGUI.HudElements.add(
                new HudElement(
                        "dungeonCategory", "f7Accordion", "purplePadTimerHudX", "purplePadTimerHudY", "purplePadTimerHudScale", "purplePadTimer",
                        (context, x, y, width, height) ->
                            context.drawCenteredTextWithShadow(MinecraftClient.getInstance().textRenderer, "§55.00s", x, y, 0xFFFFFFFF),
                        26, 9
                )
        );
    }

    @Expose
    @Accordion
    @ConfigOption(name = "Party Finder", desc = "Party Finder Features.")
    public DPUAccordion dpuAccordion = new DPUAccordion();

    public static class DPUAccordion {
        @Expose
        @ConfigOption(name = "Party Finder Preview", desc = "Shows a quick preview of the player joining in dungeons.")
        @ConfigEditorBoolean
        public boolean DPU = false;

        @Expose
        @ConfigOption(name = "Relevant Items", desc = "List all items that should be listed in the preview from a players inventory. (e.g. item1,item2,item3)")
        @ConfigEditorText
        public String relevantItems = "scylla,hyperion,astraea,valkyrie,terminator,juju,axe of the shredded,livid dagger,spirit bow,last breath";
    }

    @Expose
    @Accordion
    @ConfigOption(name = "F5/M5 Boss", desc = "Various Features for Livid.")
    public F5Accordion f5Accordion = new F5Accordion();

    public static class F5Accordion {
        @Expose
        @ConfigOption(name = "Livid Solver", desc = "Highlights the correct livid.")
        @ConfigEditorBoolean
        public boolean lividSolver = false;

        @Expose
        @ConfigOption(name = "Livid Tracer", desc = "Draws a tracer to the correct livid.")
        @ConfigEditorBoolean
        public boolean lividTracer = false;

        @Expose
        @ConfigOption(name = "Livid Solver Adapt Color", desc = "Adapts the color of the highlight depending on what livid it is.")
        @ConfigEditorBoolean
        public boolean lividSolverAdaptColor = false;

        @Expose
        @ConfigOption(name = "Livid Solver Color", desc = "The color of the hightlight (This will only allow you to change opacity when the above setting is on).")
        @ConfigEditorColour
        public String lividSolverColor = ChromaColour.special(0,255,255,0,0);

        @Expose
        @ConfigOption(name = "Correct Livid message", desc = "Message to send what the correct livid is (leave empty to disable).\n{livid} = color")
        @ConfigEditorText
        public String lividSolverMessage = "[AU] Correct Livid: {livid}!";

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
    }

    @Expose
    @Accordion
    @ConfigOption(name = "F7/M7", desc = "Various Features for F7.")
    public F7Accordion f7Accordion = new F7Accordion();

    public static class F7Accordion {
        @Expose
        @ConfigOption(name = "Diamante Alert", desc = "Chat Alert when a Diamante giant is detected.")
        @ConfigEditorBoolean
        public boolean diamanteAlert = false;

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
        @ConfigOption(name = "Wither Hitbox", desc = "Renders the wither's hitbox in F7.")
        @ConfigEditorBoolean
        public boolean witherHitbox = false;

        @Expose
        @ConfigOption(name = "Wither Hitbox Color", desc = "The color of the hitbox.")
        @ConfigEditorColour
        public String witherHitboxColor = ChromaColour.special(0,255,255,0,0);
    }

    @Expose
    @Accordion
    @ConfigOption(name = "Starred", desc = "Starmob things.")
    public StarredAccordion starredAccordion = new StarredAccordion();

    public static class StarredAccordion {
        @Expose
        @ConfigOption(name = "Shadow Assassin Highlight", desc = "Highlights Shadow Assassins (Only Highlights boots when invisible to stay legit).")
        @ConfigEditorBoolean
        public boolean saHighlight = false;

        @Expose
        @ConfigOption(name = "Shadow Assassin Color", desc = "The color for the shadow assassin highlight.")
        @ConfigEditorColour
        public String saColor = ChromaColour.special(0,255,255,0,0);
    }
}
