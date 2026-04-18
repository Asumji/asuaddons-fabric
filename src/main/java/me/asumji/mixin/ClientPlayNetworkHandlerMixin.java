package me.asumji.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import me.asumji.AsuAddons;
import me.asumji.features.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.CommonListenerCookie;
import net.minecraft.core.BlockPos;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEventPacket;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundSectionBlocksUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.monster.Giant;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.ButtonBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public abstract class ClientPlayNetworkHandlerMixin extends ClientCommonPacketListenerImpl {
    protected ClientPlayNetworkHandlerMixin(Minecraft client, Connection connection, CommonListenerCookie connectionState) {
        super(client, connection, connectionState);
    }

    @Inject(method = "handleSetEntityData", at = @At("TAIL"))
    private void onEntityTrackerUpdate(ClientboundSetEntityDataPacket packet, CallbackInfo ci, @Local Entity entity) {
        if (entity instanceof ArmorStand stand) {
            WitherHitbox.loadEntity(stand);
        }
        if (entity instanceof Giant giant) {
            BloodGiant.loadEntity(giant);
        }
        if (entity instanceof Player player) {
            LividSolver.loadEntity(player);
            ShadowAssassinHighlight.loadEntity(player);
        }
    }
}
