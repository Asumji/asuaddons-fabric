package me.asumji.features;

import me.asumji.AsuAddons;
import me.asumji.gui.config.ConfigManager;
import me.asumji.util.Shortcuts;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;

import java.awt.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static me.asumji.util.Rendering.*;

public class WitherHitbox {
    public static Entity witherEntity;

    public static void init() {
        WorldRenderEvents.AFTER_TRANSLUCENT.register(WitherHitbox::extractAndDrawWaypoint);
        ClientEntityEvents.ENTITY_LOAD.register(WitherHitbox::loadEntity);
        //ClientEntityEvents.ENTITY_UNLOAD.register(WitherHitbox::unloadEntity);
    }

    //private static void unloadEntity(Entity entity, ClientWorld clientWorld) {
        //witherEntity = null;
    //}

    private static void loadEntity(Entity entity, ClientWorld clientWorld) {
        Executors.newSingleThreadScheduledExecutor().schedule(() -> {
            if (entity.getCustomName().getString().matches("(﴾ ☠♃ Maxor ﴿|﴾ ☠♃ Storm ﴿|﴾ ☠♃ Goldor ﴿|﴾ ☠♃ Necron ﴿)")) {
                Shortcuts.sendClientMessage(Text.literal(entity.getType().toString()));
                witherEntity = entity;
            }
        }, 100, TimeUnit.MILLISECONDS);
    }

    private static void extractAndDrawWaypoint(WorldRenderContext context) {
        if (witherEntity == null || !ConfigManager.getConfig().dungeonCategory.witherHitbox) return;
        Color effectiveColor = ConfigManager.getConfig().dungeonCategory.witherHitboxColor.getEffectiveColour();
        renderWaypoint(context, FILLED, (float) (witherEntity.getX()-0.5), (float) (witherEntity.getY()-3.5), (float) (witherEntity.getZ()-0.5), (float) (witherEntity.getX()+0.5), (float) (witherEntity.getY()), (float) (witherEntity.getZ()+0.5), effectiveColor.getRGB(), (float) effectiveColor.getAlpha() / 255);
    }
}