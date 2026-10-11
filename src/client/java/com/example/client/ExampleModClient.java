package com.example.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Items;

public class ExampleModClient implements ClientModInitializer {
	private boolean raisedByMod = false;
	private int cooldown = 0;

	@Override
	public void onInitializeClient() {
		ClientTickEvents.START_CLIENT_TICK.register(this::tick);
	}

	private void tick(Minecraft mc) {
		var player = mc.player;
		if (player == null || mc.gameMode == null) {
			raisedByMod = false;
			return;
		}

		if (cooldown > 0) cooldown--;

		boolean sneaking = mc.options.keyShift.isDown();
		boolean usePressed = mc.options.keyUse.isDown();

		InteractionHand hand = null;
		if (player.getMainHandItem().is(Items.SHIELD)) {
			hand = InteractionHand.MAIN_HAND;
		} else if (player.getOffhandItem().is(Items.SHIELD)) {
			hand = InteractionHand.OFF_HAND;
		}

		boolean placing = usePressed
				&& hand == InteractionHand.OFF_HAND
				&& !player.getMainHandItem().isEmpty();

		boolean want = sneaking && hand != null && !placing;

		if (raisedByMod && !player.isUsingItem()) {
			raisedByMod = false;
			cooldown = 5;
		}

		if (want && !player.isUsingItem() && cooldown == 0) {
			mc.gameMode.useItem(player, hand);
			raisedByMod = true;
		} else if (!want && raisedByMod) {
			mc.gameMode.releaseUsingItem(player);
			raisedByMod = false;
		}
	}
}
