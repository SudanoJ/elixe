package elixe.modules.misc;

import elixe.events.OnAttackEntityEvent;
import elixe.events.OnPacketSendEvent;
import elixe.modules.Module;
import elixe.modules.ModuleCategory;
import elixe.utils.misc.ChatUtils;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;

public class Ninja extends Module {

	public Ninja() {
		super("Ninja", ModuleCategory.MISC);
	}

	public static EntityPlayer lastEntity = null;

	// hold sneak after a hit to blink onto the last target
	@EventHandler
	private Listener<OnPacketSendEvent> onPacketSendEvent = new Listener<>(e -> {
		if (mc.thePlayer == null) {
			return;
		}

		if (e.getPacket() instanceof C04PacketPlayerPosition && mc.thePlayer.isSneaking()) {
			if (lastEntity != null) {
				double x = lastEntity.posX, y = lastEntity.posY, z = lastEntity.posZ;

				mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, false));
				mc.thePlayer.setPosition(x, y, z);

				lastEntity = null;
			} else {
				ChatUtils.message(mc, true, "Você não hitou ninguém.");
			}
		}
	});

	@EventHandler
	private Listener<OnAttackEntityEvent> onAttackEntityEvent = new Listener<>(e -> {
		if (e.getAttackedEntity() instanceof EntityPlayer) {
			lastEntity = (EntityPlayer) e.getAttackedEntity();
		}
	});
}
