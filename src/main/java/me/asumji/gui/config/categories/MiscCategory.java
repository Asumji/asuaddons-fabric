package me.asumji.gui.config.categories;

import com.google.gson.annotations.Expose;
import io.github.notenoughupdates.moulconfig.annotations.*;
import me.asumji.AsuAddons;
import me.asumji.gui.move.HudElement;
import me.asumji.gui.move.MoveGUI;
import me.asumji.util.Shortcuts;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.Text;

public class MiscCategory {
    public MiscCategory() {
        MoveGUI.HudElements.add(
                new HudElement(
                        "miscCategory", "financeAccordion", "sellValueHudX", "sellValueHudY", "sellValueHudScale", "sellValueAH",
                        (context, x, y, width, height) -> {
                            TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
                            context.fill((x - width / 2), y, (x - width / 2) + width, y + 27, 0xFF5C5C5C);
                            context.fill((x - width / 2), y, (x - width / 2) + width, y + 4, 0xFF444445);
                            context.fill((x - width / 2), y, (x - width / 2) + 4, y + 27, 0xFF444445);
                            context.fill((x - width / 2), y + 27, (x - width / 2) + width, y + 27 - 4, 0xFF444445);
                            context.fill((x - width / 2) + width, y, (x - width / 2) + width - 4, y + 27, 0xFF444445);
                            context.drawTextWithShadow(textRenderer, Text.literal("§6Total Value: 250.32m"), (x - width / 2) + 7, y + 18 - textRenderer.fontHeight, 0xFFFFFFFF);
                        },
                        118, 27
                )
        );
    }

    @Expose
    @Accordion
    @ConfigOption(name = "Autopet", desc = "Autopet Features.")
    public AutoPetAccordion autoPetAccordion = new AutoPetAccordion();

    @Expose
    @Accordion
    @ConfigOption(name = "RNG Meter", desc = "RNG Meter Features.")
    public RNGMeterAccordion rngMeterAccordion = new RNGMeterAccordion();

    @Expose
    @Accordion
    @ConfigOption(name = "Bridge", desc = "Bridge Features.")
    public BridgeAccordion bridgeAccordion = new BridgeAccordion();

    public static class AutoPetAccordion {
        @Expose
        @ConfigOption(name = "Autopet Notif", desc = "Displays a title when an Autopet rule is procced.")
        @ConfigEditorBoolean
        public boolean AutopetNotif = false;

        @Expose
        @ConfigOption(name = "Autopet Ticks", desc = "Defines the ticks how long the title should stay for")
        @ConfigEditorSlider(minValue = 1, maxValue = 20, minStep = 1)
        public int AutopetTicks = 8;
    }

    public static class RNGMeterAccordion {
        @Expose
        @ConfigOption(name = "RNG Meter Details", desc = "Displays more details for the items in the rng meter menu.\nIncrement anywhere here refers to the amount of progress you get. So 1 Rev T5 = 1500 Slayer XP, 1 S+ Run = 300 Score")
        @ConfigEditorBoolean
        public boolean rngMeterDetails = false;

        @Expose
        @ConfigOption(name = "Show Assumed Increment", desc = "Displays the assumed increment.")
        @ConfigEditorBoolean
        public boolean assumedIncrement = false;

        @Expose
        @ConfigOption(name = "Show 1/x Drop Chance", desc = "Displays the drop chance as a fraction next to the percentage.")
        @ConfigEditorBoolean
        public boolean fractionDropChance = false;

        @Expose
        @ConfigOption(name = "Show Profit per Increment", desc = "Displays the profit per Increment for an item.\nDoes not include Chest price for dungeons (might add in future).")
        @ConfigEditorBoolean
        public boolean profitPerIncrement = false;
    }

    public static class BridgeAccordion {
        @Expose
        @ConfigOption(name = "Enable Bridge", desc = "Enabled the bridge features.")
        @ConfigEditorBoolean
        public boolean bridge = false;

        @Expose
        @ConfigOption(name = "Bridge Bot", desc = "The IGN of the bridge bot.")
        @ConfigEditorText
        public String bridgeBot = "";

        @Expose
        @ConfigOption(name = "Bridge Format", desc = "The Format the bridge message should have.\n{usr} for the message sender.\n{msg} for the message.\nUse & for colors.")
        @ConfigEditorText
        public String bridgeMessage = "&2Bridge > &6{usr}: &r{msg}";

        @Expose
        @ConfigOption(name = "Officer Format", desc = "The Format the officer chat message should have.\n{usr} for the message sender.\n{msg} for the message.\nUse & for colors.")
        @ConfigEditorText
        public String officerMessage = "&3Bridge > &6{usr}: &r{msg}";

        @ConfigOption(name = "Test Message", desc = "Sends a test message to preview how it looks in chat.")
        @ConfigEditorButton(buttonText = "Click")
        public Runnable testMessage = () -> {
            Shortcuts.queueClientMessage(Text.literal(bridgeMessage.replace("{usr}", "weeklies").replace("{msg}", "This is a test message.").replace("&","§")));
            Shortcuts.queueClientMessage(Text.literal(officerMessage.replace("{usr}", "weeklies").replace("{msg}", "This is a test message.").replace("&","§")));
        };
    }

    @Expose
    @Accordion
    @ConfigOption(name = "AH/BZ", desc = "Finance Features.")
    public FinanceAccordion financeAccordion = new FinanceAccordion();

    public static class FinanceAccordion {
        @Expose
        public int sellValueHudX = 50;

        @Expose
        public int sellValueHudY = 50;

        @Expose
        public float sellValueHudScale = 2;

        @Expose
        @ConfigOption(name = "Calculate Total AH Value", desc = "Adds an Overlay to show how much your Auction House is worth if everything sells.")
        @ConfigEditorBoolean
        public boolean sellValueAH = false;

        @Expose
        @ConfigOption(name = "Calculate Total Bazaar Value", desc = "Adds an Overlay to show how much your Bazaar is worth if everything sells.")
        @ConfigEditorBoolean
        public boolean sellValueBZ = false;
    }
}
