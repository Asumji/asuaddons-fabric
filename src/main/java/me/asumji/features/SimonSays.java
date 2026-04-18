package me.asumji.features;

import me.asumji.AsuAddons;
import me.asumji.gui.config.ConfigManager;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import org.apache.commons.lang3.ArrayUtils;
import org.joml.Matrix3x2fStack;

import java.awt.*;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static me.asumji.util.Rendering.renderWaypoint;

public class SimonSays {
    public static boolean inP3S1 = false;
    public static BlockPos[] buttonOrder = {};
    public static int stage = 0;
    public static int displayStage = 0;
    public static boolean clicked;
    public static boolean lastButtonWasStart = false;

    public static void init() {
        ClientReceiveMessageEvents.ALLOW_GAME.register(SimonSays::onChatMessage);
        WorldRenderEvents.BEFORE_TRANSLUCENT.register(SimonSays::extractAndDrawWaypoint);
        ClientTickEvents.END_CLIENT_TICK.register(SimonSays::tick);
        HudElementRegistry.attachElementBefore(VanillaHudElements.CHAT, Identifier.fromNamespaceAndPath(AsuAddons.MOD_ID, "sshud"), SimonSays::renderHud);
    }

    private static void renderHud(GuiGraphics drawContext, DeltaTracker renderTickCouter) {
        if (!inP3S1 || !ConfigManager.getConfig().dungeonCategory.f7Accordion.simonSaysHud) return;
        Matrix3x2fStack matrices = drawContext.pose();
        matrices.pushMatrix();
        matrices.scale(ConfigManager.getConfig().dungeonCategory.f7Accordion.simonSaysHudScale);
        drawContext.drawCenteredString(Minecraft.getInstance().font, (displayStage == 0 ? "§c" : "§a")+"SS "+displayStage+"/5", ConfigManager.getConfig().dungeonCategory.f7Accordion.simonSaysHudX, ConfigManager.getConfig().dungeonCategory.f7Accordion.simonSaysHudY, 0xFFFFFFFF);
        matrices.popMatrix();
    }

    private static void tick(Minecraft minecraft) {
        clicked = false;
    }

    private static void extractAndDrawWaypoint(WorldRenderContext context) {
        if (!ConfigManager.getConfig().dungeonCategory.f7Accordion.simonSaysSolver || !inP3S1) return;
        for (BlockPos pos : buttonOrder) {
            switch (ArrayUtils.indexOf(buttonOrder, pos)) {
                case 0 -> renderWaypoint(new AABB(pos.getX(), pos.getY()+0.35, pos.getZ()+0.3, pos.getX()-0.15, pos.getY()+0.65, pos.getZ()+0.7), new Color(0x5500FF00, true));
                case 1 -> renderWaypoint(new AABB(pos.getX(), pos.getY()+0.35, pos.getZ()+0.3, pos.getX()-0.15, pos.getY()+0.65, pos.getZ()+0.7), new Color(0x55FFFF00, true));
                default -> renderWaypoint(new AABB(pos.getX(), pos.getY()+0.35, pos.getZ()+0.3, pos.getX()-0.15, pos.getY()+0.65, pos.getZ()+0.7), new Color(0x55FF0000, true));
            }
        }
    }

    private static boolean onChatMessage(Component text, boolean b) {
       Matcher matcher = Pattern.compile("\\[BOSS] Storm: At least my son died by your hands\\.").matcher(text.getString());
        if (matcher.find()) {
            inP3S1 = true;
            displayStage = 0;
        }
        matcher = Pattern.compile("(\\(7/7\\)|\\(8/8\\))").matcher(text.getString());
        if (matcher.find() && inP3S1) {
            inP3S1 = false;
            displayStage = 0;
        }
        matcher = Pattern.compile("Sending to server .*\\.\\.\\.").matcher(text.getString());
        if (matcher.find()) {
            inP3S1 = false;
            displayStage = 0;
        }
        return true;
    }

    public static void blockUpdate(BlockPos pos, BlockState state) {
        if (pos.getZ() <= 95 && pos.getZ() >= 92 && pos.getY() >= 120 && pos.getY() <= 123) AsuAddons.LOGGER.info("Block update: " + state.getBlock().toString() + " | " + pos.toShortString());
        if (!inP3S1 || Minecraft.getInstance().player == null || !ConfigManager.getConfig().dungeonCategory.f7Accordion.simonSaysTracker) return;
        if (state.getBlock().equals(Blocks.STONE_BUTTON) && !clicked) {
            clicked = true;
            if (pos.getX() != 110) return;
            if (pos.getZ() <= 95 && pos.getZ() >= 92 && pos.getY() >= 120 && pos.getY() <= 123) {
                lastButtonWasStart = false;
                if (buttonOrder.length == 0) return;
                AsuAddons.LOGGER.info(buttonOrder[0] + " | " + new BlockPos(pos.getX()+1, pos.getY(), pos.getZ()));
                if (buttonOrder[0].equals(new BlockPos(pos.getX()+1, pos.getY(), pos.getZ()))) {
                    buttonOrder = ArrayUtils.remove(buttonOrder, 0);
                    if (buttonOrder.length == 0) {
                        Minecraft.getInstance().player.connection.sendCommand("pc SS " + stage + "/5!");
                        displayStage = stage;
                    }
                } else {
                    buttonOrder = new BlockPos[]{};
                    stage = 0;
                    displayStage = 0;
                    Minecraft.getInstance().player.connection.sendCommand("pc SS Failed!");
                }
            } else if (pos.equals(new BlockPos(110, 121, 91))) {
                lastButtonWasStart = true;
                Minecraft.getInstance().player.connection.sendCommand("pc SS Started!");
                stage = 0;
                displayStage = 0;
                buttonOrder = new BlockPos[]{};
            }
        } else if (state.getBlock().equals(Blocks.SEA_LANTERN)) {
            if (pos.getX() != 111) return;
            if (pos.getY() < 120 || pos.getY() > 123 || pos.getZ() < 92 || pos.getZ() > 95) return;
            buttonOrder = ArrayUtils.add(buttonOrder, new BlockPos(pos.getX(), pos.getY(), pos.getZ()));
            stage = buttonOrder.length;
        } else if (state.getBlock().equals(Blocks.AIR) && pos.getZ() <= 95 && pos.getZ() >= 92 && pos.getY() >= 120 && pos.getY() <= 123 && pos.getX() == 110 && !lastButtonWasStart) {
            buttonOrder = new BlockPos[]{};
            stage = 0;
            displayStage = 0;
            Minecraft.getInstance().player.connection.sendCommand("pc SS Failed!");
        }
    }
}
