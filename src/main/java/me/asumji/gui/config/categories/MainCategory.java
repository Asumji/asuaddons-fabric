package me.asumji.gui.config.categories;

import com.google.gson.annotations.Expose;
import io.github.notenoughupdates.moulconfig.annotations.Accordion;
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean;
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorButton;
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption;
import me.asumji.AsuAddons;
import me.asumji.gui.move.HudElement;
import me.asumji.gui.move.MoveGUI;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.Text;

public class MainCategory {
    public MainCategory() {
        MoveGUI.HudElements.add(
                new HudElement(
                        "mainCategory", "titleAccordion", "titleHudX", "titleHudY", "titleHudScale", "customTitles",
                        (context, x, y, width, height, scale) -> {
                            context.getMatrices().pushMatrix();
                            context.getMatrices().scale(scale);
                            context.drawCenteredTextWithShadow(MinecraftClient.getInstance().textRenderer, "Test Title", x, y, 0xFFFFFFFF);
                            context.getMatrices().popMatrix();

                            context.getMatrices().pushMatrix();
                            context.getMatrices().scale(scale/2);
                            context.drawCenteredTextWithShadow(MinecraftClient.getInstance().textRenderer, "Test Subtitle", x * 2, (y + 10) * 2, 0xFFFFFFFF);
                            context.getMatrices().popMatrix();
                        }, 47, 15
                )
        );
    }

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
