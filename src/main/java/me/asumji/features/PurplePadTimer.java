package me.asumji.features;

import me.asumji.AsuAddons;
import me.asumji.gui.config.ConfigManager;
import me.asumji.util.Shortcuts;
import me.asumji.util.Variables;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.joml.Matrix3x2fStack;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PurplePadTimer {
    private static final DecimalFormat df = new DecimalFormat("0.00");
    private static boolean timerDone = false;
    public static void init() {
        ClientReceiveMessageEvents.ALLOW_GAME.register(PurplePadTimer::onChatMessage);
        HudElementRegistry.attachElementBefore(VanillaHudElements.CHAT, Identifier.of(AsuAddons.MOD_ID, "purplepadhud"), PurplePadTimer::renderHud);
    }

    private static void renderHud(DrawContext drawContext, RenderTickCounter renderTickCounter) {
        if (Variables.TickTimers.containsKey("PurplePadTimer")) {
            int ticksLeft = Variables.TickTimers.get("PurplePadTimer");
            Matrix3x2fStack matrices = drawContext.getMatrices();
            matrices.pushMatrix();
            matrices.scale(ConfigManager.getConfig().dungeonCategory.purplePadTimerHudScale,ConfigManager.getConfig().dungeonCategory.purplePadTimerHudScale);
            drawContext.drawCenteredTextWithShadow(MinecraftClient.getInstance().textRenderer, Text.literal("§5"+df.format((float)ticksLeft/20)+"s"), ConfigManager.getConfig().dungeonCategory.purplePadTimerHudX, ConfigManager.getConfig().dungeonCategory.purplePadTimerHudY, 0xFFFFFFFF);
            matrices.popMatrix();
            if (ticksLeft == 0) timerDone = true;
        }
        if (timerDone) {
            Shortcuts.displayTitle(Text.literal("§aCrush!"),Text.empty(),0,10,0);
            timerDone = false;
        }
    }

    private static boolean onChatMessage(Text text, boolean b) {
        if (!ConfigManager.getConfig().dungeonCategory.purplePadTimer) return true;
        Matcher matcher = Pattern.compile("\\[BOSS] Storm: (ENERGY HEED MY CALL|THUNDER LET ME BE YOUR CATALYST)!").matcher(text.getString());
        if (matcher.find()) {
            Variables.TickTimers.put("PurplePadTimer", 96);
            return true;
        }
        return true;
    }
}
