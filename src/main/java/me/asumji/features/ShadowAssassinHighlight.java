package me.asumji.features;

import io.github.notenoughupdates.moulconfig.ChromaColour;
import me.asumji.AsuAddons;
import me.asumji.gui.config.ConfigManager;
import me.asumji.util.Rendering;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import java.awt.*;

public class ShadowAssassinHighlight {
    public static Player entity = null;
    public static final int ShadowAssassinBootsRGB = 6029470;

    public static void init() {
        LevelRenderEvents.BEFORE_TRANSLUCENT_TERRAIN.register(ShadowAssassinHighlight::extractAndDrawWaypoint);
        ClientTickEvents.START_CLIENT_TICK.register(ShadowAssassinHighlight::tick);
    }

    private static void extractAndDrawWaypoint(LevelRenderContext context) {
        if (entity == null || !ConfigManager.getConfig().dungeonCategory.starredAccordion.saHighlight) return;
        float tickProgress = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(false);
        Vec3 entityPos = entity.getPosition(tickProgress);
        Color effectiveColor = ChromaColour.forLegacyString(ConfigManager.getConfig().dungeonCategory.starredAccordion.saColor).getEffectiveColour();
        if (entity.isInvisible()) Rendering.renderWaypoint(new AABB(entityPos.x-0.35, entityPos.y, entityPos.z-0.35, entityPos.x+0.35, entityPos.y+0.45, entityPos.z+0.35), effectiveColor);
        else Rendering.renderWaypoint(new AABB(entityPos.x-0.35, entityPos.y, entityPos.z-0.35, entityPos.x+0.35, entityPos.y+2, entityPos.z+0.35), effectiveColor);
    }

    private static void tick(Minecraft minecraftClient) {
        if (entity != null && !entity.isAlive()) entity = null;
    }

    public static void loadEntity(Player player) {
        ItemStack bootsStack = player.getInventory().getItem(36);
        if (bootsStack.isEmpty()) return;
        if (!bootsStack.toString().equals("1 minecraft:leather_boots")) return;
        DyedItemColor color = bootsStack.getComponents().get(DataComponents.DYED_COLOR);
        if (color == null || color.rgb() != ShadowAssassinBootsRGB) return;
        entity = player;
    }
}
