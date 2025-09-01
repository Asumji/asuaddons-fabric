package me.asumji.util;

import me.asumji.AsuAddons;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Number {
    public static String shortenNumber(BigDecimal n) {
        if (n.compareTo(BigDecimal.ONE) < 0) return String.valueOf(n.setScale(2, RoundingMode.DOWN));
        String[] sizes = {"", "k", "m", "b"};
        for (int i = 0; i < sizes.length; i++) {
            if (n.divide(BigDecimal.valueOf(Math.pow(10, i * 3)), RoundingMode.DOWN).compareTo(BigDecimal.ONE) < 0) {
                return (n.divide(BigDecimal.valueOf(Math.pow(10,(i-1)*3)), RoundingMode.DOWN).setScale(2, RoundingMode.DOWN))+sizes[i-1];
            }
        }
        return (n.divide(BigDecimal.valueOf(1000000000), RoundingMode.DOWN)).setScale(2, RoundingMode.DOWN)+"b";
    }

    public static int getCata(BigDecimal xp) {
        int[] cataLevelArray = {
                0, 50, 125, 235, 395, 625, 955, 1425, 2095, 3045, 4385, 6275, 8940, 12700, 17960, 25340,
                35640, 50040, 70040, 97640, 135640, 188140, 259640, 356640, 488640, 668640, 911640, 1239640,
                1684640, 2284640, 3084640, 4149640, 5559640, 7459640, 9959640, 13259640, 17559640, 23159640,
                30359640, 39559640, 51559640, 66559640, 85559640, 109559640, 139559640, 177559640, 225559640,
                285559640, 360559640, 453559640, 569809640};

        int cata = -1;

        for (int j : cataLevelArray) {
            if (new BigDecimal(j).compareTo(xp) < 1) {
                cata += 1;
            }
        }
        if (cata == 50) cata += (xp.subtract(new BigDecimal(569809640))).divide(new BigDecimal(200000000)).intValue();
        return cata;
    }

    public static int getPetLvl(BigDecimal xp, String type, String tier) {
        int maxLevel;
        if (type.equals("GOLDEN_DRAGON") || type.equals("JADE_DRAGON")) {
            maxLevel = 200;
        } else {
            maxLevel = 100;
        }

        Map<String, Integer> offset = new HashMap<>();
        offset.put("COMMON",0);
        offset.put("UNCOMMON",6);
        offset.put("RARE",11);
        offset.put("EPIC",16);
        offset.put("LEGENDARY",20);
        offset.put("MYTHIC",20);

        int[] levellist = {
            100, 110, 120, 130, 145, 160, 175, 190, 210, 230, 250, 275, 300, 330, 360, 400, 440, 490, 540, 600, 660, 730, 800,
            880, 960, 1050, 1150, 1260, 1380, 1510, 1650, 1800, 1960, 2130, 2310, 2500, 2700, 2920, 3160, 3420, 3700, 4000, 4350,
            4750, 5200, 5700, 6300, 7000, 7800, 8700, 9700, 10800, 12000, 13300, 14700, 16200, 17800, 19500, 21300, 23200, 25200,
            27400, 29800, 32400, 35200, 38200, 41400, 44800, 48400, 52200, 56200, 60400, 64800, 69400, 74200, 79200, 84700, 90700,
            97200, 104200, 111700, 119700, 128200, 137200, 146700, 156700, 167700, 179700, 192700, 206700, 221700, 237700, 254700,
            272700, 291700, 311700, 333700, 357700, 383700, 411700, 441700, 476700, 516700, 561700, 611700, 666700, 726700,
            791700, 861700, 936700, 1016700, 1101700, 1191700, 1286700, 1386700, 1496700, 1616700, 1746700, 1886700, 0, 5555,
            1886700, 1886700, 1886700, 1886700, 1886700, 1886700, 1886700, 1886700, 1886700, 1886700, 1886700, 1886700, 1886700,
            1886700, 1886700, 1886700, 1886700, 1886700, 1886700, 1886700, 1886700, 1886700, 1886700, 1886700, 1886700, 1886700,
            1886700, 1886700, 1886700, 1886700, 1886700, 1886700, 1886700, 1886700, 1886700, 1886700, 1886700, 1886700, 1886700,
            1886700, 1886700, 1886700, 1886700, 1886700, 1886700, 1886700, 1886700, 1886700, 1886700, 1886700, 1886700, 1886700,
            1886700, 1886700, 1886700, 1886700, 1886700, 1886700, 1886700, 1886700, 1886700, 1886700, 1886700, 1886700, 1886700,
            1886700, 1886700, 1886700, 1886700, 1886700, 1886700, 1886700, 1886700, 1886700, 1886700, 1886700, 1886700, 1886700,
            1886700, 1886700, 1886700, 1886700, 1886700, 1886700, 1886700, 1886700, 1886700, 1886700, 1886700, 1886700, 1886700,
            1886700, 1886700, 1886700, 1886700, 1886700, 1886700, 1886700, 1886700};

        int rarityOffset = offset.get(tier);
        int[] levels = Arrays.stream(levellist, rarityOffset,rarityOffset+maxLevel).toArray();

        BigDecimal xpTotal = BigDecimal.ZERO;
        int level = 1;

        for (int i = 0; i < maxLevel; i++) {
            xpTotal = xpTotal.add(BigDecimal.valueOf(levels[i]));

            if (xpTotal.compareTo(xp)>0) {
                break;
            } else {
                level++;
            }
        }

        if (level > maxLevel) {
            level = maxLevel;
        }

        return level;
    }
}
