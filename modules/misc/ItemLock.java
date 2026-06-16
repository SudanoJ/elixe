package elixe.modules.misc;

import java.util.ArrayList;

import elixe.events.OnPacketSendEvent;
import elixe.events.OnTickEvent;
import elixe.modules.Module;
import elixe.modules.ModuleCategory;
import elixe.modules.option.ModuleArrayMultiple;
import elixe.modules.option.ModuleBoolean;
import elixe.utils.misc.ChatUtils;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C07PacketPlayerDigging;

public class ItemLock extends Module {

	public ItemLock() {
		super("ItemLock", ModuleCategory.MISC);

		moduleOptions.add(lockedItemsOption);
		moduleOptions.add(messageWarnOption);
		moduleOptions.add(blockStackOption);
	}

	boolean[] lockedItems;
	ModuleArrayMultiple lockedItemsOption = new ModuleArrayMultiple("locked items",
			new boolean[] { true, false, false }, new String[] { "sword", "bucket", "block" }) {
		public void valueChanged() {
			lockedItems = (boolean[]) this.getValue();
		}
	};

	boolean messageWarn;
	ModuleBoolean messageWarnOption = new ModuleBoolean("message warn", false) {
		public void valueChanged() {
			messageWarn = (boolean) this.getValue();
		}
	};

	boolean blockStack;
	ModuleBoolean blockStackOption = new ModuleBoolean("block stack drop", false) {
		public void valueChanged() {
			blockStack = (boolean) this.getValue();
		}
	};

	// 0 = sword, 1 = bucket, 2 = block
	ArrayList<Item> filteredItems = new ArrayList<Item>();
	@EventHandler
	private Listener<OnPacketSendEvent> onPacketSendEvent = new Listener<>(e -> {
		filteredItems.clear();
		if (mc.thePlayer == null)
			return;

		ItemStack item = mc.thePlayer.getCurrentEquippedItem();
		if (item != null) {
			// item.getItem() instanceof ItemSword
			if ((item.getItem() instanceof ItemSword && lockedItems[0])
					|| (item.getItem() instanceof ItemBucket && lockedItems[1])
					|| ((item.getItem() instanceof ItemBlock && lockedItems[2]))) {
				if (e.getPacket() instanceof C07PacketPlayerDigging) {
					C07PacketPlayerDigging packet = (C07PacketPlayerDigging) e.getPacket();
					if (packet.getStatus() == C07PacketPlayerDigging.Action.DROP_ITEM) {
						if (messageWarn)
							ChatUtils.message(mc, false, "§cthis item is §llocked§c!");
						e.cancel();
					} else if (packet.getStatus() == C07PacketPlayerDigging.Action.DROP_ALL_ITEMS) {
						if (blockStack) {
							e.cancel();
							if (messageWarn)
								ChatUtils.message(mc, false, "§cthis item is §llocked§c! §o(stack drop)");
						}
					}
				}
			}
		}
	});
}
