package me.asumji.mixin;

import com.mojang.datafixers.kinds.Const;
import me.asumji.AsuAddons;
import me.asumji.util.Constants;
import me.asumji.util.Shortcuts;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public class ClientConnectionMixin {

    @Inject(at = @At("HEAD"), method = "handlePacket")
    private static void init(Packet<?> packet, PacketListener listener, CallbackInfo ci) {
        if (!packet.getPacketType().toString().equals("clientbound/minecraft:ping")) return;
        if (Constants.TickTimers.isEmpty()) return;
        Constants.TickTimers.forEach((timerName, value) -> {
            if (value != 0) Constants.TickTimers.replace(timerName, value - 1);
            else Constants.TickTimers.remove(timerName);
        });
    }
}
