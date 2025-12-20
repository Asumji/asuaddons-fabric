package me.asumji.util;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.asumji.AsuAddons;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.scoreboard.*;
import org.apache.commons.lang3.ArrayUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Variables {
    public static String location = "";
    public static Map<String, String> rarities = new HashMap<>();
    public static Map<String, String> classes = new HashMap<>();
    public static Map<String, Integer> TickTimers = new ConcurrentHashMap<>();

    public static void init() {
        rarities.put("COMMON","§f");
        rarities.put("UNCOMMON","§a");
        rarities.put("RARE","§9");
        rarities.put("EPIC","§5");
        rarities.put("LEGENDARY","§6");
        rarities.put("MYTHIC","§d");
        rarities.put("DIVINE","§b");
        rarities.put("SPECIAL","§c");
        rarities.put("VERY SPECIAL","§4");

        classes.put("Archer","§c");
        classes.put("Berserk","§6");
        classes.put("Mage","§b");
        classes.put("Healer","§d");
        classes.put("Tank","§2");
    }

    public static ObjectArrayList<String> getScoreboard() {
        ObjectArrayList<String> scoreboardList = new ObjectArrayList<>();
        if (MinecraftClient.getInstance().player == null) return scoreboardList;
        Scoreboard scoreboard = Objects.requireNonNull(MinecraftClient.getInstance().getNetworkHandler()).getScoreboard();
        ScoreboardObjective objective = scoreboard.getObjectiveForSlot(ScoreboardDisplaySlot.FROM_ID.apply(1));
        for (ScoreHolder scoreHolder : scoreboard.getKnownScoreHolders()) {
            if (scoreboard.getScoreHolderObjectives(scoreHolder).containsKey(objective)) {
                Team team = scoreboard.getScoreHolderTeam(scoreHolder.getNameForScoreboard());
                if (team != null) {
                    String strLine = team.getPrefix().getString() + team.getSuffix().getString();
                    scoreboardList.add(strLine);

                    Matcher matcher = Pattern.compile(" ⏣ (.*)").matcher(strLine);
                    if (matcher.find()) location = matcher.group(1);
                }
            }
        }
        return scoreboardList;
    }

    public static ObjectArrayList<String> getTablist() {
        ObjectArrayList<String> tabList = new ObjectArrayList<>();
        for (PlayerListEntry player : Objects.requireNonNull(MinecraftClient.getInstance().getNetworkHandler()).getListedPlayerListEntries()) {
            if (player.getDisplayName() == null) continue;
            tabList.add(player.getDisplayName().getString());
        }
        return tabList;
    }
}
