package me.asumji.util.interfaces;

import net.minecraft.client.gui.GuiGraphicsExtractor;

@FunctionalInterface
public interface HudRenderer {

    void render(
            GuiGraphicsExtractor context,
            int x,
            int y,
            int width,
            int height,
            float scale
    );
}