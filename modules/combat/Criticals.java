package elixe.modules.combat;

import elixe.events.OnAttackEntityEvent;
import elixe.modules.Module;
import elixe.modules.ModuleCategory;
import elixe.utils.misc.ChatUtils;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C03PacketPlayer;

public class Criticals extends Module {

	public Criticals() {
		super("Criticals", ModuleCategory.COMBAT);
	}
	
	@EventHandler
	private Listener<OnAttackEntityEvent> onAttackEntityEvent = new Listener<>(e -> {
		if (mc.thePlayer == null) {
			ChatUtils.message(mc, "�cnull");
			return;
		}
		
//		mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
//				mc.thePlayer.posY + 0.2f, mc.thePlayer.posZ, false));
		mc.thePlayer.addVelocity(0, 0.1, 0);
//		ChatUtils.message(mc, "�apacket up ");
		
//		mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
//				mc.thePlayer.posY, mc.thePlayer.posZ, false));
//		ChatUtils.message(mc, "�cpacket down");
	});
}
