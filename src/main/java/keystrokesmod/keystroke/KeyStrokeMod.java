package keystrokesmod.keystroke;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = "keystrokesmod", name = "KeystrokesMod", version = "KMV5", acceptedMinecraftVersions = "[1.8.9]", clientSideOnly = true)
public class KeyStrokeMod {
    private static KeyStroke keyStroke;
    private static KeyStrokeRenderer keyStrokeRenderer = new KeyStrokeRenderer();
    static boolean isKeyStrokeConfigGuiToggled = false;
    
    @Mod.EventHandler
    public void init(final FMLInitializationEvent event) { }
    
    public static KeyStroke getKeyStroke() {
        return KeyStrokeMod.keyStroke;
    }
    
    public static KeyStrokeRenderer getKeyStrokeRenderer() {
        return KeyStrokeMod.keyStrokeRenderer;
    }
    
    public static void toggleKeyStrokeConfigGui() {
        KeyStrokeMod.isKeyStrokeConfigGuiToggled = true;
    }
}
