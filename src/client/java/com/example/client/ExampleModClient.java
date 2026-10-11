package com.example.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Items;

public class ExampleModClient implements ClientModInitializer {
	private boolean raisedByMod = false;

	@Override
	public void onInitializeClient() {
		ClientTickEvents.END_CLIENT_TICK.register(this::tick);
	}

	private void tick(Minecraft mc) {
		var player = mc.player;
		if (player == null || mc.gameMode == null) return;

		boolean sneaking = mc.screen == null && mc.options.keyShift.isDown();

		InteractionHand hand = null;
		if (player.getMainHandItem().is(Items.SHIELD)) {
			hand = InteractionHand.MAIN_HAND;
		} else if (player.getOffhandItem().is(Items.SHIELD)) {
			hand = InteractionHand.OFF_HAND;
		}

		if (raisedByMod && !player.isUsingItem()) {
			raisedByMod = false;
		}

		if (sneaking && hand != null && !player.isUsingItem()) {
			mc.gameMode.useItem(player, hand);
			raisedByMod = true;
		} else if (raisedByMod && (!sneaking || hand == null)) {
			mc.gameMode.releaseUsingItem(player);
			raisedByMod = false;
		}
	}
}
