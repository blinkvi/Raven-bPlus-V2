package keystrokesmod.client.module.modules.client;

import keystrokesmod.client.main.Raven;
import keystrokesmod.client.module.Category;
import keystrokesmod.client.module.ClientModule;
import keystrokesmod.client.module.ModuleInfo;
import keystrokesmod.client.utils.ChatHelper;
import keystrokesmod.client.utils.MouseManager;
import keystrokesmod.keystroke.KeyStrokeRenderer;
import net.minecraftforge.common.MinecraftForge;

@ModuleInfo(name = "Self Destruct", category = Category.Client)
public class SelfDestruct extends ClientModule {
    @Override
    public void onEnable() {
        this.disable();
        SelfDestruct.mc.displayGuiScreen(null);
        for (final ClientModule module : Raven.moduleManager.getModules()) {
            if (module != this && module.isEnabled()) {
                module.disable();
            }
        }
        MinecraftForge.EVENT_BUS.unregister(new Raven());
        MinecraftForge.EVENT_BUS.unregister(new MouseManager());
        MinecraftForge.EVENT_BUS.unregister(new KeyStrokeRenderer());
        MinecraftForge.EVENT_BUS.unregister(new ChatHelper());
    }
}
