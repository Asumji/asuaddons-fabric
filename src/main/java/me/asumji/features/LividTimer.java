package me.asumji.features;

import me.asumji.gui.config.ConfigGUI;
import me.asumji.gui.config.ConfigManager;
import me.asumji.util.Constants;
import me.asumji.util.Shortcuts;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;

import java.text.DecimalFormat;

public class LividTimer {
    private static final DecimalFormat df = new DecimalFormat("0.00");

    private static boolean timerDone = false;
    private static boolean showText = false;

    public static void init() {
        ClientReceiveMessageEvents.ALLOW_GAME.register(LividTimer::onChatMessage);
        HudRenderCallback.EVENT.register(LividTimer::renderHud);
    }

    private static void renderHud(DrawContext drawContext, RenderTickCounter renderTickCounter) {
        if (Constants.TickTimers.get("LividTimer") == null) return;
        drawContext.getMatrices().push();
        drawContext.getMatrices().scale(ConfigManager.getConfig().dungeonCategory.lividTimerHudScale,ConfigManager.getConfig().dungeonCategory.lividTimerHudScale,1);
        int ticksLeft = Constants.TickTimers.get("LividTimer");
        drawContext.getMatrices().push();
        drawContext.getMatrices().scale(ConfigManager.getConfig().dungeonCategory.lividTimerHudScale,ConfigManager.getConfig().dungeonCategory.lividTimerHudScale,1);
        drawContext.drawCenteredTextWithShadow(MinecraftClient.getInstance().textRenderer, Text.of(df.format((float)ticksLeft/20)+"s"), ConfigManager.getConfig().dungeonCategory.lividTimerHudX, ConfigManager.getConfig().dungeonCategory.lividTimerHudY, 0xFFFFFF);
        if (ticksLeft == 0 && !timerDone) {
            timerDone = true;
            showText = true;
            Shortcuts.displayTitle(Text.literal("§cLivid is vulnerable"),Text.literal(""),0,20,0);
            if (!ConfigManager.getConfig().dungeonCategory.lividTimerMessage.isEmpty()) MinecraftClient.getInstance().player.networkHandler.sendChatCommand("pc "+ConfigManager.getConfig().dungeonCategory.lividTimerMessage);
        }
        drawContext.getMatrices().pop();
    }

    private static boolean onChatMessage(Text text, boolean bool) {
        if (!text.getString().matches("\\[BOSS] Livid: Welcome, you've arrived right on time. I am Livid, the Master of Shadows.") || !ConfigManager.getConfig().dungeonCategory.lividTimer) return true;
        Constants.TickTimers.put("LividTimer",390);
        timerDone = false;
        showText = false;
        return true;
    }
}
