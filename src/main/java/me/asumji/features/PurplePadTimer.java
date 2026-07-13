package me.asumji.features;

import me.asumji.AsuAddons;
import me.asumji.gui.config.ConfigManager;
import me.asumji.util.Shortcuts;
import me.asumji.util.Variables;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PurplePadTimer {
    private static final DecimalFormat df = new DecimalFormat("0.00");
    private static boolean timerDone = false;
    private static boolean ticking = false;
    public static void init() {
        ClientReceiveMessageEvents.ALLOW_GAME.register(PurplePadTimer::onChatMessage);
        HudElementRegistry.attachElementBefore(VanillaHudElements.CHAT, Identifier.fromNamespaceAndPath(AsuAddons.MOD_ID, "purplepadhud"), PurplePadTimer::renderHud);
    }

    private static void renderHud(GuiGraphicsExtractor drawContext, DeltaTracker renderTickCounter) {
        if (Variables.TickTimers.containsKey("PurplePadTimer")) {
            int ticksLeft = Variables.TickTimers.get("PurplePadTimer");
            Matrix3x2fStack matrices = drawContext.pose();
            matrices.pushMatrix();
            matrices.scale(ConfigManager.getConfig().dungeonCategory.f7Accordion.purplePadTimerHudScale,ConfigManager.getConfig().dungeonCategory.f7Accordion.purplePadTimerHudScale);
            drawContext.centeredText(Minecraft.getInstance().font, Component.literal("§5"+df.format((float)ticksLeft/20)+"s"), ConfigManager.getConfig().dungeonCategory.f7Accordion.purplePadTimerHudX, ConfigManager.getConfig().dungeonCategory.f7Accordion.purplePadTimerHudY, 0xFFFFFFFF);
            matrices.popMatrix();
            if (ticksLeft == 0 && ticking) timerDone = true;
        }
        if (timerDone) {
            Shortcuts.displayTitle("§aCrush!", "", 10);
            ticking = false;
            timerDone = false;
        }
    }

    private static boolean onChatMessage(Component text, boolean b) {
        if (!ConfigManager.getConfig().dungeonCategory.f7Accordion.purplePadTimer) return true;
        Matcher matcher = Pattern.compile("\\[BOSS] Storm: (ENERGY HEED MY CALL|THUNDER LET ME BE YOUR CATALYST)!").matcher(text.getString());
        if (matcher.find()) {
            Variables.TickTimers.put("PurplePadTimer", 76);
            ticking = true;
            return true;
        }
        return true;
    }
}
