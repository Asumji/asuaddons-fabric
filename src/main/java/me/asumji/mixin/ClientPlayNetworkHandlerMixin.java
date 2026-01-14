package me.asumji.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import me.asumji.features.BloodGiant;
import me.asumji.features.LividSolver;
import me.asumji.features.ShadowAssassinHighlight;
import me.asumji.features.WitherHitbox;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommonNetworkHandler;
import net.minecraft.client.network.ClientConnectionState;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.mob.GiantEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin extends ClientCommonNetworkHandler {
    protected ClientPlayNetworkHandlerMixin(MinecraftClient client, ClientConnection connection, ClientConnectionState connectionState) {
        super(client, connection, connectionState);
    }

    @Inject(method = "onEntityTrackerUpdate", at = @At("TAIL"))
    private void onEntityTrackerUpdate(EntityTrackerUpdateS2CPacket packet, CallbackInfo ci, @Local Entity entity) {
        if (entity instanceof ArmorStandEntity stand) {
            WitherHitbox.loadEntity(stand);
        }
        if (entity instanceof GiantEntity giant) {
            BloodGiant.loadEntity(giant);
        }
        if (entity instanceof PlayerEntity player) {
            LividSolver.loadEntity(player);
            ShadowAssassinHighlight.loadEntity(player);
        }
    }
}
