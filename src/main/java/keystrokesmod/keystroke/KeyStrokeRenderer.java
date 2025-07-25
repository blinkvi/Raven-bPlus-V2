package keystrokesmod.keystroke;

import java.awt.Color;
import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class KeyStrokeRenderer {
    private static final int[] a = new int[] { 16777215, 16711680, 65280, 255, 16776960, 11141290 };
    private final Minecraft mc;
    private final KeyStrokeKeyRenderer[] b;
    private final KeyStrokeMouse[] c;
    
    public KeyStrokeRenderer() {
        this.mc = Minecraft.getMinecraft();
        this.b = new KeyStrokeKeyRenderer[4];
        this.c = new KeyStrokeMouse[2];
        this.b[0] = new KeyStrokeKeyRenderer(this.mc.gameSettings.keyBindForward, 26, 2);
        this.b[1] = new KeyStrokeKeyRenderer(this.mc.gameSettings.keyBindBack, 26, 26);
        this.b[2] = new KeyStrokeKeyRenderer(this.mc.gameSettings.keyBindLeft, 2, 26);
        this.b[3] = new KeyStrokeKeyRenderer(this.mc.gameSettings.keyBindRight, 50, 26);
        this.c[0] = new KeyStrokeMouse(0, 2, 50);
        this.c[1] = new KeyStrokeMouse(1, 38, 50);
    }
    
    @SubscribeEvent
    public void onRenderTick(final TickEvent.RenderTickEvent e) {
        if (this.mc.currentScreen != null) {
            if (this.mc.currentScreen instanceof KeyStrokeConfigGui) {
                try {
                    this.mc.currentScreen.handleInput();
                }
                catch (IOException ex) {}
            }
        }
        else if (this.mc.inGameHasFocus && !this.mc.gameSettings.showDebugInfo) {
            this.renderKeystrokes();
        }
    }
    
    public void renderKeystrokes() {
        if (KeyStroke.enabled) {
            int x = KeyStroke.x;
            int y = KeyStroke.y;
            final int g = this.getColor(KeyStroke.currentColorNumber);
            final boolean h = KeyStroke.showMouseButtons;
            final ScaledResolution res = new ScaledResolution(this.mc);
            final int width = 74;
            final int height = h ? 74 : 50;
            if (x < 0) {
                KeyStroke.x = 0;
                x = KeyStroke.x;
            }
            else if (x > res.getScaledWidth() - width) {
                KeyStroke.x = res.getScaledWidth() - width;
                x = KeyStroke.x;
            }
            if (y < 0) {
                KeyStroke.y = 0;
                y = KeyStroke.y;
            }
            else if (y > res.getScaledHeight() - height) {
                KeyStroke.y = res.getScaledHeight() - height;
                y = KeyStroke.y;
            }
            this.drawMovementKeys(x, y, g);
            if (h) {
                this.drawMouseButtons(x, y, g);
            }
        }
    }
    
    private int getColor(final int index) {
        return (index == 6) ? Color.getHSBColor(System.currentTimeMillis() % 3750L / 3750.0f, 1.0f, 1.0f).getRGB() : KeyStrokeRenderer.a[index];
    }
    
    private void drawMovementKeys(final int x, final int y, final int textColor) {
        for (final KeyStrokeKeyRenderer key : this.b) {
            key.renderKey(x, y, textColor);
        }
    }
    
    private void drawMouseButtons(final int x, final int y, final int textColor) {
        for (final KeyStrokeMouse button : this.c) {
            button.n(x, y, textColor);
        }
    }
    
    @SubscribeEvent
    public void onTick(final TickEvent.ClientTickEvent e) {
        if (KeyStrokeMod.isKeyStrokeConfigGuiToggled) {
            KeyStrokeMod.isKeyStrokeConfigGuiToggled = false;
            Minecraft.getMinecraft().displayGuiScreen(new KeyStrokeConfigGui());
        }
    }
}
