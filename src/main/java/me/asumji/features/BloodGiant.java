package me.asumji.features;

import me.asumji.AsuAddons;
import me.asumji.gui.config.ConfigManager;
import me.asumji.util.Shortcuts;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.GiantEntity;
import net.minecraft.text.Text;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BloodGiant {
    public static boolean watcherSpawning = false;
    public static boolean diamanteAlert = false;
    public static void init() {
        ClientReceiveMessageEvents.ALLOW_GAME.register(BloodGiant::onChatMessage);
    }

    private static boolean onChatMessage(Text text, boolean b) {
        Matcher matcher = Pattern.compile("The BLOOD DOOR has been opened!").matcher(text.getString());
        if (matcher.find()) watcherSpawning = true;
        matcher = Pattern.compile("(\\[BOSS] The Watcher: You have proven yourself\\. You may pass\\.|Sending to server .*\\.\\.\\.)").matcher(text.getString());
        if (matcher.find()) watcherSpawning = false;
        return true;
    }

    public static void loadEntity(GiantEntity giant) {
        if (!ConfigManager.getConfig().dungeonCategory.diamanteAlert || !watcherSpawning) return;
        if (!giant.getEquippedStack(EquipmentSlot.CHEST).toString().equals("1 minecraft:diamond_chestplate")) return;
        Shortcuts.displayTitle(Text.literal("§bDiamante detected"), Text.empty(), 0, 20, 0);
        Shortcuts.sendClientMessage(Text.literal(AsuAddons.MOD_PREFIX + "§bDiamante Giant detected."));
    }
}