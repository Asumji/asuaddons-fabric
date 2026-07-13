package me.asumji.gui.move;


import me.asumji.util.interfaces.HudRenderer;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class HudElement {

    private final String category;
    private final String accordion;
    private final String xProp, yProp, scaleProp, toggleProp;
    private final int width, height;

    private final HudRenderer renderer;

    public HudElement(String category, String accordion, String xProp, String yProp, String scaleProp, String toggleProp, HudRenderer renderer, int width, int height) {
        this.category = category;
        this.accordion = accordion;
        this.xProp = xProp;
        this.yProp = yProp;
        this.scaleProp = scaleProp;
        this.renderer = renderer;
        this.width = width;
        this.height = height;
        this.toggleProp = toggleProp;
    }

    public void render(GuiGraphicsExtractor ctx, int x, int y, int width, int height, float scale) {
        renderer.render(ctx, x, y, width, height, scale);
    }

    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }

    public String getCategory() { return category; }
    public String getAccordion() { return accordion; }
    public String getXName() { return xProp; }
    public String getYName() { return yProp; }
    public String getScaleName() { return scaleProp; }
    public String getToggleName() { return toggleProp; }
}