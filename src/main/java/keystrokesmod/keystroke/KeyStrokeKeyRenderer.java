package keystrokesmod.keystroke;

import java.awt.Color;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.settings.KeyBinding;

public class KeyStrokeKeyRenderer {
    private final Minecraft a;
    private final KeyBinding keyBinding;
    private final int c;
    private final int d;
    private boolean e;
    private long f;
    
    public KeyStrokeKeyRenderer(final KeyBinding i, final int j, final int k) {
        this.a = Minecraft.getMinecraft();
        this.e = true;
        this.f = 0L;
        this.keyBinding = i;
        this.c = j;
        this.d = k;
    }
    
    public void renderKey(final int l, final int m, final int color) {
        final boolean o = this.keyBinding.isKeyDown();
        final String p = Keyboard.getKeyName(this.keyBinding.getKeyCode());
        if (o != this.e) {
            this.e = o;
            this.f = System.currentTimeMillis();
        }
        double h = 1.0;
        int g = 255;
        if (o) {
            g = Math.min(255, (int)(2L * (System.currentTimeMillis() - this.f)));
            h = Math.max(0.0, 1.0 - (System.currentTimeMillis() - this.f) / 20.0);
        }
        else {
            g = Math.max(0, 255 - (int)(2L * (System.currentTimeMillis() - this.f)));
            h = Math.min(1.0, (System.currentTimeMillis() - this.f) / 20.0);
        }
        final int q = color >> 16 & 0xFF;
        final int r = color >> 8 & 0xFF;
        final int s = color & 0xFF;
        final int c = new Color(q, r, s).getRGB();
        Gui.drawRect(l + this.c, m + this.d, l + this.c + 22, m + this.d + 22, 2013265920 + (g << 16) + (g << 8) + g);
        if (KeyStroke.outline) {
            Gui.drawRect(l + this.c, m + this.d, l + this.c + 22, m + this.d + 1, c);
            Gui.drawRect(l + this.c, m + this.d + 21, l + this.c + 22, m + this.d + 22, c);
            Gui.drawRect(l + this.c, m + this.d, l + this.c + 1, m + this.d + 22, c);
            Gui.drawRect(l + this.c + 21, m + this.d, l + this.c + 22, m + this.d + 22, c);
        }
        this.a.fontRendererObj.drawString(p, l + this.c + 8, m + this.d + 8, -16777216 + ((int)(q * h) << 16) + ((int)(r * h) << 8) + (int)(s * h));
    }
}
