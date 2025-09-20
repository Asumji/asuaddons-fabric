package me.asumji.features;

import com.google.gson.*;
import me.asumji.gui.config.ConfigManager;
import me.asumji.util.Compression;
import me.asumji.util.Variables;
import me.asumji.util.Shortcuts;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import me.asumji.AsuAddons;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static me.asumji.util.HTTP.GetRequest;
import static me.asumji.util.Number.*;

public class DPU {
    private static final Gson GSON = new GsonBuilder().create();

    public static void init() {
        ClientReceiveMessageEvents.ALLOW_GAME.register(DPU::onChatMessage);
    }

    private static boolean onChatMessage(Text text, boolean bool) {
        if (!text.getString().startsWith("Party Finder >") && !ConfigManager.getConfig().dungeonCategory.DPU) return true;
        Matcher matcher = Pattern.compile("(\\S*) joined the dungeon group!").matcher(text.getString());
        if (!matcher.find()) return true;
        GetRequest("https://api.mojang.com/users/profiles/minecraft/"+matcher.group(1)).thenAcceptAsync(mojangData -> {
            String uuid = GSON.fromJson(mojangData.body(), JsonObject.class).get("id").getAsString();
            GetRequest(AsuAddons.APIPROXY+"v2/skyblock/profiles?uuid="+uuid).thenAcceptAsync(HypixelData -> {
                JsonObject JSON = GSON.fromJson(HypixelData.body(), JsonObject.class);
                for (JsonElement jsonElement : JSON.getAsJsonArray("profiles")) {
                    JsonObject profile = jsonElement.getAsJsonObject();
                    if (!profile.get("selected").getAsBoolean()) continue;
                    String cata = String.valueOf(getCata(profile.getAsJsonObject("members").getAsJsonObject(uuid).getAsJsonObject("dungeons").getAsJsonObject("dungeon_types").getAsJsonObject("catacombs").get("experience").getAsBigDecimal()));
                    String secrets = profile.getAsJsonObject("members").getAsJsonObject(uuid).getAsJsonObject("dungeons").get("secrets").getAsString();
                    String mp = profile.getAsJsonObject("members").getAsJsonObject(uuid).getAsJsonObject("accessory_bag_storage").get("highest_magical_power").getAsString();
                    String bank = profile.getAsJsonObject("banking").get("balance") == null ? "§cAPI Off" : shortenNumber(profile.getAsJsonObject("banking").get("balance").getAsBigDecimal());
                    String[] pets = {"§cNone", "§cNo", "§cNo Edrag"};
                    MutableText armor = Text.literal("");
                    MutableText items = Text.literal("");
                    String pb = "";

                    if (profile.getAsJsonObject("members").getAsJsonObject(uuid).getAsJsonObject("inventory").getAsJsonObject("inv_armor") != null) {
                        NbtList armorContent = Compression.decodeInv(profile.getAsJsonObject("members").getAsJsonObject(uuid).getAsJsonObject("inventory").getAsJsonObject("inv_armor"));
                        for (int i = armorContent.size()-1; i >= 0; i--) {
                            NbtCompound piece = armorContent.getCompoundOrEmpty(i);
                            if (piece.getInt("id",0)==0) continue;
                            String name = piece.getCompoundOrEmpty("tag").getCompoundOrEmpty("display").getString("Name","");
                            String lore = name+"\n";
                            NbtList loreContent = piece.getCompoundOrEmpty("tag").getCompoundOrEmpty("display").getListOrEmpty("Lore");
                            for (int j = 0; j < loreContent.size(); j++) {
                                String line = loreContent.getString(j,"");
                                lore += line + "\n";
                            }
                            String finalLore = lore.trim();
                            armor.append(Text.literal(name+"  ").styled(style -> style.withHoverEvent(new HoverEvent.ShowText(Text.literal(finalLore)))));
                        }
                    }

                    if (profile.getAsJsonObject("members").getAsJsonObject(uuid).getAsJsonObject("inventory").getAsJsonObject("inv_contents") != null) {
                        NbtList invContent = Compression.decodeInv(profile.getAsJsonObject("members").getAsJsonObject(uuid).getAsJsonObject("inventory").getAsJsonObject("inv_contents"));
                        for (int i = invContent.size()-1; i >= 0; i--) {
                            NbtCompound item = invContent.getCompoundOrEmpty(i);
                            if (item.getInt("id",0)==0) continue;
                            String name = item.getCompoundOrEmpty("tag").getCompoundOrEmpty("display").getString("Name","");
                            String lore = name+"\n";
                            NbtList loreContent = item.getCompoundOrEmpty("tag").getCompoundOrEmpty("display").getListOrEmpty("Lore");
                            for (int j = 0; j < loreContent.size(); j++) {
                                String line = loreContent.getString(j,"");
                                lore += line + "\n";
                            }
                            String finalLore = lore.trim();
                            for (String rItem : ConfigManager.getConfig().dungeonCategory.relevantItems.split(",")) {
                                if (name.toLowerCase().contains(rItem.toLowerCase())) {
                                    items.append(Text.literal(name+"  ").styled(style -> style.withHoverEvent(new HoverEvent.ShowText(Text.literal(finalLore)))));
                                }
                            }
                        }
                    }

                    for (JsonElement petElement : profile.getAsJsonObject("members").getAsJsonObject(uuid).getAsJsonObject("pets_data").getAsJsonArray("pets")) {
                        JsonObject pet = petElement.getAsJsonObject();
                        if (pet.get("active").getAsBoolean()) {
                            pets[0] = "§7[Lvl " + getPetLvl(pet.get("exp").getAsBigDecimal(), pet.get("type").getAsString(), pet.get("tier").getAsString()) + "] " + Variables.rarities.get(pet.get("tier").getAsString()) + pet.get("type").getAsString().replaceAll("_"," ");
                        }
                        if (pet.get("type").getAsString().equals("SPIRIT") && pet.get("tier").getAsString().equals("LEGENDARY")) pets[1] = "§aYes";
                        if (pet.get("type").getAsString().equals("ENDER_DRAGON")) {
                            pets[2] = "§7[Lvl " + getPetLvl(pet.get("exp").getAsBigDecimal(), pet.get("type").getAsString(), pet.get("tier").getAsString()) + "] " + Variables.rarities.get(pet.get("tier").getAsString()) + pet.get("type").getAsString().replaceAll("_"," ");
                        }
                    }

                    JsonObject catacombs = profile.getAsJsonObject("members").getAsJsonObject(uuid).getAsJsonObject("dungeons").getAsJsonObject("dungeon_types").getAsJsonObject("catacombs").getAsJsonObject("fastest_time_s_plus");
                    if (catacombs != null) {
                        for (String floor : catacombs.keySet()) {
                            Date pbDate = new Date(catacombs.get(floor).getAsInt());
                            if (!floor.equals("best"))
                                pb += "§aF" + floor + ": §6 " + pbDate.getMinutes() + ":" + (String.valueOf(pbDate.getSeconds()).length() == 1 ? "0" + pbDate.getSeconds() : pbDate.getSeconds()) + "\n";
                        }
                    }
                    JsonObject masterCatacombs = profile.getAsJsonObject("members").getAsJsonObject(uuid).getAsJsonObject("dungeons").getAsJsonObject("dungeon_types").getAsJsonObject("master_catacombs").getAsJsonObject("fastest_time_s_plus");
                    if (masterCatacombs != null) {
                        for (String floor : masterCatacombs.keySet()) {
                            Date pbDate = new Date(masterCatacombs.get(floor).getAsInt());
                            if (!floor.equals("best"))
                                pb += "§cM" + floor + ": §6 " + pbDate.getMinutes() + ":" + (String.valueOf(pbDate.getSeconds()).length() == 1 ? "0" + pbDate.getSeconds() : pbDate.getSeconds()) + "\n";
                        }
                    }
                    String finalPb = pb.trim();

                    Shortcuts.sendClientMessage(Text.literal(
                        "§cName:§b " + MinecraftClient.getInstance().getSession().getUsername() +
                        "\n§6Cata: §a" + cata +
                        "\n§6Secrets: §c" + secrets +
                        "\n§6MP: §c" + mp +
                        "\n§6Bank: " + bank +
                        "\n§6Spirit: " + pets[1] +
                        "\n\n§6Items:§r\n")
                        .append(items)
                        .append(Text.literal("\n\n§6Armor:§r\n")
                        .append(armor)
                        .append(Text.literal("\n\n§6Pet: §r" + pets[0] + "§7 / " + pets[2]))
                        .append(Text.literal("\n§4[Kick from Party]").styled(style -> style.withClickEvent(new ClickEvent.RunCommand("/party kick " + matcher.group(1)))))
                        .append(Text.literal("        "))
                        .append(Text.literal("§7[Ignore]").styled(style -> style.withClickEvent(new ClickEvent.RunCommand("/ignore add " + matcher.group(1)))))
                        .append(Text.literal("        "))
                        .append(Text.literal("§6[PBs]").styled(style -> style.withHoverEvent(new HoverEvent.ShowText(Text.literal(finalPb)))))
                    ));
                }
            });
        });
        return true;
    }
}
