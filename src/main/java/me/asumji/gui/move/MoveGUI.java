package me.asumji.gui.move;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.asumji.AsuAddons;
import me.asumji.gui.config.ConfigGUI;
import me.asumji.gui.config.ConfigManager;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static me.asumji.AsuAddons.GSON;

public class MoveGUI extends Screen {

    public static final List<HudElement> HudElements = new ArrayList<>();

    private static final File CONFIG_FILE = new File("config/asuaddons/config.json");
    private static JsonObject config = null;

    private static boolean showAll = true;
    private static HudElement selectedElement = null;
    private static final DecimalFormat df = new DecimalFormat("0.00");

    public MoveGUI() {
        super(Text.literal("Move GUI"));
    }

    public JsonElement getProperty(String property, String category, String accordion) {
        return config.getAsJsonObject(category)
                .getAsJsonObject(accordion)
                .get(property);
    }

    @Override
    public void init() {
        selectedElement = null;
        config = GSON.fromJson(GSON.toJson(ConfigManager.getConfig()), JsonObject.class);

        ButtonWidget buttonWidget = ButtonWidget.builder(Text.literal((showAll ? "Hide" : "Show") + " disabled features"), (btn) -> {
            showAll = !showAll;
            btn.setMessage(Text.literal((showAll ? "Hide" : "Show") + " disabled features"));
            selectedElement = null;
        }).dimensions(this.width / 2 - 70, this.height - 20, 140, 20).build();

        this.addDrawableChild(buttonWidget);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        for (HudElement element : HudElements) {
            if (!getProperty(element.getToggleName(), element.getCategory(), element.getAccordion()).getAsBoolean() && !showAll) continue;
            int x = getProperty(element.getXName(), element.getCategory(), element.getAccordion()).getAsInt();
            int y = getProperty(element.getYName(), element.getCategory(), element.getAccordion()).getAsInt();
            float scale = getProperty(element.getScaleName(), element.getCategory(), element.getAccordion()).getAsFloat();

            int width = element.getWidth();
            int height = element.getHeight();

            element.render(context, x, y, width, height, scale);

            if (element == selectedElement) {
                context.fill((int) ((x - width / 2f) * scale), (int) (y * scale), (int) ((x + width / 2f) * scale), (int) ((y + height) * scale), 0x33FFFFFF);

                context.drawTextWithShadow(
                        textRenderer,
                        element.getXName() + ": " + x,
                        (int) ((x - width / 2f) * scale),
                        (int) (y * scale - textRenderer.fontHeight * 3),
                        0xFFFFFFFF
                );

                context.drawTextWithShadow(
                        textRenderer,
                        element.getYName() + ": " + y,
                        (int) ((x - width / 2f) * scale),
                        (int) (y * scale - textRenderer.fontHeight * 2),
                        0xFFFFFFFF
                );

                context.drawTextWithShadow(
                        textRenderer,
                        element.getScaleName() + ": " + df.format(scale),
                        (int) ((x - width / 2f) * scale),
                        (int) (y * scale - textRenderer.fontHeight),
                        0xFFFFFFFF
                );
            } else {
                if (mouseX >= (x - width / 2f) * scale && mouseX <= (x + width / 2f) * scale && mouseY >= y * scale && mouseY <= (y + height) * scale)
                    context.fill((int) ((x - width / 2f) * scale), (int) (y * scale), (int) ((x + width / 2f) * scale), (int) ((y + height) * scale), 0x11FFFFFF);
            }
        }
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        for (HudElement element : HudElements) {
            if (!getProperty(element.getToggleName(), element.getCategory(), element.getAccordion()).getAsBoolean() && !showAll) continue;
            int x = getProperty(element.getXName(), element.getCategory(), element.getAccordion()).getAsInt();
            int y = getProperty(element.getYName(), element.getCategory(), element.getAccordion()).getAsInt();
            float scale = getProperty(element.getScaleName(), element.getCategory(), element.getAccordion()).getAsFloat();

            int width = element.getWidth();
            int height = element.getHeight();

            if (click.x() >= (x - width / 2f) * scale && click.x() <= (x + width / 2f) * scale && click.y() >= y * scale && click.y() <= (y + height) * scale)
                selectedElement = element;
        }
        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean mouseDragged(Click click, double offsetX, double offsetY) {
        if (selectedElement == null) return super.mouseDragged(click, offsetX, offsetY);
        if (click.x() >= this.width / 2f - 70 && click.x() <= this.width / 2f + 70 && click.y() >= this.height - 20 && click.y() <= this.height) return super.mouseDragged(click, offsetX, offsetY);

        float scale = getProperty(selectedElement.getScaleName(), selectedElement.getCategory(), selectedElement.getAccordion()).getAsFloat();
        config.getAsJsonObject(selectedElement.getCategory())
                .getAsJsonObject(selectedElement.getAccordion())
                .addProperty(selectedElement.getXName(), (int) (click.x() / scale));

        config.getAsJsonObject(selectedElement.getCategory())
                .getAsJsonObject(selectedElement.getAccordion())
                .addProperty(selectedElement.getYName(), (int) (click.y() / scale));

        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (selectedElement == null) return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);

        JsonObject obj = config.getAsJsonObject(selectedElement.getCategory()).getAsJsonObject(selectedElement.getAccordion());

        float oldScale = obj.get(selectedElement.getScaleName()).getAsFloat();
        float newScale = oldScale + (float) verticalAmount / 5f;

        obj.addProperty(selectedElement.getScaleName(), newScale);

        obj.addProperty(selectedElement.getXName(), (int) (obj.get(selectedElement.getXName()).getAsInt() * oldScale / newScale));
        obj.addProperty(selectedElement.getYName(), (int) (obj.get(selectedElement.getYName()).getAsInt() * oldScale / newScale));

        return true;
    }

    @Override
    public void close() {
        try (FileWriter fw = new FileWriter(CONFIG_FILE)) {
            fw.write(GSON.toJson(config));
        } catch (IOException e) {
            AsuAddons.LOGGER.info(e.toString());
        }

        ConfigManager.setConfig(GSON.fromJson(config, ConfigGUI.class));
        config = null;
        this.client.setScreen(null);
    }
}