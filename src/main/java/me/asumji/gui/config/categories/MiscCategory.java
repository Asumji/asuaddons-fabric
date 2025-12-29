package me.asumji.gui.config.categories;

import com.google.gson.annotations.Expose;
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean;
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorSlider;
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption;
import io.github.notenoughupdates.moulconfig.annotations.Accordion;

public class MiscCategory {
    @Expose
    @Accordion
    @ConfigOption(name = "Autopet", desc = "Autopet Features.")
    public AutoPetAccordion autoPetAccordion = new AutoPetAccordion();

    @Expose
    @Accordion
    @ConfigOption(name = "RNG Meter", desc = "RNG Meter Features.")
    public RNGMeterAccordion rngMeterAccordion = new RNGMeterAccordion();

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
}
