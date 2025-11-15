package me.asumji.features;

import me.asumji.AsuAddons;
import me.asumji.gui.config.ConfigManager;
import me.asumji.util.Shortcuts;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.GiantEntity;
import net.minecraft.text.Text;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BloodGiant {
    public static boolean watcherSpawning = true;
    public static void init() {
        ClientEntityEvents.ENTITY_LOAD.register(BloodGiant::loadEntity);
        ClientReceiveMessageEvents.ALLOW_GAME.register(BloodGiant::onChatMessage);
    }

    private static boolean onChatMessage(Text text, boolean b) {
        Matcher matcher = Pattern.compile("The BLOOD DOOR has been opened!").matcher(text.getString());
        if (matcher.find()) watcherSpawning = true;
        matcher = Pattern.compile("(\\[BOSS] The Watcher: You have proven yourself\\. You may pass\\.|Sending to server .*\\.\\.\\.)").matcher(text.getString());
        if (matcher.find()) watcherSpawning = false;
        return true;
    }

    private static void loadEntity(Entity entity, ClientWorld clientWorld) {
        if (!(entity instanceof GiantEntity giant) || !ConfigManager.getConfig().dungeonCategory.diamanteAlert || !watcherSpawning) return;
        Executors.newSingleThreadScheduledExecutor().schedule(() -> {
            if (giant.getEquippedStack(EquipmentSlot.CHEST).toString().equals("1 minecraft:diamond_chestplate")) {
                Shortcuts.sendClientMessage(Text.of(AsuAddons.MOD_PREFIX + "§bDiamante Giant detected."));
            }
        }, 100, TimeUnit.MILLISECONDS);
   }
}
