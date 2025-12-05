package me.asumji.features;

import io.github.notenoughupdates.moulconfig.ChromaColour;
import me.asumji.AsuAddons;
import me.asumji.gui.config.ConfigManager;
import me.asumji.util.Rendering;
import me.asumji.util.Shortcuts;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static me.asumji.util.Rendering.*;

public class WitherHitbox {
    public static Entity witherEntity;

    public static void init() {
        WorldRenderEvents.BEFORE_TRANSLUCENT.register(WitherHitbox::extractAndDrawWaypoint);
        ClientEntityEvents.ENTITY_LOAD.register(WitherHitbox::loadEntity);
        ClientTickEvents.START_CLIENT_TICK.register(WitherHitbox::tick);
    }

    private static void tick(MinecraftClient minecraftClient) {
        if (witherEntity != null && !witherEntity.isAlive()) witherEntity = null;
    }

    private static void loadEntity(Entity entity, ClientWorld clientWorld) {
        Executors.newSingleThreadScheduledExecutor().schedule(() -> {
            if (entity instanceof ArmorStandEntity && Objects.requireNonNull(entity.getCustomName()).getString().matches("(﴾ ☠♃ Maxor ﴿|﴾ ☠♃ Storm ﴿|﴾ ☠♃ Goldor ﴿|﴾ ☠♃ Necron ﴿)")) witherEntity = entity;
        }, 100, TimeUnit.MILLISECONDS);
    }

    private static void extractAndDrawWaypoint(WorldRenderContext context) {
        if (witherEntity == null || !ConfigManager.getConfig().dungeonCategory.witherHitbox) return;
        AsuAddons.LOGGER.info(String.valueOf(witherEntity.isAlive()));
        Color effectiveColor = ChromaColour.forLegacyString(ConfigManager.getConfig().dungeonCategory.witherHitboxColor).getEffectiveColour();
        renderWaypoint(context, FILLED, (float) (witherEntity.getX()-0.5), (float) (witherEntity.getY()-3.5), (float) (witherEntity.getZ()-0.5), (float) (witherEntity.getX()+0.5), (float) (witherEntity.getY()), (float) (witherEntity.getZ()+0.5), effectiveColor.getRGB(), (float) effectiveColor.getAlpha() / 255);
    }
}