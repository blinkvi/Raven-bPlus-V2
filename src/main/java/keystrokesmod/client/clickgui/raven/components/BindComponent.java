package keystrokesmod.client.clickgui.raven.components;

import java.awt.Color;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import keystrokesmod.client.clickgui.raven.Component;
import keystrokesmod.client.clickgui.theme.Theme;
import keystrokesmod.client.module.modules.client.GuiModule;
import net.minecraft.client.Minecraft;

public class BindComponent extends Component
{
    private boolean isBinding;
    private final ModuleComponent p;
    private int o;
    private int x;
    private int y;
    
    public BindComponent(final ModuleComponent b, final int o) {
        this.p = b;
        this.x = b.category.getX() + b.category.getWidth();
        this.y = b.category.getY() + b.o;
        this.o = o;
    }
    
    @Override
    public void draw() {
        GL11.glPushMatrix();
        GL11.glScaled(0.5, 0.5, 0.5);
        this.dr(this.isBinding ? "Select a key..." : ("Bind" + ": " + Keyboard.getKeyName(this.p.mod.getKeycode())));
        GL11.glPopMatrix();
    }
    
    @Override
    public void update(final int mousePosX, final int mousePosY) {
        final boolean h = this.i(mousePosX, mousePosY);
        this.y = this.p.category.getY() + this.o;
        this.x = this.p.category.getX();
    }
    
    @Override
    public void mouseDown(final int x, final int y, final int b) {
    	if (!this.p.isVisible()) return;
    	
        if (this.i(x, y) && b == 0 && this.p.open) {
            this.isBinding = !this.isBinding;
        }
    }
    
    @Override
    public void mouseReleased(final int x, final int y, final int m) {
    }
    
    @Override
    public void keyTyped(final char t, final int k) {
        if (!this.p.mod.getName().equalsIgnoreCase("AutoConfig") && this.isBinding) {
            if (k == 11) {
                if (this.p.mod instanceof GuiModule) {
                    this.p.mod.setbind(54);
                }
                else {
                    this.p.mod.setbind(0);
                }
            }
            else {
                this.p.mod.setbind(k);
            }
            this.isBinding = false;
        }
    }
    
    @Override
    public void setComponentStartAt(final int n) {
        this.o = n;
    }
    
    public boolean i(final int x, final int y) {
        return x > this.x && x < this.x + this.p.category.getWidth() && y > this.y - 1 && y < this.y + 12;
    }
    
    @Override
    public int height() {
        return 16;
    }
    
    private void dr(final String s) {
        mc.fontRendererObj.drawStringWithShadow(s, (float)((this.p.category.getX() + 4) * 2), (float)((this.p.category.getY() + this.o + 3) * 2), Theme.getMainColor().getRGB());
    }
}
