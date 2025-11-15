package me.asumji.features;

import me.asumji.AsuAddons;
import me.asumji.gui.config.ConfigManager;
import me.asumji.util.Variables;
import me.asumji.util.Shortcuts;
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

public class LividTimer {
    private static final DecimalFormat df = new DecimalFormat("0.00");

    private static boolean timerDone = false;
    public static void init() {
        ClientReceiveMessageEvents.ALLOW_GAME.register(LividTimer::onChatMessage);
        HudElementRegistry.attachElementBefore(VanillaHudElements.CHAT, Identifier.of(AsuAddons.MOD_ID, "lividhud"), LividTimer::renderHud);
    }

    private static void renderHud(DrawContext drawContext, RenderTickCounter renderTickCounter) {
        if (Variables.TickTimers.get("LividTimer") == null) return;
        int ticksLeft = Variables.TickTimers.get("LividTimer");
        AsuAddons.LOGGER.info(String.valueOf(ticksLeft));
        Matrix3x2fStack matrices = drawContext.getMatrices();
        matrices.pushMatrix();
        matrices.scale(ConfigManager.getConfig().dungeonCategory.lividTimerHudScale,ConfigManager.getConfig().dungeonCategory.lividTimerHudScale);
        drawContext.drawCenteredTextWithShadow(MinecraftClient.getInstance().textRenderer, Text.of(df.format((float)ticksLeft/20)+"s"), ConfigManager.getConfig().dungeonCategory.lividTimerHudX, ConfigManager.getConfig().dungeonCategory.lividTimerHudY, 0xFFFFFFFF);
        matrices.popMatrix();
        if (ticksLeft == 0 && !timerDone) {
            timerDone = true;
            Shortcuts.displayTitle(Text.literal("§cLivid is vulnerable"), Text.literal(""), 0, 20, 0);
            if (!ConfigManager.getConfig().dungeonCategory.lividTimerMessage.isEmpty())
                MinecraftClient.getInstance().player.networkHandler.sendChatCommand("pc " + ConfigManager.getConfig().dungeonCategory.lividTimerMessage);
        }
    }

    private static boolean onChatMessage(Text text, boolean bool) {
        if (!text.getString().matches("\\[BOSS] Livid: Welcome, you've arrived right on time. I am Livid, the Master of Shadows.") || !ConfigManager.getConfig().dungeonCategory.lividTimer) return true;
        Variables.TickTimers.put("LividTimer",390);
        timerDone = false;
        return true;
    }
}
