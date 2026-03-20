package me.asumji.util;

import com.google.gson.JsonObject;
import me.asumji.AsuAddons;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import java.io.ByteArrayInputStream;
import java.util.Base64;

public class Compression {
    public static ListTag decodeInv(JsonObject data) {
        try {
            return NbtIo.readCompressed(new ByteArrayInputStream(Base64.getDecoder().decode(data.get("data").getAsString())), NbtAccounter.unlimitedHeap()).getListOrEmpty("i");
        } catch (Exception e) {
                AsuAddons.LOGGER.info(e.toString());
        }
        return null;
    }
}
