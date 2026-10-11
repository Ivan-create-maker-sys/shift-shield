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
		ClientTickEvents.START_CLIENT_TICK.register(this::startTick);
		ClientTickEvents.END_CLIENT_TICK.register(this::endTick);
	}

	private boolean isPlacing(Minecraft mc) {
		var player = mc.player;
		return mc.options.keyUse.isDown()
				&& !player.getMainHandItem().is(Items.SHIELD)
				&& player.getOffhandItem().is(Items.SHIELD)
				&& !player.getMainHandItem().isEmpty();
	}

	private void startTick(Minecraft mc) {
		var player = mc.player;
		if (player == null || mc.gameMode == null) return;

		if (raisedByMod && player.isUsingItem() && isPlacing(mc)) {
			mc.gameMode.releaseUsingItem(player);
			raisedByMod = false;
		}
	}

	private void endTick(Minecraft mc) {
		var player = mc.player;
		if (player == null || mc.gameMode == null) {
			raisedByMod = false;
			return;
		}

		boolean sneaking = mc.options.keyShift.isDown();

		InteractionHand hand = null;
		if (player.getMainHandItem().is(Items.SHIELD)) {
			hand = InteractionHand.MAIN_HAND;
		} else if (player.getOffhandItem().is(Items.SHIELD)) {
			hand = InteractionHand.OFF_HAND;
		}

		boolean want = sneaking && hand != null && !isPlacing(mc);

		if (raisedByMod && !player.isUsingItem()) {
			raisedByMod = false;
		}

		if (want && !player.isUsingItem()) {
			mc.gameMode.useItem(player, hand);
			raisedByMod = true;
		} else if (!want && raisedByMod) {
			mc.gameMode.releaseUsingItem(player);
			raisedByMod = false;
		}
	}
}
