package me.asumji.mixin;

import me.asumji.util.Variables;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Connection.class)
public class ClientConnectionMixin {

    @Inject(at = @At("HEAD"), method = "genericsFtw")
    private static void handlePacket(Packet<?> packet, PacketListener listener, CallbackInfo ci) {
        if (!packet.type().toString().equals("clientbound/minecraft:ping")) return;
        if (Variables.TickTimers.isEmpty()) return;
        Variables.TickTimers.forEach((timerName, value) -> {
            if (value != 0) Variables.TickTimers.replace(timerName, value - 1);
            else Variables.TickTimers.remove(timerName);
        });
    }
}
