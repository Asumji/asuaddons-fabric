package me.asumji.util;

import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.asumji.AsuAddons;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.scoreboard.*;

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
        if (MinecraftClient.getInstance().player == null) return scoreboardList;
        Scoreboard scoreboard = Objects.requireNonNull(MinecraftClient.getInstance().getNetworkHandler()).getScoreboard();
        ScoreboardObjective objective = scoreboard.getObjectiveForSlot(ScoreboardDisplaySlot.FROM_ID.apply(1));
        for (ScoreHolder scoreHolder : scoreboard.getKnownScoreHolders()) {
            if (scoreboard.getScoreHolderObjectives(scoreHolder).containsKey(objective)) {
                Team team = scoreboard.getScoreHolderTeam(scoreHolder.getNameForScoreboard());
                if (team != null) {
                    String strLine = team.getPrefix().getString() + team.getSuffix().getString();
                    scoreboardList.add(strLine);
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
