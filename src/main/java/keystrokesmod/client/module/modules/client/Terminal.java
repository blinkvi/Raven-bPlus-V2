package keystrokesmod.client.module.modules.client;

import keystrokesmod.client.clickgui.raven.ClickGui;
import keystrokesmod.client.main.Raven;
import keystrokesmod.client.module.Category;
import keystrokesmod.client.module.ClientModule;
import keystrokesmod.client.module.ModuleInfo;
import keystrokesmod.client.module.setting.impl.TickSetting;
import keystrokesmod.client.utils.Utils;
import keystrokesmod.client.utils.animations.TimeAnimation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@ModuleInfo(name = "Terminal", category = Category.Client, enabled = true)
public class Terminal extends ClientModule {
	public static boolean visible = false;
	public static boolean b = false;
	public static TimeAnimation animation;
	public static TickSetting animate;
	
	@Override
	public void onEnable() {
		Raven.clickGui.terminal.show();
		(animation = new TimeAnimation(500.0f)).start();
	}

	@SubscribeEvent
	public void tick(final TickEvent.PlayerTickEvent e) {
		if (Utils.Player.isPlayerInGame() && this.enabled && mc.currentScreen instanceof ClickGui
				&& Raven.clickGui.terminal.hidden()) {
			Raven.clickGui.terminal.show();
		}
	}

	@Override
	public void onDisable() {
		Raven.clickGui.terminal.hide();
		if (Terminal.animation != null) {
			Terminal.animation.start();
		}
	}
}
