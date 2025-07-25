package keystrokesmod.client.module.modules.client;

import org.lwjgl.input.Keyboard;

import keystrokesmod.client.main.Raven;
import keystrokesmod.client.module.Category;
import keystrokesmod.client.module.ClientModule;
import keystrokesmod.client.module.ModuleInfo;
import keystrokesmod.client.module.setting.impl.ComboSetting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

@ModuleInfo(name = "Gui", category = Category.Client, key = 54)
public class GuiModule extends ClientModule {

	public final ComboSetting mode = new ComboSetting("Mode", this, Colors.PastelPink, Colors.values());
	
	public GuiModule() {
		this.withEnabled(false);
	}

    @Override
    public void onEnable() {
        mc.displayGuiScreen(Raven.clickGui);
        super.onEnable();
    }

    @SubscribeEvent
    public void onInputKeyboard(InputEvent.KeyInputEvent event) {
        if (Keyboard.getEventKey() == this.keycode || Keyboard.getEventKey() == Keyboard.KEY_ESCAPE) {
            if (mc.currentScreen == null) {
            	mc.displayGuiScreen(Raven.clickGui);
                this.disable();
                this.onDisable();
            } else {
                mc.displayGuiScreen(null);
                this.disable();
                this.onDisable();
            }
        }
    }

    @Override
    public void onDisable() {
    	mc.displayGuiScreen(null);
    	super.onDisable();
    }

    public enum Colors {
        Mai, Sassan, Gold, Steel, Emerald, Orange, Amethyst, Lily, PastelPink, Cherry
    }
}
