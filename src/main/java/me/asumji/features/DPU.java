package me.asumji.features;

import com.google.gson.*;
import me.asumji.gui.config.ConfigManager;
import me.asumji.util.Compression;
import me.asumji.util.Variables;
import me.asumji.util.Shortcuts;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import me.asumji.AsuAddons;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static me.asumji.util.HTTP.GetRequest;
import static me.asumji.util.Number.*;

public class DPU {

    public static void init() {
        ClientReceiveMessageEvents.ALLOW_GAME.register(DPU::onChatMessage);
    }

    private static boolean onChatMessage(Component text, boolean bool) {
        if (!text.getString().startsWith("Party Finder >") || !ConfigManager.getConfig().dungeonCategory.dpuAccordion.DPU) return true;
        Matcher matcher = Pattern.compile("(\\S*) joined the dungeon group!").matcher(text.getString());
        if (!matcher.find()) return true;
        GetRequest("https://api.mojang.com/users/profiles/minecraft/"+matcher.group(1)).thenAcceptAsync(mojangData -> {
            String uuid = AsuAddons.GSON.fromJson(mojangData.body(), JsonObject.class).get("id").getAsString();
            GetRequest(AsuAddons.API_PROXY +"v2/skyblock/profiles?uuid="+uuid).thenAcceptAsync(HypixelData -> {
                JsonObject JSON = AsuAddons.GSON.fromJson(HypixelData.body(), JsonObject.class);
                if (!JSON.get("success").getAsBoolean()) {
                    Shortcuts.queueClientMessage(Component.literal(AsuAddons.MOD_PREFIX + "§cAPI Request failed: " + JSON.get("cause").getAsString()));
                    return;
                }
                try {
                    for (JsonElement jsonElement : JSON.getAsJsonArray("profiles")) {
                        JsonObject profile = jsonElement.getAsJsonObject();
                        if (!profile.get("selected").getAsBoolean()) continue;
                        JsonObject player = profile.getAsJsonObject("members").getAsJsonObject(uuid);

                        String cata = String.valueOf(getCata(player.getAsJsonObject("dungeons").getAsJsonObject("dungeon_types").getAsJsonObject("catacombs").get("experience").getAsBigDecimal()));
                        String secrets = player.getAsJsonObject("dungeons").get("secrets").getAsString();
                        String mp = player.getAsJsonObject("accessory_bag_storage").get("highest_magical_power").getAsString();
                        String bank = profile.getAsJsonObject("banking") == null ? "§cAPI Off" : shortenNumber(profile.getAsJsonObject("banking").get("balance").getAsBigDecimal());
                        String[] pets = {"§cNone", "§cNo", "§cNo Edrag"};
                        MutableComponent armor = Component.literal("");
                        MutableComponent items = Component.literal("");
                        String pb = "";


                        if (player.getAsJsonObject("inventory") != null) {
                            if (player.getAsJsonObject("inventory").getAsJsonObject("inv_armor") != null) {
                                ListTag armorContent = Compression.decodeInv(player.getAsJsonObject("inventory").getAsJsonObject("inv_armor"));
                                for (int i = armorContent.size() - 1; i >= 0; i--) {
                                    CompoundTag piece = armorContent.getCompoundOrEmpty(i);
                                    if (piece.getIntOr("id", 0) == 0) continue;
                                    String name = piece.getCompoundOrEmpty("tag").getCompoundOrEmpty("display").getStringOr("Name", "");
                                    String lore = name + "\n";
                                    ListTag loreContent = piece.getCompoundOrEmpty("tag").getCompoundOrEmpty("display").getListOrEmpty("Lore");
                                    for (int j = 0; j < loreContent.size(); j++) {
                                        String line = loreContent.getStringOr(j, "");
                                        lore += line + "\n";
                                    }
                                    String finalLore = lore.trim();
                                    armor.append(Component.literal(name + "  ").withStyle(style -> style.withHoverEvent(new HoverEvent.ShowText(Component.literal(finalLore)))));
                                }
                            }

                            if (player.getAsJsonObject("inventory").getAsJsonObject("inv_contents") != null) {
                                ListTag invContent = Compression.decodeInv(player.getAsJsonObject("inventory").getAsJsonObject("inv_contents"));
                                for (int i = invContent.size() - 1; i >= 0; i--) {
                                    CompoundTag item = invContent.getCompoundOrEmpty(i);
                                    if (item.getIntOr("id", 0) == 0) continue;
                                    String name = item.getCompoundOrEmpty("tag").getCompoundOrEmpty("display").getStringOr("Name", "");
                                    String lore = name + "\n";
                                    ListTag loreContent = item.getCompoundOrEmpty("tag").getCompoundOrEmpty("display").getListOrEmpty("Lore");
                                    for (int j = 0; j < loreContent.size(); j++) {
                                        String line = loreContent.getStringOr(j, "");
                                        lore += line + "\n";
                                    }
                                    String finalLore = lore.trim();
                                    for (String rItem : ConfigManager.getConfig().dungeonCategory.dpuAccordion.relevantItems.split(",")) {
                                        if (name.toLowerCase().contains(rItem.toLowerCase())) {
                                            items.append(Component.literal(name + "  ").withStyle(style -> style.withHoverEvent(new HoverEvent.ShowText(Component.literal(finalLore)))));
                                        }
                                    }
                                }
                            }
                        } else {
                            armor.append(Component.literal("§cAPI Off"));
                            items.append(Component.literal("§cAPI Off"));
                        }

                        for (JsonElement petElement : player.getAsJsonObject("pets_data").getAsJsonArray("pets")) {
                            JsonObject pet = petElement.getAsJsonObject();
                            if (pet.get("active").getAsBoolean()) {
                                pets[0] = "§7[Lvl " + getPetLvl(pet.get("exp").getAsBigDecimal(), pet.get("type").getAsString(), pet.get("tier").getAsString()) + "] " + Variables.rarities.get(pet.get("tier").getAsString()) + pet.get("type").getAsString().replaceAll("_", " ");
                            }
                            if (pet.get("type").getAsString().equals("SPIRIT") && pet.get("tier").getAsString().equals("LEGENDARY"))
                                pets[1] = "§aYes";
                            if (pet.get("type").getAsString().equals("ENDER_DRAGON")) {
                                pets[2] = "§7[Lvl " + getPetLvl(pet.get("exp").getAsBigDecimal(), pet.get("type").getAsString(), pet.get("tier").getAsString()) + "] " + Variables.rarities.get(pet.get("tier").getAsString()) + pet.get("type").getAsString().replaceAll("_", " ");
                            }
                        }

                        JsonObject catacombs = player.getAsJsonObject("dungeons").getAsJsonObject("dungeon_types").getAsJsonObject("catacombs").getAsJsonObject("fastest_time_s_plus");
                        if (catacombs != null) {
                            for (String floor : catacombs.keySet()) {
                                Date pbDate = new Date(catacombs.get(floor).getAsInt());
                                if (!floor.equals("best"))
                                    pb += "§aF" + floor + ": §6 " + pbDate.getMinutes() + ":" + (String.valueOf(pbDate.getSeconds()).length() == 1 ? "0" + pbDate.getSeconds() : pbDate.getSeconds()) + "\n";
                            }
                        }
                        JsonObject masterCatacombs = player.getAsJsonObject("dungeons").getAsJsonObject("dungeon_types").getAsJsonObject("master_catacombs").getAsJsonObject("fastest_time_s_plus");
                        if (masterCatacombs != null) {
                            for (String floor : masterCatacombs.keySet()) {
                                Date pbDate = new Date(masterCatacombs.get(floor).getAsInt());
                                if (!floor.equals("best"))
                                    pb += "§cM" + floor + ": §6 " + pbDate.getMinutes() + ":" + (String.valueOf(pbDate.getSeconds()).length() == 1 ? "0" + pbDate.getSeconds() : pbDate.getSeconds()) + "\n";
                            }
                        }
                        String finalPb = pb.trim();

                        Shortcuts.queueClientMessage(Component.literal(
                            "§cName:§b " + matcher.group(1) +
                            "\n§6Cata: §a" + cata +
                            "\n§6Secrets: §c" + secrets +
                            "\n§6MP: §c" + mp +
                            "\n§6Bank: " + bank +
                            "\n§6Spirit: " + pets[1] +
                            "\n\n§6Items:§r\n")
                            .append(items)
                            .append(Component.literal("\n\n§6Armor:§r\n"))
                            .append(armor)
                            .append(Component.literal("\n\n§6Pet: §r" + pets[0] + "§7 / " + pets[2]))
                            .append(Component.literal("\n§4[Kick from Party]").withStyle(style -> style.withClickEvent(new ClickEvent.RunCommand("/party kick " + matcher.group(1)))))
                            .append(Component.literal("        "))
                            .append(Component.literal("§7[Ignore]").withStyle(style -> style.withClickEvent(new ClickEvent.RunCommand("/ignore add " + matcher.group(1)))))
                            .append(Component.literal("        "))
                            .append(Component.literal("§6[PBs]").withStyle(style -> style.withHoverEvent(new HoverEvent.ShowText(Component.literal(finalPb)))))
                        );
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });
        return true;
    }
}
