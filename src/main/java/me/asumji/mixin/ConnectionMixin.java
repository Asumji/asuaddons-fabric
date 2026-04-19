package me.asumji.mixin;

import me.asumji.features.SimonSays;
import me.asumji.util.Variables;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
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
        } else if (packet instanceof ClientboundAnimatePacket animatePacket) {
            if (animatePacket.getAction() != 0 || !SimonSays.inP3S1 || animatePacket.getId() == Minecraft.getInstance().player.getId()) return;
            Entity ent = Minecraft.getInstance().level.getEntity(animatePacket.getId());
            float range = 6.0F;
            Vec3 eye = ent.getEyePosition(1.0F);
            Vec3 look = ent.getLookAngle();
            Vec3 end = eye.add(look.x * range, look.y * range, look.z * range);

            BlockHitResult hit = Minecraft.getInstance().level.clip(new ClipContext(eye, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, ent));
            if (hit.getType() == HitResult.Type.BLOCK) {
                BlockPos pos = new BlockPos(110, hit.getBlockPos().getY(), hit.getBlockPos().getZ());
                if (!pos.equals(new BlockPos(110, 121, 91))) return;
                BlockState state = Minecraft.getInstance().level.getBlockState(pos);
                SimonSays.blockUpdate(pos, state);
            }
        }
    }
}
