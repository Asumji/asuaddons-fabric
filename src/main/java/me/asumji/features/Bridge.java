package me.asumji.features;

import me.asumji.gui.config.ConfigManager;
import me.asumji.util.Shortcuts;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.text.Text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Bridge {
    public static void init() {
        ClientReceiveMessageEvents.ALLOW_GAME.register(Bridge::onChatMessage);
    }

    private static boolean onChatMessage(Text text, boolean b) {
        if (!ConfigManager.getConfig().miscCategory.bridgeAccordion.bridge) return true;
        Matcher matcher = Pattern.compile("(Guild|Officer) > (?:\\[[^]]+] )?(\\S*) ?(?:\\[[^]]+])?: (.*): (.*)").matcher(text.getString().replaceAll("§.",""));
        if (!matcher.find()) return true;
        if (!ConfigManager.getConfig().miscCategory.bridgeAccordion.bridgeBot.equals(matcher.group(2))) return true;
        String message = matcher.group(1).equals("Guild") ? ConfigManager.getConfig().miscCategory.bridgeAccordion.bridgeMessage : ConfigManager.getConfig().miscCategory.bridgeAccordion.officerMessage;
        Shortcuts.queueClientMessage(Text.literal(message.replace("{usr}",matcher.group(3)).replace("{msg}",matcher.group(4)).replace("&","§")));
        return false;
    }
}
