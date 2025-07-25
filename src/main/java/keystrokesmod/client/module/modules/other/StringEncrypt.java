package keystrokesmod.client.module.modules.other;

import keystrokesmod.client.clickgui.raven.ClickGui;
import keystrokesmod.client.events.RenderTextEvent;
import keystrokesmod.client.module.Category;
import keystrokesmod.client.module.ClientModule;
import keystrokesmod.client.module.ModuleInfo;
import keystrokesmod.client.module.setting.impl.DescriptionSetting;
import keystrokesmod.client.module.setting.impl.SliderSetting;
import keystrokesmod.client.module.setting.impl.TickSetting;
import keystrokesmod.client.utils.Utils;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleInfo(name = "StringEncrypt", category = Category.Other)
public class StringEncrypt extends ClientModule {
    private static int m3s = 1;
    private boolean m3t = false;
    public TickSetting ignoreDebug = new TickSetting("Ignore debug", this, false);
    public TickSetting ignoreAllGui = new TickSetting("Ignore all GUI", this, false);
    public SliderSetting value = new SliderSetting("Value", this, 1.0, 1.0, 4.0, 1.0);
    public DescriptionSetting moduleDesc = new DescriptionSetting("Mode: &k", this);

    @Override
    public void onEnable() {
        if (value.getInput() == 3.0) {
            m3s = Utils.Java.rand().nextInt(10) - 5;
            if (m3s == 0) {
                m3s = 1;
            }
        }
    }
    
    @Override
    public void guiUpdate() {
        switch ((int)value.getInput()) {
            case 1: {
                this.m3t = false;
                moduleDesc.setDesc("Mode: &k");
                break;
            }
            case 2: {
                this.m3t = false;
                moduleDesc.setDesc("Mode: 3 char");
                break;
            }
            case 3: {
                if (!this.m3t) {
                    m3s = Utils.Java.rand().nextInt(10) - 5;
                    if (m3s == 0) {
                        m3s = 1;
                    }
                }
                this.m3t = true;
                moduleDesc.setDesc("Mode: Char shift");
                break;
            }
            case 4: {
                this.m3t = false;
                moduleDesc.setDesc("Mode: Blank");
                break;
            }
        }
    }
    
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onRenderText(RenderTextEvent event) {
        if (mc.currentScreen instanceof ClickGui) return;

        if (ignoreDebug.isToggled() && mc.gameSettings.showDebugInfo) return;

        if (ignoreAllGui.isToggled() && mc.currentScreen != null) return;

        String originalText = event.text;

        String newText = "";

        if (value.getInput() == 1.0) {
            StringBuilder result = new StringBuilder();
            StringBuilder formatting = new StringBuilder();
            boolean inFormat = false;

            for (int i = 0; i < originalText.length(); i++) {
                String c = Character.toString(originalText.charAt(i));
                if (c.equals("§")) {
                    inFormat = true;
                    formatting.append(c);
                } else if (inFormat) {
                    inFormat = false;
                    formatting.append(c);
                } else {
                    result.append(formatting).append("§k").append(c);
                    formatting.setLength(0);
                }
            }

            newText = result.toString();
        } else if (value.getInput() == 2.0) {
            newText = originalText.length() > 3 ? originalText.substring(0, 3) : originalText;
        } else if (value.getInput() == 3.0) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < originalText.length(); i++) {
                char shifted = (char)(originalText.charAt(i) + m3s);
                sb.append(shifted);
            }
            newText = sb.toString();
        } else {
            newText = "";
        }

        event.text = newText;
    }

}
