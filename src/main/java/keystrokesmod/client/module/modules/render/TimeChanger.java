package keystrokesmod.client.module.modules.render;

import keystrokesmod.client.module.Category;
import keystrokesmod.client.module.ClientModule;
import keystrokesmod.client.module.ModuleInfo;
import keystrokesmod.client.module.setting.impl.SliderSetting;
import keystrokesmod.client.utils.Utils;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@ModuleInfo(name = "Time Changer", category = Category.Render)
public class TimeChanger extends ClientModule {

	public final SliderSetting time = new SliderSetting("Time", this, 0, 0, 1, 0.01f);
	
	@Override
	public void onDisable() {
		clear();
	}
	
    @SubscribeEvent
    public void onTick(TickEvent event) {
		if (!Utils.Player.isPlayerInGame()) return;
		clear();
		mc.theWorld.setWorldTime((long) (time.getInput() * 22999));
    }
	
	@Override
    public boolean onReceive(Packet packet) {
    	if (packet instanceof S03PacketTimeUpdate) {
			return true;
		} else if (packet instanceof S2BPacketChangeGameState) {
			S2BPacketChangeGameState wrapped = (S2BPacketChangeGameState) packet;
			if (wrapped.getGameState() == 1 || wrapped.getGameState() == 2) {
				return true;
			}
		}
        return false;
    }

	public void clear() {
		if (!Utils.Player.isPlayerInGame()) return;
		mc.theWorld.setRainStrength(0);
		mc.theWorld.getWorldInfo().setCleanWeatherTime(Integer.MAX_VALUE);
		mc.theWorld.getWorldInfo().setRainTime(0);
		mc.theWorld.getWorldInfo().setThunderTime(0);
		mc.theWorld.getWorldInfo().setRaining(false);
		mc.theWorld.getWorldInfo().setThundering(false);
	}
}
