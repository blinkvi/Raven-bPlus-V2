package keystrokesmod.client.module.modules.other;

import keystrokesmod.client.module.Category;
import keystrokesmod.client.module.ClientModule;
import keystrokesmod.client.module.ModuleInfo;

@ModuleInfo(name = "NameHider", category = Category.Other)
public class NameHider extends ClientModule {
	public static String n = "You";
	public static String playerNick = "";

	@Override
	public String getUnformattedTextForChat(String text) {
		if (mc.thePlayer != null) {
			text = playerNick.isEmpty() ? text.replace(mc.thePlayer.getName(), n) : text.replace(playerNick, n);
		}

		return text;
	}
}
