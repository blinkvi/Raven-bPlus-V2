package keystrokesmod.client.module.modules.other;

import keystrokesmod.client.module.Category;
import keystrokesmod.client.module.ClientModule;
import keystrokesmod.client.module.ModuleInfo;
import keystrokesmod.client.utils.Utils;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

@ModuleInfo(name = "Fake Chat", category = Category.Other)
public class FakeChat extends ClientModule {
    public static String msg = "&eThis is a fake chat message.";
    public static final String command = "fakechat";
    public static final String c4 = "&cInvalid message.";

    @Override
    public void onEnable() {
        if (FakeChat.msg.contains("\\n")) {
            final String[] split2;
            final String[] split = split2 =msg.split("\\\\n");
            for (final String s : split2) {
                this.sm(s);
            }
        }
        else {
            this.sm(FakeChat.msg);
        }
        this.disable();
    }
    
    private void sm(final String txt) {
       mc.thePlayer.addChatMessage((IChatComponent)new ChatComponentText(Utils.Client.reformat(txt)));
    }
}
