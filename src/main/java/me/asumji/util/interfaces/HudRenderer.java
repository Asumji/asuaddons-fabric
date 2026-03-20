package me.asumji.util.interfaces;

import net.minecraft.client.gui.GuiGraphics;

@FunctionalInterface
public interface HudRenderer {

    void render(
            GuiGraphics context,
            int x,
            int y,
            int width,
            int height,
            float scale
    );
}