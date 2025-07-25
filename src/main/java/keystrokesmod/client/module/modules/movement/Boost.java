package keystrokesmod.client.module.modules.movement;

import keystrokesmod.client.main.Raven;
import keystrokesmod.client.module.Category;
import keystrokesmod.client.module.ClientModule;
import keystrokesmod.client.module.ModuleInfo;
import keystrokesmod.client.module.setting.impl.DescriptionSetting;
import keystrokesmod.client.module.setting.impl.SliderSetting;
import keystrokesmod.client.utils.ReflectUtil;

@ModuleInfo(name = "Boost", category = Category.Movement)
public class Boost extends ClientModule {
    private final DescriptionSetting c = new DescriptionSetting("20 ticks are in 1 second", this);
    private final SliderSetting a = new SliderSetting("Multiplier", this, 2.0, 1.0, 3.0, 0.05);
    private final SliderSetting b = new SliderSetting("Time (ticks)", this, 15.0, 1.0, 80.0, 1.0);
    private int i = 0;
    private boolean t = false;
    
    @Override
    public void onEnable() {
        final Timer timer = (Timer) Raven.moduleManager.getModuleByClazz(Timer.class);
        if (timer != null && timer.isEnabled()) {
            this.t = true;
            timer.disable();
        }
    }
    
    @Override
    public void onDisable() {
        this.i = 0;
        if (ReflectUtil.getTimer().timerSpeed != 1.0f) {
            ReflectUtil.resetTimer();
        }
        if (this.t) {
            final Timer timer = (Timer) Raven.moduleManager.getModuleByClazz(Timer.class);
            if (timer != null) {
                timer.enable();
            }
        }
        this.t = false;
    }
    
    @Override
    public void update() {
        if (this.i == 0) {
            this.i = mc.thePlayer.ticksExisted;
        }
        ReflectUtil.getTimer().timerSpeed = (float)a.getInput();
        if (this.i == mc.thePlayer.ticksExisted - b.getInput()) {
            ReflectUtil.resetTimer();
            this.disable();
        }
    }
}
