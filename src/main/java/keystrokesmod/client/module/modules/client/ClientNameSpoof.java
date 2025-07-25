package keystrokesmod.client.module.modules.client;

import keystrokesmod.client.module.Category;
import keystrokesmod.client.module.ClientModule;
import keystrokesmod.client.module.ModuleInfo;
import keystrokesmod.client.module.setting.impl.DescriptionSetting;

@ModuleInfo(name = "ClientNameSpoof", category = Category.Client)
public class ClientNameSpoof extends ClientModule {
	private final DescriptionSetting desc = new DescriptionSetting("Command f3name [name]", this);

    public static String newName = "";
}