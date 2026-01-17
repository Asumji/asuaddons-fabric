package me.asumji.util.interfaces;

import net.minecraft.client.gui.DrawContext;

@FunctionalInterface
public interface HudRenderer {

    void render(
            DrawContext context,
            int x,
            int y,
            int width,
            int heigt
    );
}