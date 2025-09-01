package me.asumji.gui.config.categories;

import com.google.gson.annotations.Expose;
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean;
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorSlider;
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption;

public class MiscCategory {
    @Expose
    @ConfigOption(name = "Autopet Notif", desc = "Displays a title when an Autopet rule is procced.")
    @ConfigEditorBoolean
    public boolean AutopetNotif = false;
    
    @Expose
    @ConfigOption(name = "Autopet Ticks", desc = "Defines the ticks how long the title should stay for")
    @ConfigEditorSlider(minValue = 1, maxValue = 20, minStep = 1)
    public int AutopetTicks = 8;
}
