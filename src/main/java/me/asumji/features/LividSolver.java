package me.asumji.features;

import io.github.notenoughupdates.moulconfig.ChromaColour;
import me.asumji.gui.config.ConfigManager;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import static me.asumji.util.Rendering.*;

public class LividSolver {
    public static boolean spawning = false;
    public static Map<String, String> blockToLivid = new HashMap<>();
    public static Map<String, Color> lividToColor = new HashMap<>();
    public static int correctLividId = 0;

    public static void init() {
        ClientReceiveMessageEvents.ALLOW_GAME.register(LividSolver::onChatMessage);
        LevelRenderEvents.BEFORE_TRANSLUCENT_TERRAIN.register(LividSolver::extractAndDrawWaypoint);

        blockToLivid.put("Red Wool", "Hockey Livid");
        blockToLivid.put("Yellow Wool", "Arcade Livid");
        blockToLivid.put("Lime Wool", "Smile Livid");
        blockToLivid.put("Green Wool", "Frog Livid");
        blockToLivid.put("Blue Wool", "Scream Livid");
        blockToLivid.put("Magenta Wool", "Crossed Livid");
        blockToLivid.put("Purple Wool", "Purple Livid");
        blockToLivid.put("Gray Wool", "Doctor Livid");
        blockToLivid.put("White Wool", "Vendetta Livid");

        lividToColor.put("Hockey Livid", new Color(0xFF5555));
        lividToColor.put("Arcade Livid", new Color(0xFFFF55));
        lividToColor.put("Smile Livid", new Color(0x55FF55));
        lividToColor.put("Frog Livid", new Color(0x00AA00));
        lividToColor.put("Scream Livid", new Color(0x5555FF));
        lividToColor.put("Crossed Livid", new Color(0xFF55FF));
        lividToColor.put("Purple Livid", new Color(0xAA00AA));
        lividToColor.put("Doctor Livid", new Color(0xAAAAAA));
        lividToColor.put("Vendetta Livid", new Color(0xFFFFFF));
    }

    private static void extractAndDrawWaypoint(LevelRenderContext context) {
        Entity correctLividEntity = Minecraft.getInstance().level.getEntity(correctLividId);
        if (correctLividEntity == null) {
            correctLividId = 0;
            return;
        }
        Color effectiveColor = ChromaColour.forLegacyString(ConfigManager.getConfig().dungeonCategory.f5Accordion.lividSolverColor).getEffectiveColour();
        renderWaypoint(new AABB(correctLividEntity.getX()-0.5, correctLividEntity.getY(), correctLividEntity.getZ()-0.5, correctLividEntity.getX()+0.5, correctLividEntity.getY()+2, correctLividEntity.getZ()+0.5), ConfigManager.getConfig().dungeonCategory.f5Accordion.lividSolverAdaptColor ? lividToColor.get(correctLividEntity.getName().getString()) : effectiveColor);

        float tickProgress = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(false);
        Vec3 eyeVector = Minecraft.getInstance().player.getPosition(tickProgress).add(Minecraft.getInstance().player.getViewVector(tickProgress));
        Vec3 lividPos = correctLividEntity.getPosition(tickProgress);
        if (ConfigManager.getConfig().dungeonCategory.f5Accordion.lividTracer) renderLine(4F, new Vec3(eyeVector.x, eyeVector.y + 1.61, eyeVector.z), new Vec3(lividPos.x, lividPos.y+1, lividPos.z), ConfigManager.getConfig().dungeonCategory.f5Accordion.lividSolverAdaptColor ? lividToColor.get(correctLividEntity.getName().getString()) : effectiveColor);
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
