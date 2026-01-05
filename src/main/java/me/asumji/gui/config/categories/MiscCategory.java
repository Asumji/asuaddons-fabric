package me.asumji.gui.config.categories;

import com.google.gson.annotations.Expose;
import io.github.notenoughupdates.moulconfig.annotations.*;
import me.asumji.util.Shortcuts;
import net.minecraft.text.Text;

public class MiscCategory {
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
}
