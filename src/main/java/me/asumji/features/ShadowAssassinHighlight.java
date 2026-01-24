package me.asumji.features;

import io.github.notenoughupdates.moulconfig.ChromaColour;
import me.asumji.gui.config.ConfigManager;
import me.asumji.util.Rendering;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;

import java.awt.*;

public class ShadowAssassinHighlight {
    public static PlayerEntity entity = null;
    public static final int ShadowAssassinBootsRGB = 6029470;

    public static void init() {
        WorldRenderEvents.BEFORE_TRANSLUCENT.register(ShadowAssassinHighlight::extractAndDrawWaypoint);
        ClientTickEvents.START_CLIENT_TICK.register(ShadowAssassinHighlight::tick);
    }

    private static void extractAndDrawWaypoint(WorldRenderContext context) {
        if (entity == null || !ConfigManager.getConfig().dungeonCategory.starredAccordion.saHighlight) return;
        float tickProgress = MinecraftClient.getInstance().getRenderTickCounter().getTickProgress(false);
        Vec3d entityPos = entity.getLerpedPos(tickProgress);
        Color effectiveColor = ChromaColour.forLegacyString(ConfigManager.getConfig().dungeonCategory.starredAccordion.saColor).getEffectiveColour();
        if (entity.isInvisible()) Rendering.renderWaypoint(context, Rendering.FILLED, (float) (entityPos.x-0.35), (float) entityPos.y, (float) (entityPos.z-0.35), (float) (entityPos.x+0.35), (float) (entityPos.y+0.45), (float) (entityPos.z+0.35), effectiveColor.getRGB(), effectiveColor.getAlpha() / 255f);
        else Rendering.renderWaypoint(context, Rendering.FILLED, (float) (entityPos.x-0.35), (float) entityPos.y, (float) (entityPos.z-0.35), (float) (entityPos.x+0.35), (float) (entityPos.y+2), (float) (entityPos.z+0.35), effectiveColor.getRGB(), effectiveColor.getAlpha() / 255f);
    }

    private static void tick(MinecraftClient minecraftClient) {
        if (entity != null && !entity.isAlive()) entity = null;
    }

    public static void loadEntity(PlayerEntity player) {
        ItemStack bootsStack = player.getInventory().getStack(36);
        if (bootsStack.isEmpty()) return;
        if (!bootsStack.toString().equals("1 minecraft:leather_boots")) return;
        DyedColorComponent color = bootsStack.getComponents().get(DataComponentTypes.DYED_COLOR);
        if (color == null || color.rgb() != ShadowAssassinBootsRGB) return;
        entity = player;
    }
}
