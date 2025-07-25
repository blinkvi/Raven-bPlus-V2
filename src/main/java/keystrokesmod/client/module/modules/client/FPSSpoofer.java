package keystrokesmod.client.module.modules.client;

import java.util.concurrent.ThreadLocalRandom;

import keystrokesmod.client.module.Category;
import keystrokesmod.client.module.ClientModule;
import keystrokesmod.client.module.ModuleInfo;
import keystrokesmod.client.module.setting.impl.DescriptionSetting;
import keystrokesmod.client.module.setting.impl.DoubleSliderSetting;
import keystrokesmod.client.utils.ReflectUtil;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@ModuleInfo(name = "FPSSpoofer", category = Category.Client)
public class FPSSpoofer extends ClientModule {
	private final DescriptionSetting desc = new DescriptionSetting("Spoofs your fps", this);
    private final DoubleSliderSetting fps = new DoubleSliderSetting("FPS", this, 99860, 100000, 0, 100000, 100);

    private int ticksPassed;
    
    @Override
    public void onEnable() {
        this.ticksPassed = 0;
    }

    @SubscribeEvent
    public void onTick(final TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            this.guiUpdate();
            int fakeFps = ThreadLocalRandom.current().nextInt(
                    (int) fps.getInputMin(),
                    (int) fps.getInputMax() + 1
                );
            ReflectUtil.setFpsCounter(fakeFps);
            this.ticksPassed = 0;
            ++this.ticksPassed;
        }
    }
}