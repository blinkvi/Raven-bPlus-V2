package keystrokesmod.client.module.modules.combat;

import keystrokesmod.client.module.Category;
import keystrokesmod.client.module.ClientModule;
import keystrokesmod.client.module.ModuleInfo;
import keystrokesmod.client.module.setting.impl.TickSetting;
import keystrokesmod.client.module.setting.impl.DescriptionSetting;
import keystrokesmod.client.utils.ReflectUtil;
import keystrokesmod.client.utils.Utils;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@ModuleInfo(name = "DelayRemover", category = Category.Combat)
public class DelayRemover extends ClientModule {
	private final DescriptionSetting desc = new DescriptionSetting("Remove click and jump delay", this);

    public TickSetting jump = new TickSetting("Jump", this, false);
    public TickSetting click = new TickSetting("1.7 HitReg", this, true);
    
    @SubscribeEvent
    public void playerTickEvent(final TickEvent.PlayerTickEvent event) {
        if (Utils.Player.isPlayerInGame()) {
            if (!mc.inGameHasFocus) {
                return;
            }
            
            if (jump.isToggled()) {
            	ReflectUtil.setJumpTicks(0);
            }
            
            if (click.isToggled()) {
            	ReflectUtil.setLeftClickCounter(0);
            }
        }
    }
}
