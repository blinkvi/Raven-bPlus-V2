package keystrokesmod.client.module.modules.other;

import keystrokesmod.client.clickgui.raven.ClickGui;
import keystrokesmod.client.module.Category;
import keystrokesmod.client.module.ClientModule;
import keystrokesmod.client.module.ModuleInfo;
import keystrokesmod.client.module.setting.impl.DescriptionSetting;
import keystrokesmod.client.module.setting.impl.SliderSetting;
import keystrokesmod.client.module.setting.impl.TickSetting;
import keystrokesmod.client.utils.Utils;

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
    
    @Override
    public String getUnformattedTextForChat(final String text) {
        if (mc.currentScreen instanceof ClickGui) {
            return text;
        }
        if (ignoreDebug.isToggled() && mc.gameSettings.showDebugInfo) {
            return text;
        }
        if (ignoreAllGui.isToggled() && mc.currentScreen != null) {
            return text;
        }
        if (value.getInput() == 1.0) {
            final StringBuilder s2 = new StringBuilder();
            StringBuilder s3 = new StringBuilder();
            boolean w = false;
            for (int i = 0; i < text.length(); ++i) {
                final String c = Character.toString(text.charAt(i));
                if (c.equals("�")) {
                    w = true;
                    s3.append(c);
                }
                else if (w) {
                    w = false;
                    s3.append(c);
                }
                else {
                    s2.append((CharSequence)s3).append("�").append("k").append(c);
                    s3 = new StringBuilder();
                }
            }
            return s2.toString();
        }
        if (value.getInput() == 2.0) {
            return (text.length() > 3) ? text.substring(0, 3) : text;
        }
        if (value.getInput() != 3.0) {
            return "";
        }
        final StringBuilder s2 = new StringBuilder();
        for (int j = 0; j < text.length(); ++j) {
            final char c2 = (char)(text.charAt(j) + m3s);
            s2.append(c2);
        }
        return s2.toString();
    }
}
