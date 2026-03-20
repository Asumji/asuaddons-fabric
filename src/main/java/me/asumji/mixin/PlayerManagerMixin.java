package me.asumji.mixin;

import me.asumji.gui.config.ConfigManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerList.class)
public class PlayerManagerMixin {
    @Inject(at = @At("HEAD"), method = "remove")
    private void remove(ServerPlayer player, CallbackInfo ci) {
        ConfigManager.saveConfig("leave");
    }
}
