package me.asumji.features;

import me.asumji.AsuAddons;
import me.asumji.gui.config.ConfigManager;
import me.asumji.util.Variables;
import me.asumji.util.Shortcuts;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.joml.Matrix3x2fStack;

import java.text.DecimalFormat;

public class LividTimer {
    private static final DecimalFormat df = new DecimalFormat("0.00");

    private static boolean timerDone = false;
    public static void init() {
        ClientReceiveMessageEvents.ALLOW_GAME.register(LividTimer::onChatMessage);
        HudElementRegistry.attachElementBefore(VanillaHudElements.CHAT, Identifier.fromNamespaceAndPath(AsuAddons.MOD_ID, "lividhud"), LividTimer::renderHud);
    }

    private static void renderHud(GuiGraphicsExtractor drawContext, DeltaTracker renderTickCounter) {
        if (Variables.TickTimers.get("LividTimer") == null) return;
        int ticksLeft = Variables.TickTimers.get("LividTimer");
        Matrix3x2fStack matrices = drawContext.pose();
        matrices.pushMatrix();
        matrices.scale(ConfigManager.getConfig().dungeonCategory.f5Accordion.lividTimerHudScale);
        drawContext.centeredText(Minecraft.getInstance().font, Component.literal(df.format((float)ticksLeft/20)+"s"), ConfigManager.getConfig().dungeonCategory.f5Accordion.lividTimerHudX, ConfigManager.getConfig().dungeonCategory.f5Accordion.lividTimerHudY, 0xFFFFFFFF);
        matrices.popMatrix();
        if (ticksLeft == 0 && !timerDone) {
            timerDone = true;
            Shortcuts.displayTitle("§cLivid is vulnerable", "", 20);
            if (!ConfigManager.getConfig().dungeonCategory.f5Accordion.lividTimerMessage.isEmpty())
                Minecraft.getInstance().player.connection.sendCommand("pc " + ConfigManager.getConfig().dungeonCategory.f5Accordion.lividTimerMessage);
        }
    }

    private static boolean onChatMessage(Component text, boolean bool) {
        if (!text.getString().matches("\\[BOSS] Livid: Welcome, you've arrived right on time. I am Livid, the Master of Shadows.") || !ConfigManager.getConfig().dungeonCategory.f5Accordion.lividTimer) return true;
        Variables.TickTimers.put("LividTimer",390);
        timerDone = false;
        return true;
    }
}
