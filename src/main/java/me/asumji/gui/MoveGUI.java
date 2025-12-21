package me.asumji.gui;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.asumji.AsuAddons;
import me.asumji.gui.config.ConfigGUI;
import me.asumji.gui.config.ConfigManager;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.io.*;
import java.text.DecimalFormat;

import static me.asumji.AsuAddons.GSON;

public class MoveGUI extends Screen {
    public static String[][] GUIElements = {};

    private static final File CONFIG_FILE = new File("config/asuaddons/config.json");
    private static JsonObject config = null;

    private static String[] selectedElement = null;
    private static final DecimalFormat df = new DecimalFormat("0.00");

    public MoveGUI() {
        super(Text.literal("Move GUI"));
    }

    public JsonElement getProperty(String property, String category, String accordion) {
        return config.getAsJsonObject(category).getAsJsonObject(accordion).get(property);
    }

    @Override
    public void init() {
        selectedElement = null;
        config = GSON.fromJson(GSON.toJson(ConfigManager.getConfig()), JsonObject.class);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        for (String[] element : GUIElements) {
            context.getMatrices().pushMatrix();
            context.getMatrices().scale(getProperty(element[5],element[1], element[2]).getAsFloat(), getProperty(element[5],element[1], element[2]).getAsFloat());
            context.drawCenteredTextWithShadow(this.textRenderer, element[0], getProperty(element[3],element[1], element[2]).getAsInt(), getProperty(element[4],element[1], element[2]).getAsInt(), 0xFFFFFFFF);
            if (element == selectedElement)
                context.fill(getProperty(element[3], element[1], element[2]).getAsInt() - (this.textRenderer.getWidth(element[0]) / 2) - 1, getProperty(element[4], element[1], element[2]).getAsInt() - 1, getProperty(element[3], element[1], element[2]).getAsInt() + (this.textRenderer.getWidth(element[0]) / 2) + 1, getProperty(element[4], element[1], element[2]).getAsInt() + this.textRenderer.fontHeight + 1, 0x33FFFFFF);
            context.getMatrices().popMatrix();

            if (element == selectedElement) {
                context.drawTextWithShadow(this.textRenderer, element[3] + ": " + getProperty(element[3], element[1], element[2]).getAsString(), (int) ((getProperty(element[3], element[1], element[2]).getAsInt() - ((float) this.textRenderer.getWidth(element[0]) / 2) - 1) * getProperty(element[5], element[1], element[2]).getAsFloat()), (int) ((getProperty(element[4], element[1], element[2]).getAsInt() - 1) * getProperty(element[5], element[1], element[2]).getAsFloat() - this.textRenderer.fontHeight*3), 0xFFFFFFFF);
                context.drawTextWithShadow(this.textRenderer, element[4] + ": " + getProperty(element[4], element[1], element[2]).getAsString(), (int) ((getProperty(element[3], element[1], element[2]).getAsInt() - ((float) this.textRenderer.getWidth(element[0]) / 2) - 1) * getProperty(element[5], element[1], element[2]).getAsFloat()), (int) ((getProperty(element[4], element[1], element[2]).getAsInt() - 1) * getProperty(element[5], element[1], element[2]).getAsFloat() - this.textRenderer.fontHeight*2), 0xFFFFFFFF);
                context.drawTextWithShadow(this.textRenderer, element[5] + ": " + df.format(getProperty(element[5], element[1], element[2]).getAsFloat()), (int) ((getProperty(element[3], element[1], element[2]).getAsInt() - ((float) this.textRenderer.getWidth(element[0]) / 2) - 1) * getProperty(element[5], element[1], element[2]).getAsFloat()), (int) ((getProperty(element[4], element[1], element[2]).getAsInt() - 1) * getProperty(element[5], element[1], element[2]).getAsFloat() - this.textRenderer.fontHeight), 0xFFFFFFFF);
            }
        }
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        for (String[] element : GUIElements) {
            if ((click.x() >= (getProperty(element[3], element[1], element[2]).getAsInt() - ((float) this.textRenderer.getWidth(element[0]) / 2) - 1) * getProperty(element[5], element[1], element[2]).getAsFloat() && click.x() <= (getProperty(element[3], element[1], element[2]).getAsInt() + ((float) this.textRenderer.getWidth(element[0]) / 2) + 1) * getProperty(element[5], element[1], element[2]).getAsFloat() && click.y() >= (getProperty(element[4], element[1], element[2]).getAsInt() - 1) * getProperty(element[5], element[1], element[2]).getAsFloat() && click.y() <= (getProperty(element[4], element[1], element[2]).getAsInt() + this.textRenderer.fontHeight + 1) * getProperty(element[5], element[1], element[2]).getAsFloat())) {
                selectedElement = element;
            }
        }
        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (selectedElement == null) return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
        float scaleBefore = getProperty(selectedElement[5],selectedElement[1],selectedElement[2]).getAsFloat();
        config.getAsJsonObject(selectedElement[1]).getAsJsonObject(selectedElement[2]).addProperty(selectedElement[5], getProperty(selectedElement[5],selectedElement[1],selectedElement[2]).getAsFloat() + (float) verticalAmount/5);
        config.getAsJsonObject(selectedElement[1]).getAsJsonObject(selectedElement[2]).addProperty(selectedElement[3], (int) (getProperty(selectedElement[3],selectedElement[1],selectedElement[2]).getAsInt()*scaleBefore/getProperty(selectedElement[5],selectedElement[1],selectedElement[2]).getAsFloat()));
        config.getAsJsonObject(selectedElement[1]).getAsJsonObject(selectedElement[2]).addProperty(selectedElement[4], (int) (getProperty(selectedElement[4],selectedElement[1],selectedElement[2]).getAsInt()*scaleBefore/getProperty(selectedElement[5],selectedElement[1],selectedElement[2]).getAsFloat()));
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean mouseDragged(Click click, double offsetX, double offsetY) {
        if (selectedElement == null) return super.mouseDragged(click, offsetX, offsetY);
        config.getAsJsonObject(selectedElement[1]).getAsJsonObject(selectedElement[2]).addProperty(selectedElement[3], (int) (click.x()/getProperty(selectedElement[5],selectedElement[1],selectedElement[2]).getAsFloat()));
        config.getAsJsonObject(selectedElement[1]).getAsJsonObject(selectedElement[2]).addProperty(selectedElement[4], (int) (click.y()/getProperty(selectedElement[5],selectedElement[1],selectedElement[2]).getAsFloat()));
        return super.mouseDragged(click, offsetX, offsetY);
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