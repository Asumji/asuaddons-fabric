package me.asumji.features;

import me.asumji.AsuAddons;
import me.asumji.gui.config.ConfigManager;
import me.asumji.util.Variables;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClearTickTimer {
    static boolean inClear = false;

    public static void init() {
        ClientReceiveMessageEvents.ALLOW_GAME.register(ClearTickTimer::onChatMessage);
        HudRenderCallback.EVENT.register(ClearTickTimer::renderHud);
    }

    private static boolean onChatMessage(Text text, boolean b) {
        Matcher matcher = Pattern.compile("\\w* is now ready.").matcher(text.getString());
        if (matcher.find()) inClear = true;
        matcher = Pattern.compile("(\\[BOSS] The Watcher: You have proven yourself\\. You may pass\\.|Sending to server .*\\.\\.\\.)").matcher(text.getString());
        if (matcher.find()) inClear = false;
        return true;
    }

    private static void renderHud(DrawContext drawContext, RenderTickCounter renderTickCounter) {
        if (!ConfigManager.getConfig().miscCategory.clearTickTimer || !inClear) return;
        Variables.TickTimers.putIfAbsent("ClearTickTimer", 20);
        int ticksLeft = Variables.TickTimers.get("ClearTickTimer");
        drawContext.getMatrices().push();
        drawContext.getMatrices().scale(ConfigManager.getConfig().miscCategory.clearTickTimerHudScale,ConfigManager.getConfig().miscCategory.clearTickTimerHudScale,1);
        drawContext.drawCenteredTextWithShadow(MinecraftClient.getInstance().textRenderer, Text.of(ticksLeft > 10 ? "§a"+ticksLeft : ticksLeft > 5 ? "§6"+ticksLeft : "§c"+ticksLeft), ConfigManager.getConfig().miscCategory.clearTickTimerHudX, ConfigManager.getConfig().miscCategory.clearTickTimerHudY, 0xFFFFFF);
        drawContext.getMatrices().pop();
    }
}
