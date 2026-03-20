package me.asumji.gui.move;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.asumji.AsuAddons;
import me.asumji.gui.config.ConfigGUI;
import me.asumji.gui.config.ConfigManager;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
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
        super(Component.literal("Move GUI"));
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

        Button buttonWidget = Button.builder(Component.literal((showAll ? "Hide" : "Show") + " disabled features"), (btn) -> {
            showAll = !showAll;
            btn.setMessage(Component.literal((showAll ? "Hide" : "Show") + " disabled features"));
            selectedElement = null;
        }).bounds(this.width / 2 - 70, this.height - 20, 140, 20).build();

        this.addRenderableWidget(buttonWidget);
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
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

                context.drawString(
                        font,
                        element.getXName() + ": " + x,
                        (int) ((x - width / 2f) * scale),
                        (int) (y * scale - font.lineHeight * 3),
                        0xFFFFFFFF
                );

                context.drawString(
                        font,
                        element.getYName() + ": " + y,
                        (int) ((x - width / 2f) * scale),
                        (int) (y * scale - font.lineHeight * 2),
                        0xFFFFFFFF
                );

                context.drawString(
                        font,
                        element.getScaleName() + ": " + df.format(scale),
                        (int) ((x - width / 2f) * scale),
                        (int) (y * scale - font.lineHeight),
                        0xFFFFFFFF
                );

                if (Math.abs(this.width / 2f - x * scale) < 5) {
                    context.fill(this.width / 2 - 1, 0, this.width / 2 + 1, (int) (y * scale), 0x33FFFFFF);
                    context.fill(this.width / 2 - 1, (int) ((y + height) * scale), this.width / 2 + 1, this.height, 0x33FFFFFF);
                }
                if (Math.abs(this.height / 2f - (y + height / 2f) * scale) < 5) {
                    context.fill(0, this.height / 2 - 1, (int) ((x - width / 2f) * scale), this.height / 2 + 1, 0x33FFFFFF);
                    context.fill((int) ((x + width / 2f) * scale), this.height / 2 - 1, this.width, this.height / 2 + 1, 0x33FFFFFF);
                }
            } else {
                if (mouseX >= (x - width / 2f) * scale && mouseX <= (x + width / 2f) * scale && mouseY >= y * scale && mouseY <= (y + height) * scale)
                    context.fill((int) ((x - width / 2f) * scale), (int) (y * scale), (int) ((x + width / 2f) * scale), (int) ((y + height) * scale), 0x11FFFFFF);
            }
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
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
        return true;
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent click, double offsetX, double offsetY) {
        if (selectedElement == null) return super.mouseDragged(click, offsetX, offsetY);
        if (click.x() >= this.width / 2f - 70 && click.x() <= this.width / 2f + 70 && click.y() >= this.height - 20 && click.y() <= this.height) return super.mouseDragged(click, offsetX, offsetY);

        float scale = getProperty(selectedElement.getScaleName(), selectedElement.getCategory(), selectedElement.getAccordion()).getAsFloat();
        double x = click.x();
        double y = click.y()-(selectedElement.getHeight()/2f*scale);

        if (Math.abs(this.width / 2f - click.x()) < 15)
            x = this.width / 2f;
        if (Math.abs(this.height / 2f - click.y()) < 15)
            y = this.height / 2f - (selectedElement.getHeight() / 2f * scale);

        config.getAsJsonObject(selectedElement.getCategory())
                .getAsJsonObject(selectedElement.getAccordion())
                .addProperty(selectedElement.getXName(), (int) (x / scale));

        config.getAsJsonObject(selectedElement.getCategory())
                .getAsJsonObject(selectedElement.getAccordion())
                .addProperty(selectedElement.getYName(), (int) (y / scale));

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
    public void onClose() {
        try (FileWriter fw = new FileWriter(CONFIG_FILE)) {
            fw.write(GSON.toJson(config));
        } catch (IOException e) {
            AsuAddons.LOGGER.info(e.toString());
        }

        ConfigManager.setConfig(GSON.fromJson(config, ConfigGUI.class));
        config = null;
        this.minecraft.setScreen(null);
    }
}