package keystrokesmod.client.module.modules.client;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.lwjgl.input.Mouse;

import keystrokesmod.client.clickgui.raven.ClickGui;
import keystrokesmod.client.main.Raven;
import keystrokesmod.client.module.Category;
import keystrokesmod.client.module.ClientModule;
import keystrokesmod.client.module.ModuleInfo;
import keystrokesmod.client.module.setting.impl.ComboSetting;
import keystrokesmod.client.module.setting.impl.DescriptionSetting;
import keystrokesmod.client.module.setting.impl.TickSetting;
import keystrokesmod.client.utils.Utils;
import keystrokesmod.client.utils.Utils.HUD.PositionMode;
import keystrokesmod.client.utils.font.FontRenderer;
import keystrokesmod.client.utils.font.Fonts;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@ModuleInfo(name = "HUD", category = Category.Client)
public class HUD extends ClientModule {
    public final ComboSetting mode = new ComboSetting("Mode: ", this, ColourModes.ASTOLFO2, ColourModes.values());
    private final ComboSetting fontMode = new ComboSetting("Font Mode", this, "Rubik", "Rubik", "Drip", "Semi bold");
    public final TickSetting alphabeticalSort = new TickSetting("Alphabetical sort", this, false);
    
    private final DescriptionSetting desc = new DescriptionSetting("Hide Category", this);
    public final TickSetting hideClient = new TickSetting("Hide Client", this, false);
    public final TickSetting hideCombat = new TickSetting("Hide Combat", this, false);
    public final TickSetting hideMinigames = new TickSetting("Hide Minigames", this, false);
    public final TickSetting hideMovement = new TickSetting("Hide Movement", this, false);
    public final TickSetting hideOther = new TickSetting("Hide Other", this, false);
    public final TickSetting hidePlayer = new TickSetting("Hide Player", this, false);
    public final TickSetting hideRender = new TickSetting("Hide Render", this, false);
    public final TickSetting hideWorld = new TickSetting("Hide World", this, false);
    
    public static int hudX = 5;
    public static int hudY = 70;
    public static PositionMode positionMode;
    
    private static boolean draggingModuleList = false;
    private static float dragOffsetX = 0;
	private static float dragOffsetY = 0;

    @Override
    public void onEnable() {
        Raven.moduleManager.sort();
    }
    
    @Override
    public void guiButtonToggled(final TickSetting b) {
       if (b == alphabeticalSort) {
            Raven.moduleManager.sort();
        }
    }
    
