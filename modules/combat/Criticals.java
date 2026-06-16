package elixe.modules.combat;

import java.util.Random;

import elixe.events.OnAttackEntityEvent;
import elixe.events.OnTickEvent;
import elixe.modules.Module;
import elixe.modules.ModuleCategory;
import elixe.modules.option.ModuleArray;
import elixe.modules.option.ModuleInteger;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.network.play.client.C03PacketPlayer;

public class Criticals extends Module {

	public Criticals() {
		super("Criticals", ModuleCategory.COMBAT);
		moduleOptions.add(modeOption);
		moduleOptions.add(chanceOption);
		moduleOptions.add(delayOption);
	}

	// 0 = packet (fake the fall via position packets), 1 = jump (real mini-hop)
	int mode;
	ModuleArray modeOption = new ModuleArray("mode", 0, new String[] { "packet", "jump" }, false) {
		public void valueChanged() {
			mode = (int) this.getValue();
		}
	};

	int chance;
	ModuleInteger chanceOption = new ModuleInteger("chance", 90, 1, 100) {
		public void valueChanged() {
			chance = (int) this.getValue();
		}
	};

	int delay;
	ModuleInteger delayOption = new ModuleInteger("delay ms", 0, 0, 500) {
		public void valueChanged() {
			delay = (int) this.getValue();
		}
	};

	private final Random random = new Random();
	private int groundTicks;
	private long lastCrit;

	@EventHandler
	private Listener<OnTickEvent> onTickEvent = new Listener<>(e -> {
		if (mc.thePlayer == null) {
			return;
		}
		// sustained ground contact reads as a legit standing crit
		if (mc.thePlayer.onGround) {
			groundTicks++;
		} else {
			groundTicks = 0;
		}
	});

	@EventHandler
	private Listener<OnAttackEntityEvent> onAttackEntityEvent = new Listener<>(e -> {
		if (mc.thePlayer == null || mc.thePlayer.ridingEntity != null) {
			return;
		}
		if (groundTicks <= 2) {
			return;
		}
		if (mc.thePlayer.isInWater() || mc.thePlayer.isInLava() || mc.thePlayer.isOnLadder()) {
			return;
		}

		// randomized skips + cooldown so it doesn't read as a 100% crit rate
		if (random.nextInt(100) >= chance) {
			return;
		}
		if (System.currentTimeMillis() - lastCrit < delay) {
			return;
		}

		if (mode == 0) {
			double x = mc.thePlayer.posX, y = mc.thePlayer.posY, z = mc.thePlayer.posZ;
			mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.0625, z, true));
			mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, false));
			mc.thePlayer.onCriticalHit(e.getAttackedEntity());
		} else {
			// real upward motion so the following hits land while falling
			mc.thePlayer.motionY = 0.42d;
		}

		lastCrit = System.currentTimeMillis();
	});
}
