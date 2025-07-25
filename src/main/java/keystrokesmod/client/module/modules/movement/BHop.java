package keystrokesmod.client.module.modules.movement;

import keystrokesmod.client.main.Raven;
import keystrokesmod.client.module.Category;
import keystrokesmod.client.module.ClientModule;
import keystrokesmod.client.module.ModuleInfo;
import keystrokesmod.client.module.setting.impl.SliderSetting;
import keystrokesmod.client.utils.Utils;
import net.minecraft.client.settings.KeyBinding;

@ModuleInfo(name = "BHop", category = Category.Movement)
public class BHop extends ClientModule {
    private final SliderSetting a = new SliderSetting("Speed", this, 2.0, 1.0, 15.0, 0.2);
    private final double bspd = 0.0025;

    @Override
    public void update() {
        final ClientModule fly = Raven.moduleManager.getModuleByClazz(Fly.class);
        if (fly != null && !fly.isEnabled() && Utils.Player.isMoving() && !mc.thePlayer.isInWater()) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), false);
            mc.thePlayer.noClip = true;
            if (mc.thePlayer.onGround) {
                mc.thePlayer.jump();
            }
            mc.thePlayer.setSprinting(true);
            final double spd = 0.0025 * a.getInput();
            final double m = (float)(Math.sqrt(mc.thePlayer.motionX * mc.thePlayer.motionX + mc.thePlayer.motionZ * mc.thePlayer.motionZ) + spd);
            Utils.Player.bop(m);
        }
    }
}
