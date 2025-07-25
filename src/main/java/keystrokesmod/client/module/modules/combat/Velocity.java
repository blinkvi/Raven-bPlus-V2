package keystrokesmod.client.module.modules.combat;

import org.lwjgl.input.Keyboard;

import keystrokesmod.client.module.Category;
import keystrokesmod.client.module.ClientModule;
import keystrokesmod.client.module.ModuleInfo;
import keystrokesmod.client.module.setting.impl.TickSetting;
import keystrokesmod.client.module.setting.impl.SliderSetting;
import keystrokesmod.client.utils.Utils;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleInfo(name = "Velocity", category = Category.Combat)
public class Velocity extends ClientModule {
    private final SliderSetting a = new SliderSetting("Horizontal", this, 90.0, 0.0, 100.0, 1.0);
    private final SliderSetting b = new SliderSetting("Vertical", this, 100.0, 0.0, 100.0, 1.0);
    private final SliderSetting c = new SliderSetting("Chance", this, 100.0, 0.0, 100.0, 1.0);
    private final TickSetting d = new TickSetting("Only while targeting", this, false);
    private final TickSetting e = new TickSetting("Disable while holding S", this, false);
    
    @SubscribeEvent
    public void c(final LivingEvent.LivingUpdateEvent ev) {
        if (Utils.Player.isPlayerInGame() && mc.thePlayer.maxHurtTime > 0 && mc.thePlayer.hurtTime == mc.thePlayer.maxHurtTime) {
            if (d.isToggled() && (mc.objectMouseOver == null || mc.objectMouseOver.entityHit == null)) {
                return;
            }
            
            if (e.isToggled() && Keyboard.isKeyDown(mc.gameSettings.keyBindBack.getKeyCode())) {
                return;
            }
            
            if (c.getInput() != 100.0) {
                final double ch = Math.random();
                if (ch >= c.getInput() / 100.0) {
                    return;
                }
            }
            
            if (a.getInput() != 100.0) {
            	mc.thePlayer.motionX *= a.getInput() / 100.0;
            	mc.thePlayer.motionZ *= a.getInput() / 100.0;
            }
            if (b.getInput() != 100.0) {
            	mc.thePlayer.motionY *= b.getInput() / 100.0;
            }
        }
    }
}
