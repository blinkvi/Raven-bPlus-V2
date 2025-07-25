package keystrokesmod.client.mixin.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import keystrokesmod.client.main.Raven;
import keystrokesmod.client.module.ClientModule;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

@Mixin(GuiNewChat.class)
public class MixinGuiNewChat {
    @Inject(method = "setChatLine", at = @At("HEAD"), cancellable = true)
    private void onSetChatLine(IChatComponent chatComponent, int chatLineId, int updateCounter, boolean displayOnly, CallbackInfo ci) {
        for (ClientModule m : Raven.moduleManager.getModules()) {
            if (m.isEnabled()) {
                String modified = m.getUnformattedTextForChat(chatComponent.getUnformattedTextForChat());
                chatComponent = new ChatComponentText(modified);
            }
        }
    }
}