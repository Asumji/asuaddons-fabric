package me.asumji.features;

import com.google.gson.JsonObject;
import me.asumji.gui.config.ConfigManager;
import me.asumji.util.Number;
import me.asumji.util.Variables;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static me.asumji.AsuAddons.GSON;

public class RNGMeterTooltip {
    public static Map<String, Integer> meters = new HashMap<>();

    public static void init() {
        meters.put("Revenant Horror", 1500);
        meters.put("Tarantula Broodfather", 1500);
        meters.put("Sven Packmaster", 500);
        meters.put("Voidgloom Seraph", 500);
        meters.put("Riftstalker Bloodfiend", 150);
        meters.put("Inferno Demonlord", 500);
        meters.put("Catacombs (F1)", 270); meters.put("Catacombs (M1)", 270);
        meters.put("Catacombs (F2)", 270); meters.put("Catacombs (M2)", 270);
        meters.put("Catacombs (F3)", 270); meters.put("Catacombs (M3)", 270);
        meters.put("Catacombs (F4)", 300); meters.put("Catacombs (M4)", 300);
        meters.put("Catacombs (F5)", 300); meters.put("Catacombs (M5)", 300);
        meters.put("Catacombs (F6)", 300); meters.put("Catacombs (M6)", 300);
        meters.put("Catacombs (F7)", 300); meters.put("Catacombs (M7)", 300);
        meters.put("Crystal Nucleus", 1000);

        ItemTooltipCallback.EVENT.register(RNGMeterTooltip::tooltipCallback);
    }

    private static void tooltipCallback(ItemStack itemStack, Item.TooltipContext tooltipContext, TooltipType tooltipType, List<Text> texts) {
        if (!ConfigManager.getConfig().miscCategory.rngMeterAccordion.rngMeterDetails) return;
        Matcher matcher = Pattern.compile("(?:\\(\\d/\\d\\) )?(" + StringUtils.join(meters.keySet(), "|").replace("(", "\\(").replace(")", "\\)") + ") RNG Meter").matcher(MinecraftClient.getInstance().currentScreen.getTitle().getString());
        if (!matcher.find() || itemStack.getComponents().get(DataComponentTypes.CUSTOM_DATA) == null) return;
        int currentScore = 0;
        int neededScore = 0;

        for (Text text : texts) {
            Matcher lineMatcher = Pattern.compile("([,.\\w]+)/([,.\\w]+)").matcher(text.getString());
            if (lineMatcher.find()) {
                currentScore = Number.expandNumber(lineMatcher.group(1).replace(",", "")).intValue();
                neededScore = Number.expandNumber(lineMatcher.group(2).replace(",", "")).intValue();
                continue;
            }
            lineMatcher = Pattern.compile("Odds: .* \\((?:[\\d.]+% )?([\\d.]+)%\\)").matcher(text.getString());
            if (lineMatcher.find()) {
                double percentage = Double.parseDouble(lineMatcher.group(1));
                if (ConfigManager.getConfig().miscCategory.rngMeterAccordion.fractionDropChance) texts.set(texts.indexOf(text), text.copy().append(Text.literal(" §7(1/" + (int) (100.0 / percentage) + ")")));
            }
        }

        int currentIncrements = currentScore / meters.get(matcher.group(1));
        int neededIncrements = neededScore / meters.get(matcher.group(1));

        texts.add(Text.empty());
        if (ConfigManager.getConfig().miscCategory.rngMeterAccordion.assumedIncrement) texts.add(Text.literal("§7Assumed Increment: §d" + meters.get(matcher.group(1)).toString()));
        texts.add(Text.literal("§7Increments left: §d" + (neededIncrements - currentIncrements) + " (" + currentIncrements + "§5/§d" + neededIncrements + ")"));
        if (!ConfigManager.getConfig().miscCategory.rngMeterAccordion.profitPerIncrement || Variables.bazaar == null || Variables.lowestbin == null) return;
        JsonObject itemCustomData = GSON.fromJson(itemStack.getComponents().get(DataComponentTypes.CUSTOM_DATA).toString(), JsonObject.class);
        if (itemCustomData.get("id") == null) return;
        String itemId = itemCustomData.get("id").getAsString();
        if (itemId.equals("ENCHANTED_BOOK")) {
            String key = itemCustomData.getAsJsonObject("enchantments").keySet().iterator().next();
            itemId = "ENCHANTMENT_"+key.toUpperCase()+"_"+itemCustomData.getAsJsonObject("enchantments").get(key).getAsString();
        }
        if (neededIncrements == 0) return;
        if (Variables.lowestbin.get(itemId) != null) {
            texts.add(Text.literal("§7Money per Increment: §6" + Number.shortenNumber(BigDecimal.valueOf(Variables.lowestbin.get(itemId).getAsInt()/neededIncrements))));
        } else if (Variables.bazaar.get(itemId) != null) {
            texts.add(Text.literal("§7Money per Increment: §6" + Number.shortenNumber(BigDecimal.valueOf(Variables.bazaar.getAsJsonObject(itemId).getAsJsonObject("quick_status").get("buyPrice").getAsInt()/neededIncrements))));
        } else {
            texts.add(Text.literal("§cCould not find price of item."));
        }
    }
}
