package keystrokesmod.client.module.modules.player;

import keystrokesmod.client.module.Category;
import keystrokesmod.client.module.ClientModule;
import keystrokesmod.client.module.ModuleInfo;
import keystrokesmod.client.module.setting.impl.SliderSetting;
import keystrokesmod.client.utils.ReflectUtil;
import keystrokesmod.client.utils.Utils;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@ModuleInfo(name = "FastBreak", category = Category.Player)
public class FastBreak extends ClientModule {
    private final SliderSetting breakDamage = new SliderSetting("BreakDamage", this, 0.8, 0.1, 1, 0.1);

    @SubscribeEvent
    public void onPlayerTick(final TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && Utils.Player.isPlayerInGame() && mc.inGameHasFocus) {
        	ReflectUtil.setBlockHitDelay(0);
        	
            if (ReflectUtil.getCurBlockDamage() > breakDamage.getInput())
            	ReflectUtil.setCurBlockDamage(1f);
        }
    }
}