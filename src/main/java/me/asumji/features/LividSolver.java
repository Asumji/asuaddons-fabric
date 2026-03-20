package me.asumji.features;

import io.github.notenoughupdates.moulconfig.ChromaColour;
import me.asumji.gui.config.ConfigManager;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
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
        Entity correctLividEntity = Minecraft.getInstance().level.getEntity(correctLividId);
        if (correctLividEntity == null) {
            correctLividId = 0;
            return;
        }
        Color effectiveColor = ChromaColour.forLegacyString(ConfigManager.getConfig().dungeonCategory.f5Accordion.lividSolverColor).getEffectiveColour();
        renderWaypoint(context, FILLED, (float) (correctLividEntity.getX()-0.5), (float) (correctLividEntity.getY()), (float) (correctLividEntity.getZ()-0.5), (float) (correctLividEntity.getX()+0.5), (float) (correctLividEntity.getY()+2), (float) (correctLividEntity.getZ()+0.5), ConfigManager.getConfig().dungeonCategory.f5Accordion.lividSolverAdaptColor ? lividToColor.get(correctLividEntity.getName().getString()) : effectiveColor.getRGB(), effectiveColor.getAlpha() / 255f);

        float tickProgress = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(false);
        Vec3 eyeVector = Minecraft.getInstance().player.getPosition(tickProgress).add(Minecraft.getInstance().player.getViewVector(tickProgress));
        Vec3 lividPos = correctLividEntity.getPosition(tickProgress);
        if (ConfigManager.getConfig().dungeonCategory.f5Accordion.lividTracer) renderLine(context, FILLED, 0.02, (float) eyeVector.x, (float) (eyeVector.y + 1.61), (float) eyeVector.z, (float) lividPos.x, (float) (lividPos.y+1), (float) lividPos.z, ConfigManager.getConfig().dungeonCategory.f5Accordion.lividSolverAdaptColor ? lividToColor.get(correctLividEntity.getName().getString()) : effectiveColor.getRGB(), 1);
    }

    private static boolean onChatMessage(Component text, boolean b) {
        if (!text.getString().matches("\\[BOSS] Livid: Welcome, you've arrived right on time. I am Livid, the Master of Shadows.")) return true;
        spawning = true;
        return true;
    }

    public static void loadEntity(Player playerEntity) {
        if (playerEntity == null || !spawning || !Minecraft.getInstance().player.hasEffect(MobEffects.BLINDNESS) || !ConfigManager.getConfig().dungeonCategory.f5Accordion.lividSolver) return;
        String blockName = Minecraft.getInstance().level.getBlockState(new BlockPos(5, 110, 42)).getBlock().getName().getString();
        String correctLividName = blockToLivid.get(blockName);
        if (playerEntity.getName().getString().equals(correctLividName)) {
            correctLividId = playerEntity.getId();
            if (!ConfigManager.getConfig().dungeonCategory.f5Accordion.lividSolverMessage.isEmpty()) Minecraft.getInstance().player.connection.sendCommand("pc " + ConfigManager.getConfig().dungeonCategory.f5Accordion.lividSolverMessage.replace("{livid}", blockName.split(" ")[0]));
            spawning = false;
        }
    }
}
