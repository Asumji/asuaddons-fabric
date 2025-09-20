package me.asumji.features;

import me.asumji.gui.config.ConfigManager;
import me.asumji.util.Shortcuts;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.text.Text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Autopet {

    public static void init() {
        ClientReceiveMessageEvents.ALLOW_GAME.register(Autopet::onChatMessage);
    }

    private static boolean onChatMessage(Text text, boolean bool) {
        if (!text.getString().matches("§.Autopet §.equipped your §.\\[Lvl \\d+] (§.)([A-z ]+)(?:§. ✦)?§.! §.§.VIEW RULE") && !ConfigManager.getConfig().miscCategory.AutopetNotif) return true;
        Matcher matcher = Pattern.compile("§.Autopet §.equipped your §.\\[Lvl \\d+] (§.)([A-z ]+)(?:§. ✦)?§.! §.§.VIEW RULE").matcher(text.getString());
        if (!matcher.find()) return true;
        Shortcuts.displayTitle(Text.literal(matcher.group(1)+matcher.group(2)),Text.literal("§cAutopet"),0,ConfigManager.getConfig().miscCategory.AutopetTicks,0);
        return true;
    }
}
