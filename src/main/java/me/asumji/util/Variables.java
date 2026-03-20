package me.asumji.util;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import com.google.gson.JsonObject;
import me.asumji.AsuAddons;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.ScoreHolder;
import net.minecraft.world.scores.Scoreboard;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static me.asumji.util.HTTP.GetRequest;

public class Variables {
    public static Map<String, String> rarities = new HashMap<>();
    public static Map<String, String> classes = new HashMap<>();
    public static Map<String, Integer> TickTimers = new ConcurrentHashMap<>();
    public static JsonObject lowestbin = null;
    public static JsonObject bazaar = null;

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

        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(Variables::loadPrices, 10, 600, TimeUnit.SECONDS);
    }

    private static void loadPrices() {
        GetRequest("https://moulberry.codes/lowestbin.json").thenAcceptAsync(data -> lowestbin = AsuAddons.GSON.fromJson(data.body(), JsonObject.class));
        GetRequest(AsuAddons.API_PROXY+"v2/skyblock/bazaar").thenAcceptAsync(data -> bazaar = AsuAddons.GSON.fromJson(data.body(), JsonObject.class).getAsJsonObject("products"));
    }

    public static ObjectArrayList<String> getScoreboard() {
        ObjectArrayList<String> scoreboardList = new ObjectArrayList<>();
        if (Minecraft.getInstance().player == null) return scoreboardList;
        Scoreboard scoreboard = Objects.requireNonNull(Minecraft.getInstance().getConnection()).scoreboard();
        Objective objective = scoreboard.getDisplayObjective(DisplaySlot.BY_ID.apply(1));
        for (ScoreHolder scoreHolder : scoreboard.getTrackedPlayers()) {
            if (scoreboard.listPlayerScores(scoreHolder).containsKey(objective)) {
                PlayerTeam team = scoreboard.getPlayersTeam(scoreHolder.getScoreboardName());
                if (team != null) {
                    String strLine = team.getPlayerPrefix().getString() + team.getPlayerSuffix().getString();
                    scoreboardList.add(strLine);
                }
            }
        }
        return scoreboardList;
    }

    public static ObjectArrayList<String> getTablist() {
        ObjectArrayList<String> tabList = new ObjectArrayList<>();
        for (PlayerInfo player : Objects.requireNonNull(Minecraft.getInstance().getConnection()).getListedOnlinePlayers()) {
            if (player.getTabListDisplayName() == null) continue;
            tabList.add(player.getTabListDisplayName().getString());
        }
        return tabList;
    }
}
