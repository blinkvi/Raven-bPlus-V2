package keystrokesmod.client.utils;

import java.util.ArrayList;
import java.util.List;

import keystrokesmod.client.main.Raven;
import net.minecraft.entity.Entity;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class MouseManager implements IMinecraft {
    private static final List<Long> leftClicks = new ArrayList<Long>();
    private static final List<Long> rightClicks = new ArrayList<Long>();
    public static long leftClickTimer = 0;
    public static long rightClickTimer = 0;
    
    @SubscribeEvent
    public void onMouseUpdate(final MouseEvent mouse) {
        if (mouse.buttonstate) {
            if (mouse.button == 0) {
                addLeftClick();
                if (Raven.debugger && mc.objectMouseOver != null) {
                    final Entity en = mc.objectMouseOver.entityHit;
                    if (en == null) {
                        return;
                    }
                }
            }
            else if (mouse.button == 1) {
                addRightClick();
            }
        }
    }
    
    public static void addLeftClick() {
        leftClicks.add(leftClickTimer = System.currentTimeMillis());
    }
    
    public static void addRightClick() {
        rightClicks.add(rightClickTimer = System.currentTimeMillis());
    }
    
    public static int getLeftClickCounter() {
        if (!Utils.Player.isPlayerInGame()) {
            return leftClicks.size();
        }
        for (final Long lon : leftClicks) {
            if (lon < System.currentTimeMillis() - 1000L) {
                leftClicks.remove(lon);
                break;
            }
        }
        return leftClicks.size();
    }
    
    public static int getRightClickCounter() {
        if (!Utils.Player.isPlayerInGame()) {
            return leftClicks.size();
        }
        for (final Long lon : rightClicks) {
            if (lon < System.currentTimeMillis() - 1000L) {
                rightClicks.remove(lon);
                break;
            }
        }
        return rightClicks.size();
    }
}
