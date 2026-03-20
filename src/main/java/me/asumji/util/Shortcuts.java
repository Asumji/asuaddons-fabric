package me.asumji.util;

import me.asumji.AsuAddons;
import me.asumji.gui.config.ConfigManager;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundClearTitlesPacket;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.resources.Identifier;
import org.apache.commons.lang3.ArrayUtils;

public class Shortcuts {
    public static Component[] messages = {};
    public static AUTitle[] titles = {};

    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(Shortcuts::tick);
        HudElementRegistry.attachElementBefore(VanillaHudElements.CHAT, Identifier.fromNamespaceAndPath(AsuAddons.MOD_ID, "titlehud"), Shortcuts::renderHud);
    }

    private static void renderHud(GuiGraphics context, DeltaTracker renderTickCounter) {
        if (Minecraft.getInstance().player == null) return;
        float scale = ConfigManager.getConfig().mainCategory.titleAccordion.titleHudScale;
        int x = ConfigManager.getConfig().mainCategory.titleAccordion.titleHudX;
        int y = ConfigManager.getConfig().mainCategory.titleAccordion.titleHudY;

        int offset = 0;
        for (AUTitle title : titles) {
            if (title.getStayTicks() <= 0) titles = ArrayUtils.remove(titles, ArrayUtils.indexOf(titles, title));
            context.pose().pushMatrix();
            context.pose().scale(scale);
            context.drawCenteredString(Minecraft.getInstance().font, title.getTitle(), x, y + offset, 0xFFFFFFFF);
            context.pose().popMatrix();
            offset += 10;

            if (title.getSubtitle().isEmpty()) continue;
            context.pose().pushMatrix();
            context.pose().scale(scale/2);
            context.drawCenteredString(Minecraft.getInstance().font, title.getSubtitle(), x * 2, (y + offset) * 2, 0xFFFFFFFF);
            context.pose().popMatrix();
            offset += 10;
        }
    }

    private static void tick(Minecraft minecraftClient) {
        if (Minecraft.getInstance().player == null) return;
        for (AUTitle title : titles) {
            title.setStayTicks(title.getStayTicks() - 1);
        }
        if (messages.length == 0) return;
        Component[] tempMsgs = messages;
        messages = new Component[]{};
        for (Component message : tempMsgs) {
            Minecraft.getInstance().player.displayClientMessage(message, false);
        }
    }

    public static void queueClientMessage(Component message) {
        messages = ArrayUtils.add(messages, message);
    }

    public static void displayTitle(String title, String subtitle, int stayTicks) {
        if (!ConfigManager.getConfig().mainCategory.titleAccordion.customTitles) {
            if (Minecraft.getInstance().getConnection() == null) return;
            Minecraft.getInstance().getConnection().handleTitlesClear(new ClientboundClearTitlesPacket(true));
            Minecraft.getInstance().getConnection().setTitlesAnimation(new ClientboundSetTitlesAnimationPacket(0, stayTicks, 0));
            Minecraft.getInstance().getConnection().setTitleText(new ClientboundSetTitleTextPacket(Component.literal(title)));
            Minecraft.getInstance().getConnection().setSubtitleText(new ClientboundSetSubtitleTextPacket(Component.literal(subtitle)));
        } else {
            if (ConfigManager.getConfig().mainCategory.titleAccordion.stackingTitles) titles = ArrayUtils.add(titles, new AUTitle(title, subtitle, stayTicks));
            else titles = new AUTitle[]{new AUTitle(title, subtitle, stayTicks)};
        }
    }

    public static int getChatWidth() {
        return ChatComponent.getWidth(Minecraft.getInstance().options.chatWidth().get());
    }

    //Credit to ctjs
    public static String getChatBreak(String str) {
        int length = Minecraft.getInstance().font.width(str);
        int times = getChatWidth() / length;
        return str.repeat(times);
    }

    private static class AUTitle {
        private final String title;
        private final String subtitle;
        private int stayTicks;

        public AUTitle(String title, String subtitle, int stayTicks) {
            this.title = title;
            this.subtitle = subtitle;
            this.stayTicks = stayTicks;
        }

        public String getTitle() { return title; }
        public void setStayTicks(int ticks) { stayTicks = ticks; }
        public String getSubtitle() { return subtitle; }
        public int getStayTicks() { return stayTicks; }
    }
}
