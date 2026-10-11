package com.example.client.mixin;

import com.example.client.ExampleModClient;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin {
	@Redirect(
		method = {"modifyInput", "aiStep"},
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/player/LocalPlayer;isUsingItem()Z"
		),
		require = 0
	)
	private boolean noShieldSlowdown(LocalPlayer self) {
		return self.isUsingItem() && !ExampleModClient.suppressRelease;
	}
}
