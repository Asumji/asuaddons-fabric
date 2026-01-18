package me.asumji.util;

import me.asumji.AsuAddons;
import me.asumji.gui.config.ConfigManager;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.network.packet.s2c.play.ClearTitleS2CPacket;
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleFadeS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.ArrayUtils;

public class Shortcuts {
    public static Text[] messages = {};
    public static AUTitle[] titles = {};

    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(Shortcuts::tick);
        HudElementRegistry.attachElementBefore(VanillaHudElements.CHAT, Identifier.of(AsuAddons.MOD_ID, "titlehud"), Shortcuts::renderHud);
    }

    private static void renderHud(DrawContext context, RenderTickCounter renderTickCounter) {
        if (MinecraftClient.getInstance().player == null) return;
        float scale = ConfigManager.getConfig().mainCategory.titleAccordion.titleHudScale;
        int x = ConfigManager.getConfig().mainCategory.titleAccordion.titleHudX;
        int y = ConfigManager.getConfig().mainCategory.titleAccordion.titleHudY;

        int offset = 0;
        for (AUTitle title : titles) {
            context.getMatrices().pushMatrix();
            context.getMatrices().scale(scale);
            context.drawCenteredTextWithShadow(MinecraftClient.getInstance().textRenderer, title.getTitle(), x, y + offset, 0xFFFFFFFF);
            context.getMatrices().popMatrix();
            offset += 10;

            if (title.getSubtitle().isEmpty()) continue;
            context.getMatrices().pushMatrix();
            context.getMatrices().scale(scale/2);
            context.drawCenteredTextWithShadow(MinecraftClient.getInstance().textRenderer, title.getSubtitle(), x * 2, (y + offset) * 2, 0xFFFFFFFF);
            context.getMatrices().popMatrix();
            offset += 10;
            if (title.getStayTicks() <= 0) titles = ArrayUtils.remove(titles, ArrayUtils.indexOf(titles, title));
        }
    }

    private static void tick(MinecraftClient minecraftClient) {
        if (MinecraftClient.getInstance().player == null) return;
        for (AUTitle title : titles) {
            title.setStayTicks(title.getStayTicks() - 1);
        }
        if (messages.length == 0) return;
        Text[] tempMsgs = messages;
        messages = new Text[]{};
        for (Text message : tempMsgs) {
            MinecraftClient.getInstance().player.sendMessage(message, false);
        }
    }

    public static void queueClientMessage(Text message) {
        messages = ArrayUtils.add(messages, message);
    }

    public static void displayTitle(String title, String subtitle, int stayTicks) {
        if (!ConfigManager.getConfig().mainCategory.titleAccordion.customTitles) {
            if (MinecraftClient.getInstance().getNetworkHandler() == null) return;
            MinecraftClient.getInstance().getNetworkHandler().onTitleClear(new ClearTitleS2CPacket(true));
            MinecraftClient.getInstance().getNetworkHandler().onTitleFade(new TitleFadeS2CPacket(0, stayTicks, 0));
            MinecraftClient.getInstance().getNetworkHandler().onTitle(new TitleS2CPacket(Text.literal(title)));
            MinecraftClient.getInstance().getNetworkHandler().onSubtitle(new SubtitleS2CPacket(Text.literal(subtitle)));
        } else {
            titles = ArrayUtils.add(titles, new AUTitle(title, subtitle, stayTicks));
        }
    }

    public static int getChatWidth() {
        return ChatHud.getWidth(MinecraftClient.getInstance().options.getChatWidth().getValue());
    }

    //Credit to ctjs
    public static String getChatBreak(String str) {
        int length = MinecraftClient.getInstance().textRenderer.getWidth(str);
        int times = getChatWidth() / length;
        return str.repeat(times);
    }

    public static class AUTitle {
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
