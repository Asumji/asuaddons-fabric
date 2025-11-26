package me.asumji.util;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.scoreboard.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Variables {
    public static String[] scoreboard = {};
    public static Map<String, String> rarities = new HashMap<>();
    //See ClientConnectionMixin for Timer Logic
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
    }

    public static ObjectArrayList<String> getScoreboard(MinecraftClient minecraftClient) {
        if (minecraftClient.player == null) return new ObjectArrayList<>();
        ObjectArrayList<String> stringLines = new ObjectArrayList<>();
        Scoreboard scoreboard = minecraftClient.player.getScoreboard();
        ScoreboardObjective objective = scoreboard.getObjectiveForSlot(ScoreboardDisplaySlot.FROM_ID.apply(1));
        for (ScoreHolder scoreHolder : scoreboard.getKnownScoreHolders()) {
            if (scoreboard.getScoreHolderObjectives(scoreHolder).containsKey(objective)) {
                Team team = scoreboard.getScoreHolderTeam(scoreHolder.getNameForScoreboard());
                if (team != null) {
                    String strLine = team.getPrefix().getString() + team.getSuffix().getString();
                    stringLines.add(strLine);
                }
            }
        }
        return stringLines;
    }
}
