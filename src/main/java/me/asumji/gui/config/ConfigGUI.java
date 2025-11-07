package me.asumji.gui.config;

import com.google.gson.annotations.Expose;
import io.github.notenoughupdates.moulconfig.Config;
import io.github.notenoughupdates.moulconfig.annotations.*;
import io.github.notenoughupdates.moulconfig.common.text.StructuredText;
import me.asumji.AsuAddons;
import me.asumji.gui.config.categories.DungeonCategory;
import me.asumji.gui.config.categories.MiscCategory;

public class ConfigGUI extends Config {
    @Override
    public StructuredText getTitle() {
        return StructuredText.of("§6§lAsuAddons v"+AsuAddons.modVersion+" §7- §5MoulConfig §bby Moulberry & nea89o");
    }

    @Expose
    @Category(name = "Dungeons", desc = "All features around dungeons.")
    public DungeonCategory dungeonCategory = new DungeonCategory();

    @Expose
    @Category(name = "Misc", desc = "All Miscellaneous features.")
    public MiscCategory miscCategory = new MiscCategory();
}