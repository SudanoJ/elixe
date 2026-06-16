package elixe.modules.movement;

import elixe.events.OnPacketSendEvent;
import elixe.modules.Module;
import elixe.modules.ModuleCategory;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.network.play.client.C03PacketPlayer;

public class NoFall extends Module {
	public NoFall() {
		super("NoFall", ModuleCategory.MOVEMENT);
	}
	
	@EventHandler
	private Listener<OnPacketSendEvent> onPacketSendEvent = new Listener<>(e -> {
		if (mc.thePlayer == null)
			return;
		
		if(e.getPacket() instanceof C03PacketPlayer) {
			C03PacketPlayer packet = (C03PacketPlayer) e.getPacket();
			if(mc.thePlayer.fallDistance > 3.3) {
				packet.onGround = true;
				// modifiquei o field onGround da packet para conseguir alterar o valor da boolean
			}
		}
	});
}
