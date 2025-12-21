package me.asumji.features;

import io.github.notenoughupdates.moulconfig.ChromaColour;
import me.asumji.gui.config.ConfigManager;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;

import java.awt.*;

import static me.asumji.util.Rendering.*;

public class WitherHitbox {
    public static Entity witherEntity;

    public static void init() {
        WorldRenderEvents.BEFORE_TRANSLUCENT.register(WitherHitbox::extractAndDrawWaypoint);
        ClientTickEvents.START_CLIENT_TICK.register(WitherHitbox::tick);
    }

    private static void tick(MinecraftClient minecraftClient) {
        if (witherEntity != null && !witherEntity.isAlive()) witherEntity = null;
    }

    private static void extractAndDrawWaypoint(WorldRenderContext context) {
        if (witherEntity == null || !ConfigManager.getConfig().dungeonCategory.f7Accordion.witherHitbox) return;
        Color effectiveColor = ChromaColour.forLegacyString(ConfigManager.getConfig().dungeonCategory.f7Accordion.witherHitboxColor).getEffectiveColour();
        renderWaypoint(context, FILLED, (float) (witherEntity.getX()-0.5), (float) (witherEntity.getY()-3.5), (float) (witherEntity.getZ()-0.5), (float) (witherEntity.getX()+0.5), (float) (witherEntity.getY()), (float) (witherEntity.getZ()+0.5), effectiveColor.getRGB(), (float) effectiveColor.getAlpha() / 255);
    }

    public static void loadEntity(ArmorStandEntity stand) {
        if (!stand.hasCustomName() || !stand.isInvisible() || !stand.isCustomNameVisible()) return;
        if (!stand.getCustomName().getString().matches("(﴾ ☠♃ Maxor ﴿|﴾ ☠♃ Storm ﴿|﴾ ☠♃ Goldor ﴿|﴾ ☠♃ Necron ﴿)")) return;
        witherEntity = stand;
    }
}