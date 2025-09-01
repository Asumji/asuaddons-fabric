package me.asumji.mixin;

import me.asumji.gui.config.ConfigManager;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
    @Inject(at = @At("HEAD"), method = "remove")
    private void init(ServerPlayerEntity player, CallbackInfo ci) {
        ConfigManager.saveConfig("leave");
    }
}
