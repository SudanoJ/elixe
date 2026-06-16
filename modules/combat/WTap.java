package elixe.modules.combat;

import org.lwjgl.input.Keyboard;

import elixe.events.OnAttackEntityEvent;
import elixe.events.OnTickEvent;
import elixe.modules.Module;
import elixe.modules.ModuleCategory;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Briefly releases the forward key for one tick right after hitting a player,
 * which resets the sprint state and lands extra knockback (the classic "W-tap").
 */
public class WTap extends Module {

	public WTap() {
		super("WTap", ModuleCategory.COMBAT);
	}

	private int ticks;

	@EventHandler
	private Listener<OnAttackEntityEvent> onAttackEntityEvent = new Listener<>(e -> {
		if (mc.thePlayer == null || !mc.thePlayer.onGround) {
			return;
		}
		if (e.getAttackedEntity() instanceof EntityPlayer) {
			ticks = 2;
		}
	});

	@EventHandler
	private Listener<OnTickEvent> onTickEvent = new Listener<>(e -> {
		if (mc.thePlayer == null) {
			return;
		}
		int code = mc.gameSettings.keyBindForward.getKeyCode();
		if (ticks == 2) {
			KeyBinding.setKeyBindState(code, false);
			ticks--;
		} else if (ticks == 1) {
			// restore to the physical key state
			KeyBinding.setKeyBindState(code, code >= 0 && Keyboard.isKeyDown(code));
			ticks--;
		}
	});
}
