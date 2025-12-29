package me.asumji.util;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.network.packet.s2c.play.ClearTitleS2CPacket;
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleFadeS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.text.Text;
import org.apache.commons.lang3.ArrayUtils;

public class Shortcuts {
    public static Text[] messages = {};

    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(Shortcuts::tick);
    }

    private static void tick(MinecraftClient minecraftClient) {
        if (MinecraftClient.getInstance().player == null || messages.length == 0) return;
        Text[] tempMsgs = messages;
        messages = new Text[]{};
        for (Text message : tempMsgs) {
            MinecraftClient.getInstance().player.sendMessage(message, false);
        }
    }

    public static void queueClientMessage(Text message) {
        messages = ArrayUtils.add(messages, message);
    }

    public static void displayTitle(Text title, Text subtitle, int fadeInTicks, int stayTicks, int fadeOutTicks) {
        MinecraftClient.getInstance().getNetworkHandler().onTitleClear(new ClearTitleS2CPacket(true));
        MinecraftClient.getInstance().getNetworkHandler().onTitleFade(new TitleFadeS2CPacket(fadeInTicks,stayTicks,fadeOutTicks));
        MinecraftClient.getInstance().getNetworkHandler().onTitle(new TitleS2CPacket(title));
        MinecraftClient.getInstance().getNetworkHandler().onSubtitle(new SubtitleS2CPacket(Text.of(subtitle)));
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
}
