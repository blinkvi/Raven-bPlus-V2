package keystrokesmod.client.module.modules.player;

import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import keystrokesmod.client.module.Category;
import keystrokesmod.client.module.ClientModule;
import keystrokesmod.client.module.ModuleInfo;
import keystrokesmod.client.utils.Clock;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;

@ModuleInfo(name = "Blink", category = Category.Player)
public class Blink extends ClientModule {
	private Clock clock = new Clock(0);
	private Queue<Packet<?>> packets = new ConcurrentLinkedQueue<>();
	
	@Override
	public void onDisable() {
	    if (mc.thePlayer == null || mc.thePlayer.sendQueue == null) {
	        packets.clear();
	        return;
	    }

	    if (!packets.isEmpty()) {
	        for (Packet<?> packet : packets) {
	            mc.thePlayer.sendQueue.addToSendQueue(packet);
	        }
	        clock.start();
	    }

	    packets.clear();
	}
	
	@Override
	public boolean onSend(Packet packet) {
        if (packet instanceof C03PacketPlayer) {
            packets.add(packet);
            return true;
         } else if (packet instanceof C08PacketPlayerBlockPlacement || packet instanceof C07PacketPlayerDigging || packet instanceof C09PacketHeldItemChange || packet instanceof C02PacketUseEntity) {
            this.packets.add(packet);
            return true;
         }
		return false;
	}
}
