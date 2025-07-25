package keystrokesmod.client.module.modules.other;

import keystrokesmod.client.events.RenderTextEvent;
import keystrokesmod.client.module.Category;
import keystrokesmod.client.module.ClientModule;
import keystrokesmod.client.module.ModuleInfo;
import keystrokesmod.client.utils.Utils;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleInfo(name = "NameHider", category = Category.Other)
public class NameHider extends ClientModule {
	public static String name = "";
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onRenderText(RenderTextEvent event) {
		if (!Utils.Player.isPlayerInGame()) return;

		String text = event.text;
		String ownName = mc.getSession().getUsername();
		String displayName = checkName();

		if (text.startsWith("/") || text.startsWith(".")) {
			return;
		}

		if (text.contains(ownName)) {
			text = text.replace(ownName, displayName);
			event.text = text;
		}
	}
	
	public String checkName() {
		return name.isEmpty() ? "You" : name;
	}
}
