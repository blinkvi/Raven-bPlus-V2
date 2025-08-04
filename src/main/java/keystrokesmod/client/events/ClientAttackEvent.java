package keystrokesmod.client.events;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.common.eventhandler.Event;

public class ClientAttackEvent extends Event {
	public EntityLivingBase target;

	public ClientAttackEvent(EntityLivingBase target) {
		this.target = target;
	}
}
