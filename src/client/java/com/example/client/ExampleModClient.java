package com.example.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Items;

public class ExampleModClient implements ClientModInitializer {
	// Задержка перед повторным подъёмом щита, в тиках (20 тиков = 1 секунда)
	private static final int RAISE_DELAY_TICKS = 5;

	public static volatile boolean suppressRelease = false;
	private boolean raisedByMod = false;
	private int cooldown = 0;

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

	private boolean isAttacking(Minecraft mc) {
		return mc.options.keyAttack.isDown();
	}

	private void releaseShield(Minecraft mc) {
		suppressRelease = false;
		mc.gameMode.releaseUsingItem(mc.player);
		raisedByMod = false;
	}

	private void startTick(Minecraft mc) {
		var player = mc.player;
		if (player == null || mc.gameMode == null) return;

		if (raisedByMod && player.isUsingItem()
				&& (isPlacing(mc) || isAttacking(mc))) {
			releaseShield(mc);
		}
	}

	private void endTick(Minecraft mc) {
		var player = mc.player;
		if (player == null || mc.gameMode == null) {
			raisedByMod = false;
			suppressRelease = false;
			cooldown = 0;
			return;
		}

		boolean sneaking = mc.options.keyShift.isDown();
		boolean busy = isPlacing(mc) || isAttacking(mc);

		if (busy) {
			cooldown = RAISE_DELAY_TICKS;
		} else if (cooldown > 0) {
			cooldown--;
		}

		InteractionHand hand = null;
		if (player.getMainHandItem().is(Items.SHIELD)) {
			hand = InteractionHand.MAIN_HAND;
		} else if (player.getOffhandItem().is(Items.SHIELD)) {
			hand = InteractionHand.OFF_HAND;
		}

		boolean want = sneaking && hand != null && !busy && cooldown == 0;

		if (raisedByMod && !player.isUsingItem()) {
			raisedByMod = false;
			suppressRelease = false;
		}

		if (want && !player.isUsingItem()) {
			suppressRelease = true;
			mc.gameMode.useItem(player, hand);
			raisedByMod = true;
		} else if (!want && raisedByMod) {
			releaseShield(mc);
		}
	}
}
