package me.asumji.features;

import io.github.notenoughupdates.moulconfig.ChromaColour;
import me.asumji.gui.config.ConfigManager;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import static me.asumji.util.Rendering.*;

public class LividSolver {
    public static boolean spawning = false;
    public static Map<String, String> blockToLivid = new HashMap<>();
    public static Map<String, Integer> lividToColor = new HashMap<>();
    public static int correctLividId = 0;

    public static void init() {
        ClientReceiveMessageEvents.ALLOW_GAME.register(LividSolver::onChatMessage);
        WorldRenderEvents.BEFORE_TRANSLUCENT.register(LividSolver::extractAndDrawWaypoint);

        blockToLivid.put("Red Wool", "Hockey Livid");
        blockToLivid.put("Yellow Wool", "Arcade Livid");
        blockToLivid.put("Lime Wool", "Smile Livid");
        blockToLivid.put("Green Wool", "Frog Livid");
        blockToLivid.put("Blue Wool", "Scream Livid");
        blockToLivid.put("Magenta Wool", "Crossed Livid");
        blockToLivid.put("Purple Wool", "Purple Livid");
        blockToLivid.put("Gray Wool", "Doctor Livid");
        blockToLivid.put("White Wool", "Vendetta Livid");

        lividToColor.put("Hockey Livid", 0xFF5555);
        lividToColor.put("Arcade Livid", 0xFFFF55);
        lividToColor.put("Smile Livid", 0x55FF55);
        lividToColor.put("Frog Livid", 0x00AA00);
        lividToColor.put("Scream Livid", 0x5555FF);
        lividToColor.put("Crossed Livid", 0xFF55FF);
        lividToColor.put("Purple Livid", 0xAA00AA);
        lividToColor.put("Doctor Livid", 0xAAAAAA);
        lividToColor.put("Vendetta Livid", 0xFFFFFF);
    }

    private static void extractAndDrawWaypoint(WorldRenderContext context) {
        Entity correctLividEntity = MinecraftClient.getInstance().world.getEntityById(correctLividId);
        if (correctLividEntity == null) {
            correctLividId = 0;
            return;
        }
        Color effectiveColor = ChromaColour.forLegacyString(ConfigManager.getConfig().dungeonCategory.f5Accordion.lividSolverColor).getEffectiveColour();
        renderWaypoint(context, FILLED, (float) (correctLividEntity.getX()-0.5), (float) (correctLividEntity.getY()), (float) (correctLividEntity.getZ()-0.5), (float) (correctLividEntity.getX()+0.5), (float) (correctLividEntity.getY()+2), (float) (correctLividEntity.getZ()+0.5), ConfigManager.getConfig().dungeonCategory.f5Accordion.lividSolverAdaptColor ? lividToColor.get(correctLividEntity.getName().getString()) : effectiveColor.getRGB(), (float) effectiveColor.getAlpha() / 255);

        float tickProgress = MinecraftClient.getInstance().getRenderTickCounter().getTickProgress(false);
        Vec3d eyeVector = MinecraftClient.getInstance().player.getLerpedPos(tickProgress).add(MinecraftClient.getInstance().player.getRotationVec(tickProgress));
        Vec3d lividPos = correctLividEntity.getLerpedPos(tickProgress);
        if (ConfigManager.getConfig().dungeonCategory.f5Accordion.lividTracer) renderLine(context, FILLED, 0.02, (float) eyeVector.x, (float) (eyeVector.y + 1.61), (float) eyeVector.z, (float) lividPos.x, (float) (lividPos.y+1), (float) lividPos.z, ConfigManager.getConfig().dungeonCategory.f5Accordion.lividSolverAdaptColor ? lividToColor.get(correctLividEntity.getName().getString()) : effectiveColor.getRGB(), 1);
    }

    private static boolean onChatMessage(Text text, boolean b) {
        if (!text.getString().matches("\\[BOSS] Livid: Welcome, you've arrived right on time. I am Livid, the Master of Shadows.")) return true;
        spawning = true;
        return true;
    }

    public static void loadEntity(PlayerEntity playerEntity) {
        if (playerEntity == null || !spawning || !MinecraftClient.getInstance().player.hasStatusEffect(StatusEffects.BLINDNESS) || !ConfigManager.getConfig().dungeonCategory.f5Accordion.lividSolver) return;
        String blockName = MinecraftClient.getInstance().world.getBlockState(new BlockPos(5, 110, 42)).getBlock().getName().getString();
        String correctLividName = blockToLivid.get(blockName);
        if (playerEntity.getName().getString().equals(correctLividName)) {
            correctLividId = playerEntity.getId();
            if (!ConfigManager.getConfig().dungeonCategory.f5Accordion.lividSolverMessage.isEmpty()) MinecraftClient.getInstance().player.networkHandler.sendChatCommand("pc " + ConfigManager.getConfig().dungeonCategory.f5Accordion.lividSolverMessage.replace("{livid}", blockName.split(" ")[0]));
            spawning = false;
        }
    }
}
