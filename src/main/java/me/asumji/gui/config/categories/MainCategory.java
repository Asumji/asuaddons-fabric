package me.asumji.gui.config.categories;

import com.google.gson.annotations.Expose;
import io.github.notenoughupdates.moulconfig.annotations.Accordion;
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean;
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorButton;
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption;
import me.asumji.gui.move.HudElement;
import me.asumji.gui.move.MoveGUI;
import net.minecraft.client.Minecraft;

public class MainCategory {
    public static boolean initialized = false;

    public MainCategory() {
        if (initialized) return;
        MoveGUI.HudElements.add(
                new HudElement(
                        "mainCategory", "titleAccordion", "titleHudX", "titleHudY", "titleHudScale", "customTitles",
                        (context, x, y, width, height, scale) -> {
                            context.pose().pushMatrix();
                            context.pose().scale(scale);
                            context.centeredText(Minecraft.getInstance().font, "Test Title", x, y, 0xFFFFFFFF);
                            context.pose().popMatrix();

                            context.pose().pushMatrix();
                            context.pose().scale(scale/2);
                            context.centeredText(Minecraft.getInstance().font, "Test Subtitle", x * 2, (y + 10) * 2, 0xFFFFFFFF);
                            context.pose().popMatrix();
                        }, 47, 15
                )
        );
        initialized = true;
    }

    @ConfigOption(name = "Move GUI Elements", desc = "Allows you to move the GUI Elements.")
    @ConfigEditorButton(buttonText = "Click")
    public Runnable moveHud = () ->
            Minecraft.getInstance().schedule(() -> Minecraft.getInstance().setScreen(new MoveGUI()));

    @Expose
    public boolean firstLaunch = true;

    @Expose
    public String webhookUrl = "";

    @Expose
    public String lastestAction = "";

    @Expose
    public String downloaded = "";

    @Expose
    public int jarNumber = 9999999;

    @Expose
    @ConfigOption(name = "Auto Updater", desc = "Enables automatic updates whenever a new action run has been found.")
    @ConfigEditorBoolean
    public boolean autoUpdates = true;

    @Expose
    @Accordion
    @ConfigOption(name = "Custom Titles", desc = "Title features")
    public TitleAccordion titleAccordion = new TitleAccordion();

    public static class TitleAccordion {
        @Expose
        @ConfigOption(name = "Enable", desc = "Enable this to use custom titles instead of vanilla ones.\nThis means you will be able to move and scale the Title.")
        @ConfigEditorBoolean
        public boolean customTitles = false;

        @Expose
        @ConfigOption(name = "Allow Stacking", desc = "Allows concurrent titles to stack below each other rather than replace each other.")
        @ConfigEditorBoolean
        public boolean stackingTitles = false;

        @Expose
        public int titleHudX = 50;

        @Expose
        public int titleHudY = 50;

        @Expose
        public float titleHudScale = 2;
    }
}
