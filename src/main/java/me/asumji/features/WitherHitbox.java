package me.asumji.features;

import io.github.notenoughupdates.moulconfig.ChromaColour;
import me.asumji.gui.config.ConfigManager;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.phys.AABB;

import java.awt.*;

import static me.asumji.util.Rendering.*;

public class WitherHitbox {
    public static Entity witherEntity;

    public static void init() {
        LevelRenderEvents.BEFORE_TRANSLUCENT_TERRAIN.register(WitherHitbox::extractAndDrawWaypoint);
        ClientTickEvents.START_CLIENT_TICK.register(WitherHitbox::tick);
    }

    private static void tick(Minecraft minecraftClient) {
        if (witherEntity != null && !witherEntity.isAlive()) witherEntity = null;
    }

    private static void extractAndDrawWaypoint(LevelRenderContext context) {
        if (witherEntity == null || !ConfigManager.getConfig().dungeonCategory.f7Accordion.witherHitbox) return;
        Color effectiveColor = ChromaColour.forLegacyString(ConfigManager.getConfig().dungeonCategory.f7Accordion.witherHitboxColor).getEffectiveColour();
        renderWaypoint(new AABB(witherEntity.getX()-0.5, witherEntity.getY()-3.5, witherEntity.getZ()-0.5, witherEntity.getX()+0.5, witherEntity.getY(), witherEntity.getZ()+0.5), effectiveColor);
    }

    public static void loadEntity(ArmorStand stand) {
        if (!stand.hasCustomName() || !stand.isInvisible() || !stand.isCustomNameVisible()) return;
        if (!stand.getCustomName().getString().matches("(﴾ ☠♃ Maxor ﴿|﴾ ☠♃ Storm ﴿|﴾ ☠♃ Goldor ﴿|﴾ ☠♃ Necron ﴿)")) return;
        witherEntity = stand;
    }
}