package me.asumji.util;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Constants {
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
}