    @SubscribeEvent
    public void a(final TickEvent.RenderTickEvent ev) {
        if (ev.phase == TickEvent.Phase.END && Utils.Player.isPlayerInGame()) {
            if (mc.gameSettings.showDebugInfo || mc.currentScreen instanceof ClickGui || mc.currentScreen instanceof GuiContainer || mc.currentScreen instanceof GuiIngameMenu) return;
            final int margin = 2;
            int y = hudY;
            int del = 0;
            if (!alphabeticalSort.isToggled()) {
                if (positionMode == PositionMode.UPLEFT || positionMode == PositionMode.UPRIGHT) {
                    Raven.moduleManager.sortShortLong();
                }
                else if (positionMode == PositionMode.DOWNLEFT || positionMode == PositionMode.DOWNRIGHT) {
                    Raven.moduleManager.sortLongShort();
                }
            }
            final List<ClientModule> en = new ArrayList<ClientModule>(Raven.moduleManager.getModules());
            if (en.isEmpty()) {
                return;
            }
            final int textBoxWidth = Raven.moduleManager.getLongestActiveModule();
            final int textBoxHeight = Raven.moduleManager.getBoxHeight(margin);
            FontRenderer font = getFonts();
            if (hudX < 0) hudX = margin;
            if (hudY < 0) hudY = margin;
            
            if (hudX + textBoxWidth > mc.displayWidth / 2) hudX = mc.displayWidth / 2 - textBoxWidth - margin;
            if (hudY + textBoxHeight > mc.displayHeight / 2) hudY = mc.displayHeight / 2 - textBoxHeight;
            
            for (final ClientModule m : en) {
                if (m.isEnabled() && m != this && m.shouldDisplay(this)) {
                    if (positionMode == PositionMode.DOWNRIGHT || positionMode == PositionMode.UPRIGHT) {
                        if (mode.is(ColourModes.RAVEN)) {
                            font.drawString(m.getName(), hudX + (float)(textBoxWidth - font.getStringWidth(m.getName())), (float)y, Utils.Client.rainbowDraw(2L, del));
                            y += font.getHeight() + margin;
                            del -= 120;
                        }
                        else if (mode.is(ColourModes.RAVEN2)) {
                            font.drawString(m.getName(), hudX + (float)(textBoxWidth - font.getStringWidth(m.getName())), (float)y, Utils.Client.rainbowDraw(2L, del));
                            y += font.getHeight() + margin;
                            del -= 10;
                        }
                        else if (mode.is(ColourModes.ASTOLFO)) {
                            font.drawString(m.getName(), hudX + (float)(textBoxWidth - font.getStringWidth(m.getName())), (float)y, Utils.Client.astolfoColorsDraw(10, 14));
                            y += font.getHeight() + margin;
                            del -= 120;
                        }
                        else if (mode.is(ColourModes.ASTOLFO2)) {
                            font.drawString(m.getName(), hudX + (float)(textBoxWidth - font.getStringWidth(m.getName())), (float)y, Utils.Client.astolfoColorsDraw(10, del));
                            y += font.getHeight() + margin;
                            del -= 120;
                        }
                        else {
                            if (mode.is(ColourModes.ASTOLFO3)) {
                                font.drawString(m.getName(), hudX + (float)(textBoxWidth - font.getStringWidth(m.getName())), (float)y, Utils.Client.astolfoColorsDraw(10, del));
                                y += font.getHeight() + margin;
                                del -= 10;
                            }
                        }
                    }
                    else if (mode.is(ColourModes.RAVEN)) {
                        font.drawString(m.getName(), (float)hudX, (float)y, Utils.Client.rainbowDraw(2L, del));
                        y += font.getHeight() + margin;
                        del -= 120;
                    }
                    else if (mode.is(ColourModes.RAVEN2)) {
                        font.drawString(m.getName(), (float)hudX, (float)y, Utils.Client.rainbowDraw(2L, del));
                        y += font.getHeight() + margin;
                        del -= 10;
                    }
                    else if (mode.is(ColourModes.ASTOLFO)) {
                        font.drawString(m.getName(), (float)hudX, (float)y, Utils.Client.astolfoColorsDraw(10, 14));
                        y += font.getHeight() + margin;
                        del -= 120;
                    }
                    else if (mode.is(ColourModes.ASTOLFO2)) {
                        font.drawString(m.getName(), (float)hudX, (float)y, Utils.Client.astolfoColorsDraw(10, del));
                        y += font.getHeight() + margin;
                        del -= 120;
                    }
                    else {
                        if (mode.is(ColourModes.ASTOLFO3)) {
                            font.drawString(m.getName(), (float)hudX, (float)y, Utils.Client.astolfoColorsDraw(10, del));
                            y += font.getHeight() + margin;
                            del -= 10;
                        }
                    }
                }
            }
        }
    }
    
    @Override
    public void onDrag(int mouseX, int mouseY, float partialTicks) {
    	FontRenderer font = getFonts();
        List<ClientModule> activeModules = Raven.moduleManager.getModules().stream()
            .filter(module -> module.isEnabled() && module != Raven.moduleManager.getModuleByClazz(HUD.class))
            .collect(Collectors.toList());

        if (activeModules.isEmpty()) {
            return;
        }

        float maxWidth = 0;
        for (ClientModule module : activeModules) {
            float w = font.getStringWidth(module.getName()) + 4;
            if (w > maxWidth) maxWidth = w;
        }

        float height = activeModules.size() * (font.getHeight() + 2);
        boolean mouseDown = Mouse.isButtonDown(0);

        if (mouseDown) {
            boolean hovering = mouseX >= hudX && mouseX <= hudX + maxWidth && mouseY >= hudY && mouseY <= hudY + height;

            if (hovering && !draggingModuleList) {
                draggingModuleList = true;
                dragOffsetX = mouseX - hudX;
                dragOffsetY = mouseY - hudY;
            }

            if (draggingModuleList) {
                hudX = (int) (mouseX - dragOffsetX);
                hudY = (int) (mouseY - dragOffsetY);
            }
        } else {
            draggingModuleList = false;
        }
    }

    public FontRenderer getFonts() {
    	FontRenderer font = null;
    	
    	switch (fontMode.getMode()) {
    	case "Rubik":
    		font = Fonts.RUBIK.get(16);
    		break;
    	case "Drip":
    		font  = Fonts.DRIP.get(16);
    		break;
    	case "Semi bold":
    		font = Fonts.SEMIBOLD.get(16);
    		break;
    	}
    	
    	return font;
    }

    public enum ColourModes {
        RAVEN, 
        RAVEN2, 
        ASTOLFO, 
        ASTOLFO2, 
        ASTOLFO3, 
        KOPAMED;
    }
}
