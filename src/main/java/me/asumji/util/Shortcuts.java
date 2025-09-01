package me.asumji.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.s2c.play.ClearTitleS2CPacket;
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleFadeS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.text.Text;

public class Shortcuts {
    public static void sendClientMessage(Text message) {
        if (MinecraftClient.getInstance().player != null) MinecraftClient.getInstance().player.sendMessage(message, false);
    }

    public static void displayTitle(Text title, Text subtitle, int fadeInTicks, int stayTicks, int fadeOutTicks) {
        MinecraftClient.getInstance().getNetworkHandler().onTitleClear(new ClearTitleS2CPacket(true));
        MinecraftClient.getInstance().getNetworkHandler().onTitleFade(new TitleFadeS2CPacket(fadeInTicks,stayTicks,fadeOutTicks));
        MinecraftClient.getInstance().getNetworkHandler().onTitle(new TitleS2CPacket(title));
        MinecraftClient.getInstance().getNetworkHandler().onSubtitle(new SubtitleS2CPacket(Text.of(subtitle)));
    }
}
