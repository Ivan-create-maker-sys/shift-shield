package com.example.mixin.client;

import com.example.client.ExampleModClient;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiPlayerGameMode.class)
public class ExampleClientMixin {
	@Inject(method = "releaseUsingItem", at = @At("HEAD"), cancellable = true)
	private void keepShield(Player player, CallbackInfo ci) {
		if (ExampleModClient.suppressRelease) {
			ci.cancel();
		}
	}
}
