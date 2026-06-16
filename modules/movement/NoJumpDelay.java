package elixe.modules.movement;

import elixe.events.OnTickEvent;
import elixe.modules.Module;
import elixe.modules.ModuleCategory;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;

/**
 * Removes the vanilla post-jump cooldown by keeping {@code jumpTicks} at 0, so
 * you can jump every tick (rapid bunny-hops / no delay when bonking your head).
 */
public class NoJumpDelay extends Module {

	public NoJumpDelay() {
		super("NoJumpDelay", ModuleCategory.MOVEMENT);
	}

	@EventHandler
	private Listener<OnTickEvent> onTickEvent = new Listener<>(e -> {
		if (mc.thePlayer != null) {
			mc.thePlayer.jumpTicks = 0;
		}
	});
}
