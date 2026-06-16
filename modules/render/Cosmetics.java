package elixe.modules.render;

import elixe.cosmetics.CosmeticRegistry;
import elixe.events.OnTabPlayerNameEvent;
import elixe.modules.Module;
import elixe.modules.ModuleCategory;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;

/**
 * Shows registered per-UUID cosmetics. Currently prepends a colored symbol to the
 * tab-list name of cosmetic owners (visible to other Elixe users only).
 */
public class Cosmetics extends Module {

	public Cosmetics() {
		super("Cosmetics", ModuleCategory.RENDER);
	}

	@EventHandler
	private Listener<OnTabPlayerNameEvent> onTabPlayerNameEvent = new Listener<>(e -> {
		String prefix = CosmeticRegistry.getPrefix(e.getUuid());
		if (prefix != null) {
			e.setName(prefix + e.getName());
		}
	});
}
