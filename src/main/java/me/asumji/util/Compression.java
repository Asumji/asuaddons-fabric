package me.asumji.util;

import com.google.gson.JsonObject;
import me.asumji.AsuAddons;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtSizeTracker;

import java.io.ByteArrayInputStream;
import java.util.Base64;

public class Compression {
    public static NbtList decodeInv(JsonObject data) {
        try {
            return NbtIo.readCompressed(new ByteArrayInputStream(Base64.getDecoder().decode(data.get("data").getAsString())), NbtSizeTracker.ofUnlimitedBytes()).getListOrEmpty("i");
        } catch (Exception e) {
                AsuAddons.LOGGER.info(e.toString());
        }
        return null;
    }
}
