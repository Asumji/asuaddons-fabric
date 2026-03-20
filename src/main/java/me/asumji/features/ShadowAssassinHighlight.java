package me.asumji.features;

import io.github.notenoughupdates.moulconfig.ChromaColour;
import me.asumji.gui.config.ConfigManager;
import me.asumji.util.Rendering;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.phys.Vec3;
import java.awt.*;

public class ShadowAssassinHighlight {
    public static Player entity = null;
    public static final int ShadowAssassinBootsRGB = 6029470;

    public static void init() {
        WorldRenderEvents.BEFORE_TRANSLUCENT.register(ShadowAssassinHighlight::extractAndDrawWaypoint);
        ClientTickEvents.START_CLIENT_TICK.register(ShadowAssassinHighlight::tick);
    }

    private static void extractAndDrawWaypoint(WorldRenderContext context) {
        if (entity == null || !ConfigManager.getConfig().dungeonCategory.starredAccordion.saHighlight) return;
        float tickProgress = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(false);
        Vec3 entityPos = entity.getPosition(tickProgress);
        Color effectiveColor = ChromaColour.forLegacyString(ConfigManager.getConfig().dungeonCategory.starredAccordion.saColor).getEffectiveColour();
        if (entity.isInvisible()) Rendering.renderWaypoint(context, Rendering.FILLED, (float) (entityPos.x-0.35), (float) entityPos.y, (float) (entityPos.z-0.35), (float) (entityPos.x+0.35), (float) (entityPos.y+0.45), (float) (entityPos.z+0.35), effectiveColor.getRGB(), effectiveColor.getAlpha() / 255f);
        else Rendering.renderWaypoint(context, Rendering.FILLED, (float) (entityPos.x-0.35), (float) entityPos.y, (float) (entityPos.z-0.35), (float) (entityPos.x+0.35), (float) (entityPos.y+2), (float) (entityPos.z+0.35), effectiveColor.getRGB(), effectiveColor.getAlpha() / 255f);
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
