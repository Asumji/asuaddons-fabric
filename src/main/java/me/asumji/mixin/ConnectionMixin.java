package me.asumji.mixin;

import me.asumji.features.SimonSays;
import me.asumji.util.Variables;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.world.level.block.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Connection.class)
public class ConnectionMixin {

    @Inject(at = @At("HEAD"), method = "genericsFtw")
    private static void handlePacket(Packet<?> packet, PacketListener listener, CallbackInfo ci) {
        String type = packet.type().toString();

        if (type.equals("clientbound/minecraft:ping")) {
            SimonSays.serverTick();
            if (Variables.TickTimers.isEmpty()) return;
            Variables.TickTimers.forEach((timerName, value) -> {
                if (value != 0) Variables.TickTimers.replace(timerName, value - 1);
                else Variables.TickTimers.remove(timerName);
            });
        } else if (packet instanceof ClientboundSectionBlocksUpdatePacket blockPacket) {
            blockPacket.runUpdates(((blockPos, blockState) -> {
                if (blockState.getBlock() == Blocks.SEA_LANTERN) SimonSays.blockUpdate(blockPos, blockState);
            }));
        } else if (packet instanceof ClientboundBlockUpdatePacket blockPacket) {
            SimonSays.blockUpdate(blockPacket.getPos(), blockPacket.getBlockState());
        }
    }
}
