package keystrokesmod.client.module.modules.render;

import keystrokesmod.client.module.Category;
import keystrokesmod.client.module.ClientModule;
import keystrokesmod.client.module.ModuleInfo;
import keystrokesmod.client.module.setting.impl.DescriptionSetting;
import keystrokesmod.client.utils.Utils;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@ModuleInfo(name = "Fullbright", category = Category.Render)
public class Fullbright extends ClientModule {
	private final DescriptionSetting desc = new DescriptionSetting("No more darkness!", this);
    private float defaultGamma;
    private final float clientGamma = 10000.0f;
    
    @Override
    public void onEnable() {
        this.defaultGamma = mc.gameSettings.gammaSetting;
        super.onEnable();
    }
    
    @Override
    public void onDisable() {
        super.onEnable();
        mc.gameSettings.gammaSetting = this.defaultGamma;
    }
    
    @SubscribeEvent
    public void onPlayerTick(final TickEvent.PlayerTickEvent e) {
        if (!Utils.Player.isPlayerInGame()) {
            this.onDisable();
            return;
        }
        
        if (mc.gameSettings.gammaSetting != this.clientGamma) {
            mc.gameSettings.gammaSetting = this.clientGamma;
        }
    }
}
