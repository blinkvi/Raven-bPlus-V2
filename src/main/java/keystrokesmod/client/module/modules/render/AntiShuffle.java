package keystrokesmod.client.module.modules.render;

import keystrokesmod.client.module.Category;
import keystrokesmod.client.module.ClientModule;
import keystrokesmod.client.module.ModuleInfo;
import keystrokesmod.client.module.setting.impl.DescriptionSetting;

@ModuleInfo(name = "AntiShuffle", category = Category.Render)
public class AntiShuffle extends ClientModule {
    private final DescriptionSetting a = new DescriptionSetting("Remove &k", this);
    
    @Override
    public String getUnformattedTextForChat(final String text) {
        return text.replace("�k", "");
    }
}
