package keystrokesmod.client.module.modules.player;

import keystrokesmod.client.main.Raven;
import keystrokesmod.client.module.Category;
import keystrokesmod.client.module.ClientModule;
import keystrokesmod.client.module.ModuleInfo;
import keystrokesmod.client.module.modules.movement.Fly;
import keystrokesmod.client.module.setting.impl.TickSetting;
import keystrokesmod.client.module.setting.impl.DescriptionSetting;
import keystrokesmod.client.module.setting.impl.SliderSetting;

@ModuleInfo(name = "FallSpeed", category = Category.Player)
public class FallSpeed extends ClientModule {
    private final DescriptionSetting dc = new DescriptionSetting("Vanilla max: 3.92", this);
    private final SliderSetting a = new SliderSetting("Motion", this, 5.0, 0.0, 8.0, 0.1);
    private final TickSetting b = new TickSetting("Disable XZ motion", this, true);

    @Override
    public void update() {
        if (mc.thePlayer.fallDistance >= 2.5) {
            final ClientModule fly = Raven.moduleManager.getModuleByClazz(Fly.class);
            final ClientModule noFall = Raven.moduleManager.getModuleByClazz(NoFall.class);
            if ((fly != null && fly.isEnabled()) || (noFall != null && noFall.isEnabled())) {
                return;
            }
            if (mc.thePlayer.capabilities.isCreativeMode || mc.thePlayer.capabilities.isFlying) {
                return;
            }
            if (mc.thePlayer.isOnLadder() || mc.thePlayer.isInWater() || mc.thePlayer.isInLava()) {
                return;
            }
            mc.thePlayer.motionY = -a.getInput();
            if (b.isToggled()) {
                mc.thePlayer.motionZ = 0.0;
                mc.thePlayer.motionX = 0.0;
            }
        }
    }
}
