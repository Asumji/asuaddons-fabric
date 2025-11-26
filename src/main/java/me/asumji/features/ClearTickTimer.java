package me.asumji.features;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.asumji.AsuAddons;
import me.asumji.gui.config.ConfigManager;
import me.asumji.util.Variables;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.scoreboard.*;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.joml.Matrix3x2fStack;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClearTickTimer {
    //TODO: Sync timer with actual server timing (current approach idea: start timer when auto-closing timer steps a second (client desync?, wrong timing?))
    static boolean inClear = false;
    static boolean timerStarted = false;
    static String prevSecond = "";

    public static void init() {
        ClientReceiveMessageEvents.ALLOW_GAME.register(ClearTickTimer::onChatMessage);
        HudElementRegistry.attachElementBefore(VanillaHudElements.CHAT, Identifier.of(AsuAddons.MOD_ID, "clearhud"), ClearTickTimer::renderHud);
        ClientTickEvents.END_CLIENT_TICK.register(ClearTickTimer::onTick);
    }

    private static void onTick(MinecraftClient minecraftClient) {
        if (!inClear || timerStarted) return;
        ObjectArrayList<String> scoreboard = Variables.getScoreboard(minecraftClient);
        //AsuAddons.LOGGER.info(prevSecond + " | " + scoreboard.get(5));
        if (prevSecond.isEmpty()) prevSecond = scoreboard.get(5);
        if (!prevSecond.equals(scoreboard.get(5))) timerStarted = true;
    }

    private static void renderHud(DrawContext drawContext, RenderTickCounter renderTickCounter) {
        if (!ConfigManager.getConfig().miscCategory.clearTickTimer || !timerStarted) return;
        Variables.TickTimers.putIfAbsent("ClearTickTimer", 20);
        int ticksLeft = Variables.TickTimers.getOrDefault("ClearTickTimer", 0);
        Matrix3x2fStack matrices = drawContext.getMatrices();
        matrices.pushMatrix();
        matrices.scale(ConfigManager.getConfig().miscCategory.clearTickTimerHudScale,ConfigManager.getConfig().miscCategory.clearTickTimerHudScale);
        drawContext.drawCenteredTextWithShadow(MinecraftClient.getInstance().textRenderer, Text.literal(ticksLeft > 10 ? "§a"+ticksLeft : ticksLeft > 5 ? "§6"+ticksLeft : "§c"+ticksLeft), ConfigManager.getConfig().miscCategory.clearTickTimerHudX, ConfigManager.getConfig().miscCategory.clearTickTimerHudY, 0xFFFFFFFF);
        matrices.popMatrix();
    }

    private static boolean onChatMessage(Text text, boolean b) {
        Matcher matcher = Pattern.compile("\\w* is now ready.").matcher(text.getString());
        if (matcher.find()) inClear = true;
        matcher = Pattern.compile("(\\[BOSS] The Watcher: You have proven yourself\\. You may pass\\.|Sending to server .*\\.\\.\\.)").matcher(text.getString());
        if (matcher.find()) {
            inClear = false;
            timerStarted = false;
            prevSecond = "";
        }
        return true;
    }
}
