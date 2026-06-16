package elixe.modules.movement;

import elixe.events.OnPacketSendEvent;
import elixe.modules.Module;
import elixe.modules.ModuleCategory;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.network.play.client.C03PacketPlayer;

public class Fly extends Module {
	public Fly() {
		super("Fly", ModuleCategory.MOVEMENT);
	}
	
	@EventHandler
	private Listener<OnPacketSendEvent> onPacketSendEvent = new Listener<>(e -> {
		if (mc.thePlayer == null)
			return;
		
		if(e.getPacket() instanceof C03PacketPlayer) {
			mc.thePlayer.capabilities.allowFlying = true;
		}
	});
}
