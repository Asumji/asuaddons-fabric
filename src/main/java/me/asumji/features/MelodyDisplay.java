package me.asumji.features;

import me.asumji.AsuAddons;
import me.asumji.gui.config.ConfigManager;
import me.asumji.util.Variables;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.joml.Matrix3x2fStack;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MelodyDisplay {
    public static String melodyProgress = "";
    public static int section = 0;
    public static void init() {
        ClientReceiveMessageEvents.ALLOW_GAME.register(MelodyDisplay::onChatMessage);
        HudElementRegistry.attachElementBefore(VanillaHudElements.CHAT, Identifier.fromNamespaceAndPath(AsuAddons.MOD_ID, "melodyhud"), MelodyDisplay::renderHud);
    }

    private static void renderHud(GuiGraphics drawContext, DeltaTracker renderTickCounter) {
        if (!ConfigManager.getConfig().dungeonCategory.f7Accordion.melodyDisplay || section == 0) return;
        Matrix3x2fStack matrices = drawContext.pose();
        matrices.pushMatrix();
        matrices.scale(ConfigManager.getConfig().dungeonCategory.f7Accordion.melodyHudScale,ConfigManager.getConfig().dungeonCategory.f7Accordion.melodyHudScale);
        drawContext.drawCenteredString(Minecraft.getInstance().font,Component.literal(melodyProgress),ConfigManager.getConfig().dungeonCategory.f7Accordion.melodyHudX,ConfigManager.getConfig().dungeonCategory.f7Accordion.melodyHudY,0xFFFFFFFF);
        matrices.popMatrix();
    }

    private static boolean onChatMessage(Component text, boolean b) {
        Matcher matcher = Pattern.compile("\\[BOSS] Goldor: Who dares trespass into my domain\\?").matcher(text.getString());
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

        matcher = Pattern.compile("Party > (?:\\[.*] )?(.*): .* (\\d/\\d|\\d+%)").matcher(text.getString().replaceAll("§.",""));
        if (matcher.find() && !text.getString().contains("SS")) {
            String name = String.valueOf(matcher.group(1));
            String progress = String.valueOf(matcher.group(2));
            String role = "";
            String roleColor = "";
            for (String line : Variables.getTablist()) {
                Matcher classMatcher = Pattern.compile("\\[\\d+] "+name+" (?:. )?\\((.+) .+\\)").matcher(line);
                if (classMatcher.find()) {
                    role = classMatcher.group(1);
                    roleColor = Variables.classes.get(role);
                }
            }
            if (role.isEmpty()) {
                AsuAddons.LOGGER.info("MelodyDisplay: Role not found!");
                for (String line : Variables.getTablist()) {
                    AsuAddons.LOGGER.info(line);
                }
                melodyProgress = "§e" + name + " " + progress;
            } else {
                melodyProgress = roleColor + name + " (" + role.charAt(0) + ") §e" + progress;
            }
            return true;
        }
        return true;
    }
}
