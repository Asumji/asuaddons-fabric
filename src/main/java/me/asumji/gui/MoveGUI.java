package me.asumji.gui;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import me.asumji.AsuAddons;
import me.asumji.gui.config.ConfigManager;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.io.*;

public class MoveGUI extends Screen {
    private final String placeholder;
    private final String category;
    private final String xProperty;
    private final String yProperty;
    private final String scaleProperty;

    private double x = 0;
    private double y = 0;
    private float scale = 1;

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = new File("config/asuaddons/config.json");
    private static JsonObject config = new JsonObject();

    public MoveGUI(String placeholder, String category, String xProperty, String yProperty, String scaleProperty) {
        super(Text.literal("Move GUI"));
        this.placeholder = placeholder;
        this.category = category;
        this.xProperty = xProperty;
        this.yProperty = yProperty;
        this.scaleProperty = scaleProperty;
    }

    @Override
    public void init() {
        ConfigManager.saveConfig("MoveGUI");
        try (FileReader fr = new FileReader(CONFIG_FILE)) {
            config = GSON.fromJson(fr, JsonObject.class);
            x = config.getAsJsonObject(category).get(xProperty).getAsInt();
            y = config.getAsJsonObject(category).get(yProperty).getAsInt();
            scale = config.getAsJsonObject(category).get(scaleProperty).getAsFloat();
        } catch (Exception e) {
            AsuAddons.LOGGER.info(e.toString());
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.getMatrices().pushMatrix();
        context.getMatrices().scale(scale,scale);
        context.drawCenteredTextWithShadow(this.textRenderer, placeholder, (int) x, (int) y, 0xFFFFFFFF);
        context.getMatrices().popMatrix();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        float scaleBefore = scale;
        scale += (float) verticalAmount/5;
        x = (int) (x*scaleBefore)/scale;
        y = (int) (y*scaleBefore)/scale;
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean mouseDragged(Click click, double offsetX, double offsetY) {
        x = (int) click.x()/scale;
        y = (int) click.y()/scale;
        return super.mouseDragged(click, offsetX, offsetY);
    }

    @Override
    public void close() {
        ConfigManager.saveConfig("MoveGUI");
        config.getAsJsonObject(category).addProperty(xProperty,(int) x);
        config.getAsJsonObject(category).addProperty(yProperty,(int) y);
        config.getAsJsonObject(category).addProperty(scaleProperty,scale);
        try (FileWriter fw = new FileWriter(CONFIG_FILE)) {
            fw.write(GSON.toJson(config));
        } catch (IOException e) {
            AsuAddons.LOGGER.info(e.toString());
        }
        ConfigManager.loadConfig();
        this.client.setScreen(null);
    }
}