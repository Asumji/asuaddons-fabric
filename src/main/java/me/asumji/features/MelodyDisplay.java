package me.asumji.features;

import me.asumji.AsuAddons;
import me.asumji.gui.config.ConfigManager;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.joml.Matrix3x2fStack;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MelodyDisplay {
    public static String melodyProgress = "";
    public static int section = 0;
    public static void init() {
        ClientReceiveMessageEvents.ALLOW_GAME.register(MelodyDisplay::onChatMessage);
        HudElementRegistry.attachElementBefore(VanillaHudElements.CHAT, Identifier.of(AsuAddons.MOD_ID, "melodyhud"), MelodyDisplay::renderHud);
    }

    private static void renderHud(DrawContext drawContext, RenderTickCounter renderTickCounter) {
        if (!ConfigManager.getConfig().dungeonCategory.melodyDisplay || section == 0) return;
        Matrix3x2fStack matrices = drawContext.getMatrices();
        matrices.pushMatrix();
        matrices.scale(ConfigManager.getConfig().dungeonCategory.melodyHudScale,ConfigManager.getConfig().dungeonCategory.melodyHudScale);
        drawContext.drawCenteredTextWithShadow(MinecraftClient.getInstance().textRenderer,Text.literal(melodyProgress),ConfigManager.getConfig().dungeonCategory.melodyHudX,ConfigManager.getConfig().dungeonCategory.melodyHudY,0xFFFFFFFF);
        matrices.popMatrix();
    }

    private static boolean onChatMessage(Text text, boolean b) {
        Matcher matcher = Pattern.compile("\\[BOSS] Storm: At least my son died by your hands\\.").matcher(text.getString());
        if (matcher.find()) {
            section = 1;
            return true;
        }
        matcher = Pattern.compile("(\\(7/7\\)|\\(8/8\\))").matcher(text.getString());
        if (matcher.find() && section > 0) {
            section++;
            if (section > 4) section = 0;
            melodyProgress = "";
            return true;
        }
        matcher = Pattern.compile("Sending to server .*\\.\\.\\.").matcher(text.getString());
        if (matcher.find()) {
            section = 0;
            melodyProgress = "";
            return true;
        }
        matcher = Pattern.compile("§.Party §.> §b\\[.*] (.*)§.: .* (\\d/\\d|\\d+%)").matcher(text.getString());
        if (matcher.find()) {
            melodyProgress = "§e" + matcher.group(1) + " " + matcher.group(2);
            return true;
        }
        return true;
    }
}
