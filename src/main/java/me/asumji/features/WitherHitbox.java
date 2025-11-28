package me.asumji.features;

import io.github.notenoughupdates.moulconfig.ChromaColour;
import me.asumji.gui.config.ConfigManager;
import me.asumji.util.Shortcuts;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
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
    }

    private static void loadEntity(Entity entity, ClientWorld clientWorld) {
        Executors.newSingleThreadScheduledExecutor().schedule(() -> {
            if (entity.getCustomName().getString().matches("(﴾ ☠♃ Maxor ﴿|﴾ ☠♃ Storm ﴿|﴾ ☠♃ Goldor ﴿|﴾ ☠♃ Necron ﴿)")) witherEntity = entity;
        }, 100, TimeUnit.MILLISECONDS);
    }

    private static void extractAndDrawWaypoint(WorldRenderContext context) {
        if (witherEntity == null || !ConfigManager.getConfig().dungeonCategory.witherHitbox) return;
        Color effectiveColor = ChromaColour.forLegacyString(ConfigManager.getConfig().dungeonCategory.witherHitboxColor).getEffectiveColour();
        renderWaypoint(context, FILLED, (float) (witherEntity.getX()-0.5), (float) (witherEntity.getY()-3.5), (float) (witherEntity.getZ()-0.5), (float) (witherEntity.getX()+0.5), (float) (witherEntity.getY()), (float) (witherEntity.getZ()+0.5), effectiveColor.getRGB(), (float) effectiveColor.getAlpha() / 255);
    }
}