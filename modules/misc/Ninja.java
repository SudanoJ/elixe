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

	@EventHandler
	private Listener<OnPacketSendEvent> onPacketSendEvent = new Listener<>(e -> {
		if (mc.thePlayer == null)
			return;
			
		if(e.getPacket() instanceof C04PacketPlayerPosition) {
			if(mc.thePlayer.isSneaking()) {
				if(lastEntity != null) {

//					ChatUtils.message(mc, true, "Teleportado para " + lastEntity.getDisplayName() + ".");
					mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
					mc.thePlayer.posY + 0.2f, mc.thePlayer.posZ, false));
				lastEntity = null;
				} else {
					ChatUtils.message(mc, true, "Voc� n�o hitou ningu�m.");
				}
			}
		}
		
	});
	
	@EventHandler
	private Listener<OnAttackEntityEvent> onAttackEntityEvent = new Listener<>(e -> {
		if(e.getAttackedEntity() instanceof EntityPlayer)
		lastEntity = (EntityPlayer) e.getAttackedEntity();
	});
	
}
