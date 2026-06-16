package elixe.modules.misc;
	
import elixe.events.OnTickEvent;
import elixe.modules.Module;
import elixe.modules.ModuleCategory;
import elixe.modules.option.ModuleBoolean;
import elixe.modules.option.ModuleInteger;
import elixe.utils.player.InventoryItem;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class AntiMonk extends Module {

	public AntiMonk() {
		super("AntiMonk", ModuleCategory.MISC);
		moduleOptions.add(swapDelayOption);
		moduleOptions.add(swapItemOption);
	}

	int ticksSinceLastClick = 0;
	int swapDelay;
	ModuleInteger swapDelayOption = new ModuleInteger("swap delay", 50, 1, 100) {
		public void valueChanged() {
			swapDelay = (int) this.getValue();
		}
	};

	boolean swapItem;
	ModuleBoolean swapItemOption = new ModuleBoolean("swap item", false) {
		public void valueChanged() {
			swapItem = (boolean) this.getValue();
		}
	};

	@EventHandler
	private Listener<OnTickEvent> onTickEvent = new Listener<>(e -> {

		if (mc.currentScreen instanceof GuiInventory) {
			// check hotbar slot 1 specifically (not the currently-held item)
			ItemStack item = mc.thePlayer.inventory.getStackInSlot(0);
			ItemStack swordItem = new ItemStack(Items.diamond_sword);

			if (!ItemStack.areItemStacksEqual(item, swordItem)) {
	            int trySword = InventoryItem.findItem(9, 36, Items.diamond_sword, mc);
	            if (trySword != -1 && (!swapItem || trySword != 36)) {
	                ticksSinceLastClick++;
	                if (ticksSinceLastClick >= swapDelay) {
	                    if (swapItem) {
	                        mc.playerController.windowClick(0, trySword, 0, 0, mc.thePlayer);
	                        mc.playerController.windowClick(0, 36, 0, 0, mc.thePlayer);
	                        mc.playerController.windowClick(0, trySword, 0, 0, mc.thePlayer);
	                    } else if (InventoryItem.hasSpaceHotbar(mc)) {
	                        mc.playerController.windowClick(0, trySword, 0, 1, mc.thePlayer);
	                    }
	                    ticksSinceLastClick = 0;
	                }
				}
			}
		} else
			ticksSinceLastClick = 0;
	});
}
